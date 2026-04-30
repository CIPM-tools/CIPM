package cipm.consistency.models;

import java.nio.file.Path;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;

public interface ModelFacade {

    public void initialize(Path rootPath);

    public ModelDirLayout getDirLayout();

    /**
     * For single resource models
     * 
     * @return the model resource
     */
    public Resource getResource();

    /**
     * Returns all resources (example: PCM)
     * 
     * @return the model resource
     */
    public List<Resource> getResources();
    
    /**
     * Reload models from disk
     */
    public void reload();

//    public List<Resource> createModelResources();

//    public T getModel();

//    public List<Resource> loadOrCreateModelResources();

//    public void saveToDisk();

//    public boolean existsOnDisk();
}
