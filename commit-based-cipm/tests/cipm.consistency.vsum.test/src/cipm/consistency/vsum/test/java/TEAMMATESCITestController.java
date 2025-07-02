package cipm.consistency.vsum.test.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.evaluator.PropagationEvaluator;
import cipm.consistency.vsum.test.evaluator.commitHistory.CommitHistoryEvaluator;
import jamopp.resource.JavaResource2Factory;

public class TEAMMATESCITestController {
	private static final String COMMIT_TAG_V_8_0_0_RC_0 = "648425746bb9434051647c8266dfab50a8f2d6a3";
	private static final String[] COMMIT_HASHES = {
		COMMIT_TAG_V_8_0_0_RC_0,
		"48b67bae03babf5a5e578aefce47f0285e8de8b4", 
		"83f518e279807dc7eb7023d008a4d1ab290fefee",
		"f33d0bcd5843678b832efd8ee2963e72a95ecfc9",
		"ce4463a8741840fd25a41b14801eab9193c7ed18"
	};
	// This version is the next one after the last commit in COMMIT_HASHES.
	private static final String COMMIT_TAG_V_8_0_0_RC_2 = "8a97db611be37ae1975715723e1913de4fd675e8";

	private static final Logger LOGGER = Logger.getLogger(TEAMMATESCITestController.class);
	private CommitIntegrationState<JavaModelFacade> state;
	private TEAMMATESCommitIntegration teammatesController;
	
	private Path localRepository = Paths.get("target", "TEAMMATES");
	private String remoteRepository = "https://github.com/TEAMMATES/teammates.git";
	private Path rootPath = Paths.get("target", "TEAMMATESCITest");
    private Path manualModelsPath = Paths.get("target", "manual");

    /**
     * 
     * @param overwrite
     *            Are existing files (models, etc.) to be deleted before initializing the commit
     *            integration state?
     * @throws GitAPIException
     * @throws IOException
     * @throws org.eclipse.jgit.api.errors.TransportException
     * @throws InvalidRemoteException
     */
    protected void setup(boolean overwrite) {
        // Create new empty state
        this.teammatesController = new TEAMMATESCommitIntegration(this.rootPath);

        // overwrite existing files?
        try {
        	this.teammatesController.initialize(this.teammatesController);
        	this.state = this.teammatesController.getState();        	
            // state.initialize(this.teammatesController, this.teammatesController.getRootPath(), overwrite);
            if (Files.exists(this.localRepository)) {
            	this.teammatesController.getGitRepositoryWrapper().withLocalDirectory(this.localRepository.resolve(".git"));
            } else {
            	this.teammatesController.getGitRepositoryWrapper().withRemoteRepositoryCopy(this.localRepository, this.remoteRepository);
            }
            CommitIntegrationSettingsContainer.initialize(Paths.get("teammates-exec-files", "settings.properties"));
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            failTest("Unable to setup commit integration state");
        }
    }

    @BeforeEach
    public void setup() {
        LoggingSetup.setMinLogLevel(Level.DEBUG);
        setup(false);
//        LoggingSetup.resetLogLevels();
    }

    /*
     * Deletes all testdata before running a new batch of tests
     */
    @BeforeAll
    public static void deleteDataBeforeRunningTests() {
    	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java", new JavaResource2Factory());
    	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("javaxmi", new JavaResource2Factory());
//        try {
//            Files.walk(TESTDATA_PATH)
//                .sorted(Comparator.reverseOrder())
//                .forEach(path -> {
//                    if (!path.equals(TESTDATA_PATH)) {
//                        path.toFile()
//                            .delete();
//                    }
//                });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @AfterEach
    public void cleanupAfterTest() {
        state.dispose();
    }

    protected void failTest(String msg) {
        LOGGER.error(msg);
        Assert.fail(msg);
    }

    /**
     * Propagates the given commits and evaluates every propagation.
     * 
     * @param commitIds
     *            The commits to be propagated
     * @return The list of all the propagations.
     */
    protected List<Propagation> propagateAndEvaluate(String... commitIds) {
        var evaluateImmediately = false;

        var historyEvalDir = this.state.getDirLayout()
            .getRootDirPath()
            .getParent();
        var commitHistoryEvaluator = new CommitHistoryEvaluator();

        List<Propagation> allPropagations = new ArrayList<>();
        try {
            String previousCommitId = null;
            for (var commitId : commitIds) {
                if (commitId == null) {
                    // do an empty propagation to reset the models
                     this.teammatesController.propagateChanges(commitId);
                    continue;
                }

                var propagations = this.teammatesController.propagateChanges(previousCommitId, commitId);
                previousCommitId = commitId;
                if (propagations.isEmpty() || propagations.size() > 1 || propagations.get(0).isEmpty()) {
                    continue;
                }
                
                var propagation = propagations.get(0).get();
                if (evaluateImmediately) {
                    var eval = evaluatePropagation(propagation);
                    commitHistoryEvaluator.addEvaluationDataContainer(eval);
                    if (!eval.valid()) {
                        failTest("Propagation failed evaluation (immediate abort)");
                    }
                }
                allPropagations.add(propagation);
            }

            var failures = 0;
            if (!evaluateImmediately) {
                LOGGER.info("\n\tEvaluating all propagations");
                var i = 1;
                for (var propagation : allPropagations) {
                    var eval = evaluatePropagation(propagation);
                    commitHistoryEvaluator.addEvaluationDataContainer(eval);
                    if (!eval.valid()) {
                        failures++;
                        LOGGER.error(String.format("Propagation #%d failed evaluation\n", i));
                    }
                    i++;
                }
            }

            // Evaluate the complete commit history
            commitHistoryEvaluator.evaluate();
            commitHistoryEvaluator.write(historyEvalDir);


            if (failures > 0) {
                LOGGER.warn(String.format("%d propagations where invalid", failures));
            }

            return allPropagations;
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        return null;
    }

    protected EvaluationDataContainer evaluatePropagation(Propagation propagation) {
        if (propagation == null) {
            Assert.fail("PropagatedChanges may not be null");
        }

        var evaluator = new PropagationEvaluator<>(propagation, this.teammatesController, this.manualModelsPath);

        var result = evaluator.evaluate();

        var evaluationDataContainer = EvaluationDataContainer.get();
        evaluationDataContainer.setSuccessful(result);
        var evaluationFileName = "evaluationData.json";
        var evaluationPath = propagation.getCommitIntegrationStateCopyPath()
            .resolve(evaluationFileName);
        EvaluationDataContainerReaderWriter.write(evaluationDataContainer, evaluationPath);

        return evaluationDataContainer;
    }
    
    @Test
    public void testTeammates() {
    	propagateAndEvaluate(COMMIT_HASHES);
    }
}
