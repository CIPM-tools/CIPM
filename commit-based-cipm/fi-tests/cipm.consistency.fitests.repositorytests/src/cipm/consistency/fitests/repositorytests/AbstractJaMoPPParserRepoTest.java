package cipm.consistency.fitests.repositorytests;

import cipm.consistency.fitests.repositorytests.util.RepoCacheSimilarityResultProvider;
import cipm.consistency.fitests.repositorytests.util.RepoTestSimilarityResultCache;
import cipm.consistency.fitests.repositorytests.util.RepoTestSimilarityValueEstimator;
import cipm.consistency.fitests.similarity.SimilarityTestLogger;
import cipm.consistency.fitests.similarity.eobject.ResourceHelper;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPResourceParsingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.AbstractJaMoPPParserSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.parser.FileUtil;
import cipm.consistency.fitests.similarity.jamopp.parser.IModelResourceWrapper;
import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.IExpectedSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.testfactory.IJaMoPPParserTestGenerationStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.testfactory.ReflexiveSymmetricIterationTestGenerationStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ITimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurementKeyBuilder;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurementKeyUtil;
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
 * parsing models from GIT repositories and checking their similarity. <br>
 * <br>
 * Here, model source file directories and model source parent directories are
 * both equal to the local repository clone directory. This means, all Java
 * models are assumed to be stored in their respective repository clones. <br>
 * <br>
 * Note: Since dynamic tests are used here, the
 * {@link org.junit.jupiter.api.BeforeEach} and
 * {@link org.junit.jupiter.api.AfterEach} methods will be triggered <b><i> only
 * once at the start / end of each test method annotated with
 * {@link org.junit.jupiter.api.TestFactory} </i></b>, as opposed to before /
 * after each dynamic test. In that sense, they are similar to their static
 * versions {@link org.junit.jupiter.api.BeforeAll} and
 * {@link org.junit.jupiter.api.AfterAll} method.
 * 
 * @author Alp Torac Genc
 * 
 * @see {@link AbstractJaMoPPParserSimilarityTest#createTests()}
 */
public abstract class AbstractJaMoPPParserRepoTest extends AbstractJaMoPPParserSimilarityTest {
	/**
	 * Contains expected similarity results needed by tests
	 */
	private static RepoTestSimilarityResultCache similarityResultCache = new RepoTestSimilarityResultCache();

	/**
	 * The pattern of "gradle-wrapper.jar" file path, which should be excluded when
	 * parsing Java model resources to avoid IOExceptions.
	 */
	private static final String gradleWrapperJarPathPattern = ".*?/gradle-wrapper\\.jar";

	/**
	 * @see {@link RepoParserTestFileLayout#setRepoCloneRootDirName(String)}
	 */
	private static final String repoCloneRootDirName = "repo-clones";

	/**
	 * The segment in remote GIT repository URLs, which are followed by the commit
	 * hash
	 */
	private static final String repoURICommitSegment = "commit";

	/**
	 * @see {@link RepoParserTestFileLayout#setExpectedSimilarityResultCacheDirName(String)}
	 */
	private static final String expectedSimilarityResultCacheDirName = "results-cache";

	/**
	 * @see {@link RepoParserTestFileLayout#setExpectedSimilarityResultCacheFileName(String)}
	 */
	private static final String expectedSimilarityResultCacheFileName = "resultsCache.json";

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserRepoTest}: Loads expected similarity results
	 * needed by tests, if their file exists. See
	 * {@link AbstractJaMoPPParserRepoTest} for more information.
	 */
	@BeforeEach
	@Override
	public void setUp() {
		super.setUp();

		var resultCachePath = this.getTestFileLayout().getExpectedSimilarityResultCachePath();
		if (this.getResourceTestOptions().shouldUseCachedExpectedSimilarityResults()) {
			this.startTimeMeasurement(RepoTimeMeasurementTag.LOAD_EXPECTED_SIMILARITY_RESULTS);
			SimilarityTestLogger
					.logDebugMsg(String.format("Checking for cached expected similarity results for %s at %s",
							this.getCurrentTestClassName(), resultCachePath), this.getClass());
			if (resultCachePath.toFile().exists()) {
				SimilarityTestLogger.logDebugMsg(String.format("Cached expected similarity results exist"),
						this.getClass());
				try (BufferedReader reader = Files.newBufferedReader(resultCachePath)) {
					SimilarityTestLogger.logDebugMsg(String.format("Reading cached expected similarity results"),
							this.getClass());
					similarityResultCache = new RepoTestSimilarityResultCache(
							new Gson().fromJson(reader, similarityResultCache.getClass()));
					SimilarityTestLogger.logDebugMsg(String.format("Read cached expected similarity results"),
							this.getClass());
				} catch (IOException e) {
					SimilarityTestLogger
							.logDebugMsg(String.format("Could not read cached expected similarity results for %s at %s",
									this.getCurrentTestClassName(), resultCachePath), this.getClass());
				}
			}
			this.stopTimeMeasurement();
		} else {
			SimilarityTestLogger.logDebugMsg(String.format("No saved expected similarity results found for %s at %s",
					this.getCurrentTestClassName(), resultCachePath), this.getClass());
		}
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserRepoTest}: Saves the computed expected similarity
	 * results needed by tests and deletes the local repository clone, if desired.
	 * See {@link AbstractJaMoPPParserRepoTest} for more information.
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

			SimilarityTestLogger.logDebugMsg(String.format("Saving cached expected similarity results for %s at %s",
					this.getCurrentTestClassName(), resultCachePath), this.getClass());

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
				gson.toJson(similarityResultCache, similarityResultCache.getClass(), gsonWriter);
			} catch (IOException e) {
				Assertions.fail(String.format("Could not save the expected similarity results at %s", resultCachePath),
						e);
			}

			SimilarityTestLogger.logDebugMsg(String.format("Saved cached expected similarity results for %s at %s",
					this.getCurrentTestClassName(), resultCachePath), this.getClass());
			this.stopTimeMeasurement();
		}

		var localRepoPath = this.getTestFileLayout().getModelSourceParentRootDirPath();
		if (this.getResourceTestOptions().shouldDeleteRepositoryClones() && localRepoPath.toFile().exists()) {
			this.startTimeMeasurement(RepoTimeMeasurementTag.DELETE_LOCAL_REPO_CLONE);
			FileUtil.deleteAll(localRepoPath);
			this.stopTimeMeasurement();
		}

		this.stopTimeMeasurement();
		super.tearDown();
	}

	@Override
	protected void startTimeMeasurement(ParserTestTimeMeasurementKeyBuilder keyBuilder, ITimeMeasurementTag tag) {
		super.startTimeMeasurement(
				keyBuilder.withRepositoryName(getRepoName()).withRepositoryURI(this.getRepoURI().toString()), tag);
	}

	@Override
	protected RepoParserTestFileLayout initParserTestFileLayout() {
		var parserTestLayout = super.initParserTestFileLayout();
		var layout = new RepoParserTestFileLayout(parserTestLayout);
		layout.setRepoName(this.getRepoName());
		layout.setExpectedSimilarityResultCacheDirName(expectedSimilarityResultCacheDirName);
		layout.setExpectedSimilarityResultCacheFileName(expectedSimilarityResultCacheFileName);
		layout.setRepoCloneRootDirName(repoCloneRootDirName);
		return layout;
	}

	@Override
	protected RepoParserTestFileLayout getTestFileLayout() {
		return (RepoParserTestFileLayout) super.getTestFileLayout();
	}

	/**
	 * Refer to {@link RepoTestSimilarityResultCache} for more information on
	 * expected similarity results.
	 * 
	 * @return The expected similarity checking result for the given commits.
	 */
	protected Boolean getExpectedResult(String lhsCommit, String rhsCommit) {
		return similarityResultCache.getResult(lhsCommit, rhsCommit);
	}

	/**
	 * Prepares model resources and expected similarity results needed by tests and
	 * caches them. Must be executed before dynamic tests.
	 * 
	 * @return The cached model resources parsed from {@link #getCommitIDs()}
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
			if (similarityResultCache.getResult(commitID1, commitID2) == null) {
				SimilarityTestLogger.logDebugMsg(
						String.format("Expected similarity result missing for: %s vs %s", commitID1, commitID2),
						this.getClass());
				expectedResultsExist[0] = false;
				// Check for the other ones as well, for debugging purposes
			}
		}));

		for (var cID : commitIDList) {
			if (!ResourceHelper.resourceFileExists(this.getTestFileLayout().getModelResourceSaveURIForCommit(cID))) {
				SimilarityTestLogger.logDebugMsg(String.format("Model resource missing for: %s", cID), this.getClass());
				commitResourcesExist = false;
				// Check for the other ones as well, for debugging purposes
			}
		}

		Git git = null;

		if (!expectedResultsExist[0] || !commitResourcesExist) {
			SimilarityTestLogger.logDebugMsg(
					"Remote repository must be cloned due to missing resources / expected similarity results",
					this.getClass());
			git = this.cloneRepo();
		}

		if (!expectedResultsExist[0]) {
			SimilarityTestLogger.logDebugMsg(String.format("Computing missing expected similarity results"),
					this.getClass());

			this.computeExpectedSimilarityResults(git, commitIDList);

			SimilarityTestLogger.logDebugMsg(String.format("Computed missing similarity results"), this.getClass());
		}

		if (!commitResourcesExist) {
			SimilarityTestLogger.logDebugMsg(String.format("Preparing missing model resources"), this.getClass());

			commitResources.addAll(this.prepareReposForCommits(commitIDList, git));

			SimilarityTestLogger.logDebugMsg(String.format("Prepared missing model resources"), this.getClass());
		} else {
			for (var cID : commitIDList) {
				var cachedCommitURI = this.getTestFileLayout().getModelResourceSaveURIForCommit(cID);
				var res = new JaMoPPModelResourceWrapper();
				this.startTimeMeasurement(
						getTimeMeasurementKeyBuilder().withModelResourceLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedURIString(cachedCommitURI)),
						GeneralTimeMeasurementTag.LOAD_MODEL_RESOURCE);
				res.loadModelResource(cachedCommitURI);
				this.stopTimeMeasurement();
				this.getCacheUtil().addToCache(cachedCommitURI.toString(), res);
				commitResources.add(this.getCacheUtil().getFromCache(cachedCommitURI.toString()).getModelResource());
			}
		}

		if (git != null) {
			SimilarityTestLogger.logDebugMsg("Closing repository wrapper", this.getClass());

			this.startTimeMeasurement(RepoTimeMeasurementTag.CLOSE_REPOSITORY);
			git.getRepository().close();
			git.close();
			this.stopTimeMeasurement();

			SimilarityTestLogger.logDebugMsg(String.format("Closed repository wrapper"), this.getClass());
		}

		var mainLocalClonePath = this.getTestFileLayout().getModelSourceParentRootDirPath();

		SimilarityTestLogger.logDebugMsg(
				String.format("Cleaning main local repository clone under: %s", mainLocalClonePath.toString()),
				this.getClass());

		this.startTimeMeasurement(RepoTimeMeasurementTag.DELETE_LOCAL_REPO_CLONE);
		FileUtil.deleteAll(mainLocalClonePath);
		this.stopTimeMeasurement();

		SimilarityTestLogger.logDebugMsg("Cleaned main local repository clone", this.getClass());

		SimilarityTestLogger.logDebugMsg(String.format("Repository model resources are cached"), this.getClass());

		return commitResources;
	}

	/**
	 * Computes and caches expected similarity results necessary for the tests.
	 * 
	 * @param git          The GIT object associated with the in-memory
	 *                     representation of the GIT repository
	 * @param commitIDList A list of commit hashes, for which expected similarity
	 *                     results should be computed.
	 */
	protected void computeExpectedSimilarityResults(Git git, List<String> commitIDList) {
		var testStrats = this.getTestGenerationStrategies();
		var expectedValueEstimator = new RepoTestSimilarityValueEstimator();

		/*
		 * Determine whether all required expected results are in the cache based on
		 * what commits are compared to one another in the tests
		 */
		testStrats.forEach((ts) -> ts.getTestResourceIterator(commitIDList.size()).forEachRemaining((idxs) -> {
			var commitID1 = commitIDList.get(idxs[0]);
			var commitID2 = commitIDList.get(idxs[1]);
			if (similarityResultCache.getResult(commitID1, commitID2) == null) {
				SimilarityTestLogger.logDebugMsg(
						String.format("Computing expected similarity result for: %s vs %s", commitID1, commitID2),
						this.getClass());
				this.startTimeMeasurement(
						getTimeMeasurementKeyBuilder().withLeftCommitID(commitID1).withRightCommitID(commitID2),
						GeneralTimeMeasurementTag.EXPECTED_SIMILARITY_RESULT_COMPUTATION);
				var result = expectedValueEstimator.getExpectedSimilarityValueFor(git, commitID1, commitID2);

				similarityResultCache.addResult(commitID1, commitID2, result);
				this.stopTimeMeasurement();

				SimilarityTestLogger.logDebugMsg(String.format("Computed expected similarity result (%s) for: %s vs %s",
						result, commitID1, commitID2), this.getClass());
			}
		}));
	}

	/**
	 * Clones the repository desired by this test.
	 * 
	 * @param repoToCloneURI URI to the (remote) repository, which should be locally
	 *                       cloned
	 * @param repoClonePath  The path to the folder, where the repository clone will
	 *                       reside
	 * 
	 * @return An object that can be used to perform GIT operations on the
	 *         repository clone.
	 */
	protected Git cloneRepo(String repoToCloneURI, Path repoClonePath) {
		this.startTimeMeasurement(RepoTimeMeasurementTag.CLONE_REPOSITORY);
		Git git = null;

		// Repository clone does not exist, clone it
		if (!repoClonePath.toFile().exists() || repoClonePath.toFile().list() == null
				|| repoClonePath.toFile().list().length == 0) {
			try {
				SimilarityTestLogger.logDebugMsg(String.format("Cloning remote repository (%s) to: %s", repoToCloneURI,
						repoClonePath.toString()), this.getClass());
				git = Git.cloneRepository().setURI(repoToCloneURI).setDirectory(repoClonePath.toFile())
						.setCloneAllBranches(true).call();
				SimilarityTestLogger.logDebugMsg(String.format("Cloning successful"), this.getClass());
			} catch (GitAPIException e) {
				e.printStackTrace();
				Assertions.fail("Could not clone repository");
			}
		}
		// Repository clone folder exists, try to open it
		// If it does not open, delete it and re-try
		else {
			try {
				git = Git.open(repoClonePath.toFile());
			} catch (IOException e) {
				// Faulty repository clone, delete and re-try
				SimilarityTestLogger.logDebugMsg("Could not open existing repository, deleting it and re-cloning",
						this.getClass());
				FileUtil.deleteAll(repoClonePath);
				if (repoClonePath.toFile().exists() && repoClonePath.toFile().list().length != 0) {
					throw new IllegalStateException("Could not delete faulty repository clone");
				}
				git = this.cloneRepo(repoToCloneURI, repoClonePath);
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
		return this.cloneRepo(this.getRepoURI().toString(), this.getTestFileLayout().getModelSourceParentRootDirPath());
	}

	/**
	 * @return The cache key for the model resource parsed from the given commit
	 *         hash of the repository, when its model resource is inserted into the
	 *         cache via {@link #parseModelWithCaching(Path, URI, String)}.
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
	 *                   the local repository clone, which should be created with
	 *                   {@link #cloneRepo()}.
	 */
	protected Collection<Resource> prepareReposForCommits(List<String> commits, Git git) {

		var commitResources = new ArrayList<Resource>();

		// Checkout and copy local repository clone for each
		// commit except the last one. For the last one, just checkout to that commit to
		// spare 1 copy operation
		SimilarityTestLogger.logDebugMsg("Caching model resources for commits", this.getClass());
		var commitCount = commits.size();
		for (int i = 0; i < commitCount; i++) {
			var commitID = commits.get(i);
			var commitResURI = this.getTestFileLayout().getModelResourceSaveURIForCommit(commitID);

			SimilarityTestLogger.logDebugMsg(String.format("Checking out: %s", commitID), this.getClass());

			this.startTimeMeasurement(getTimeMeasurementKeyBuilder().withCommitID(commitID),
					RepoTimeMeasurementTag.CHECKOUT_TO_COMMIT);
			try {
				git.checkout().setName(commitID).call();
			} catch (GitAPIException e) {
				SimilarityTestLogger.logDebugMsg(String.format("Error while checking out: %s", commitID),
						this.getClass());
				throw new IllegalArgumentException(e);
			}
			this.stopTimeMeasurement();

			SimilarityTestLogger.logDebugMsg(String.format("Checked out: %s", commitID), this.getClass());

			SimilarityTestLogger.logDebugMsg(String.format("Caching resource for: %s", commitID), this.getClass());

			var targetPath = this.getTestFileLayout().getRepoClonePathForCommit(commitID);
			IModelResourceWrapper commitRes = null;

			/*
			 * Load the cached model resource for the commit, if it exists. Otherwise parse
			 * it.
			 */
			if (targetPath.toFile().exists()) {
				commitRes = this.parseModelWithCaching(targetPath, commitResURI,
						getCacheKeyForCommit(this.getRepoURI(), commitID));
			} else {
				commitRes = this.parseModelWithCaching(git.getRepository().getDirectory().getParentFile().toPath(),
						commitResURI, getCacheKeyForCommit(this.getRepoURI(), commitID));
				commitRes.setModelResourcesURI(commitResURI);
			}

			commitResources.add(commitRes.getModelResource());
			SimilarityTestLogger.logDebugMsg(String.format("Cached resource for: %s", commitID), this.getClass());
		}
		SimilarityTestLogger.logDebugMsg(String.format("Prepared model resources for commits"), this.getClass());
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
	 * @return The URI to the (remote) repository, which will be locally cloned and
	 *         used in tests.
	 */
	protected abstract URI getRepoURI();

	/**
	 * @return The name of the (remote) repository, which will be locally cloned and
	 *         used in tests.
	 */
	protected String getRepoName() {
		return this.getRepoURI().lastSegment();
	}

	/**
	 * @return An object that provides expected similarity results for model
	 *         resources parsed from commits in tests.
	 */
	protected IExpectedSimilarityResultProvider getExpectedSimilarityResultProviderForCommits() {
		return new RepoCacheSimilarityResultProvider(similarityResultCache);
	}

	@Override
	protected Collection<IJaMoPPParserTestGenerationStrategy> getTestGenerationStrategies() {
		var strats = new ArrayList<IJaMoPPParserTestGenerationStrategy>();
		strats.add(new ReflexiveSymmetricIterationTestGenerationStrategy());
		return strats;
	}

	/**
	 * Extends the super method by parsing and caching model resources from the
	 * local repository clone before generating dynamic tests. <br>
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

		opts.setShouldDeleteRepositoryClones(false);
		opts.setShouldSaveCachedExpectedSimilarityResults(true);
		opts.setShouldUseCachedExpectedSimilarityResults(true);
		return opts;
	}
}
