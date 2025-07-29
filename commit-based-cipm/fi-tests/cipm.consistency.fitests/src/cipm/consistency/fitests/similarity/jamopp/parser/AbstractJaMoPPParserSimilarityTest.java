package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;
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

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

/**
 * An abstract test class, which can be used for implementing tests that involve
 * parsing models from Java-related files and checking their similarity. <br>
 * <br>
 * It does not include any hard-coded model source file directory to allow
 * models at different locations to be usable in tests. If there is a group of
 * model source file directories, it is recommended to make an abstract test
 * class for them, in order to store details about the common sub-path of those
 * models and details on what directories contain model source files.
 * 
 * @author Alp Torac Genc
 * 
 * @see {@link #createTests()}
 */
public abstract class AbstractJaMoPPParserSimilarityTest extends AbstractJaMoPPSimilarityTest {

	/**
	 * An object that caches and grants access to the parsed models, which were
	 * cached after being parsed. <br>
	 * <br>
	 * Make sure that it persists throughout tests, which are supposed to make use
	 * of it.
	 * 
	 * @see {@link #parseModelsDirWithoutCaching(Path)}
	 * @see {@link #parseModelsDirWithCaching(Path)}
	 */
	private static final CacheUtil resourceCache = new CacheUtil();

	private ParserTestFileLayout layout;

	/**
	 * The relative path to the directory, where parsed model resource files are to
	 * be saved (if desired).
	 */
	private static final Path testModelResourceFilesSaveDirPath = Path.of("target", "testResources");

	/**
	 * The relative path to the directory, where the contents of
	 * {@link #resourceCache} are to be saved (if desired).
	 */
	private static final Path cacheSaveDirPath = testModelResourceFilesSaveDirPath.resolve("testmodel-cache");

	/**
	 * The relative path to the directory, where time measurements are to be saved
	 * (if desired).
	 */
	private static final Path timeMeasurementsFileSavePath = Path.of("target", "timeMeasurements");

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserSimilarityTest}: Sets up the file layout
	 * {@link ParserTestFileLayout}
	 */
	@BeforeEach
	@Override
	public void setUp() {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_BEFOREEACH);
		super.setUp();

		this.layout = this.initParserTestFileLayout();

		this.stopTimeMeasurement();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractJaMoPPParserSimilarityTest}: Performs various operations on
	 * the model resource files that were parsed in the dynamic tests, according to
	 * the preferences that are encoded in the methods of this test, such as
	 * {@link AbstractJaMoPPParserSimilarityTest#shouldSaveCachedResources()}. It
	 * then saves the time measurements taken during the tests. <b><i>Note: Since
	 * dynamic tests are used here, this method will be triggered only once at the
	 * end of each test method annotated with
	 * {@link org.junit.jupiter.api.TestFactory}, as opposed to after each dynamic
	 * test. </i></b>
	 */
	@AfterEach
	@Override
	public void tearDown() {
		this.logDebugMsg("Tearing down after parser test");

		this.startTimeMeasurement(GeneralTimeMeasurementTag.TEST_AFTEREACH);
		var cachedResources = resourceCache.getCachedResources();

		if (this.getResourceTestOptions().shouldSaveCachedResources()) {
			this.logDebugMsg("Saving all cached resources after parser test");
			this.startTimeMeasurement(GeneralTimeMeasurementTag.SAVE_MODEL_RESOURCE);
			cachedResources.forEach((res) -> res.saveResources());
			this.stopTimeMeasurement();
			this.logDebugMsg("Saved all cached resources after parser test");
		}

		if (this.getResourceTestOptions().shouldDeleteAllResources()) {
			this.logDebugMsg("Deleting all cached resources after parser test");
			this.startTimeMeasurement(GeneralTimeMeasurementTag.DELETE_MODEL_RESOURCE);
			cachedResources.forEach((res) -> res.deleteResources());
			this.stopTimeMeasurement();
			this.logDebugMsg("Deleted all cached resources after parser test");
		} else if (this.getResourceTestOptions().shouldUnloadAllResources()) {
			this.logDebugMsg("Unloading all cached resources after parser test");
			this.startTimeMeasurement(GeneralTimeMeasurementTag.UNLOAD_MODEL_RESOURCE);
			cachedResources.forEach((res) -> res.unloadResources());
			this.stopTimeMeasurement();
			this.logDebugMsg("Unloaded all cached resources after parser test");
		}

		if (this.getResourceTestOptions().shouldRemoveResourcesFromCache()) {
			this.logDebugMsg("Removing all cached resources from cache after parser test");
			this.startTimeMeasurement(GeneralTimeMeasurementTag.MODEL_RESOURCE_CACHE_ACCESS);
			resourceCache.cleanCache();
			this.stopTimeMeasurement();
			this.logDebugMsg("Removed all cached resources from cache after parser test");
		}

		super.tearDown();
		this.stopTimeMeasurement();

		this.saveTimeMeasurements();

		this.logDebugMsg("Tore down after parser test");
	}

	protected ParserTestFileLayout initParserTestFileLayout() {
		var layout = new ParserTestFileLayout();
		layout.setModelSourceFileRootDirPath(new File("").getAbsoluteFile().toPath());
		layout.setTestModelResourceFilesSaveDirPath(testModelResourceFilesSaveDirPath);
		layout.setCacheSaveDirPath(cacheSaveDirPath);
		layout.setTimeMeasurementsFileSavePath(timeMeasurementsFileSavePath);
		layout.setModelResourceFileExtension(this.getResourceFileExtension());
		return layout;
	}

	protected ParserTestFileLayout getTestFileLayout() {
		return this.layout;
	}

	/**
	 * Saves the time measurements taken during tests via
	 * {@code startTimeMeasurement} and {@code stopTimeMeasurement} calls.
	 */
	protected void saveTimeMeasurements() {
		this.logDebugMsg("Saving time measurements");
		ParserTestTimeMeasurer.getInstance().save(this.layout.getTimeMeasurementsFileSavePath());
		this.logDebugMsg("Saved time measurements");
	}

	/**
	 * A variant of {@link #startTimeMeasurement(String, ITimeMeasurementTag)} for
	 * individual model source file directories.
	 */
	protected void startTimeMeasurement(Path modelDir, ITimeMeasurementTag tag) {
		this.startTimeMeasurement(modelDir != null ? this.layout.getCacheKeyForModelSourceFileDir(modelDir) : null,
				tag);
	}

	/**
	 * Delegates to the time measuring mechanism and signals that a time measurement
	 * with the given parameters should be started. <br>
	 * <br>
	 * Refer to the documentation of {@link ParserTestTimeMeasurer} for more
	 * information.
	 * 
	 * @param key The key of the taken time measurement, which describes what the
	 *            time measurement is taken from
	 * @param tag The tag of the time measurement, which is used to group time
	 *            measurements
	 */
	protected void startTimeMeasurement(String key, ITimeMeasurementTag tag) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(key, tag);
	}

	/**
	 * A variant of {@link #startTimeMeasurement(String, ITimeMeasurementTag)} for
	 * the current concrete test class.
	 */
	protected void startTimeMeasurement(ITimeMeasurementTag tag) {
		this.startTimeMeasurement(this.getCurrentTestClassName(), tag);
	}

	/**
	 * Delegates to the time measuring mechanism and signals that the most recently
	 * started time measurement (via
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}) should be
	 * stopped. <br>
	 * <br>
	 * This method is to be seen as the closing bracket for the opening bracket
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}, such that the
	 * time elapsed while executing the lines between that method call and this
	 * method call is the time measurement. Not using them similar to brackets will
	 * result in inaccurate measurements. <br>
	 * <br>
	 * Refer to the documentation of {@link ParserTestTimeMeasurer} for more
	 * information.
	 */
	protected void stopTimeMeasurement() {
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
	}

	/**
	 * @return A utility object that can be used to perform file operations.
	 */
	protected FileUtil getFileUtil() {
		return new FileUtil();
	}

	/**
	 * @return A utility object, which encapsulates caching logic (for parsed
	 *         models) and can be used to hasten tests.
	 */
	protected CacheUtil getCacheUtil() {
		return resourceCache;
	}

	/**
	 * Parses all Java-Model files under the given directory into a {@link Resource}
	 * instance. Uses no means of caching. <br>
	 * <br>
	 * <b>Note: This method will parse ALL such files. Therefore, the given model
	 * directory should only contain one Java-Model.</b>
	 * 
	 * @param modelDir A directory that contains all files of a model
	 * 
	 * @see {@link #isResourceRelevant()}
	 * @see {@link #prepareArtificialResource(Resource, URI)}
	 */
	protected IModelResourceWrapper parseModelsDirWithoutCaching(Path modelDir) {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.PARSE_MODEL_RESOURCE);
		var wrapper = new JaMoPPModelResourceWrapper(this.getResourceHelper(), this.getResourceParsingStrategy());
		wrapper.parseModelResource(modelDir, this.layout.getModelResourceURI(modelDir));
		this.stopTimeMeasurement();
		return wrapper;
	}

	/**
	 * A variant of {@link #parseModelsDirWithCaching(Path, URI, String)} that uses
	 * the given path as cache key (converts it to string via
	 * {@code path.toString()})
	 */
	protected IModelResourceWrapper parseModelsDirWithCaching(Path modelDir) {
		return this.parseModelsDirWithCaching(modelDir, modelDir.toString());
	}

	/**
	 * A variant of {@link #parseModelsDirWithCaching(Path, URI, String)} that uses
	 * {@code this.getModelResourceURI(modelDir)} as cached model URI.
	 */
	protected IModelResourceWrapper parseModelsDirWithCaching(Path modelDir, String cacheKey) {
		return this.parseModelsDirWithCaching(modelDir, this.layout.getModelResourceURI(modelDir), cacheKey);
	}

	/**
	 * Works similar to {@link #parseModelsDirWithCaching(Path)}, except for the
	 * caching part: <br>
	 * <br>
	 * Checks the cache first for previously parsed model resources, if cacheKey is
	 * not null. If a model resource from the given path was previously parsed and
	 * cached under cacheKey, returns the cached model resource (at cachedModelURI)
	 * instead. If there were no cached model resources for the given path, adds the
	 * parsed model resource to the cache under cacheKey.
	 */
	protected IModelResourceWrapper parseModelsDirWithCaching(Path modelDir, URI cachedModelURI, String cacheKey) {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.MODEL_RESOURCE_CACHE_ACCESS);
		var cache = this.getCacheUtil();
		var modelName = this.getDisplayNameForModelDir(modelDir);

		IModelResourceWrapper resWrapper = null;

		/*
		 * If it exists, Loading the model resource alone is sufficient, because
		 * potentially required contents that are stored externally will be
		 * automatically loaded in the background when needed.
		 */

		if (cacheKey != null) {
			// Search for the resource in the cache
			if (cache.isInCache(cacheKey)) {
				this.logDebugMsg(String.format("%s is in cache, using cached version", modelName));
				resWrapper = cache.getFromCache(cacheKey);
				if (!resWrapper.isModelResourceLoaded()) {
					this.startTimeMeasurement(GeneralTimeMeasurementTag.LOAD_MODEL_RESOURCE);
					resWrapper.loadParsedResources();
					this.stopTimeMeasurement();
				}
			}

			// Search for the resource file in cache save location
			if (resWrapper == null) {
				resWrapper = new JaMoPPModelResourceWrapper(this.getResourceHelper());
				this.startTimeMeasurement(GeneralTimeMeasurementTag.LOAD_MODEL_RESOURCE);
				resWrapper.loadModelResource(cachedModelURI);
				this.stopTimeMeasurement();
				if (resWrapper.isModelResourceLoaded()) {
					this.logDebugMsg(String.format("Loaded %s from its resource file", modelName));
				}
			}
		}

		// Resource is completely new, parse it from scratch
		if (resWrapper == null || !resWrapper.isModelResourceLoaded()) {
			resWrapper = this.parseModelsDirWithoutCaching(modelDir);
		}

		var key = cacheKey != null ? cacheKey : modelDir.toString();
		cache.addToCache(key, resWrapper);
		this.stopTimeMeasurement();

		this.logDebugMsg(String.format("%s parsed (with caching)", this.getDisplayNameForModelDir(modelDir)));
		return resWrapper;
	}

	/**
	 * @param modelDir A directory that directly contains the Java-model files
	 * @return The test display name for the given modelPath
	 */
	protected String getDisplayNameForModelDir(Path modelPath) {
		var nameCount = modelPath.getNameCount();

		var startIndex = nameCount > 2 ? nameCount - 2 : nameCount - 1;
		var endIndex = nameCount;

		return modelPath.subpath(startIndex, endIndex).toString();
	}

	/**
	 * Defaults to using {@link #isModelSourceFileDirectoryName(String)} on the file
	 * name. Check the concrete implementation for more details.
	 * 
	 * @param f The file object representing the directory
	 * 
	 * @return Whether a given directory contains any Java elements, from which a
	 *         Java model can be parsed.
	 */
	protected boolean isModelSourceFileDirectory(File f) {
		return this.isModelSourceFileDirectoryName(f.getName());
	}

	/**
	 * Derives the path to the model resources from their URI.
	 * 
	 * @param resArr An array of parsed model resources
	 * @return Dynamic test instances for the given model resources
	 * @see {@link #createTests()}
	 */
	public Collection<DynamicNode> createTests(Resource[] resArr) {
		var pathArr = new Path[resArr.length];

		for (int i = 0; i < pathArr.length; i++) {
			pathArr[i] = Path.of(resArr[i].getURI().path());
		}

		return this.createTests(pathArr, resArr);
	}

	/**
	 * Parses model resources (with caching) for each given path.
	 * 
	 * @param pathArr An array of paths to model directories
	 * @return Dynamic test instances for the models under the given paths
	 * @see {@link #createTests()}
	 */
	public Collection<DynamicNode> createTests(Path[] pathArr) {
		var resArr = new Resource[pathArr.length];

		for (int i = 0; i < resArr.length; i++) {
			resArr[i] = this.parseModelsDirWithCaching(pathArr[i]).getModelResource();
		}

		return this.createTests(pathArr, resArr);
	}

	/**
	 * @param pathArr An array of paths to model directories
	 * @param resArr  An array of parsed model resources
	 * @return Dynamic test instances for the given model resources
	 * @see {@link #createTests()}
	 */
	public Collection<DynamicNode> createTests(Path[] pathArr, Resource[] resArr) {
		if (pathArr.length != resArr.length) {
			Assertions.fail("Lengths of path and resource arrays do not match");
		}

		var tests = new ArrayList<DynamicNode>();

		this.getTestGenerationStrategies().forEach((testStrat) -> {
			this.getTestFactories().forEach((tf) -> {
				var testsForModelDirs = new ArrayList<DynamicNode>();
				testStrat.getTestResourceIterator(resArr.length).forEachRemaining((idxs) -> {
					var path1 = pathArr[idxs[0]];
					var res1 = resArr[idxs[0]];
					var path2 = pathArr[idxs[1]];
					var res2 = resArr[idxs[1]];
					testsForModelDirs.add(tf.createTestsFor(res1, path1, res2, path2));
				});
				tests.add(DynamicContainer.dynamicContainer(String.format("%s (with %s)", tf.getTestDescription(),
						testStrat.getTestGenerationStrategyDescription()), testsForModelDirs));
			});
		});

		return tests;
	}

	/**
	 * Generates dynamic tests for each model directories based on the registered
	 * {@link AbstractJaMoPPParserSimilarityTestFactory} instances. Implemented here
	 * in efforts to have a unified template for dynamic test generation. <br>
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
	 *      {@link #discoverModelSourceParentDirsAt(Path)} for locating model
	 *      directories
	 * @see {@link TestFactory} for what tests are to be generated
	 */
	@TestFactory
	public Collection<DynamicNode> createTests() {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.DYNAMIC_TEST_CREATION);

		var modelSourceFileRootDirPath = this.layout.getModelSourceFileRootDirPath();

		var tests = new ArrayList<DynamicNode>();

		var modelParentDirs = this.discoverModelSourceParentDirsAt(modelSourceFileRootDirPath);
		var modelDirMap = new HashMap<Path, Collection<Path>>();

		for (var parentDir : modelParentDirs) {
			modelDirMap.put(parentDir, this.discoverModelSourceFileDirsAt(parentDir));
		}

		var testsForModelParentDirs = new ArrayList<DynamicNode>();
		modelParentDirs.forEach((md) -> {
			final var modelDirs = modelDirMap.get(md);

			var testsForModelDirs = this.createTests(modelDirs.toArray(Path[]::new));

			testsForModelParentDirs.add(DynamicContainer.dynamicContainer(
					String.format("model = %s", this.getTestFileLayout().getRelativeModelSourceParentDirPath(md)),
					testsForModelDirs));
		});

		tests.add(DynamicContainer.dynamicContainer(
				String.format("root = %s", this.getTestFileLayout().getRelativeModelSourceFileRootDirPath()),
				testsForModelParentDirs));

		this.stopTimeMeasurement();
		return tests;
	}

	/**
	 * Can be used to determine what kind of parser tests are to be generated in
	 * concrete implementors.
	 * 
	 * @return Factories of tests that should be generated for each relevant model
	 *         directories.
	 */
	protected abstract Collection<AbstractJaMoPPParserSimilarityTestFactory> getTestFactories();

	/**
	 * @param rootPath The top-most directory, whose contents should be scanned for
	 *                 model directories
	 * @return A collection of model directory paths under rootPath
	 */
	protected Collection<Path> discoverModelSourceFileDirsAt(Path rootPath) {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.DISCOVER_MODEL_RESOURCES);
		var result = new ModelDirDiscoveryStrategy((f) -> this.isModelSourceFileDirectory(f))
				.discoverModelSourceDirs(rootPath.toFile());
		this.stopTimeMeasurement();
		return result;
	}

	/**
	 * @param rootPath The top-most directory, whose contents should be scanned for
	 *                 directories containing model directories.
	 * @return A collection of paths of directories containing model directory under
	 *         rootPath
	 */
	protected Collection<Path> discoverModelSourceParentDirsAt(Path rootPath) {
		this.startTimeMeasurement(GeneralTimeMeasurementTag.DISCOVER_MODEL_RESOURCES);
		var result = new ModelDirDiscoveryStrategy((f) -> this.isModelSourceFileDirectory(f))
				.discoverModelSourceParentDirs(rootPath.toFile());
		this.stopTimeMeasurement();
		return result;
	}

	/**
	 * Check the concrete implementation for more details.
	 * 
	 * @param dirName The name of the directory, which potentially contains files of
	 *                a model
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
		opts.setShouldDeleteAllResources(false);
		opts.setShouldUnloadAllResources(false);

		opts.setShouldSaveCachedResources(true);
		opts.setShouldRemoveResourcesFromCache(false);
		return opts;
	}

	@Override
	protected ParserTestOptions getResourceTestOptions() {
		return (ParserTestOptions) super.getResourceTestOptions();
	}
}
