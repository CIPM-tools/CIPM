package cipm.consistency.vsum.test.pcm.unused;

import java.nio.file.Path;
import java.util.List;

import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmTEAMMATESCommitIntegration extends PcmCommitIntegration {
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
