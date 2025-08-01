package cipm.consistency.vsum.test.pcm.unused;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Supplier;

import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.cpr.pcmjava.CommitIntegrationPCMJavaChangePropagationSpecification;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.models.pcm.PcmFacade;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public abstract class PcmCommitIntegration extends PcmCommitIntegrationController implements IPcmCommitIntegration {
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
}
