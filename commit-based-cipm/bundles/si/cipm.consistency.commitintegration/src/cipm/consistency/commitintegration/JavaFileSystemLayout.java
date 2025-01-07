package cipm.consistency.commitintegration;

import java.nio.file.Path;

/**
 * This class represents the layout on the file system related to the Java models.
 * 
 * @author Martin Armbruster
 */
public class JavaFileSystemLayout {
    private static final String localJavaRepoDirName = "local-repo-clone";

    private static final String javaModelFileContainerDirName = "vsum-variant";

    private static final String javaModelFileName = "Java.javaxmi";

    private static final String instrumentationDirName = "instrumented";

    private static final String moduleConfigurationFileName = "module-configuration.properties";

    private static final String externalCallTargetPairsFileName = "external-call-target-pairs.json";

    private Path localJavaRepo;

    private Path javaModelFileContainer;

    private Path javaModelFile;

    private Path instrumentationCopy;

    private Path moduleConfiguration;

    private Path externalCallTargetPairsFile;

    public JavaFileSystemLayout(final Path parent) {
	    this.localJavaRepo = parent.resolve("..").resolve("..").resolve(JavaFileSystemLayout.localJavaRepoDirName);
	    this.javaModelFileContainer = parent.resolve(JavaFileSystemLayout.javaModelFileContainerDirName);
	    this.javaModelFile = parent.resolve(JavaFileSystemLayout.javaModelFileName);
	    this.instrumentationCopy = parent.resolve("..").resolve("..").resolve(JavaFileSystemLayout.instrumentationDirName);
	    this.moduleConfiguration = parent.resolve(JavaFileSystemLayout.moduleConfigurationFileName);
	    this.externalCallTargetPairsFile = parent.resolve(JavaFileSystemLayout.externalCallTargetPairsFileName);
    }

    public Path getLocalJavaRepo() {
    	return this.localJavaRepo;
    }
	
    public Path getJavaModelFileContainer() {
	    return this.javaModelFileContainer;
    }

    public Path getJavaModelFile() {
    	return this.javaModelFile;
    }

    public Path getInstrumentationCopy() {
    	return this.instrumentationCopy;
    }

    public Path getModuleConfiguration() {
    	return this.moduleConfiguration;
    }

    public Path getExternalCallTargetPairsFile() {
    	return this.externalCallTargetPairsFile;
    }
}
