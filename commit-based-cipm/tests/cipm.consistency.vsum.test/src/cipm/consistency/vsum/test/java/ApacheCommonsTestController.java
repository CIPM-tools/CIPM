package cipm.consistency.vsum.test.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.java.JavaModelFacade;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.vsum.test.appspace.LoggingSetup;
import cipm.consistency.vsum.test.java.ApacheCommonsRepoEntries.RepoEntry;
import jamopp.resource.JavaResource2Factory;
import tools.cipm.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;

/*
 * TODO Fix
 * tools.vitruv.applications.util.temporary.JavaTypeUtil.hasSameTargetReference(
 * TypeReference, TypeReference)
 * 
 * Currently, there are 2 scenarios that lead to NullPointerExceptions:
 * 
 * 1) reference1 == null ^ reference2 == null
 * 
 * 2) target1 == null ^ target2 == null
 * 
 * In the case of 1) or 2), the method should return false. The fix is not
 * included, since the Vitruvius packages come from a GIT submodule. Hence it
 * currently has to be fixed manually by editing JavaTypeUtil.xtend and then
 * re-generating the corresponding .java files.
 * 
 * After fixing the above-mentioned bug, the change propagation of the Apache
 * repositories at the given commits runs successfully. Note that this is a
 * commit integration test case, meaning that the models (Java, PCM, IM) will be
 * build from scratch.
 */

/**
 * Contains integration and propagation tests for the Apache commons repository.
 * The integration test case is from the "case-vitruv" branch.
 * 
 * <p>
 * Note that the submodule checks within this class may not cover cases, where
 * submodule contents are manually modified (e.g. submodule files are
 * hand-modified and committed). Therefore, this test class assumes that the
 * submodules are not manually adjusted. This especially includes <b><i>
 * deleting the submodule repositories and/or their GIT-metadata by
 * hand.</i></b>
 */
public class ApacheCommonsTestController {
	private static final Logger LOGGER = Logger.getLogger(ApacheCommonsTestController.class);
	private CommitIntegrationState<JavaModelFacade> state;
	private ApacheCommonsCommitIntegration apacheCommonsController;

	private Path localRepositoriesDir = Paths.get("target", "apache-commons");

	private Path rootPath = Paths.get("target", "ApacheCommonsTest");

	private void initContainerRepo(GitRepositoryWrapper wrapper) throws GitAPIException, IOException {
		if (Files.exists(this.localRepositoriesDir)) {
			// Initialize the repositories within this directory.
			LOGGER.debug("Initialising a pre-existing container repository");
			wrapper.withLocalDirectory(this.localRepositoriesDir).initialize();
			LOGGER.debug("Initialised a pre-existing container repository");
		} else {
			// Initialize the container repository
			LOGGER.debug("Initialising a new container repository");
			wrapper.initNewRepository(this.localRepositoriesDir.toFile());
			LOGGER.debug("Initialised the new container repository");
		}
	}

	private void ensureSubmoduleConfiguration(GitRepositoryWrapper wrapper, Map<String, RepoEntry> repoMap)
			throws GitAPIException, IOException {
		boolean submodulesChanged = false;

		// Check whether there are any submodules that should not be there
		for (var submodulePath : wrapper.getSubmodulePaths()) {

			// De-register and remove the submodule physically, if it is not supposed to be
			// considered
			//
			// Assume that the submodulePath is the same as repoId
			//
			if (!repoMap.keySet().contains(submodulePath)) {
				LOGGER.debug(submodulePath + " is not supposed to be considered");
				LOGGER.debug("De-registering " + submodulePath);
				wrapper.deregisterSubmodule(submodulePath);
				LOGGER.debug("De-registered " + submodulePath);
				LOGGER.debug("Removing " + submodulePath + " physically");
				wrapper.removeSubmodulePhysically(submodulePath);
				LOGGER.debug("Removed " + submodulePath + " physically");

				submodulesChanged = true;
			} else {
				LOGGER.debug(submodulePath + " is considered and is already registered");
			}
		}

		// Check the configuration of each specified submodule
		for (var e : repoMap.entrySet()) {
			var relativeSubmodulePath = e.getKey();
			var commitId = e.getValue().commitId;

			// Add the submodule, if it is not registered yet
			//
			// "git submodule add"
			if (!wrapper.isSubmoduleRegistered(relativeSubmodulePath)) {
				LOGGER.debug(relativeSubmodulePath + " is missing as a submodule");
				LOGGER.debug("Adding " + relativeSubmodulePath + " as a submodule");
				wrapper.addSubmodule(e.getValue().remoteRepoURI, relativeSubmodulePath);
				LOGGER.debug("Added " + relativeSubmodulePath + " as a submodule");

				submodulesChanged = true;
			} else {
				LOGGER.debug(relativeSubmodulePath + " is already registered");
			}

			// Initialise and clone the submodule, if it does not physically exist
			//
			// "git submodule init submodule_name"
			// "git submodule update submodule_name"
			if (!wrapper.isSubmodulePhysicallyPresent(relativeSubmodulePath)) {
				LOGGER.debug(relativeSubmodulePath + " is not physically present");

				// If submodule metadata files are present, remove them first,
				// so that initialising and cloning the submodule does not
				// throw exceptions
				if (!wrapper.canInitAndCloneSubmodule(relativeSubmodulePath)) {
					LOGGER.debug(relativeSubmodulePath + " metadata files exist");
					LOGGER.debug("Deleting the metadata of " + relativeSubmodulePath);
					wrapper.removeSubmodulePhysically(relativeSubmodulePath);
					LOGGER.debug("Deleted the metadata of " + relativeSubmodulePath);
				}

				LOGGER.debug("Initialising and cloning " + e.getKey());
				wrapper.initAndCloneSubmodule(relativeSubmodulePath);
				LOGGER.debug("Initialised and cloned " + e.getKey());

				// Note: Initialising and cloning the contents of a submodule does not result in
				// any changes to the container repository, hence submodulesChanged remains
				// false
			} else {
				LOGGER.debug(relativeSubmodulePath + " is already physically present");
			}

			// Checkout submodule to the given commitId, if the submodule is not already
			// checked out there
			if (!wrapper.isSubmoduleCheckedOutAt(relativeSubmodulePath, commitId)) {
				LOGGER.debug(relativeSubmodulePath + " is not checked out at " + commitId);
				LOGGER.debug("Checking out " + relativeSubmodulePath + " at " + commitId);
				wrapper.checkoutInSubmodule(relativeSubmodulePath, commitId);
				LOGGER.debug("Checked out " + relativeSubmodulePath + " at " + commitId);

				submodulesChanged = true;
			} else {
				LOGGER.debug(relativeSubmodulePath + " is already checked out at " + commitId);
			}
		}

		// Commit all submodule changes at once, in order to keep the commit history of
		// the parent repository clean and to make sure that each test case has exactly
		// one commit. Only commit, if the submodules changed at all
		if (submodulesChanged) {
			// TODO Add more information about the test case to the commit message, so that
			// which tests run and what happens is clear

			LOGGER.debug("Committing all submodule changes");
			wrapper.commitAllSubmoduleChanges("Set submodule configuration");
			LOGGER.debug("Committed all submodule changes");
		} else {
			LOGGER.debug("No submodule changes to commit");
		}
	}

	/**
	 * 
	 * @param overwrite Are existing files (models, etc.) to be deleted before
	 *                  initializing the commit integration state?
	 * @throws GitAPIException
	 * @throws IOException
	 * @throws org.eclipse.jgit.api.errors.TransportException
	 * @throws InvalidRemoteException
	 */
	protected void setup(boolean overwrite, Map<String, RepoEntry> repoMap) {
		// Create new empty state
		this.apacheCommonsController = new ApacheCommonsCommitIntegration(this.rootPath);

		// overwrite existing files?
		try {
			CommitIntegrationSettingsContainer
					.initialize(Paths.get("apache-commons-exec-files", "settings.properties"));
			this.apacheCommonsController.initialize(this.apacheCommonsController);
			this.state = this.apacheCommonsController.getState();
			var wrapper = this.state.getGitRepositoryWrapper();

			initContainerRepo(wrapper);
			ensureSubmoduleConfiguration(wrapper, repoMap);
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			failTest("Unable to setup commit integration state");
		}
	}

	@BeforeEach
	public void setup() {
		LoggingSetup.setMinLogLevel(Level.DEBUG);
	}

	@BeforeAll
	public static void setupStatic() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java", new JavaResource2Factory());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("javaxmi", new JavaResource2Factory());
		// This method call is important for the tests to run as intended, as it makes
		// sure that the InstrumentationModel can be found later on
		InstrumentationModelPackage.eINSTANCE.eClass();
	}

	@AfterEach
	public void cleanupAfterTest() {
		state.dispose();
	}

	protected void failTest(String msg) {
		LOGGER.error(msg);
		Assert.fail(msg);
	}

	@Test
	public void testApacheCommonsIntegration() {
		setup(false, ApacheCommonsRepoEntries.getCaseVitruvTestCase());

		var result = this.apacheCommonsController.propagateCurrentCheckout();
		System.out.println(result.get());
	}

	@Test
	public void testApacheCommonsPropagation_NewSubmodule() {
		setup(false, ApacheCommonsRepoEntries.getMinimalPropagationTestCase());

		var result = this.apacheCommonsController.propagateCurrentCheckout();
		System.out.println(result.get());
	}
}
