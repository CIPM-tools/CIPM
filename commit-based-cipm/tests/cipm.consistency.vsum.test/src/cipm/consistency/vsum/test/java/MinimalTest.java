package cipm.consistency.vsum.test.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.lang.java.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.lang.java.JavaParserAndPropagatorUtils.Configuration;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.evaluator.PropagationEvaluator;
import cipm.consistency.vsum.test.evaluator.commitHistory.CommitHistoryEvaluator;
import jamopp.resource.JavaResource2Factory;

public class MinimalTest {

	/**
	 * Initial commit (with 1 Java file, empty Java main method)
	 */
	private static final String commitID1 = "67d4507c6047be6bff3aad46158ee368db086bc4";
	/**
	 * With 1 Java file, main method prints "output"
	 */
	private static final String commitID2 = "5f86208a2b869aec3f5756ff2b95fbebf8e03045";
	/**
	 * First basic component addition
	 */
	private static final String commitID3 = "0ed2fca1e6e239d6dec6165741175b1cc5f542fa";
	/**
	 * Second basic component addition
	 */
	private static final String commitID4 = "c95bb64af02d82bb34cad06d934966dbc4be9673";

	private static final Logger LOGGER = Logger.getLogger(MinimalTest.class);
	private CommitIntegrationState<JavaModelFacade> state;
	private TEAMMATESCommitIntegration teammatesController;

	private Path localRepository = new File("C:\\Users\\atora\\Desktop\\MinimalRepo").getAbsoluteFile().toPath();
	private String remoteRepository = "";
	private Path rootPath = Paths.get("target", "MinimalTest");
	private Path manualModelsPath = Paths.get("target", "manual");

	/**
	 * 
	 * @param overwrite Are existing files (models, etc.) to be deleted before
	 *                  initializing the commit integration state?
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
			// state.initialize(this.teammatesController,
			// this.teammatesController.getRootPath(), overwrite);
			if (Files.exists(this.localRepository)) {
				this.teammatesController.getGitRepositoryWrapper()
						.withLocalDirectory(this.localRepository.resolve(".git"));
			} else {
				this.teammatesController.getGitRepositoryWrapper().withRemoteRepositoryCopy(this.localRepository,
						this.remoteRepository);
			}
			CommitIntegrationSettingsContainer.initialize(Paths.get("teammates-exec-files", "settings.properties"));
			JavaParserAndPropagatorUtils.setConfiguration(new Configuration(true));
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
	 * Propagates the given commits and evaluates every propagation. It assumes that
	 * the propagation starts from an empty repository. Thus, if there is any
	 * previous state, it is reseted.
	 * 
	 * @param commitIds The commits to be propagated
	 * @return The list of all the propagations.
	 */
	protected List<Propagation> propagateAndEvaluate(String... commitIds) {
		return propagateAndEvaluate(true, commitIds);
	}

	/**
	 * Propagates the given commits and evaluates every propagation.
	 * 
	 * @param startFromNull When set to true, this parameter indicates if the
	 *                      propagation should start from an empty repository, which
	 *                      resets any previous persisted state or propagation. When
	 *                      set to false, the parameter indicates that the previous
	 *                      state corresponds to the first commit of the given
	 *                      commitIds. Therefore, the propagation starts with the
	 *                      changes between the first and second commit given in the
	 *                      commitIds.
	 * @param commitIds     The commits to be propagated
	 * @return The list of all the propagations.
	 */
	protected List<Propagation> propagateAndEvaluate(boolean startFromNull, String... commitIds) {
		var evaluateImmediately = false;

		var historyEvalDir = this.state.getDirLayout().getRootDirPath().getParent();
		var commitHistoryEvaluator = new CommitHistoryEvaluator();

		List<Propagation> allPropagations = new ArrayList<>();
		try {
			String previousCommitId = startFromNull ? null : commitIds[0];
			for (int i = startFromNull ? 0 : 1; i < commitIds.length; i++) {
				var commitId = commitIds[i];
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
		var evaluationPath = propagation.getCommitIntegrationStateCopyPath().resolve(evaluationFileName);
		EvaluationDataContainerReaderWriter.write(evaluationDataContainer, evaluationPath);

		return evaluationDataContainer;
	}

	@Test
	public void testTeammates() {
		var resSet = new ResourceSetImpl();
		var res = resSet.createResource(URI.createFileURI(this.teammatesController.getState().getDirLayout()
				.getPcmDirPath().resolve("pcmChanges.changes").toString()));
		var props = propagateAndEvaluate(commitID3, commitID4);
		for (var prop : props) {
			for (var change : prop.getChanges()) {
				// Java changes
//				res.getContents().addAll(change.getOriginalChange().getEChanges());

				// PCM changes
				res.getContents().addAll(change.getConsequentialChanges().getEChanges());
			}
		}
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
