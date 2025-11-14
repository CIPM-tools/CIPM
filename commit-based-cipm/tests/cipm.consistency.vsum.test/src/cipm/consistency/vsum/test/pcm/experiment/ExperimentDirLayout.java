package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

public class ExperimentDirLayout {
	// Root path

	private final Path rootPath;
	private final Path initialJavaCodeModelPath;
	private final Path initialRepositoryModelPath;
	private final Path initialIMPath;
	private final Path initialCorrespondencePath;

	// Paths to existing test results / resources

	private final JavaToPcmPropagationDirLayout javaToPcmPropagationDirLayout;

	// Paths to copied resources

	private final JavaToPcmPropagationDirLayout copiedOldJavaToPcmPropagationDirLayout;
	private final JavaToPcmPropagationDirLayout copiedNewJavaToPcmPropagationDirLayout;
	private final JavaToPcmPropagationDirLayout propagatedDirLayout;

	private final Path experimentResultSavePath;

	public ExperimentDirLayout(Path rootPath, JavaToPcmPropagationDirLayout javaToPcmPropagationDirLayout,
			Path initialJavaCodeModelPath, Path initialRepositoryModelPath, Path initialIMPath,
			Path initialCorrespondencePath) {
		this.rootPath = rootPath;
		this.initialJavaCodeModelPath = initialJavaCodeModelPath;
		this.initialRepositoryModelPath = initialRepositoryModelPath;
		this.initialIMPath = initialIMPath;
		this.initialCorrespondencePath = initialCorrespondencePath;

		var copiedRootPath = this.rootPath.resolve(ExperimentDirLayoutConstants.getCopiedrootdirname());
		var copiedOldRootPath = copiedRootPath.resolve(ExperimentDirLayoutConstants.getCopiedoldrootdirname());
		var copiedNewRootPath = copiedRootPath.resolve(ExperimentDirLayoutConstants.getCopiednewrootdirname());
		var propPath = this.rootPath.resolve(ExperimentDirLayoutConstants.getPropagatedrootdirname());

		this.javaToPcmPropagationDirLayout = javaToPcmPropagationDirLayout;

		this.copiedOldJavaToPcmPropagationDirLayout = new JavaToPcmPropagationDirLayout(copiedOldRootPath,
				this.initialJavaCodeModelPath, this.initialRepositoryModelPath, this.initialIMPath,
				this.initialCorrespondencePath);

		this.copiedNewJavaToPcmPropagationDirLayout = new JavaToPcmPropagationDirLayout(copiedNewRootPath);

		this.propagatedDirLayout = new JavaToPcmPropagationDirLayout(propPath);

		this.experimentResultSavePath = this.rootPath
				.resolve(ExperimentDirLayoutConstants.getExperimentresultsfilename());
	}

	public JavaToPcmPropagationDirLayout getJavaToPcmPropagationDirLayout() {
		return javaToPcmPropagationDirLayout;
	}

	public Path getRootPath() {
		return rootPath;
	}

	public JavaToPcmPropagationDirLayout getCopiedOldJavaToPcmPropagationDirLayout() {
		return copiedOldJavaToPcmPropagationDirLayout;
	}

	public JavaToPcmPropagationDirLayout getCopiedNewJavaToPcmPropagationDirLayout() {
		return copiedNewJavaToPcmPropagationDirLayout;
	}

	public JavaToPcmPropagationDirLayout getPropagatedDirLayout() {
		return propagatedDirLayout;
	}

	public Path getExperimentResultSavePath() {
		return experimentResultSavePath;
	}

	public Path getInitialJavaCodeModelPath() {
		return initialJavaCodeModelPath;
	}

	public Path getInitialRepositoryModelPath() {
		return initialRepositoryModelPath;
	}

	public Path getInitialCorrespondencePath() {
		return initialCorrespondencePath;
	}

	public Path getInitialIMPath() {
		return initialIMPath;
	}

}
