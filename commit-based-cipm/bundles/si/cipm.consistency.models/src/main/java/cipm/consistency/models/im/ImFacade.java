package cipm.consistency.models.im;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

import tools.cipm.models.instrumentation.InstrumentationModel.InstrumentationModel;
import tools.cipm.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.models.ModelFacade;

public class ImFacade implements ModelFacade {

    private InstrumentationModel im;
    private ImDirLayout dirLayout;

    public ImFacade() {
        dirLayout = new ImDirLayout();
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
        FileBackedModelUtil.synchronize(im, dirLayout.getImFilePath()
            .toFile(), InstrumentationModel.class);
        try {
            this.getModel()
                .eResource()
                .save(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Persistently deactivates all instrumentation points in the underlying model.
     */
    public void deactivateAllActionIPs() {
        im.getPoints()
            .forEach(sip -> sip.getActionInstrumentationPoints()
                .forEach(aip -> aip.setActive(false)));
        this.saveToDisk();
    }

    private void createModel() {
        // build IMM
        im = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
        saveToDisk();
    }

    private void loadModel() {
        im = ModelUtil.readFromFile(dirLayout.getImFilePath()
            .toFile(), InstrumentationModel.class);
    }

    public void loadOrCreateModelResources() {
        if (!fileExists()) {
            createModel();
        } else {
            loadModel();
        }
    }

    private boolean fileExists() {
        return dirLayout.getImFilePath()
            .toFile()
            .exists();
    }

    @Override
    public ImDirLayout getDirLayout() {
        return dirLayout;
    }

    public InstrumentationModel getModel() {
        return im;
    }

    @Override
    public List<Resource> getResources() {
        return null;
    }

    @Override
    public Resource getResource() {
        if (im != null) {
            return im.eResource();
        }
        return null;
    }
}
