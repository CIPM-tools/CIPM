package cipm.consistency.fitests.repositorytests;

import cipm.consistency.fitests.repositorytests.util.RepoCacheSimilarityResultProvider;
import cipm.consistency.fitests.repositorytests.util.RepoTestResultCache;
import cipm.consistency.fitests.repositorytests.util.RepoTestSimilarityValueEstimator;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPResourceParsingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.AbstractJaMoPPParserSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.parser.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.IExpectedSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.IJaMoPPParserTestGenerationStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.IModelResourceWrapper;
import cipm.consistency.fitests.similarity.jamopp.parser.ReflexiveSymmetricIterationTestGenerationStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.JaMoPPModelResourceWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * An abstract test class, which can be used for implementing tests that involve
 * parsing models from GIT repositories and checking their similarity.
 * 
 * @author Alp Torac Genc
 * 
 * @see {@link AbstractJaMoPPParserSimilarityTest#createTests()}
 */
public abstract class AbstractJaMoPPParserRepoTest extends AbstractJaMoPPParserSimilarityTest {

	/**
	 * Contains expected results of comparing model resources
	 */
	private static RepoTestResultCache resultCache = new RepoTestResultCache();

	/**
	 * The pattern of "gradle-wrapper.jar" file path, which should be excluded when
	 * parsing model resources to avoid IOExceptions.
	 */
	private static final String gradleWrapperJarPathPattern = ".*?/gradle-wrapper\\.jar";

	/**
	 * The name of the root directory of the models
	 */
	private static final String repoModelImplDirName = "repo-clones";

	/**
	 * The segment in remote GIT repository URLs, which are followed by the commit
	 * hash
	 */
	private static final String repoURICommitSegment = "commit";

	/**
	 * The name of the folder, where contents of {@link #resultCache} should be
	 * saved. <br>
	 * <br>
	 * Note: This folder does not have to directly contain the contents of
	 * {@link #resultCache}. They may be saved in sub-directories as well.
	 */
	private static final String expectedSimilarityResultCacheDirName = "results-cache";

	/**
	 * The name of the file (with extension), where contents of {@link #resultCache}
	 * should be saved.
	 */
	private static final String expectedSimilarityResultCacheFileName = "resultsCache.json";

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserRepoTest}: Loads expected similarity checking
	 * results, if their file exists.
	 */
	@BeforeEach
	@Override
	public void setUp() {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_BEFOREEACH);
		super.setUp();

		var resultCachePath = this.getTestFileLayout().getExpectedSimilarityResultCachePath();
		if (this.getResourceTestOptions().shouldUseCachedExpectedSimilarityResults()) {
			this.startTimeMeasurement(RepoTimeMeasurementTag.LOAD_EXPECTED_SIMILARITY_RESULTS);
			this.logDebugMsg(String.format("Checking for cached expected similarity results for %s at %s",
					this.getCurrentTestClassName(), resultCachePath));
			if (resultCachePath.toFile().exists()) {
				this.logDebugMsg(String.format("Cached expected similarity results exist"));
				try (BufferedReader reader = Files.newBufferedReader(resultCachePath)) {
					this.logDebugMsg(String.format("Reading cached expected similarity results"));
					resultCache = new RepoTestResultCache(new Gson().fromJson(reader, resultCache.getClass()));
					this.logDebugMsg(String.format("Read cached expected similarity results"));
				} catch (IOException e) {
					this.logDebugMsg(String.format("Could not read cached expected similarity results for %s at %s",
							this.getCurrentTestClassName(), resultCachePath));
				}
			}
			this.stopTimeMeasurement();
		} else {
			this.logDebugMsg(String.format("No saved expected similarity results found for %s at %s",
					this.getCurrentTestClassName(), resultCachePath));
		}
		this.stopTimeMeasurement();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserRepoTest}: Saves the computed expected similarity
	 * results and deletes the local repository clone, if desired.
	 */
	@AfterEach
	@Override
	public void tearDown() {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_AFTEREACH);
		if (this.getResourceTestOptions().shouldSaveCachedExpectedSimilarityResults()) {
			this.startTimeMeasurement(RepoTimeMeasurementTag.SAVE_EXPECTED_SIMILARITY_RESULTS);
			var gson = new GsonBuilder().setPrettyPrinting().create();

			var resultCachePath = this.getTestFileLayout().getExpectedSimilarityResultCachePath();
			var resultCacheFile = resultCachePath.toFile();

			this.logDebugMsg(String.format("Saving cached expected similarity results for %s at %s",
					this.getCurrentTestClassName(), resultCachePath));

			// Re-write expected similarity results

			if (resultCacheFile.exists()) {
				resultCacheFile.delete();
			}
			resultCacheFile.getParentFile().mkdirs();
			try {
				resultCacheFile.createNewFile();
			} catch (IOException e) {
				Assertions.fail(String.format("Could not create a file for result cache at %s", resultCachePath), e);
			}

			try (BufferedWriter writer = Files.newBufferedWriter(resultCachePath);
					var gsonWriter = gson.newJsonWriter(writer)) {
				gson.toJson(resultCache, resultCache.getClass(), gsonWriter);
			} catch (IOException e) {
				Assertions.fail(String.format("Could not save the expected similarity results at %s", resultCachePath),
						e);
			}

			this.logDebugMsg(String.format("Saved cached expected similarity results for %s at %s",
					this.getCurrentTestClassName(), resultCachePath));
			this.stopTimeMeasurement();
		}

		if (this.getResourceTestOptions().shouldDeleteRepositoryClones()) {
			this.startTimeMeasurement(RepoTimeMeasurementTag.DELETE_LOCAL_REPO_CLONE);
			this.getFileUtil().deleteAll(this.getTestFileLayout().getModelSourceFileRootDirPath());
			this.stopTimeMeasurement();
		}

		this.stopTimeMeasurement();
		super.tearDown();
	}

	@Override
	protected RepoParserTestFileLayout initParserTestFileLayout() {
		var parserTestLayout = super.initParserTestFileLayout();
		var layout = new RepoParserTestFileLayout(parserTestLayout);
		layout.setRepoName(this.getRepoName());
		layout.setExpectedSimilarityResultCacheDirName(expectedSimilarityResultCacheDirName);
		layout.setExpectedSimilarityResultCacheFileName(expectedSimilarityResultCacheFileName);
		layout.setRepoModelImplDirName(repoModelImplDirName);
		return layout;
	}

	@Override
	protected RepoParserTestFileLayout getTestFileLayout() {
		return (RepoParserTestFileLayout) super.getTestFileLayout();
	}

	/**
	 * @return The expected similarity checking result for the given commits. Note
	 *         that similarity checking is symmetric, meaning that swapping lhs and
	 *         rhs commits should not change the return value.
	 */
	protected Boolean getExpectedResult(String lhsCommit, String rhsCommit) {
		return resultCache.getResult(lhsCommit, rhsCommit);
	}

	/**
	 * Adds model resources to {@link #resultCache} for all commits relevant for
	 * this test. Must be executed before all tests.
	 * 
	 * @see {@link #getCommitIDs()}
	 */
	protected Collection<Resource> cacheCommitResources() {
		var commitResources = new ArrayList<Resource>();
		var commitResourcesExist = true;
		final var expectedResultsExist = new boolean[] { true };

		var commitIDList = this.getCommitIDs();
		var testStrats = this.getTestGenerationStrategies();

		/*
		 * Determine whether all required expected results are in the cache based on
		 * what commits are compared to one another in the tests
		 */
		testStrats.forEach((ts) -> ts.getTestResourceIterator(commitIDList.size()).forEachRemaining((idxs) -> {
			var commitID1 = commitIDList.get(idxs[0]);
			var commitID2 = commitIDList.get(idxs[1]);
			if (resultCache.getResult(commitID1, commitID2) == null) {
				this.logDebugMsg(
						String.format("Expected similarity result missing for: %s vs %s", commitID1, commitID2));
				expectedResultsExist[0] = false;
				// Check for the other ones as well, for debugging purposes
			}
		}));

		for (var cID : commitIDList) {
			if (!this.getResourceHelper()
					.resourceFileExists(this.getTestFileLayout().getModelResourceSaveURIForCommit(cID))) {
				this.logDebugMsg(String.format("Model resource missing for: %s", cID));
				commitResourcesExist = false;
				// Check for the other ones as well, for debugging purposes
			}
		}

		Git git = null;

		if (!expectedResultsExist[0] || !commitResourcesExist) {
			this.logDebugMsg("Remote repository must be cloned due to missing resources / expected similarity results");
			git = this.cloneRepo();
		}

		if (!expectedResultsExist[0]) {
			this.logDebugMsg(String.format("Computing missing expected similarity results"));

			this.computeExpectedSimilarityResults(git, commitIDList);

			this.logDebugMsg(String.format("Computed missing similarity results"));
		}

		if (!commitResourcesExist) {
			this.logDebugMsg(String.format("Preparing missing model resources"));

			commitResources.addAll(this.prepareReposForCommits(commitIDList, git));

			this.logDebugMsg(String.format("Prepared missing model resources"));
		} else {
			for (var cID : commitIDList) {
				var cachedCommitURI = this.getTestFileLayout().getModelResourceSaveURIForCommit(cID);
				var res = new JaMoPPModelResourceWrapper(this.getResourceHelper());
				this.startTimeMeasurement(GeneralTimeMeasurementTag.LOAD_MODEL_RESOURCE);
				res.loadModelResource(cachedCommitURI);
				this.stopTimeMeasurement();
				this.getCacheUtil().addToCache(cachedCommitURI.toString(), res);
				commitResources.add(this.getCacheUtil().getFromCache(cachedCommitURI.toString()).getModelResource());
			}
		}

		if (git != null) {
			this.logDebugMsg("Closing repository wrapper");

			this.startTimeMeasurement(RepoTimeMeasurementTag.CLOSE_REPOSITORY);
			git.getRepository().close();
			git.close();
			this.stopTimeMeasurement();

			this.logDebugMsg(String.format("Closed repository wrapper"));
		}

		var mainLocalClonePath = this.getTestFileLayout().getModelSourceFileRootDirPath();

		this.logDebugMsg(
				String.format("Cleaning main local repository clone under: %s", mainLocalClonePath.toString()));

		this.startTimeMeasurement(RepoTimeMeasurementTag.DELETE_LOCAL_REPO_CLONE);
		this.getFileUtil().deleteAll(mainLocalClonePath);
		this.stopTimeMeasurement();

		this.logDebugMsg("Cleaned main local repository clone");

		this.logDebugMsg(String.format("Repository model resources are cached"));

		return commitResources;
	}

	/**
	 * 
	 * @param git          The GIT object associated with the in-memory
	 *                     representation of the GIT repository
	 * @param commitIDList A list of commit hashes, for which expected similarity
	 *                     results should be computed.
	 */
	protected void computeExpectedSimilarityResults(Git git, List<String> commitIDList) {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.EXPECTED_SIMILARITY_RESULT_COMPUTATION);
		var testStrats = this.getTestGenerationStrategies();
		var expectedValueEstimator = new RepoTestSimilarityValueEstimator();

		/*
		 * Determine whether all required expected results are in the cache based on
		 * what commits are compared to one another in the tests
		 */
		testStrats.forEach((ts) -> ts.getTestResourceIterator(commitIDList.size()).forEachRemaining((idxs) -> {
			var commitID1 = commitIDList.get(idxs[0]);
			var commitID2 = commitIDList.get(idxs[1]);
			if (resultCache.getResult(commitID1, commitID2) == null) {
				this.logDebugMsg(
						String.format("Computing expected similarity result for: %s vs %s", commitID1, commitID2));
				var result = expectedValueEstimator.getExpectedSimilarityValueFor(git, commitID1, commitID2);

				resultCache.addResult(commitID1, commitID2, result);

				this.logDebugMsg(String.format("Computed expected similarity result (%s) for: %s vs %s", result,
						commitID1, commitID2));
			}
		}));
		this.stopTimeMeasurement();
	}

	/**
	 * Clones the repository desired by this test.
	 * 
	 * @param repoToCloneURI URI to the repository, which should be cloned (local or
	 *                       remote)
	 * @param clonePath      The path to the folder, where the repository under
	 *                       repoToCloneURI will be cloned
	 * 
	 * @return An object that can be used to perform GIT operations on the
	 *         repository clone.
	 */
	protected Git cloneRepo(String repoToCloneURI, Path clonePath) {
		this.startTimeMeasurement(RepoTimeMeasurementTag.CLONE_REPOSITORY);
		Git git = null;

		// Repository clone does not exist, clone it
		if (!clonePath.toFile().exists() || clonePath.toFile().list() == null
				|| clonePath.toFile().list().length == 0) {
			try {
				this.logDebugMsg(
						String.format("Cloning remote repository (%s) to: %s", repoToCloneURI, clonePath.toString()));
				git = Git.cloneRepository().setURI(repoToCloneURI).setDirectory(clonePath.toFile())
						.setCloneAllBranches(true).call();
				this.logDebugMsg(String.format("Cloning successful"));
			} catch (GitAPIException e) {
				e.printStackTrace();
				Assertions.fail("Could not clone repository");
			}
		}
		// Repository clone folder exists, try to open it
		// If it does not open, delete it and re-try
		else {
			try {
				git = Git.open(clonePath.toFile());
			} catch (IOException e) {
				// Faulty repository clone, delete and re-try
				this.logDebugMsg("Could not open existing repository, deleting it and re-cloning");
				this.getFileUtil().deleteAll(clonePath);
				if (clonePath.toFile().exists() && clonePath.toFile().list().length != 0) {
					throw new IllegalStateException("Could not delete faulty repository clone");
				}
				git = this.cloneRepo(repoToCloneURI, clonePath);
			}
		}

		this.stopTimeMeasurement();
		return git;
	}

	/**
	 * Calls {@link #cloneRepo()} with default parameters.
	 * 
	 * @see {@link #cloneRepo()}
	 */
	protected Git cloneRepo() {
		// Do not explicitly add a folder for this repository, since GIT will do that
		// implicitly
		return this.cloneRepo(this.getRepoURI().toString(), this.getTestFileLayout().getModelSourceFileRootDirPath());
	}

	/**
	 * @return The cache key for the model resource parsed from the given commit
	 *         hash of the repository, when its model resource is inserted into the
	 *         cache via {@link #parseModelsDirWithCaching(Path, URI, String)}.
	 */
	protected String getCacheKeyForCommit(URI repoURI, String commitID) {
		return repoURI.appendSegment(repoURICommitSegment).appendSegment(commitID).toString();
	}

	/**
	 * Adds model resources to cache model resources for each given commit. Requires
	 * the remote repository to be cloned first (see parameter descriptions). <br>
	 * <br>
	 * <b><i>MODIFIES THE URI OF THE PARSED MODEL RESOURCES</i></b>
	 * 
	 * @param commits    Commits for which a model resource will be parsed
	 * @param gitWrapper The object that can be used to perform GIT operations on
	 *                   the "main" repository clone, which should be created with
	 *                   {@link #cloneRepo()}.
	 */
	protected Collection<Resource> prepareReposForCommits(List<String> commits, Git git) {

		var commitResources = new ArrayList<Resource>();

		// Checkout and copy local repository clone for each
		// commit except the last one. For the last one, just checkout to that commit to
		// spare 1 copy operation
		this.logDebugMsg("Caching model resources for commits");
		var commitCount = commits.size();
		for (int i = 0; i < commitCount; i++) {
			var commitID = commits.get(i);
			var commitResURI = this.getTestFileLayout().getModelResourceSaveURIForCommit(commitID);

			this.logDebugMsg(String.format("Checking out: %s", commitID));

			this.startTimeMeasurement(RepoTimeMeasurementTag.CHECKOUT_TO_COMMIT);
			try {
				git.checkout().setName(commitID).call();
			} catch (GitAPIException e) {
				this.logDebugMsg(String.format("Error while checking out: %s", commitID));
				throw new IllegalArgumentException(e);
			}
			this.stopTimeMeasurement();

			this.logDebugMsg(String.format("Checked out: %s", commitID));

			this.logDebugMsg(String.format("Caching resource for: %s", commitID));

			var targetPath = this.getTestFileLayout().getRepoClonePathForCommit(commitID);
			IModelResourceWrapper commitRes = null;

			/*
			 * Load the cached model resource for the commit, if it exists. Otherwise parse
			 * it.
			 */
			if (targetPath.toFile().exists()) {
				commitRes = this.parseModelsDirWithCaching(targetPath, commitResURI,
						getCacheKeyForCommit(this.getRepoURI(), commitID));
			} else {
				commitRes = this.parseModelsDirWithCaching(git.getRepository().getDirectory().getParentFile().toPath(),
						commitResURI, getCacheKeyForCommit(this.getRepoURI(), commitID));
				commitRes.setModelResourcesURI(commitResURI);
			}

			commitResources.add(commitRes.getModelResource());
			this.logDebugMsg(String.format("Cached resource for: %s", commitID));
		}
		this.logDebugMsg(String.format("Prepared model resources for commits"));
		return commitResources;
	}

	/**
	 * @implSpec Checks whether the given directory name matches any of the commit
	 *           hashes featured in tests.
	 */
	@Override
	protected boolean isModelSourceFileDirectoryName(String dirName) {
		return this.getCommitIDs().stream().anyMatch((c) -> dirName.equals(c));
	}

	/**
	 * @implSpec Adds {@value #gradleWrapperJarPathPattern} to exclusion patterns of
	 *           the given parser, in order for that file to not be locked during
	 *           tests. If it were locked, trying to delete it (while deleting the
	 *           local repository clone) does not work and may lead to IOExceptions.
	 */
	@Override
	protected JaMoPPResourceParsingStrategy initResourceParsingStrategy() {
		var strat = super.initResourceParsingStrategy();
		strat.addExclusionPattern(gradleWrapperJarPathPattern);
		return strat;
	}

	/**
	 * @return A list of all commits from the repository of this test, which are
	 *         relevant.
	 */
	protected abstract List<String> getCommitIDs();

	/**
	 * @return The URI to the repository, which will be used in tests.
	 */
	protected abstract URI getRepoURI();

	/**
	 * @return The name of the repository that is used in this test.
	 */
	protected String getRepoName() {
		return this.getRepoURI().lastSegment();
	}

	/**
	 * @return An object that provides expected similarity results for parsed
	 *         commits in tests.
	 */
	protected IExpectedSimilarityResultProvider getExpectedSimilarityResultProviderForCommits() {
		return new RepoCacheSimilarityResultProvider(resultCache);
	}

	@Override
	protected Collection<IJaMoPPParserTestGenerationStrategy> getTestGenerationStrategies() {
		var strats = new ArrayList<IJaMoPPParserTestGenerationStrategy>();
		strats.add(new ReflexiveSymmetricIterationTestGenerationStrategy());
		return strats;
	}

	/**
	 * Extends the super method by preparing repository clones before generating
	 * dynamic tests. <br>
	 * <br>
	 * {@inheritDoc}
	 */
	@TestFactory
	@Override
	public Collection<DynamicNode> createTests() {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_OVERHEAD);
		var resArr = this.cacheCommitResources().toArray(Resource[]::new);
		var tests = super.createTests(resArr);
		this.stopTimeMeasurement();
		return tests;
	}

	@Override
	protected RepoParserTestOptions getResourceTestOptions() {
		return (RepoParserTestOptions) super.getResourceTestOptions();
	}

	@Override
	protected RepoParserTestOptions initResourceTestOptions() {
		var superOpts = super.initResourceTestOptions();
		var opts = new RepoParserTestOptions();

		opts.copyOptionsFrom(superOpts);

		opts.setShouldDeleteRepositoryClones(true);
		opts.setShouldSaveCachedExpectedSimilarityResults(true);
		opts.setShouldUseCachedExpectedSimilarityResults(true);
		return opts;
	}
}
