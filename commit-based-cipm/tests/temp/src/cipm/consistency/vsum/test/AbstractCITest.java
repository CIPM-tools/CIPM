package cipm.consistency.vsum.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
import cipm.consistency.commitintegration.CommitIntegrationController;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

/**
 * An abstract superclass for test cases providing the setup.
 * 
 * @author Martin Armbruster
 */
public abstract class AbstractCITest {
	private static final Logger LOGGER = Logger.getLogger("cipm." + AbstractCITest.class.getSimpleName());
	private final String evaluationResultFileNamePrefix = "eval_";
	protected CommitIntegrationController controller;

	@BeforeEach
	public void setUp() throws Exception {
		Logger logger = Logger.getLogger("cipm");
		logger.setLevel(Level.ALL);
		logger = Logger.getLogger("jamopp");
		logger.setLevel(Level.ALL);
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
				ConsoleAppender.SYSTEM_OUT);
		logger.addAppender(ap);
		Thread.sleep(5000);
		
		// Needed to instantiate the singleton CommitIntegrationSettingsContainer for the first time,
		// so that CommitIntegrationController can be instantiated, since getJavaPCMSpecification() requires it
		if (CommitIntegrationSettingsContainer.getSettingsContainer() == null) {
			Path settingsPath = Paths.get(getSettingsPath());
			CommitIntegrationSettingsContainer.initialize(settingsPath);
		}
		
		controller = new CommitIntegrationController(Paths.get(getTestPath()), getRepositoryPath(),
				Paths.get(getSettingsPath()), getJavaPCMSpecification());
	}

	/**
	 * Propagates changes between two commits and performs a partial evaluation on
	 * the result.
	 * 
	 * @param oldCommit the first commit. Can be null.
	 * @param newCommit the second commit.
	 * @param num       the number of the propagation.
	 * @return true if the changes were propagated. false otherwise.
	 */
	@SuppressWarnings("restriction")
	protected boolean executePropagationAndEvaluation(String oldCommit, String newCommit, int num)
			throws Exception {
		EvaluationDataContainer evalResult = new EvaluationDataContainer();
		evalResult.setNumberOfPropagation(num);
		EvaluationDataContainer.setGlobalContainer(evalResult);
		String repoFile = this.controller.getVSUMFacade().getPCMWrapper().getRepository().eResource()
				.getURI().toFileString();
		FileUtils.copyFile(new File(repoFile), new File(this.getTestPath(), "Repository.repository"));
		FileUtils.copyFile(new File(repoFile), new File(this.getTestPath(), "Repository_" + num + "_mu.repository"));
		boolean result = this.controller.propagateChanges(oldCommit, newCommit, true);
		if (result) {
			Resource javaModel = this.controller.getJavaModelResource();
			Resource instrumentedModel = this.controller.getLastInstrumentedModelResource();
			Path root = this.controller.getVSUMFacade().getFileLayout().getRootPath();
			LOGGER.debug("Evaluating the instrumentation.");
			new InstrumentationEvaluator().evaluateInstrumentationDependently(
					this.controller.getVSUMFacade().getInstrumentationModel(), javaModel, instrumentedModel,
					this.controller.getVSUMFacade().getVSUM().getCorrespondenceModel());
			var resultFile = root.resolve(this.evaluationResultFileNamePrefix + newCommit + ".json");
			EvaluationDataContainerReaderWriter.write(evalResult, resultFile);
			LOGGER.debug("Copying the propagated state.");
			updateBackupRepository(root, "Changes: " + oldCommit + " to " + newCommit + " (" + num + ")", (gitDir) -> {
				try {
					FileUtils.copyDirectory(root.resolve("vsum-all").toFile(), gitDir.resolve("vsum-all").toFile());
					FileUtils.copyFileToDirectory(root.resolve(".commits").toFile(), gitDir.toFile());
					FileUtils.copyFileToDirectory(resultFile.toFile(), gitDir.toFile());
				} catch (IOException e) {
					fail(e);
				}
			});
			LOGGER.debug("Finished the evaluation.");
		}
		return result;
	}

	/**
	 * Performs an evaluation independent of the change propagation. It requires
	 * that changes between two commits has been propagated. It is recommended that
	 * this method is not executed with the executePropagationAndEvaluation method
	 * at the same time because this can cause a OutOfMemoryError.
	 * 
	 * @throws IOException if an IO operation cannot be performed.
	 */
	@SuppressWarnings("restriction")
	protected void performIndependentEvaluation() throws Exception {
		String[] commits = this.controller.loadCommits();
		String oldCommit = commits[0];
		String newCommit = commits[1];
		
		LOGGER.debug("Evaluating the propagation " + oldCommit + "->" + newCommit);
		Path root = this.controller.getVSUMFacade().getFileLayout().getRootPath();
		var evalResultFile = root.resolve(this.evaluationResultFileNamePrefix + newCommit + ".json");
		EvaluationDataContainer evalResult = EvaluationDataContainerReaderWriter.read(evalResultFile);
		EvaluationDataContainer.setGlobalContainer(evalResult);
		Resource javaModel = this.controller.getJavaModelResource();
		
		LOGGER.debug("Evaluating the Java model.");
		new JavaModelEvaluator().evaluateJavaModels(javaModel,
				this.controller.getCommitChangePropagator().getJavaFileSystemLayout().getLocalJavaRepo(),
				evalResult.getJavaComparisonResult(),
				this.controller.getCommitChangePropagator().getJavaFileSystemLayout().getModuleConfiguration());
		
		// For the initial commit (i.e., number of propagation equals 0), no comparison is performed.
		if (evalResult.getNumberOfPropagation() != 0 && this.getReferenceRepositoryModelDirectoryName() != null) {
			LOGGER.debug("Evaluating the Repository Model.");
			new RepositoryModelEvaluator().evaluateRepositoryModel(
				Paths.get("..", "..", "..", "data", this.getReferenceRepositoryModelDirectoryName(), "Repository_" + evalResult.getNumberOfPropagation() + "_mu.repository"),
				this.controller.getVSUMFacade().getPCMWrapper().getRepository());
		}
		
		LOGGER.debug("Evaluating the instrumentation model.");
		new IMUpdateEvaluator().evaluateIMUpdate(this.controller.getVSUMFacade().getPCMWrapper().getRepository(),
				this.controller.getVSUMFacade().getInstrumentationModel(), evalResult.getImEvalResult(),
				this.getTestPath());
		
		LOGGER.debug("Evaluating the instrumentation.");
		new InstrumentationEvaluator().evaluateInstrumentationIndependently(
				this.controller.getVSUMFacade().getInstrumentationModel(), javaModel,
				this.controller.getCommitChangePropagator().getJavaFileSystemLayout(),
				this.controller.getVSUMFacade().getVSUM().getCorrespondenceModel());
		EvaluationDataContainerReaderWriter.write(evalResult, evalResultFile);
		
		updateBackupRepository(root, "Updated evaluation for: " + oldCommit + " to " + newCommit, (Path gitDir) -> {
			try {
				FileUtils.copyFileToDirectory(evalResultFile.toFile(), gitDir.toFile());
			} catch (IOException e) {
				fail(e);
			}
		});
		
		LOGGER.debug("Finished the evaluation.");
	}
	
	private void updateBackupRepository(Path root, String commitMessage, Consumer<Path> repositoryUpdater) throws Exception {
		Path copyContainer = root.resolveSibling(root.getFileName().toString() + "-git");
		Path copyGitDir = copyContainer.resolve(".git");
		Git copy;
		if (Files.notExists(copyGitDir)) {
			copy = Git.init().setDirectory(copyContainer.toFile()).call();
		} else {
			copy = Git.open(copyContainer.toFile());
		}
		repositoryUpdater.accept(copyContainer);
		copy.add().addFilepattern(".").call();
		copy.commit().setAuthor("CIPM", "").setMessage(commitMessage).call();
		copy.getRepository().close();
		copy.close();
	}

	@AfterEach
	public void tearDown() throws Exception {
		controller.shutdown();
	}

	/**
	 * Returns the path to the local directory in which the data is stored.
	 * 
	 * @return the path.
	 */
	protected abstract String getTestPath();

	/**
	 * Returns the path to the remote repository from which the commits are fetched.
	 * 
	 * @return the path.
	 */
	protected abstract String getRepositoryPath();

	/**
	 * Returns the path to the settings file.
	 * 
	 * @return the path.
	 */
	protected abstract String getSettingsPath();
	
	/**
	 * Returns the CPRs between Java and the PCM.
	 * 
	 * @return the CPRs.
	 */
	protected abstract ChangePropagationSpecification getJavaPCMSpecification();
	
	protected abstract String getReferenceRepositoryModelDirectoryName();
}
