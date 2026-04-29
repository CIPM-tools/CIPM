package cipm.consistency.commitintegration.lang.detection.strategy;

import cipm.consistency.commitintegration.lang.detection.ComponentCandidates;
import java.nio.file.Path;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Interface for strategies which detect components in the source code.
 * 
 * @author Martin Armbruster
 */
public interface ComponentDetectionStrategy {
    /**
     * Detects the component for a Java model in a Resource.
     * 
     * @param res
     *            the Resource containing the Java model.
     * @param file
     *            path to the Java file corresponding to the Java model in the Resource.
     * @param projectRoot
     *            path to the repository which contains the complete project and source code.
     * @param candidate
     *            the storage of the module candidates.
     */
    public void detectComponent(Resource res, Path projectRoot, ComponentCandidates candidate);
}
