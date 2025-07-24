package cipm.consistency.vsum.test.pcm;

import java.nio.file.Path;
import java.util.List;

import cipm.consistency.commitintegration.CommitIntegrationFailureMode;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmTEAMMATESCommitIntegration extends PcmCommitIntegration {
	private CommitIntegrationFailureMode failureMode = CommitIntegrationFailureMode.ABORT;
	private Path rootPath;
	private Path javaModelResourcePath;

	public PcmTEAMMATESCommitIntegration(Path root, Path javaModelResourcePath) {
		this.rootPath = root;
		this.javaModelResourcePath = javaModelResourcePath;
	}

	public PcmCommitIntegrationState getState() {
		return this.state;
	}

	@Override
	public Path getRootPath() {
		return this.rootPath;
	}

	@Override
	public CommitIntegrationFailureMode getFailureMode() {
		return this.failureMode;
	}

	@Override
	public void setFailureMode(CommitIntegrationFailureMode failureMode) {
		this.failureMode = failureMode;
	}

	@Override
	protected Path getJavaModelResourcePath() {
		// TODO Maybe use dir layout for this as well
		return this.javaModelResourcePath;
	}
	
	@Override
	protected List<ChangePropagationSpecification> getPCMToJavaSpecs() {
		// TODO Add Teammates-specific CPRs here
		return List.of();
	}
}
