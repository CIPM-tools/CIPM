package cipm.consistency.vsum.test.pcm;

import java.nio.file.Path;

import cipm.consistency.commitintegration.CommitIntegrationDirLayout;

public class PcmCommitIntegrationDirLayout extends CommitIntegrationDirLayout {
	private static final String pcmChangesFileName = "pcmChanges.changes";

	private Path pcmChangesFilePath;

	@Override
	public void initialize(Path rootDirPath) {
		// TODO Auto-generated method stub
		super.initialize(rootDirPath);
		this.pcmChangesFilePath = rootDirPath.resolve(pcmChangesFileName);
	}

	public Path getPcmChangesFilePath() {
		return this.pcmChangesFilePath;
	}

	public void setPcmChangesFilePath(Path pcmChangesFilePath) {
		this.pcmChangesFilePath = pcmChangesFilePath;
	}
}
