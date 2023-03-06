package cipm.consistency.vsum.test.appspace.tests;

import java.io.IOException;
import java.nio.file.Path;

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
public class CaseStudy2Test extends AppSpaceCITestController {
    private static final String CASESTUDY_REPO_DIR = "/home/burgey/documents/studium/ma/src/gitlab.sickcn.net/tburglu/color-sorter";
    private static final String COMMIT_TAG_MASTER = "916fc523f0780eeed6c7b17fdda098e47a0f2160";
    
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var path = Path.of(CASESTUDY_REPO_DIR);
        return super.getGitRepositoryWrapper().withLocalDirectory(path).initialize();
    }

    @Test
    public void testIntegratingVersion() {
        propagateAndEvaluate(null, COMMIT_TAG_MASTER);
    }
}
