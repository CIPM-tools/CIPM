package cipm.consistency.commitintegration.lang.java;

import java.nio.file.Path;

import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.BuildFileBasedComponentDetectionStrategy;

public class JavaBuildFileBasedComDetectionStrategy extends BuildFileBasedComponentDetectionStrategy {
	private static final String MAVEN_POM_FILE_NAME = "pom.xml";
	private static final String GRADLE_BUILD_FILE_NAME = "build.gradle";
	private static final String DOCKERFILE_FILE_NAME = "Dockerfile";
	private boolean checkForDockerfiles;

	public JavaBuildFileBasedComDetectionStrategy() {
		this(true);
	}

	public JavaBuildFileBasedComDetectionStrategy(boolean checkForDockerfiles) {
		this.checkForDockerfiles = checkForDockerfiles;
	}

	@Override
	protected ComponentState checkDirectoryForComponent(Path parent) {
		boolean buildFileExistence = checkSiblingExistence(parent, MAVEN_POM_FILE_NAME)
				|| checkSiblingExistence(parent, GRADLE_BUILD_FILE_NAME);
		boolean dockerFileExistence = checkSiblingExistence(parent, DOCKERFILE_FILE_NAME);
		if (buildFileExistence) {
			if (dockerFileExistence && this.checkForDockerfiles) {
				return ComponentState.MICROSERVICE_COMPONENT;
			} else if (!this.checkForDockerfiles) {
				return ComponentState.REGULAR_COMPONENT;
			} else {
				return ComponentState.COMPONENT_CANDIDATE;
			}
		}
		return null;
	}
}
