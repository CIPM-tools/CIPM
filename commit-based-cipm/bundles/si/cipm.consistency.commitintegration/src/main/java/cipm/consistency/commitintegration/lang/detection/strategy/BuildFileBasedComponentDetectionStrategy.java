package cipm.consistency.commitintegration.lang.detection.strategy;

import cipm.consistency.commitintegration.lang.detection.ComponentCandidates;
import cipm.consistency.commitintegration.lang.detection.ComponentState;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.emf.ecore.resource.Resource;

public abstract class BuildFileBasedComponentDetectionStrategy implements ComponentDetectionStrategy {

    protected abstract ComponentState checkDirectoryForComponent(Path parent);

    @Override
    public void detectComponent(Resource res, Path container, ComponentCandidates candidate) {
        if (!res.getURI()
            .isFile()) {
            return;
        }
        var sourceFile = res.getURI()
            .toFileString();
        Path sourceFilePath = Path.of(sourceFile)
            .toAbsolutePath();
        Path parent = sourceFilePath.getParent();
        // Beginning with the source file, the file system hierarchy is searched upwards
        // until the container directory is reached.
        while (container.compareTo(parent) != 0) {
            var moduleState = checkDirectoryForComponent(parent);
            if (moduleState != null) {
                var modName = parent.getParent()
                    .getFileName()
                    .toString();
                candidate.addModuleClassifier(moduleState, modName, res);
            }
            parent = parent.getParent();
        }
    }

    protected boolean checkSiblingExistence(Path file, String siblingName) {
        Path sibling = file.resolveSibling(siblingName);
        return Files.exists(sibling);
    }

}