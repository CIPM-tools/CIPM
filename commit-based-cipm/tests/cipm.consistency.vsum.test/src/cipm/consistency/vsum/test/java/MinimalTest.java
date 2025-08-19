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
import org.junit.jupiter.api.Assertions;
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
	 * Initial commit (with fake Object class)
	 */
	private static final String commitID1 = "733720dc533fa707ae55c9c3f1367b06ecdf8a44";
	/**
	 * First basic component (teammates.common) addition
	 */
	private static final String commitID2 = "11e9611198ddde7b8feb52213d37be7f2c2e13dd";
	/**
	 * Second basic component (teammates.logic.api) addition
	 */
	private static final String commitID3 = "72ca58c5ec5f507fc3a91928925dd12bfd170f40";

	private static final Logger LOGGER = Logger.getLogger(MinimalTest.class);
	private CommitIntegrationState<JavaModelFacade> state;
	private TEAMMATESCommitIntegration teammatesController;

	private Path localRepository = new File("C:\\Users\\atora\\Desktop\\MinimalRepo").getAbsoluteFile().toPath();
	private String remoteRepository = "";

	private Path targetPath = Paths.get("target").toAbsolutePath();
	private Path rootPath = targetPath.resolve(MinimalTest.class.getSimpleName());
	private Path manualModelsPath = targetPath.resolve("manual");

	private Path pcmMinimalTestPath = targetPath.resolve("PcmMinimalTest");

	private String oldCommitID = commitID2;
	private String newCommitID = commitID3;

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
	public void createPCMMinimalTestResources() {
		this.createChangeResource();

		var oldCommitRootPath = this.targetPath
				.resolve(String.format("%s-1-%s", MinimalTest.class.getSimpleName(), oldCommitID.substring(0, 7)));
		this.copyPCMMinimalTestModelResources(oldCommitRootPath, this.pcmMinimalTestPath.resolve("oldCommit"), oldCommitID);

		var newCommitRootPath = this.targetPath
				.resolve(String.format("%s-2-%s", MinimalTest.class.getSimpleName(), newCommitID.substring(0, 7)));
		this.copyPCMMinimalTestModelResources(newCommitRootPath, this.pcmMinimalTestPath.resolve("newCommit"), newCommitID);
	}

	private void createChangeResource() {
		var resSet = new ResourceSetImpl();
		var changesFileName = "oldToNewCommitPcmChanges.changes";

		var minimalTestChangesPath = this.teammatesController.getState().getDirLayout().getRootDirPath()
				.resolve(changesFileName);
		var changeRes = resSet.createResource(URI.createFileURI(minimalTestChangesPath.toString()));
		var props = propagateAndEvaluate(oldCommitID, newCommitID);
		for (var prop : props) {
			for (var change : prop.getChanges()) {
				// Java changes
//				res.getContents().addAll(change.getOriginalChange().getEChanges());

				// PCM changes
				changeRes.getContents().addAll(change.getConsequentialChanges().getEChanges());
			}
		}
		try {
			// Save the consequential changes under "MinimalTest"
			changeRes.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		var pcmMinimalTestChangesPath = this.pcmMinimalTestPath.resolve(changesFileName);
		// Copy consequential changes to "PcmMinimalTest"
		this.copyFile(minimalTestChangesPath.toFile(), pcmMinimalTestChangesPath);
		this.fixURIsAndHREFs(pcmMinimalTestChangesPath);
	}

	private void copyFile(File f, Path targetPath) {
		try {
			var targetFile = targetPath.toFile();
			if (!targetFile.exists()) {
				targetFile.getParentFile().mkdirs();
//				targetFile.createNewFile();
			}
			Files.copy(f.toPath(), targetFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
	}

	private void fixURIsAndHREFs(Path changesFilePath) {
		try {
			var changesString = Files.readString(changesFilePath);
			changesString = changesString.replaceAll("MinimalTest/pcm", "PcmMinimalTest/oldCommit");
			changesString = changesString.replaceAll("href=\"pcm/", "href=\"oldCommit/");
			Files.write(changesFilePath, changesString.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
			Assertions.fail(e1);
		}
	}

	private void copyPCMMinimalTestModelResources(Path commitRootPath, Path copyTargetPath, String commitID) {
		var codeModelPath = commitRootPath.resolve("code").resolve("Java.javaxmi");
		this.copyFile(codeModelPath.toFile(), copyTargetPath.resolve("Java.javaxmi"));
		var pcmModelsPath = commitRootPath.resolve("pcm");
		for (var f : pcmModelsPath.toFile().listFiles()) {
			if (!f.getName().endsWith(".repository")) {
				this.copyFile(f, copyTargetPath.resolve(f.getName()));
			} else {
				if (f.getName().contains(commitID)) {
					// Parsed repository
					this.copyFile(f, copyTargetPath.resolve("Repository.repository"));
				}
			}
		}
	}
}
