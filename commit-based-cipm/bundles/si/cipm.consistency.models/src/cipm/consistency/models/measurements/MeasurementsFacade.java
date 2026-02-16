package cipm.consistency.models.measurements;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.MeasurementsRepository;
import cipm.consistency.models.ModelFacade;

public class MeasurementsFacade implements ModelFacade {

    private Measurements measurements;
    private MeasurementsDirLayout dirLayout;

    public MeasurementsFacade() {
        dirLayout = new MeasurementsDirLayout();
    }

    @Override
    public void initialize(Path rootPath) {
        dirLayout.initialize(rootPath);
        loadOrCreateModelResources();
    }

    @Override
    public void reload() {
        loadOrCreateModelResources();
    }

    public void saveToDisk() {
        FileBackedModelUtil.synchronize(measurements, dirLayout.getMeasurementsFilePath()
            .toFile(), Measurements.class);
        try {
            this.getModel()
                .eResource()
                .save(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createModel() {
        measurements = MeasurementsFactory.eINSTANCE.createMeasurements();

        // Initialize with a default repository and block so records can be added immediately
        MeasurementsRepository repository = MeasurementsFactory.eINSTANCE.createMeasurementsRepository();
        repository.setUri("default");
        measurements.getRepositories().add(repository);

        MeasurementsBlock block = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
        repository.getBlocks().add(block);

        saveToDisk();
    }

    private void loadModel() {
        measurements = ModelUtil.readFromFile(dirLayout.getMeasurementsFilePath()
            .toFile(), Measurements.class);
    }

    public void loadOrCreateModelResources() {
        if (!fileExists()) {
            createModel();
        } else {
            loadModel();
        }
    }

    private boolean fileExists() {
        return dirLayout.getMeasurementsFilePath()
            .toFile()
            .exists();
    }

    @Override
    public MeasurementsDirLayout getDirLayout() {
        return dirLayout;
    }

    public Measurements getModel() {
        return measurements;
    }

    @Override
    public List<Resource> getResources() {
        return null;
    }

    @Override
    public Resource getResource() {
        if (measurements != null) {
            return measurements.eResource();
        }
        return null;
    }
}
