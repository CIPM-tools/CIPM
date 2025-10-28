package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

public class PcmToJavaChangePropagationDirLayout {

	private static final String repositoryFileName = "Repository.repository";
	
	// Paths to existing Teammates test results / resources

	private Path javaChangesPath;
	private Path pcmChangesPath;

	private Path oldJavaModelResourcePath;
	private Path newJavaModelResourcePath;

	private Path oldPcmRepositoryResourcePath;
	private Path newPcmRepositoryResourcePath;

	private Path oldImResourcePath;
	private Path newImResourcePath;

	private Path javaToPCMPropagationEvalPath;
	private Path pcmToJavaPropagationEvalPath;

	private Path javaToPcmToImPropagationEvalPath;
	private Path pcmToImPropagationEvalPath;

	// Paths to experiment results / resources

	private Path copiedJavaChangesPath;
	private Path copiedPcmChangesPath;

	private Path copiedOldJavaModelResourcePath;
	private Path copiedNewJavaModelResourcePath;

	private Path copiedOldPcmRepositoryResourcePath;
	private Path copiedNewPcmRepositoryResourcePath;

	private Path copiedOldImResourcePath;
	private Path copiedNewImResourcePath;

	private Path propagatedRootPath;
	private Path propagatedJavaModelPath;
	private Path propagatedPcmModelPath;
	private Path propagatedImModelPath;
	
	private Path experimentResultSavePath;

	public PcmToJavaChangePropagationDirLayout(Path changesRootPath) {
		// TODO Implement
	}

	public Path getJavaChangesPath() {
		return javaChangesPath;
	}

	public Path getPcmChangesPath() {
		return pcmChangesPath;
	}

	public Path getOldJavaModelResourcePath() {
		return oldJavaModelResourcePath;
	}

	public Path getNewJavaModelResourcePath() {
		return newJavaModelResourcePath;
	}

	public Path getOldPcmRepositoryResourcePath() {
		return oldPcmRepositoryResourcePath;
	}

	public Path getNewPcmRepositoryResourcePath() {
		return newPcmRepositoryResourcePath;
	}

	public Path getOldImResourcePath() {
		return oldImResourcePath;
	}

	public Path getNewImResourcePath() {
		return newImResourcePath;
	}

	public Path getJavaToPCMPropagationEvalPath() {
		return javaToPCMPropagationEvalPath;
	}

	public Path getPcmToJavaPropagationEvalPath() {
		return pcmToJavaPropagationEvalPath;
	}

	public Path getJavaToPcmToImPropagationEvalPath() {
		return javaToPcmToImPropagationEvalPath;
	}

	public Path getPcmToImPropagationEvalPath() {
		return pcmToImPropagationEvalPath;
	}

	public Path getExperimentResultSavePath() {
		return experimentResultSavePath;
	}

	public Path getCopiedJavaChangesPath() {
		return copiedJavaChangesPath;
	}

	public Path getCopiedPcmChangesPath() {
		return copiedPcmChangesPath;
	}

	public Path getCopiedOldJavaModelResourcePath() {
		return copiedOldJavaModelResourcePath;
	}

	public Path getCopiedNewJavaModelResourcePath() {
		return copiedNewJavaModelResourcePath;
	}

	public Path getCopiedOldPcmRepositoryResourcePath() {
		return copiedOldPcmRepositoryResourcePath;
	}

	public Path getCopiedNewPcmRepositoryResourcePath() {
		return copiedNewPcmRepositoryResourcePath;
	}

	public Path getCopiedOldImResourcePath() {
		return copiedOldImResourcePath;
	}

	public Path getCopiedNewImResourcePath() {
		return copiedNewImResourcePath;
	}

	public Path getPropagatedRootPath() {
		return propagatedRootPath;
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

	public String getRepositoryFileName() {
		return repositoryFileName;
	}
}
