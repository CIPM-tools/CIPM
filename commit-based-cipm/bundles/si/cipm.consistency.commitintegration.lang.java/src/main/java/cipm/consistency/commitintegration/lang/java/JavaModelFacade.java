package cipm.consistency.commitintegration.lang.java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;

import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.models.code.CodeModelDirLayout;
import cipm.consistency.models.code.CodeModelFacade;

public class JavaModelFacade implements CodeModelFacade {
	// TODO: Check this class and Lua's part. Several duplicated parts can be extracted.
    private static final Logger LOGGER = Logger.getLogger(JavaModelFacade.class.getName());
    private JavaFileSystemLayout dirLayout = new JavaFileSystemLayout();
    private ComponentDetector componentDetector = new ComponentDetectorImpl();

    private Resource currentResource;

    @Override
    public void initialize(Path dirPath) {
        this.dirLayout.initialize(dirPath);
        if (existsOnDisk()) {
            loadParsedFile();
        }
    }

    public void setComponentDetectionStrategies(List<ComponentDetectionStrategy> strategies) {
        for (var strat : strategies) {
            this.componentDetector.addComponentDetectionStrategy(strat);
        }
    }

    @Override
    public Resource parseSourceCodeDir(Path sourceCodeDir) {
        LOGGER.debug("Propagating the current worktree");
        
        var javaResource = JavaParserAndPropagatorUtils.parseJavaCodeIntoOneModel(
        		sourceCodeDir, this.dirLayout.getParsedCodePath(), this.dirLayout.getModuleConfiguration(), this.componentDetector);

        // TODO: Add option to configure storing the Java model.
         try {
			javaResource.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (!validateResource(javaResource)) {
            LOGGER.error("Code model is invalid!");
            return null;
        }
        
        this.currentResource = javaResource;
        return javaResource;
    }
    
    private static boolean validateResource(Resource resource) {
    	var resourceValid = true;
    	for (var rootElement : resource.getContents()) {
    		resourceValid &= validateEObject(rootElement);
    	}
    	return resourceValid;
    }

    /**
     * Validates a given EObject and all of its children.
     */
    private static boolean validateEObject(EObject rootEObject) {
        var result = Diagnostician.INSTANCE.validate(rootEObject);
        var contentsValid = isValidDiagnostic(result);
        for (var childResult : result.getChildren()) {
            contentsValid &= isValidDiagnostic(childResult);
        }
        if (!contentsValid) {
        	LOGGER.warn(result.getMessage());
            for (var diag : result.getChildren()) {
                LOGGER.warn(diag.getMessage());
            }
        }
        return contentsValid;
    }
    
    private static boolean isValidDiagnostic(Diagnostic diagnostic) {
    	return diagnostic.getSeverity() != Diagnostic.CANCEL && diagnostic.getSeverity() != Diagnostic.ERROR;
    }

    public boolean existsOnDisk() {
        return dirLayout.getParsedCodePath()
            .toFile()
            .exists();
    }

    private void loadParsedFile() {
        var resourceSet = new ResourceSetImpl();
        currentResource = resourceSet.getResource(dirLayout.getParsedCodeURI(), true);
    }

    @Override
    public CodeModelDirLayout getDirLayout() {
        return dirLayout;
    }

    @Override
    public List<Resource> getResources() {
        return null;
    }

    @Override
    public Resource getResource() {
        return currentResource;
    }

    @Override
    public Path createNamedCopyOfParsedModel(String name) throws IOException {
        var path = getDirLayout().getParsedCodePath();
        var copyPath = path.resolveSibling("parsed-" + name + ".code.javaxmi");
        FileUtils.copyFile(path.toFile(), copyPath.toFile());
        return copyPath;
    }

    @Override
    public void reload() {
    }
}
