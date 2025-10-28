package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

public class PcmToJavaChangePropagationDirLayout {

	private Path changesRootPath;

	private Path oldJavaModelResourcePath;
	private Path newJavaModelResourcePath;

	private Path oldPcmRepositoryResourcePath;
	private Path newPcmRepositoryResourcePath;

	private Path oldImResourcePath;
	private Path newImResourcePath;

	private Path javaChangesPath;
	private Path pcmChangesPath;

	private Path javaToPCMPropagationEvalPath;
	private Path pcmToJavaPropagationEvalPath;

	private Path javaToPcmToImPropagationEvalPath;
	private Path pcmToImPropagationEvalPath;

	public PcmToJavaChangePropagationDirLayout(Path changesRootPath) {
		this.changesRootPath = changesRootPath;
		// TODO Implement
	}

	public Path getChangesRootPath() {
		return changesRootPath;
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

}
