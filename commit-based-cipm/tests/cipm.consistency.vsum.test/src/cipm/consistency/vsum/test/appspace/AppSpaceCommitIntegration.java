package cipm.consistency.vsum.test.appspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;

import com.google.common.base.Supplier;

import cipm.consistency.commitintegration.CommitIntegration;
import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.commitintegration.CommitIntegrationFailureMode;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.lua.LuaModelFacade;
import cipm.consistency.commitintegration.lang.lua.appspace.detection.AppSpaceComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.lua.appspace.detection.StdLibCrownComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.lua.changeresolution.HierarchicalStateBasedChangeResolutionStrategy;
import mir.reactions.imInit.ImInitChangePropagationSpecification;
import mir.reactions.luaPcmUpdate.LuaPcmUpdateChangePropagationSpecification;
import mir.reactions.pcmImUpdate.PcmImUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

public abstract class AppSpaceCommitIntegration extends CommitIntegrationController<LuaModelFacade>
        implements CommitIntegration<LuaModelFacade> {

    private static final String LUA_FILE_EXTENSION = "lua";
    private static final boolean GIT_DETECT_RENAMES = true;

    @Override
    public Supplier<LuaModelFacade> getCodeModelFacadeSupplier() {
        return () -> {
            var model = new LuaModelFacade();
            model.setComponentDetectionStrategies(getComponentDetectionStrategies());
            return model;
        };
    }

    protected List<ComponentDetectionStrategy> getComponentDetectionStrategies() {
        return List.of(new AppSpaceComponentDetectionStrategy(), new StdLibCrownComponentDetectionStrategy());
    }

    @Override
    public List<ChangePropagationSpecification> getChangeSpecs() {
        List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
        changeSpecs.add(new PcmInitChangePropagationSpecification());
        changeSpecs.add(new LuaPcmUpdateChangePropagationSpecification());
        changeSpecs.add(new ImInitChangePropagationSpecification());
        changeSpecs.add(new PcmImUpdateChangePropagationSpecification());
        return changeSpecs;
    }

    /*
     * This returns an uninitialized git repo that is initialized by subclasses
     */
    @Override
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return new GitRepositoryWrapper(LUA_FILE_EXTENSION, GIT_DETECT_RENAMES);
    }

    @Override
    public StateBasedChangeResolutionStrategy getStateBasedChangeResolutionStrategy() {
        return new HierarchicalStateBasedChangeResolutionStrategy(false);
    }
}
