package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import cipm.consistency.fitests.similarity.SimilarityTestLogger;
import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.parser.testfactory.AbstractJaMoPPParserSimilarityTestFactory;
import cipm.consistency.fitests.similarity.jamopp.parser.testfactory.IJaMoPPParserTestGenerationStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.DefaultTimeMeasurementDataStructure;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GSONPersistingStrategy;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ITimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurementKey;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurementKeyBuilder;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurementKeyUtil;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurer;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.StopwatchStrategy;

/**
 * An abstract test class, which can be used for implementing tests that involve
 * parsing models from Java model source files and checking their similarity.
 * <br>
 * <br>
 * It does not include any hard-coded model source file directory to allow
 * models at different locations to be parsed and used in tests. If certain
 * groups of model source file directories are to be used in tests, it is
 * recommended to make an abstract test class for them for storing their common
 * details. <br>
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
 * @see {@link #createTests()}
 */
public abstract class AbstractJaMoPPParserSimilarityTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * @see {@link #getCacheUtil()}
	 */
	private static final CacheUtil resourceCache = new CacheUtil();

	/**
	 * @see {@link #getTestFileLayout()}
	 */
	private ParserTestFileLayout layout;

	/**
	 * The parent path of all time measurement files. Used to compute
	 * {@link ParserTestFileLayout#setTimeMeasurementsFileSavePath(Path)}
	 */
	private static final Path timeMeasurementsSaveRootPath = Path.of("target", "timeMeasurements");
	/**
	 * The prefix of the directory name, under which all time measurement files from
	 * the entirety of this test run are to be saved.
	 * {@link #previousTimeMeasurementCount} is appended to the end of this to get
	 * the full directory name.
	 */
	private static final String timeMeasurementSaveFolderPrefix = "Test run - ";
	/**
	 * The amount of saved time measurement folders under
	 * {@link #timeMeasurementsSaveRootPath} from previous test runs. Used to
	 * compute {@link ParserTestFileLayout#setTimeMeasurementsFileSavePath(Path)}
	 * <p>
	 * Computed here as a static final variable, so that the current test run uses
	 * the same folder across all concrete test classes
	 */
	private static final int previousTimeMeasurementCount = timeMeasurementsSaveRootPath.toFile().list().length + 1;
	/**
	 * @see {@link ParserTestFileLayout#getTimeMeasurementFileExtension()}
	 */
	private static final String timeMeasurementFileExtension = "json";

	/**
	 * @see {@link ParserTestFileLayout#setTestModelResourceFilesSaveDirPath(Path)}
	 */
	private static final Path testModelResourceFilesSaveDirPath = Path.of("target", "testResources");

	/**
	 * @see {@link ParserTestFileLayout#setTestModelResourceFilesSaveDirPath(Path)}
	 */
	private static final Path cacheSaveDirPath = testModelResourceFilesSaveDirPath.resolve("testmodel-cache");

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserSimilarityTest}: Sets up the file layout for the
	 * test {@link #getTestFileLayout()} and the time measuring mechanism
	 * {@link #setupForTimeMeasurements()}. See
	 * {@link AbstractJaMoPPParserSimilarityTest} for more information.
	 */
	@BeforeEach
	@Override
	public void setUp() {
		SimilarityTestLogger.logDebugMsg("Setting up before parser test", this.getClass());
		this.setupForTimeMeasurements();
		ParserTestTimeMeasurer.getInstance().startTimeMeasuring();
		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_BEFOREEACH);
		super.setUp();

		this.layout = this.initParserTestFileLayout();
		ParserTestTimeMeasurementKeyUtil.setRelativizationPath(this.getTestFileLayout().getTestFilesSavePath());

		this.stopTimeMeasurement();
		SimilarityTestLogger.logDebugMsg("Set up before parser test", this.getClass());
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserSimilarityTest}: Performs various operations on
	 * model resources that were parsed in the dynamic tests, according to the
	 * preferences that are encoded in {@link #getResourceTestOptions()}, such as
	 * {@link ParserTestOptions#shouldSaveCachedModelResources()}. It then finishes
	 * time measurement taking and saves the time measurements from the current test
	 * class. See {@link AbstractJaMoPPParserSimilarityTest} for more information.
	 */
	@AfterEach
	@Override
	public void tearDown() {
		SimilarityTestLogger.logDebugMsg("Tearing down after parser test", this.getClass());

		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_AFTEREACH);
		var cachedResources = resourceCache.getCachedResources();

		if (this.getResourceTestOptions().shouldSaveCachedModelResources()) {
			SimilarityTestLogger.logDebugMsg("Saving all cached resources after parser test", this.getClass());
			cachedResources.forEach((res) -> {
				this.startTimeMeasurement(
						getTimeMeasurementKeyBuilder().withParsedModelLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedURIString(res.getModelResource().getURI())),
						GeneralTimeMeasurementTag.SAVE_MODEL_RESOURCE);
				res.saveResources();
				this.stopTimeMeasurement();
			});
			SimilarityTestLogger.logDebugMsg("Saved all cached resources after parser test", this.getClass());
		}

		if (this.getResourceTestOptions().shouldDeleteAllModelResources()) {
			SimilarityTestLogger.logDebugMsg("Deleting all cached resources after parser test", this.getClass());
			cachedResources.forEach((res) -> {
				this.startTimeMeasurement(
						getTimeMeasurementKeyBuilder().withParsedModelLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedURIString(res.getModelResource().getURI())),
						GeneralTimeMeasurementTag.DELETE_MODEL_RESOURCE);
				res.deleteResources();
				this.stopTimeMeasurement();
			});
			SimilarityTestLogger.logDebugMsg("Deleted all cached resources after parser test", this.getClass());
		} else if (this.getResourceTestOptions().shouldUnloadAllModelResources()) {
			SimilarityTestLogger.logDebugMsg("Unloading all cached resources after parser test", this.getClass());
			cachedResources.forEach((res) -> {
				this.startTimeMeasurement(
						getTimeMeasurementKeyBuilder().withParsedModelLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedURIString(res.getModelResource().getURI())),
						GeneralTimeMeasurementTag.UNLOAD_MODEL_RESOURCE);
				res.unloadResources();
				this.stopTimeMeasurement();
			});
			SimilarityTestLogger.logDebugMsg("Unloaded all cached resources after parser test", this.getClass());
		}

		if (this.getResourceTestOptions().shouldRemoveModelResourcesFromCache()) {
			SimilarityTestLogger.logDebugMsg("Removing all cached resources from cache after parser test",
					this.getClass());
			this.startTimeMeasurement(GeneralTimeMeasurementTag.MODEL_RESOURCE_CACHE_ACCESS);
			resourceCache.cleanCache();
			this.stopTimeMeasurement();
			SimilarityTestLogger.logDebugMsg("Removed all cached resources from cache after parser test",
					this.getClass());
		}

		super.tearDown();
		this.stopTimeMeasurement();
		ParserTestTimeMeasurer.getInstance().finishTimeMeasuring();

		this.saveTimeMeasurements();
		SimilarityTestLogger.logDebugMsg("Tore down after parser test", this.getClass());
	}

	/**
	 * Returns a builder for {@link ParserTestTimeMeasurementKey} instances, which
	 * should be included to time measurements to describe what they are taken from.
	 * 
	 * @return An object that can be used to construct
	 *         {@link ParserTestTimeMeasurementKey} instances
	 */
	protected ParserTestTimeMeasurementKeyBuilder getTimeMeasurementKeyBuilder() {
		return new ParserTestTimeMeasurementKeyBuilder();
	}

	/**
	 * @return Creates the value of {@link #getTestFileLayout()}
	 */
	protected ParserTestFileLayout initParserTestFileLayout() {
		var layout = new ParserTestFileLayout();
		layout.setModelSourceParentRootDirPath(new File("").getAbsoluteFile().toPath());
		layout.setTestModelResourceFilesSaveDirPath(testModelResourceFilesSaveDirPath);
		layout.setCacheSaveDirPath(cacheSaveDirPath);

		layout.setTimeMeasurementsFileSavePath(
				timeMeasurementsSaveRootPath.resolve(timeMeasurementSaveFolderPrefix + previousTimeMeasurementCount));
		layout.setTimeMeasurementFileExtension(timeMeasurementFileExtension);

		layout.setModelResourceFileExtension(this.getResourceParsingStrategy().getResourceFileExtension());

		return layout;
	}

	/**
	 * @return An object encapsulating the file layout for the test
	 */
	protected ParserTestFileLayout getTestFileLayout() {
		return layout;
	}

	/**
	 * Can be overridden in sub-classes.
	 * 
	 * @return The path, at which time measurements of the currently running test
	 *         class will be saved.
	 */
	protected Path getTimeMeasurementFileSavePathForCurrentTestClass() {
		var dateFormatInFileName = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
		var startTime = ParserTestTimeMeasurer.getInstance().getDataStructure().getStartTime();
		var endTime = ParserTestTimeMeasurer.getInstance().getDataStructure().getEndTime();

		var fileName = String.format("%s___%s-%s.%s", dateFormatInFileName.format(startTime),
				dateFormatInFileName.format(endTime), this.getCurrentTestClassName(),
				this.getTestFileLayout().getTimeMeasurementFileExtension());

		return this.getTestFileLayout().getTimeMeasurementsFileSavePath().resolve(fileName);
	}

	/**
	 * Sets up {@link ParserTestTimeMeasurer} for taking time measurements. Should
	 * be called prior to taking time measurements.
	 */
	protected void setupForTimeMeasurements() {
		ParserTestTimeMeasurer.getInstance().setDataStructure(new DefaultTimeMeasurementDataStructure());
		ParserTestTimeMeasurer.getInstance().setMeasuringStrat(new StopwatchStrategy());
		ParserTestTimeMeasurer.getInstance()
				.setPersistingStrat(new GSONPersistingStrategy(DateTimeFormatter.ISO_DATE_TIME));
	}

	/**
	 * Saves the time measurements taken during tests.
	 */
	protected void saveTimeMeasurements() {
		SimilarityTestLogger.logDebugMsg("Saving time measurements", this.getClass());
		ParserTestTimeMeasurer.getInstance().save(this.getTimeMeasurementFileSavePathForCurrentTestClass());
		ParserTestTimeMeasurer.getInstance().reset();
		SimilarityTestLogger.logDebugMsg("Saved time measurements", this.getClass());
	}

	/**
	 * Delegates to {@link ParserTestTimeMeasurer}. Refer to the documentation of
	 * {@link ParserTestTimeMeasurer} for more information. <br>
	 * <br>
	 * Complements the (so far) built {@link ParserTestTimeMeasurementKey} instance
	 * with information about this test, finishes building it and delegates it.
	 */
	protected void startTimeMeasurement(ParserTestTimeMeasurementKeyBuilder keyBuilder, ITimeMeasurementTag tag) {
		ParserTestTimeMeasurer.getInstance()
				.startTimeMeasurement(keyBuilder.withTestClassName(getCurrentTestClassName()).createKey(), tag);
	}

	/**
	 * A variant of
	 * {@link #startTimeMeasurement(ParserTestTimeMeasurementKey, ITimeMeasurementTag)}
	 * for the current concrete test class.
	 */
	protected void startTimeMeasurement(ITimeMeasurementTag tag) {
		startTimeMeasurement(getTimeMeasurementKeyBuilder(), tag);
	}

	/**
	 * Delegates to {@link ParserTestTimeMeasurer}. Refer to the documentation of
	 * {@link ParserTestTimeMeasurer} for more information.
	 */
	protected void stopTimeMeasurement() {
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
	}

	/**
	 * An object that caches and grants access to parsed models, which were cached
	 * after being parsed. <br>
	 * <br>
	 * Make sure that the {@link CacheUtil} instance persists and the same instance
	 * is used across all concrete test classes. That way, previously parsed model
	 * resource instances can be re-used.
	 * 
	 * @return A utility object, which encapsulates caching logic (for parsed
	 *         models) and can be used to hasten tests.
	 * 
	 * @see {@link #parseModelWithoutCaching(Path)}
	 * @see {@link #parseModelWithCaching(Path)}
	 */
	protected CacheUtil getCacheUtil() {
		return resourceCache;
	}

	/**
	 * Parses all model source files under the given model source file directory
	 * into a {@link Resource} instance. Uses no means of caching. <br>
	 * <br>
	 * <b>Note: This method will only parse one model from all model source files.
	 * Therefore, the given model source file directory should only belong to one
	 * model.</b>
	 * 
	 * @param modelSourceFileDirPath Path to a model source file directory that
	 *                               contains all model source files of a (and only
	 *                               one) model
	 * 
	 * @see {@link #isResourceRelevant()}
	 * @see {@link #prepareArtificialResource(Resource, URI)}
	 */
	protected IModelResourceWrapper parseModelWithoutCaching(Path modelSourceFileDirPath) {
		this.startTimeMeasurement(
				getTimeMeasurementKeyBuilder()
						.withOriginalModelLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedPathString(modelSourceFileDirPath))
						.withResourceParsingStrategyClassName(
								this.getResourceParsingStrategy().getClass().getSimpleName()),
				GeneralTimeMeasurementTag.PARSE_MODEL_RESOURCE);
		var wrapper = new JaMoPPModelResourceWrapper(this.getResourceParsingStrategy());
		wrapper.parseModelResource(modelSourceFileDirPath,
				this.getTestFileLayout().getModelResourceURI(modelSourceFileDirPath));
		this.stopTimeMeasurement();
		return wrapper;
	}

	/**
	 * A variant of {@link #parseModelWithCaching(Path, URI, String)} that uses the
	 * given path model source file directory as cache key (converts it to string
	 * via {@code path.toString()})
	 */
	protected IModelResourceWrapper parseModelWithCaching(Path modelSourceFileDirPath) {
		return this.parseModelWithCaching(modelSourceFileDirPath, modelSourceFileDirPath.toString());
	}

	/**
	 * A variant of {@link #parseModelWithCaching(Path, URI, String)} that uses
	 * {@link ParserTestFileLayout#getModelResourceURI(Path)} as cached model
	 * resource URI.
	 */
	protected IModelResourceWrapper parseModelWithCaching(Path modelSourceFileDirPath, String modelResourceCacheKey) {
		return this.parseModelWithCaching(modelSourceFileDirPath,
				this.getTestFileLayout().getModelResourceURI(modelSourceFileDirPath), modelResourceCacheKey);
	}

	/**
	 * Works similar to {@link #parseModelWithCaching(Path)}, except for the caching
	 * part: <br>
	 * <br>
	 * Checks the cache first for previously parsed model resources, if
	 * modelResourceCacheKey is not null. If a model resource from the given path
	 * model source file directory was previously parsed and cached under
	 * modelResourceCacheKey, returns the cached model resource (at
	 * modelResourceCachedURI) instead. If there were no cached model resources for
	 * the given path, adds the parsed model resource to the cache under
	 * modelResourceCacheKey.
	 */
	protected IModelResourceWrapper parseModelWithCaching(Path modelSourceFileDirPath, URI modelResourceCachedURI,
			String modelResourceCacheKey) {
		this.startTimeMeasurement(
				getTimeMeasurementKeyBuilder()
						.withOriginalModelLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedPathString(modelSourceFileDirPath))
						.withParsedModelLocation(
								ParserTestTimeMeasurementKeyUtil.getAdaptedURIString(modelResourceCachedURI)),
				GeneralTimeMeasurementTag.MODEL_RESOURCE_CACHE_ACCESS);
		var cache = this.getCacheUtil();
		var modelName = this.getDisplayNameForModelSourceFileDir(modelSourceFileDirPath);

		IModelResourceWrapper resWrapper = null;

		/*
		 * If it exists, Loading the model resource alone is sufficient, because
		 * potentially required contents that are stored externally will be
		 * automatically loaded in the background when needed.
		 */

		if (modelResourceCacheKey != null) {
			// Search for the resource in the cache
			if (cache.isInCache(modelResourceCacheKey)) {
				SimilarityTestLogger.logDebugMsg(String.format("%s is in cache, using cached version", modelName),
						this.getClass());
				resWrapper = cache.getFromCache(modelResourceCacheKey);
				if (!resWrapper.isModelResourceLoaded()) {
					this.startTimeMeasurement(
							getTimeMeasurementKeyBuilder()
									.withOriginalModelLocation(ParserTestTimeMeasurementKeyUtil
											.getAdaptedPathString(modelSourceFileDirPath))
									.withParsedModelLocation(ParserTestTimeMeasurementKeyUtil
											.getAdaptedURIString(modelResourceCachedURI)),
							GeneralTimeMeasurementTag.LOAD_MODEL_RESOURCE);
					resWrapper.loadParsedResources();
					this.stopTimeMeasurement();
				}
			}

			// Search for the resource file in cache save location
			if (resWrapper == null) {
				resWrapper = new JaMoPPModelResourceWrapper();
				this.startTimeMeasurement(
						getTimeMeasurementKeyBuilder()
								.withOriginalModelLocation(
										ParserTestTimeMeasurementKeyUtil.getAdaptedPathString(modelSourceFileDirPath))
								.withParsedModelLocation(
										ParserTestTimeMeasurementKeyUtil.getAdaptedURIString(modelResourceCachedURI)),
						GeneralTimeMeasurementTag.LOAD_MODEL_RESOURCE);
				resWrapper.loadModelResource(modelResourceCachedURI);
				this.stopTimeMeasurement();
				if (resWrapper.isModelResourceLoaded()) {
					SimilarityTestLogger.logDebugMsg(String.format("Loaded %s from its resource file", modelName),
							this.getClass());
				}
			}
		}

		// Resource is completely new, parse it from scratch
		if (resWrapper == null || !resWrapper.isModelResourceLoaded()) {
			resWrapper = this.parseModelWithoutCaching(modelSourceFileDirPath);
		}

		var key = modelResourceCacheKey != null ? modelResourceCacheKey : modelSourceFileDirPath.toString();
		cache.addToCache(key, resWrapper);
		this.stopTimeMeasurement();

		SimilarityTestLogger.logDebugMsg(String.format("%s parsed (with caching)",
				this.getDisplayNameForModelSourceFileDir(modelSourceFileDirPath)), this.getClass());
		return resWrapper;
	}

	/**
	 * @param modelSourceFileDirPath Path to the model source file directory
	 * @return The test display name for the given path
	 */
	protected String getDisplayNameForModelSourceFileDir(Path modelSourceFileDirPath) {
		var nameCount = modelSourceFileDirPath.getNameCount();

		var startIndex = nameCount > 2 ? nameCount - 2 : nameCount - 1;
		var endIndex = nameCount;

		return modelSourceFileDirPath.subpath(startIndex, endIndex).toString();
	}

	/**
	 * Defaults to using {@link #isModelSourceFileDirectoryName(String)} on the file
	 * name. Check the concrete implementation for more details.
	 * 
	 * @param f The file object representing the model source file directory
	 * 
	 * @return Whether a given (possible) model source file directory contains any
	 *         Java elements, from which a Java model can be parsed.
	 */
	protected boolean isModelSourceFileDirectory(File f) {
		return this.isModelSourceFileDirectoryName(f.getName());
	}

	/**
	 * Derives the path to the model resources from their URI. Assumes that the URI
	 * of the provided resources point at the model source file directory.
	 * 
	 * @param modelResources An array of parsed model resources
	 * @return Dynamic test instances for the given model resources
	 * @see {@link #createTests()}
	 */
	public Collection<DynamicNode> createTests(Resource[] modelResources) {
		var pathArr = new Path[modelResources.length];

		for (int i = 0; i < pathArr.length; i++) {
			pathArr[i] = Path.of(modelResources[i].getURI().path());
		}

		return this.createTests(pathArr, modelResources);
	}

	/**
	 * Parses model resources (with caching) for each given model source file
	 * directory path.
	 * 
	 * @param modelSourceFileDirPaths An array of paths to model source file
	 *                                directories
	 * @return Dynamic test instances for the models under the given paths
	 * @see {@link #createTests()}
	 */
	public Collection<DynamicNode> createTests(Path[] modelSourceFileDirPaths) {
		var resArr = new Resource[modelSourceFileDirPaths.length];

		for (int i = 0; i < resArr.length; i++) {
			resArr[i] = this.parseModelWithCaching(modelSourceFileDirPaths[i]).getModelResource();
		}

		return this.createTests(modelSourceFileDirPaths, resArr);
	}

	/**
	 * Paths to model source file directories and corresponding parsed model
	 * resources should be provided in the same order.
	 * 
	 * @param modelSourceFileDirPaths An array of paths to model source file
	 *                                directories
	 * @param modelResources          An array of parsed model resources
	 * @return Dynamic test instances for the given model resources
	 * @see {@link #createTests()}
	 */
	public Collection<DynamicNode> createTests(Path[] modelSourceFileDirPaths, Resource[] modelResources) {
		if (modelSourceFileDirPaths.length != modelResources.length) {
			Assertions.fail("Lengths of path and resource arrays do not match");
		}

		var tests = new ArrayList<DynamicNode>();

		this.getTestGenerationStrategies().forEach((testGenStrat) -> {
			this.getTestFactories().forEach((tf) -> {
				var testsForModelSourceFileDirs = new ArrayList<DynamicNode>();
				testGenStrat.getTestResourceIterator(modelResources.length).forEachRemaining((idxs) -> {
					var modelSourceFileDirPath1 = modelSourceFileDirPaths[idxs[0]];
					var modelResource1 = modelResources[idxs[0]];
					var modelSourceFileDirPath2 = modelSourceFileDirPaths[idxs[1]];
					var modelResource2 = modelResources[idxs[1]];
					testsForModelSourceFileDirs.add(tf.createTestsFor(modelResource1, modelSourceFileDirPath1,
							modelResource2, modelSourceFileDirPath2));
				});
				tests.add(DynamicContainer.dynamicContainer(String.format("%s (with %s)", tf.getTestDescription(),
						testGenStrat.getTestGenerationStrategyDescription()), testsForModelSourceFileDirs));
			});
		});

		return tests;
	}

	/**
	 * Generates dynamic tests for each model source file directory based on the
	 * registered {@link AbstractJaMoPPParserSimilarityTestFactory} instances.
	 * Implemented here in efforts to have a unified template for dynamic test
	 * generation. <br>
	 * <br>
	 * <b>Can be overridden in implementors; in order to add preparatory actions,
	 * clean up actions or to change the default test generation. <i> DUE TO HOW
	 * JUNIT WORKS, GENERATED TESTS WILL NOT REGISTER UNLESS ANNOTED AS
	 * {@code TestFactory} IN OVERRIDING VERSIONS TOO. </i></b> <br>
	 * <br>
	 * Unless overridden in implementors, JUnit will detect this method as a
	 * {@link TestFactory}, which will run the tests generated here.
	 * 
	 * @see {@link #discoverModelSourceFileDirsAt(Path)} and
	 *      {@link #discoverModelSourceParentDirsAt(Path)} for finding models to
	 *      parse
	 * @see {@link TestFactory} for what tests are to be generated
	 */
	@TestFactory
	public Collection<DynamicNode> createTests() {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.DYNAMIC_TEST_CREATION);

		var modelSourceParentRootDirPath = this.getTestFileLayout().getModelSourceParentRootDirPath();

		var tests = new ArrayList<DynamicNode>();

		var modelSourceParentDirs = this.discoverModelSourceParentDirsAt(modelSourceParentRootDirPath);
		var modelSourceFileDirMap = new HashMap<Path, Collection<Path>>();

		for (var modelSourceParentDir : modelSourceParentDirs) {
			modelSourceFileDirMap.put(modelSourceParentDir, this.discoverModelSourceFileDirsAt(modelSourceParentDir));
		}

		var testsForModelSourceParentDirs = new ArrayList<DynamicNode>();
		modelSourceParentDirs.forEach((md) -> {
			final var modelSourceFileDirs = modelSourceFileDirMap.get(md);

			var testsForModelSourceFileDirs = this.createTests(modelSourceFileDirs.toArray(Path[]::new));

			testsForModelSourceParentDirs.add(DynamicContainer.dynamicContainer(
					String.format("model = %s", this.getTestFileLayout().getRelativeModelSourceParentDirPath(md)),
					testsForModelSourceFileDirs));
		});

		tests.add(DynamicContainer.dynamicContainer(
				String.format("root = %s", this.getTestFileLayout().getRelativeModelSourceParentRootDirPath()),
				testsForModelSourceParentDirs));

		this.stopTimeMeasurement();
		return tests;
	}

	/**
	 * Can be used to determine what kind of parser tests are to be generated in
	 * concrete implementors.
	 * 
	 * @return Factories of tests that should be generated for each relevant model
	 *         source file directories.
	 */
	protected abstract Collection<AbstractJaMoPPParserSimilarityTestFactory> getTestFactories();

	/**
	 * @param modelSourceParentDirPath A potential model source parent directory
	 *                                 path, whose contents should be scanned for
	 *                                 model source file directories
	 * @return A collection of model source file directory paths under
	 *         modelSourceParentDirPath
	 */
	protected Collection<Path> discoverModelSourceFileDirsAt(Path modelSourceParentDirPath) {
		var modelDiscoveryStrat = new ModelDiscoveryStrategy((f) -> this.isModelSourceFileDirectory(f));
		this.startTimeMeasurement(
				getTimeMeasurementKeyBuilder()
						.withModelDiscoveryPath(
								ParserTestTimeMeasurementKeyUtil.getAdaptedPathString(modelSourceParentDirPath))
						.withModelDiscoveryClassName(modelDiscoveryStrat.getClass().getSimpleName()),
				GeneralTimeMeasurementTag.DISCOVER_MODEL_RESOURCES);
		var result = modelDiscoveryStrat.discoverModelSourceFileDirs(modelSourceParentDirPath.toFile());
		this.stopTimeMeasurement();
		return result;
	}

	/**
	 * @param modelSourceParentRootPath The top-most directory, whose contents
	 *                                  should be scanned for directories containing
	 *                                  model source parent directories.
	 * @return A collection of paths of model source parent directories under
	 *         modelSourceParentRootPath
	 */
	protected Collection<Path> discoverModelSourceParentDirsAt(Path modelSourceParentRootPath) {
		var modelDiscoveryStrat = new ModelDiscoveryStrategy((f) -> this.isModelSourceFileDirectory(f));
		this.startTimeMeasurement(
				getTimeMeasurementKeyBuilder()
						.withModelDiscoveryPath(
								ParserTestTimeMeasurementKeyUtil.getAdaptedPathString(modelSourceParentRootPath))
						.withModelDiscoveryClassName(modelDiscoveryStrat.getClass().getSimpleName()),
				GeneralTimeMeasurementTag.DISCOVER_MODEL_RESOURCES);
		var result = modelDiscoveryStrat.discoverModelSourceParentDirs(modelSourceParentRootPath.toFile());
		this.stopTimeMeasurement();
		return result;
	}

	/**
	 * Check the concrete implementation for more details.
	 * 
	 * @param dirName The name of the potential model source file directory
	 * 
	 * @return Whether a given directory contains any Java elements, from which a
	 *         Java model can be parsed.
	 */
	protected abstract boolean isModelSourceFileDirectoryName(String dirName);

	/**
	 * Override to define how to iterate through model resources, while generating
	 * dynamic tests. One or more test generation strategies can be provided.
	 * Dynamic tests will be generated for each strategy in the returned collection,
	 * regardless of what dynamic tests where generated previously. <br>
	 * <br>
	 * This method allows splitting dynamic test generation; with respect to what
	 * model resources will be compared to which ones, and in what order.
	 * 
	 * @return A collection of test generation strategies, which encapsulate how
	 *         model resources are iterated and what dynamic tests are generated.
	 */
	protected abstract Collection<IJaMoPPParserTestGenerationStrategy> getTestGenerationStrategies();

	/**
	 * Defaults to true. <br>
	 * <br>
	 * Can be overridden in implementors, if necessary.
	 * 
	 * @return Whether the order of model resource contents (i.e. all EObject
	 *         instances nested directly or indirectly within) matters and should be
	 *         accounted for in the expected results.
	 */
	public boolean doesContentOrderMatter() {
		return true;
	}

	@Override
	protected ParserTestOptions initResourceTestOptions() {
		var opts = new ParserTestOptions();

		/*
		 * Parser tests require the created resource files to persist across tests, as
		 * they are cached.
		 */
		opts.setShouldDeleteAllModelResources(false);
		opts.setShouldUnloadAllModelResources(false);

		opts.setShouldSaveCachedModelResources(true);
		opts.setShouldRemoveModelResourcesFromCache(false);
		return opts;
	}

	@Override
	protected ParserTestOptions getResourceTestOptions() {
		return (ParserTestOptions) super.getResourceTestOptions();
	}
}
