package cipm.consistency.commitintegration.lang.java;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

import cipm.consistency.models.ModelDirLayoutImpl;
import cipm.consistency.models.code.CodeModelDirLayout;

/**
 * This class represents the layout on the file system related to the Java models.
 * 
 * @author Martin Armbruster
 */
public class JavaFileSystemLayout extends ModelDirLayoutImpl implements CodeModelDirLayout {
    private static final String javaModelFileName = "Java.javaxmi";

    private static final String moduleConfigurationFileName = "module-configuration.properties";

    private static final String externalCallTargetPairsFileName = "external-call-target-pairs.json";

    private Path javaModelFile;
    
    private URI javaModelFileURI;

    private Path moduleConfiguration;

    private Path externalCallTargetPairsFile;

    @Override
    public void initialize(final Path parent) {
    	super.initialize(parent);
	    this.javaModelFile = parent.resolve(JavaFileSystemLayout.javaModelFileName);
	    this.javaModelFileURI = URI.createFileURI(this.javaModelFile.toAbsolutePath().toString());
	    this.moduleConfiguration = parent.resolve(JavaFileSystemLayout.moduleConfigurationFileName);
	    this.externalCallTargetPairsFile = parent.resolve(JavaFileSystemLayout.externalCallTargetPairsFileName);
    }

    public Path getModuleConfiguration() {
    	return this.moduleConfiguration;
    }

    public Path getExternalCallTargetPairsFile() {
    	return this.externalCallTargetPairsFile;
    }

	@Override
	public Path getParsedCodePath() {
		return this.javaModelFile;
	}

	@Override
	public URI getParsedCodeURI() {
		return this.javaModelFileURI;
	}
}
