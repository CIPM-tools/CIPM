package cipm.consistency.vsum.test.appspace.tests;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.commitintegration.CommitIntegrationFailureMode;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.cpr.luapcm.Config;
import cipm.consistency.cpr.luapcm.Config.ReconstructionType;
import cipm.consistency.vsum.test.appspace.AppSpaceCITestController;

/**
 * A test class for the AppSpace Case Study
 * 
 * @author Lukas Burgey
 */
public class CaseStudy2Test extends AppSpaceCITestController {
    private static final String CASESTUDY_REPO_DIR = "/home/burgey/documents/studium/ma/src/gitlab.sickcn.net/tburglu/color-sorter";
    private static final String COMMIT_TAG_MASTER = "916fc52";
    private static final String[] VERSIONS_MASTER = { null, "f713180", "1466c57", "f57823c", "40bf36e", "473c9d9",
            "995ecc0", "956aeb9", "0da3169", "00b44f8", "88d005a", "92cb3bc", COMMIT_TAG_MASTER };

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var path = Path.of(CASESTUDY_REPO_DIR);
        return super.getGitRepositoryWrapper().withLocalDirectory(path)
            .initialize();
    }

    @Test
    public void testPropagationIndividually(TestInfo testInfo) {
        propagateAndEvaluateIndividually(testInfo, VERSIONS_MASTER);
    }

    @Test
    public void testPropagation() {
        setFailureMode(CommitIntegrationFailureMode.CLEAN);
        propagateAndEvaluate(VERSIONS_MASTER);
    }

    /**
     * This test propagates all versions to the scripts directory of the current master branch
     */
    @Test
    public void testCompleteEvaluation() {
        setFailureMode(CommitIntegrationFailureMode.ABORT);
        Config.setInternalSeffCallReconstructionType(ReconstructionType.ExternalCallAction);
        doCompleteEvaluation(VERSIONS_MASTER);
    }

    @Test
    public void testCompleteExperimenting() {
        setFailureMode(CommitIntegrationFailureMode.ABORT);
        Config.setInternalSeffCallReconstructionType(ReconstructionType.InternalAction);
        doCompleteEvaluation(VERSIONS_MASTER);
    }
}