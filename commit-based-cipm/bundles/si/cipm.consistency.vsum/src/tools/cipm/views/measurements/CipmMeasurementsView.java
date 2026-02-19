package tools.cipm.views.measurements;

import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.MeasurementsRepository;
import tools.vitruv.framework.views.ChangeableViewSource;

/**
 * View for adding measurements to the VSUM.
 * Adds one record at a time and commits changes to trigger reactions.
 */
public class CipmMeasurementsView extends BaseMeasurementsView {
	private static final Logger LOGGER = Logger.getLogger(CipmMeasurementsView.class.getName());

	protected CipmMeasurementsView(BaseMeasurementsViewType viewType, ChangeableViewSource viewSource) {
		super(viewType, viewSource);
	}

	@Override
	public void addMeasurement(Object measurement) {
		if (measurement == null) {
			LOGGER.warn("Cannot add null measurement");
			return;
		}

		if (!(measurement instanceof MeasurementRecord)) {
			LOGGER.warn("Measurement must be a MeasurementRecord, got: " + measurement.getClass().getName());
			return;
		}

		MeasurementRecord record = (MeasurementRecord) measurement;

		modifyContents((resourceSet) -> {
			// Find or create the Measurements root from the actual ResourceSet
			Measurements measurements = findOrCreateMeasurementsInResourceSet(resourceSet);

			// Find or create a MeasurementsBlock to add the record to
			MeasurementsBlock block = findOrCreateCurrentBlock(measurements);

			// Add the record to the block
			block.getRecords().add(record);

			LOGGER.debug("Added " + record.getClass().getSimpleName());
		});
	}

	@Override
	public int clearCurrentBlock() {
		final int[] cleared = {0};
		modifyContents((resourceSet) -> {
			Measurements measurements = findOrCreateMeasurementsInResourceSet(resourceSet);
			if (!measurements.getRepositories().isEmpty()) {
				MeasurementsRepository repository = measurements.getRepositories().get(0);
				if (!repository.getBlocks().isEmpty()) {
					MeasurementsBlock block = repository.getBlocks().get(repository.getBlocks().size() - 1);
					cleared[0] = block.getRecords().size();
					block.getRecords().clear();
					LOGGER.debug("Cleared " + cleared[0] + " records from current block");
				}
			}
		});
		return cleared[0];
	}

	@Override
	public void startNewBlock() {
		modifyContents((resourceSet) -> {
			Measurements measurements = findOrCreateMeasurementsInResourceSet(resourceSet);
			MeasurementsRepository repository;
			if (measurements.getRepositories().isEmpty()) {
				repository = MeasurementsFactory.eINSTANCE.createMeasurementsRepository();
				repository.setUri("default");
				measurements.getRepositories().add(repository);
			} else {
				repository = measurements.getRepositories().get(0);
			}
			MeasurementsBlock newBlock = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
			repository.getBlocks().add(newBlock);
			LOGGER.debug("Started new block (total blocks: " + repository.getBlocks().size() + ")");
		});
	}

	@Override
	public void replaceCurrentBlockWithProxy(String blockFileAbsolutePath) {
		modifyContents((resourceSet) -> {
			Measurements measurements = findOrCreateMeasurementsInResourceSet(resourceSet);
			if (measurements.getRepositories().isEmpty()) {
				LOGGER.warn("No repositories found, cannot replace block with proxy");
				return;
			}

			MeasurementsRepository repository = measurements.getRepositories().get(0);
			if (repository.getBlocks().isEmpty()) {
				LOGGER.warn("No blocks found, cannot replace block with proxy");
				return;
			}

			// Remove the last (cleared) block
			int lastIdx = repository.getBlocks().size() - 1;
			repository.getBlocks().remove(lastIdx);

			// Create a proxy block pointing to the persisted file
			MeasurementsBlock proxyBlock = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
			URI blockURI = URI.createFileURI(Path.of(blockFileAbsolutePath).toAbsolutePath().toString());
			URI fragmentURI = blockURI.appendFragment("//@repositories.0/@blocks.0");
			((InternalEObject) proxyBlock).eSetProxyURI(fragmentURI);

			repository.getBlocks().add(proxyBlock);
			LOGGER.debug("Replaced block " + lastIdx + " with proxy -> " + fragmentURI);
		});
	}

	/**
	 * Find the Measurements root object in the ResourceSet, or create one if it doesn't exist.
	 * This searches the actual ResourceSet contents (not copies) so changes are tracked.
	 */
	private Measurements findOrCreateMeasurementsInResourceSet(ResourceSet resourceSet) {
		// Search through all resources in the ResourceSet
		for (Resource resource : resourceSet.getResources()) {
			for (EObject root : resource.getContents()) {
				if (root instanceof Measurements) {
					LOGGER.debug("Found existing Measurements in resource: " + resource.getURI());
					return (Measurements) root;
				}
			}
		}

		// Create new Measurements root if not found
		LOGGER.warn("No Measurements model found in ResourceSet. Creating new one (may not trigger reactions properly).");
		Measurements newMeasurements = MeasurementsFactory.eINSTANCE.createMeasurements();

		// Try to add it to an existing resource or log warning
		if (!resourceSet.getResources().isEmpty()) {
			Resource firstResource = resourceSet.getResources().get(0);
			firstResource.getContents().add(newMeasurements);
			LOGGER.debug("Added new Measurements to resource: " + firstResource.getURI());
		}

		return newMeasurements;
	}

	/**
	 * Find or create a MeasurementsBlock to add records to.
	 */
	private MeasurementsBlock findOrCreateCurrentBlock(Measurements measurements) {
		MeasurementsRepository repository;

		if (measurements.getRepositories().isEmpty()) {
			repository = MeasurementsFactory.eINSTANCE.createMeasurementsRepository();
			repository.setUri("default");
			measurements.getRepositories().add(repository);
		} else {
			repository = measurements.getRepositories().get(0);
		}

		MeasurementsBlock block;
		if (repository.getBlocks().isEmpty()) {
			block = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
			repository.getBlocks().add(block);
		} else {
			block = repository.getBlocks().get(repository.getBlocks().size() - 1);
		}

		return block;
	}
}
