package cipm.consistency.vsum.test.pcm;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.models.ModelFacade;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.VsumDirLayout;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.IChangeRecordingView;
import tools.vitruv.framework.views.ViewTypeFactory;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * Facade to the V-SUM.
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
@SuppressWarnings("restriction")
public class PcmVsumFacadeImpl implements PcmVsumFacade {
	private static final Logger LOGGER = Logger.getLogger(PcmVsumFacadeImpl.class.getName());

	private VsumDirLayout dirLayout;
	private List<ChangePropagationSpecification> changeSpecs;
	private InternalVirtualModel vsum;

	private List<ModelFacade> models;

	// initialized is used as a breakpoint conditional
	@SuppressWarnings("unused")
	private boolean initialized = false;

	public PcmVsumFacadeImpl() {
		dirLayout = new VsumDirLayout();
	}

	public void initialize(Path rootPath, List<ModelFacade> models, List<ChangePropagationSpecification> changeSpecs) {
		dirLayout.initialize(rootPath);
		this.changeSpecs = changeSpecs;
		loadOrCreateVsum();

		this.models = models;
		loadModels(models, false);

		initialized = true;
	}

	/*
	 * load the given resource if it is not yet loaded or if we positively want to
	 * do it
	 */
	private void loadModelResource(Resource res, boolean force) {
		if (force || vsum.getModelInstance(res.getURI()) == null) {
			this.propagateResource(res, null);
		}
	}

	private void loadModel(ModelFacade model, boolean force) {
		// multiple resources
		var resources = model.getResources();
		if (resources != null) {
			for (var resource : resources) {
				if (resource != null) {
					loadModelResource(resource, force);
				}
			}
		}

		// single resource
		var resource = model.getResource();
		if (resource != null) {
			loadModelResource(resource, force);
		}
	}

	@Override
	public void loadModels(List<ModelFacade> models, boolean force) {
		for (var model : models) {
			loadModel(model, force);
		}
	}

	@Override
	public void forceReload() {
		loadModels(this.models, true);
	}

	private void loadOrCreateVsum() {
		var vsumBuilder = getVsumBuilder();

		LOGGER.info("Loading VSUM");
		vsum = vsumBuilder.buildAndInitialize();
		getChangeRecordingView(vsum);
	}

	public IChangeRecordingView getChangeRecordingView(InternalVirtualModel theVsum) {
		var viewType = ViewTypeFactory.createIdentityMappingViewType("myRecordingView");
		var viewSelector = viewType.createSelector(theVsum);

		// Selecting all elements here
		viewSelector.getSelectableElements().forEach(ele -> {
			if (ele instanceof InstrumentationModel) {
				viewSelector.setSelected(ele, true);
			}
		});
		var view = (IChangeRecordingView) viewSelector.createView().withChangeRecordingTrait();

		return view;
	}

	private VirtualModelBuilder getVsumBuilder() {
		return new VirtualModelBuilder().withStorageFolder(dirLayout.getRootDirPath())
				.withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
				.withChangePropagationSpecifications(changeSpecs);
	}

	private void checkResourceForProxies(Resource res) {
		// try to resolve all proxies before checking for unresolved ones
		EcoreUtil.resolveAll(res);

		var rootEObject = res.getContents().get(0);
		var potentialProxies = EcoreUtil.ProxyCrossReferencer.find(rootEObject);
		if (!potentialProxies.isEmpty()) {
			var proxies = potentialProxies.keySet();
			var proxyNames = proxies.stream().map(p -> p.toString()).collect(Collectors.joining(", "));

			var errorMsg = String.format("Resource contains %d proxies: %s", proxies.size(), proxyNames);
			LOGGER.error(errorMsg);
			throw new IllegalStateException(errorMsg);
		}
	}

	private boolean checkPropagationPreconditions(Resource res) {
		if (res.getContents().size() == 0) {
			LOGGER.error(String.format("Resource has no contents: %s", res.getURI()));
			return false;
		}

		if (res.getErrors().size() > 0) {
			LOGGER.error(String.format("Resource contains %d errors:", res.getErrors().size()));
			var i = 0;
			for (var error : res.getErrors()) {
				LOGGER.error(String.format("%d: %s", i, error.getMessage()));
				i++;
			}
			return false;
		}

		// warnings are only logged, they don't prevent propagation
		if (res.getWarnings().size() > 0) {
			LOGGER.debug(String.format("Resource contains %d warnings:", res.getWarnings().size()));
			var i = 0;
			for (var warning : res.getWarnings()) {
				LOGGER.debug(String.format("%d: %s", i, warning.getMessage()));
				i++;
			}
		}
		checkResourceForProxies(res);
		return true;
	}

	/**
	 * Propagate a resource into the underlying vsum
	 * 
	 * @param resource           The propagated resource
	 * @param changesToPropagate All changes that should be propagated to the
	 *                           underlying model
	 * @return The propagated changes
	 */
	@Override
	public <T extends Notifier> Propagation propagateResource(Resource resource, List<T> changesToPropagate) {
		return propagateResource(resource, null, null, changesToPropagate);
	}

	/**
	 * Propagate a resource into the underlying vsum
	 * 
	 * @param resource           The propagated resource
	 * @param targetUri          The uri where vitruv persists the propagated
	 *                           resource
	 * @param changesToPropagate All changes that should be propagated to the
	 *                           underlying model
	 * @return The propagated changes
	 */
	@Override
	public <T extends Notifier> Propagation propagateResource(Resource resource, URI targetUri, List<T> changesToPropagate) {
		return propagateResource(resource, targetUri, null, changesToPropagate);
	}

	/**
	 * Propagate a resource into the underlying vsum
	 * 
	 * @param resource           The propagated resource
	 * @param targetUri          The uri where vitruv persists the propagated
	 *                           resource
	 * @param vsum               Optional, may be used to override the vsum to which
	 *                           the change is propagated
	 * @param changesToPropagate All changes that should be propagated to the
	 *                           underlying model
	 * @return The propagated changes
	 */
	private <T extends Notifier>  Propagation propagateResource(Resource resource, URI targetUri, InternalVirtualModel vsum,
			List<T> changesToPropagate) {
		if (vsum == null) {
			vsum = this.vsum;
		}

		var view = getChangeRecordingView(vsum);

		if (changesToPropagate != null) {
			for (var change : changesToPropagate) {
				view.getChangeRecorder().addToRecording(change);
			}
		}

		if (targetUri == null) {
			targetUri = resource.getURI();
		}

		final URI actualtargetUri = targetUri;

		// try to resolve all proxies in the resource
		EcoreUtil.resolveAll(resource);

		if (!checkPropagationPreconditions(resource)) {
			LOGGER.error(
					String.format("Not propagating resource because of missing preconditions: %s", resource.getURI()));
			return null;
		}

		LOGGER.trace(String.format("Propagating resource: %s", resource.getURI().toString()));

		if (resource.getContents().size() == 0) {
			LOGGER.debug(String.format("Not propagating empty resource: %s", resource.getURI()));
			return null;
		}

		var roots = view.getRootObjects();
		if (!roots.isEmpty()) {
			var first = roots.iterator().next();
			first.eResource().getContents().clear();
		}
		new ArrayList<>(resource.getContents()).forEach(ele -> view.registerRoot(ele, actualtargetUri));

		List<PropagatedChange> changeList = List.of();
		IllegalStateException exception = null;

		try {
			changeList = view.commitChangesAndUpdate();
		} catch (IllegalStateException e) {
			LOGGER.error(e.getMessage());
			exception = e;
		}

		var propagation = new Propagation(changeList);
		propagation.setException(exception);

		logPropagatedChanges(resource, propagation);

		return propagation;
	}

	private void logPropagatedChanges(Resource res, Propagation changes) {
		if (changes.getOriginalChangeCount() > 0 || changes.getConsequentialChangeCount() > 0) {
			LOGGER.info(String.format("Propagated changes in model %s: ORIGINAL: %d  CONSEQUENTIAL: %d",
					res.getURI().lastSegment(), changes.getOriginalChangeCount(),
					changes.getConsequentialChangeCount()));
		}
	}

	@Override
	public InternalVirtualModel getVsum() {
		return vsum;
	}

	@Override
	public VsumDirLayout getDirLayout() {
		return dirLayout;
	}

	@Override
	public EditableCorrespondenceModelView<Correspondence> getCorrespondenceView() {
		if (vsum != null) {
			return vsum.getCorrespondenceModel();
		}
		return null;
	}

}
