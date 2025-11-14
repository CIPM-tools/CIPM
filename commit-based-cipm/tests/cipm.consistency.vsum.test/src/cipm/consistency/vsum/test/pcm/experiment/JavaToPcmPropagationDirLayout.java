package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

import cipm.consistency.commitintegration.CommitIntegrationDirLayout;

public class JavaToPcmPropagationDirLayout extends CommitIntegrationDirLayout {
	private Path rootPath;
	private Path changesSavePath;
	private Path javaChangesSaveFilePath;
	private Path pcmChangesSaveFilePath;
	private Path imChangesSaveFilePath;

	private Path initialJavaModelPath;
	private Path initialRepositoryPath;
	private Path initialIMPath;
	private Path initialCorrespondencesPath;

	public JavaToPcmPropagationDirLayout(Path rootDirPath) {
		this(rootDirPath, null, null, null, null);
	}

	public JavaToPcmPropagationDirLayout(Path rootDirPath, Path initialJavaModelPath, Path initialRepositoryPath,
			Path initialIMPath, Path initialCorrespondencesPath) {
		super();

		this.initialJavaModelPath = initialJavaModelPath;
		this.initialRepositoryPath = initialRepositoryPath;
		this.initialIMPath = initialIMPath;
		this.initialCorrespondencesPath = initialCorrespondencesPath;

		this.initialize(rootDirPath);
	}

	@Override
	public void initialize(Path rootDirPath) {
		super.initialize(rootDirPath);

		this.rootPath = rootDirPath;
		this.changesSavePath = this.rootPath.resolve(ExperimentDirLayoutConstants.getChangessavedirname());
		this.javaChangesSaveFilePath = this.changesSavePath
				.resolve(ExperimentDirLayoutConstants.getJavachangessavefilename());
		this.pcmChangesSaveFilePath = this.changesSavePath
				.resolve(ExperimentDirLayoutConstants.getPcmchangessavefilename());
		this.imChangesSaveFilePath = this.changesSavePath
				.resolve(ExperimentDirLayoutConstants.getImchangessavefilename());
	}

	public Path getRootPath() {
		return rootPath;
	}

	public Path getChangesSavePath() {
		return changesSavePath;
	}

	public Path getJavaChangesSaveFilePath() {
		return javaChangesSaveFilePath;
	}

	public Path getPcmChangesSaveFilePath() {
		return pcmChangesSaveFilePath;
	}

	public Path getImChangesSaveFilePath() {
		return imChangesSaveFilePath;
	}

	public Path getInitialJavaModelPath() {
		return initialJavaModelPath;
	}

	public Path getInitialRepositoryPath() {
		return initialRepositoryPath;
	}

	public Path getInitialIMPath() {
		return initialIMPath;
	}

	public Path getInitialCorrespondencesPath() {
		return initialCorrespondencesPath;
	}

	public Path getPropagatedJavaModelSavePath() {
		return this.getCodeDirPath().resolve(ExperimentDirLayoutConstants.getJavafilename());
	}

	public Path getPropagatedIMSavePath() {
		return this.getImDirPath().resolve(ExperimentDirLayoutConstants.getImfilename());
	}

	public Path getPropagatedPcmRepositoryPath() {
		return this.getPcmDirPath().resolve(ExperimentDirLayoutConstants.getPcmrepositoryfilename());
	}

	public Path getPropagatedPcmSystemPath() {
		return this.getPcmDirPath().resolve(ExperimentDirLayoutConstants.getPcmsystemfilename());
	}

	public Path getPropagatedPcmAllocationPath() {
		return this.getPcmDirPath().resolve(ExperimentDirLayoutConstants.getPcmallocationfilename());
	}

	public Path getPropagatedPcmResourceEnvironmentPath() {
		return this.getPcmDirPath().resolve(ExperimentDirLayoutConstants.getPcmresourceenvironmentfilename());
	}

	public Path getPropagatedPcmUsagePath() {
		return this.getPcmDirPath().resolve(ExperimentDirLayoutConstants.getPcmusagemodelfilename());
	}

	public Path getPropagatedCorrespondencesPath() {
		return this.getVsumDirPath().resolve(ExperimentDirLayoutConstants.getVsumdirname())
				.resolve(ExperimentDirLayoutConstants.getVsumcorrespondencemodelname());
	}
}
