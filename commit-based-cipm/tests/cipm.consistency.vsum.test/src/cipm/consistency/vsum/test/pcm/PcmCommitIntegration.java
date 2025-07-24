package cipm.consistency.vsum.test.pcm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.google.common.base.Supplier;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.CommitIntegrationPCMJavaChangePropagationSpecification;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.models.pcm.PcmFacade;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public abstract class PcmCommitIntegration extends PcmCommitIntegrationController implements IPcmCommitIntegration {
	private static final boolean GIT_DETECT_RENAMES = true;
	private GitRepositoryWrapper repoWrapper;

	@Override
	public Supplier<PcmFacade> getPcmModelFacadeSupplier() {
		return () -> {
			var model = new PcmFacade();
			model.initialize(getRootPath());
			return model;
		};
	}

	@Override
	public Supplier<CodeModelFacade> getCodeModelFacadeSupplier() {
		return () -> {
			var model = new JavaModelFacade();
			model.initialize(getJavaModelResourcePath());
			return model;
		};
	}

	@Override
	public List<ChangePropagationSpecification> getChangeSpecs() {
		List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
		changeSpecs.add(new PcmInitChangePropagationSpecification());
		changeSpecs.add(new ImInitChangePropagationSpecification());
		changeSpecs.add(new PcmImUpdateChangePropagationSpecification());
		changeSpecs.add(new CommitIntegrationPCMJavaChangePropagationSpecification());
		changeSpecs.addAll(getPCMToJavaSpecs());
		return changeSpecs;
	}

	protected abstract Path getJavaModelResourcePath();

	protected abstract List<ChangePropagationSpecification> getPCMToJavaSpecs();

	/*
	 * This returns an uninitialized git repo that is initialized by subclasses
	 */
	@Override
	public GitRepositoryWrapper getGitRepositoryWrapper()
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		if (repoWrapper == null) {
			// TODO Find a way to exclude everything other than PCM files
			// currently all files are checked for differences
			repoWrapper = new GitRepositoryWrapper(null, GIT_DETECT_RENAMES);
		}
		return repoWrapper;
	}
}
