package cipm.consistency.vsum.test.java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import cipm.consistency.commitintegration.CommitIntegration;
import cipm.consistency.commitintegration.CommitIntegrationFailureMode;
import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.detection.strategy.TEAMMATESComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.lang.java.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.lang.java.JavaParserAndPropagatorUtils.Configuration;
import cipm.consistency.cpr.javapcm.teammates.TeammatesJavaPCMChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class TEAMMATESCommitIntegration extends JavaCommitIntegration {
	private CommitIntegrationFailureMode failureMode = CommitIntegrationFailureMode.ABORT;
	private Path rootPath;
	
	public TEAMMATESCommitIntegration(Path root) {
		this.rootPath = root;
	}

	public CommitIntegrationState<JavaModelFacade> getState() {
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
	protected List<ComponentDetectionStrategy> getComponentDetectionStrategies() {
		return List.of(new TEAMMATESComponentDetectionStrategy());
	}

	@Override
	protected List<ChangePropagationSpecification> getJavaToPCMSpecs() {
		return List.of(
			new TeammatesJavaPCMChangePropagationSpecification()
//			new TeammatesJava2PcmMethodBodyChangePreprocessor()
		);
	}
	
	@Override
	public void initialize(CommitIntegration<JavaModelFacade> commitIntegration)
			throws InvalidRemoteException, TransportException, IOException, GitAPIException {
		super.initialize(commitIntegration);
		JavaParserAndPropagatorUtils.setConfiguration(new Configuration(false));
	}
}
