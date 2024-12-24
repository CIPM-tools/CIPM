package cipm.consistency.vsum.test.appspace.tests;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.vsum.test.appspace.AppSpaceCITestController;

/**
 * A test class for the AppSpace Case Study
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class CaseStudy1Test extends AppSpaceCITestController {
    private static final String CASESTUDY_SUBMODULE = "commit-based-cipm/bundles/si/cipm.consistency.vsum.test/ciTestRepos/caseStudy1";

    // the complete history of the removeDB branch
    private static final String[] COMPLETE_COMMIT_HISTORY_REMOVEDB = { null, "44b5f32", "bddb634", "0e88a9a", "4547cab",
            "fb7f8d2", "4bdd344", "e7f233c", "5ae1680", "0444950", "45de7ef", "963bbf1", "ae24901", "cbc9f51",
            "fce1b9f", "c771106", "3d6592f", "37c7c41", "1da687c", "95d4f37", "225be44" };

    private static final String[] COMMITS_DANGLING = { null, "4547cab", "fb7f8d2" };

    // this is the history used for the evaluation:
//    private static final String[] EVALUATION_HISTORY = { null, "e25fb6b", "7126aab", "d92b459", "e6d87e0", "542d2e9",
//            "6b7b35f", "1f2fb08"};
    
    // with two synthetic commits to evaluate some nasty CPR edge cases:
    private static final String[] EVALUATION_HISTORY = { null, "e25fb6b", "7126aab", "d92b459", "e6d87e0", "542d2e9",
            "6b7b35f", "1f2fb08", "616f65a", "de68b25"};

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return super.getGitRepositoryWrapper().withLocalSubmodule(CIPM_GIT_DIR, CASESTUDY_SUBMODULE)
            .initialize();
    }

    @Test
    public void testPropagationDangling() {
        propagateAndEvaluate(COMMITS_DANGLING);
    }

    @Test
    public void runEvaluation() {
        doCompleteEvaluation(EVALUATION_HISTORY);
    }
}
