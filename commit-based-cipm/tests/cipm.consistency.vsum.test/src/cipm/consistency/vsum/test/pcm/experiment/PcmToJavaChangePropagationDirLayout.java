package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

public class PcmToJavaChangePropagationDirLayout {

	private static final String repositoryFileName = "Repository.repository";
	private static final String imFileName = "imm.imm";
	private static final String javaFileName = "Java.javaxmi";

	private static final String copiedRootDirName = "copied";
	private static final String propagatedRootDirName = "propagated";

	private static final String copiedOldRootDirName = "old";
	private static final String copiedNewRootDirName = "new";

	private static final String propagatedModelRootDirName = "models";
	private static final String experimentResultsFileName = "experimentResults.json";

	// Root path

	private Path rootPath;

	// Paths to existing test results / resources

	private ChangeGeneratingCommitIntegrationDirLayout oldJavaToPcmPropagationDirLayout;
	private ChangeGeneratingCommitIntegrationDirLayout newJavaToPcmPropagationDirLayout;

	// Paths to copied resources

	private Path copiedRootPath;

	private Path copiedChangesRootPath;
	private Path copiedJavaChangesPath;
	private Path copiedPcmChangesPath;
	private Path copiedImChangesPath;

	private Path copiedOldRootPath;
	private Path copiedOldJavaModelResourcePath;
	private Path copiedOldPcmRepositoryResourcePath;
	private Path copiedOldImResourcePath;

	private Path copiedNewRootPath;
	private Path copiedNewJavaModelResourcePath;
	private Path copiedNewPcmRepositoryResourcePath;
	private Path copiedNewImResourcePath;

	// Paths to experiment results / resources

	private Path propagatedRootPath;

	private Path propagatedChangesPath;
	private Path propagatedJavaChangesPath;
	private Path propagatedPcmChangesPath;
	private Path propagatedImChangesPath;

	private Path propagatedModelsRootPath;
	private Path propagatedJavaModelPath;
	private Path propagatedPcmModelPath;
	private Path propagatedImModelPath;

	private Path experimentResultSavePath;

	public PcmToJavaChangePropagationDirLayout(
			ChangeGeneratingCommitIntegrationDirLayout oldJavaToPcmPropagationDirLayout,
			ChangeGeneratingCommitIntegrationDirLayout newJavaToPcmPropagationDirLayout, Path rootPath) {
		this.oldJavaToPcmPropagationDirLayout = oldJavaToPcmPropagationDirLayout;
		this.newJavaToPcmPropagationDirLayout = newJavaToPcmPropagationDirLayout;

		this.rootPath = rootPath;

		this.copiedRootPath = this.rootPath.resolve(copiedRootDirName);

		this.copiedChangesRootPath = this.copiedRootPath
				.resolve(this.newJavaToPcmPropagationDirLayout.getChangesSavePath().getFileName());
		this.copiedJavaChangesPath = this.copiedChangesRootPath
				.resolve(this.newJavaToPcmPropagationDirLayout.getJavaChangesSaveFilePath().getFileName());
		this.copiedPcmChangesPath = this.copiedChangesRootPath
				.resolve(this.newJavaToPcmPropagationDirLayout.getPcmChangesSaveFilePath().getFileName());
		this.copiedImChangesPath = this.copiedChangesRootPath
				.resolve(this.newJavaToPcmPropagationDirLayout.getImChangesSaveFilePath().getFileName());

		this.copiedOldRootPath = this.copiedRootPath.resolve(copiedOldRootDirName);
		this.copiedOldJavaModelResourcePath = this.copiedOldRootPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getCodeDirPath().getFileName().resolve(javaFileName));
		this.copiedOldPcmRepositoryResourcePath = this.copiedOldRootPath.resolve(
				this.oldJavaToPcmPropagationDirLayout.getPcmDirPath().getFileName().resolve(repositoryFileName));
		this.copiedOldImResourcePath = this.copiedOldRootPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getImDirPath().getFileName().resolve(imFileName));

		this.copiedNewRootPath = this.copiedRootPath.resolve(copiedNewRootDirName);
		this.copiedNewJavaModelResourcePath = this.copiedNewRootPath
				.resolve(this.newJavaToPcmPropagationDirLayout.getCodeDirPath().getFileName().resolve(javaFileName));
		this.copiedNewPcmRepositoryResourcePath = this.copiedNewRootPath.resolve(
				this.newJavaToPcmPropagationDirLayout.getPcmDirPath().getFileName().resolve(repositoryFileName));
		this.copiedNewImResourcePath = this.copiedNewRootPath
				.resolve(this.newJavaToPcmPropagationDirLayout.getImDirPath().getFileName().resolve(imFileName));

		this.propagatedRootPath = this.rootPath.resolve(propagatedRootDirName);

		this.propagatedChangesPath = this.propagatedRootPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getChangesSavePath().getFileName());
		this.propagatedJavaChangesPath = this.propagatedChangesPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getJavaChangesSaveFilePath().getFileName());
		this.propagatedPcmChangesPath = this.propagatedChangesPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getPcmChangesSaveFilePath().getFileName());
		this.propagatedImChangesPath = this.propagatedChangesPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getImChangesSaveFilePath().getFileName());

		this.propagatedModelsRootPath = this.propagatedRootPath.resolve(propagatedModelRootDirName);
		this.propagatedJavaModelPath = this.propagatedModelsRootPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getCodeDirPath().getFileName().resolve(javaFileName));
		this.propagatedPcmModelPath = this.propagatedModelsRootPath.resolve(
				this.oldJavaToPcmPropagationDirLayout.getPcmDirPath().getFileName().resolve(repositoryFileName));
		this.propagatedImModelPath = this.propagatedModelsRootPath
				.resolve(this.oldJavaToPcmPropagationDirLayout.getImDirPath().getFileName().resolve(imFileName));

		this.experimentResultSavePath = this.rootPath.resolve(experimentResultsFileName);
	}

	public ChangeGeneratingCommitIntegrationDirLayout getOldJavaToPcmPropagationDirLayout() {
		return oldJavaToPcmPropagationDirLayout;
	}

	public ChangeGeneratingCommitIntegrationDirLayout getNewJavaToPcmPropagationDirLayout() {
		return newJavaToPcmPropagationDirLayout;
	}

	public static String getRepositoryfilename() {
		return repositoryFileName;
	}

	public static String getCopiedrootdirname() {
		return copiedRootDirName;
	}

	public static String getPropagatedrootdirname() {
		return propagatedRootDirName;
	}

	public static String getCopiedoldrootdirname() {
		return copiedOldRootDirName;
	}

	public static String getCopiednewrootdirname() {
		return copiedNewRootDirName;
	}

	public static String getPropagatedmodelrootdirname() {
		return propagatedModelRootDirName;
	}

	public static String getExperimentresultsfilename() {
		return experimentResultsFileName;
	}

	public Path getRootPath() {
		return rootPath;
	}

	public Path getCopiedRootPath() {
		return copiedRootPath;
	}

	public Path getCopiedChangesRootPath() {
		return copiedChangesRootPath;
	}

	public Path getCopiedJavaChangesPath() {
		return copiedJavaChangesPath;
	}

	public Path getCopiedPcmChangesPath() {
		return copiedPcmChangesPath;
	}

	public Path getCopiedImChangesPath() {
		return copiedImChangesPath;
	}

	public Path getCopiedOldRootPath() {
		return copiedOldRootPath;
	}

	public Path getCopiedOldJavaModelResourcePath() {
		return copiedOldJavaModelResourcePath;
	}

	public Path getCopiedOldPcmRepositoryResourcePath() {
		return copiedOldPcmRepositoryResourcePath;
	}

	public Path getCopiedOldImResourcePath() {
		return copiedOldImResourcePath;
	}

	public Path getCopiedNewRootPath() {
		return copiedNewRootPath;
	}

	public Path getCopiedNewJavaModelResourcePath() {
		return copiedNewJavaModelResourcePath;
	}

	public Path getCopiedNewPcmRepositoryResourcePath() {
		return copiedNewPcmRepositoryResourcePath;
	}

	public Path getCopiedNewImResourcePath() {
		return copiedNewImResourcePath;
	}

	public Path getPropagatedRootPath() {
		return propagatedRootPath;
	}

	public Path getPropagatedChangesPath() {
		return propagatedChangesPath;
	}

	public Path getPropagatedJavaChangesPath() {
		return propagatedJavaChangesPath;
	}

	public Path getPropagatedPcmChangesPath() {
		return propagatedPcmChangesPath;
	}

	public Path getPropagatedImChangesPath() {
		return propagatedImChangesPath;
	}

	public Path getPropagatedModelsRootPath() {
		return propagatedModelsRootPath;
	}

	public Path getPropagatedJavaModelPath() {
		return propagatedJavaModelPath;
	}

	public Path getPropagatedPcmModelPath() {
		return propagatedPcmModelPath;
	}

	public Path getPropagatedImModelPath() {
		return propagatedImModelPath;
	}

	public Path getExperimentResultSavePath() {
		return experimentResultSavePath;
	}

	public static String getImfilename() {
		return imFileName;
	}

	public static String getJavafilename() {
		return javaFileName;
	}

	
}
