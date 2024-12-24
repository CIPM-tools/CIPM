package cipm.consistency.vsum.test.appspace.tests;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.vsum.test.appspace.AppSpaceCITestController;

/**
 * Testclass for a minimal case study to test the functionality of the approach
 * 
 * @author Lukas Burgey
 */
public class MinimalCaseStudyTest extends AppSpaceCITestController {
    private static final String CASESTUDY_SUBMODULE = "commit-based-cipm/bundles/si/cipm.consistency.vsum.test/ciTestRepos/minimalCaseStudy";
    private static final String COMMIT_1 = "842ec92f3406da965a5f9e7f468eb80eeb287b04";
    private static final String COMMIT_2 = "f63d05a18a5dd36f29e7312d797f2806d935e3a3";

    // this commit was a copied code snipped from case study 1, that did cause a problem over there
    private static final String COMMIT_PROBLEM = "4cbb4e16fb348920fbf28ca192e78ec5ce5f07eb";
    private static final String COMMIT_4 = "ed82c041912168e9deeea36076184806daaa312e";


    @Override
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return super.getGitRepositoryWrapper().withLocalSubmodule(CIPM_GIT_DIR, CASESTUDY_SUBMODULE)
            .initialize();
    }

    @Test
    public void testIntegratigeCompleteHistory() throws Exception {
        // propagating the same version twice
        var propagatedChanges = propagateAndEvaluate(null, COMMIT_1, COMMIT_2, COMMIT_PROBLEM, COMMIT_4);
        Assert.assertTrue("Four sets of changes must exist", propagatedChanges.size() == 4);
    }

    @Test
    public void testIntegratingLastCommit() throws Exception {
        var propagatedChanges = propagateAndEvaluate(null, COMMIT_4);
        Assert.assertTrue("One change must exists", propagatedChanges.size() == 1);
//        var result = executePropagationAndEvaluation(null, getLatestCommitId(), 0);
//        Assert.assertTrue(result);
    }

    @Test
    public void testIntegratingSameCommitTwice() throws Exception {
        // propagating the same version twice
        var propagatedChanges = propagateAndEvaluate(null, COMMIT_2, COMMIT_2);
        var lastPropagationResult = propagatedChanges.get(propagatedChanges.size() - 1);
        Assert.assertTrue("No changes must be generated when propagating a previously propagated commit",
                lastPropagationResult.isEmpty());
    }
}
