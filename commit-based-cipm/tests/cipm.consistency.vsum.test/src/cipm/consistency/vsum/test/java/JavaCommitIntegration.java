package cipm.consistency.vsum.test.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;

import com.google.common.base.Supplier;

import cipm.consistency.commitintegration.CommitIntegration;
import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.lang.java.JavaStateBasedChangeResolutionStrategy;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

public abstract class JavaCommitIntegration extends CommitIntegrationController<JavaModelFacade>
        implements CommitIntegration<JavaModelFacade> {

    private static final String JAVA_FILE_EXTENSION = "java";
    private static final boolean GIT_DETECT_RENAMES = true;
    private GitRepositoryWrapper repoWrapper;

    @Override
    public Supplier<JavaModelFacade> getCodeModelFacadeSupplier() {
        return () -> {
            var model = new JavaModelFacade();
            model.setComponentDetectionStrategies(getComponentDetectionStrategies());
            return model;
        };
    }

    protected abstract List<ComponentDetectionStrategy> getComponentDetectionStrategies();

    @Override
    public List<ChangePropagationSpecification> getChangeSpecs() {
        List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
        changeSpecs.add(new PcmInitChangePropagationSpecification());
        changeSpecs.addAll(getJavaToPCMSpecs());
        changeSpecs.add(new ImInitChangePropagationSpecification());
        changeSpecs.add(new PcmImUpdateChangePropagationSpecification());
        return changeSpecs;
    }
    
    protected abstract List<ChangePropagationSpecification> getJavaToPCMSpecs();

    /*
     * This returns an uninitialized git repo that is initialized by subclasses
     */
    @Override
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
    	if (repoWrapper == null) {
    		repoWrapper = new GitRepositoryWrapper(JAVA_FILE_EXTENSION, GIT_DETECT_RENAMES);
    	}
        return repoWrapper;
    }

    @Override
    public StateBasedChangeResolutionStrategy getStateBasedChangeResolutionStrategy() {
        return new JavaStateBasedChangeResolutionStrategy();
    }
}
