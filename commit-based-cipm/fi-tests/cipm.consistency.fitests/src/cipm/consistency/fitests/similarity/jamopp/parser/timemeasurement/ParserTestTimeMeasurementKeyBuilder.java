package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder for {@link ParserTestTimeMeasurementKey}. <br>
 * <br>
 * Use the provided methods to build the key instance. For convenience and
 * copying existing keys, {@link #fromKeyMap(Map)} and
 * {@link #fromStringKeyMap(Map)} can be used. Calling {@link #createKey()} will
 * return the key instance that was being built and reset all status information
 * within this class.
 * 
 * @author Alp Torac Genc
 */
public class ParserTestTimeMeasurementKeyBuilder {
	private Map<ParserTestTimeMeasurerKeyType, String> keyMap;

	public ParserTestTimeMeasurementKeyBuilder() {
		this.reset();
	}

	/**
	 * Includes all pairs from the given map to the key to be created
	 */
	public ParserTestTimeMeasurementKeyBuilder fromKeyMap(Map<ParserTestTimeMeasurerKeyType, String> anotherKeyMap) {
		this.keyMap.putAll(anotherKeyMap);
		return this;
	}

	/**
	 * A variant of {@link #fromKeyMap(Map)} that parses the
	 * {@link ParserTestTimeMeasurerKeyType} values before including all pairs from
	 * the given map to the key to be created
	 */
	public ParserTestTimeMeasurementKeyBuilder fromStringKeyMap(Map<String, String> anotherKeyMap) {
		for (var e : anotherKeyMap.entrySet()) {
			this.keyMap.put(ParserTestTimeMeasurerKeyType.valueOf(e.getKey()), e.getValue());
		}
		return this;
	}

	/**
	 * The class name of the instance, which is used for hierarchical model
	 * comparison (ex: JavaModelComparator)
	 */
	public ParserTestTimeMeasurementKeyBuilder withModelComparisonClassName(String modelComparisonClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.MODEL_COMPARISON_CLASS_NAME, modelComparisonClassName);
		return this;
	}

	/**
	 * The path, from which original model files are explored. Model resources will
	 * then be parsed from them.
	 */
	public ParserTestTimeMeasurementKeyBuilder withModelDiscoveryPath(String modelDiscoveryPath) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.MODEL_DISCOVERY_PATH, modelDiscoveryPath);
		return this;
	}

	/**
	 * The class name of the instance, which is used for discovering original model
	 * files (ex: ModelDirDiscoveryStrategy)
	 */
	public ParserTestTimeMeasurementKeyBuilder withModelDiscoveryClassName(String modelDiscoveryClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.MODEL_DISCOVERY_CLASS_NAME, modelDiscoveryClassName);
		return this;
	}

	/**
	 * The class name of the instance, which is used to parse model resources (ex:
	 * JaMoPPResourceParsingStrategy)
	 */
	public ParserTestTimeMeasurementKeyBuilder withResourceParsingStrategyClassName(
			String resourceParsingStrategyClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RESOURCE_PARSING_STRATEGY_CLASS_NAME,
				resourceParsingStrategyClassName);
		return this;
	}

	/**
	 * The location, under which all original model files can be found. These files
	 * were parsed to create the corresponding model resource. The location can be
	 * any form of String that can be used to navigate (ex: Path.toString() or
	 * URI.toString()). <br>
	 * <br>
	 * Use this, if only one model is considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withOriginalModelLocation(String originalModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.ORIGINAL_MODEL_LOCATION, originalModelLocation);
		return this;
	}

	/**
	 * The location, under which the parsed model resource can be found. The
	 * location can be any form of String that can be used to navigate (ex:
	 * Path.toString() or URI.toString()). <br>
	 * <br>
	 * Use this, if only one model is considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withParsedModelLocation(String parsedModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.PARSED_MODEL_LOCATION, parsedModelLocation);
		return this;
	}

	/**
	 * The path, under which all (left hand side) original model files can be found.
	 * These files were parsed to create the corresponding model resource. The
	 * location can be any form of String that can be used to navigate (ex:
	 * Path.toString() or URI.toString()). <br>
	 * <br>
	 * Use this, if two models are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withOriginalLeftModelLocation(String originalModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.ORIGINAL_LEFT_MODEL_LOCATION, originalModelLocation);
		return this;
	}

	/**
	 * The location, under which the (left hand side) parsed model resource can be
	 * found. The location can be any form of String that can be used to navigate
	 * (ex: Path.toString() or URI.toString()). <br>
	 * <br>
	 * Use this, if two models are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withParsedLeftModelLocation(String parsedModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.PARSED_LEFT_MODEL_LOCATION, parsedModelLocation);
		return this;
	}

	/**
	 * The path, under which all (right hand side) original model files can be
	 * found. These files were parsed to create the corresponding model resource.The
	 * location can be any form of String that can be used to navigate (ex:
	 * Path.toString() or URI.toString()). <br>
	 * <br>
	 * <br>
	 * Use this, if two models are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withOriginalRightModelLocation(String originalModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.ORIGINAL_RIGHT_MODEL_LOCATION, originalModelLocation);
		return this;
	}

	/**
	 * The location, under which the (right hand side) parsed model resource can be
	 * found. The location can be any form of String that can be used to navigate
	 * (ex: Path.toString() or URI.toString()). <br>
	 * <br>
	 * Use this, if two models are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withParsedRightModelLocation(String parsedModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.PARSED_RIGHT_MODEL_LOCATION, parsedModelLocation);
		return this;
	}

	/**
	 * The name of the test class (ex: CWARepoTest)
	 */
	public ParserTestTimeMeasurementKeyBuilder withTestClassName(String testClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.TEST_CLASS_NAME, testClassName);
		return this;
	}

	/**
	 * The name of the test factory class, which was used to create the dynamic
	 * tests (ex: ModelComparisonTestFactory)
	 */
	public ParserTestTimeMeasurementKeyBuilder withTestFactoryClassName(String testFactoryClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.TEST_FACTORY_CLASS_NAME, testFactoryClassName);
		return this;
	}

	/**
	 * The class name of the instance, which provides expected similarity results to
	 * dynamic tests (ex: ResourceReferenceEqualitySimilarityResultProvider)
	 */
	public ParserTestTimeMeasurementKeyBuilder withExpectedSimilarityResultProviderClassName(
			String expectedSimilarityResultProviderClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.EXPECTED_SIMILARITY_RESULT_PROVIDER_CLASS_NAME,
				expectedSimilarityResultProviderClassName);
		return this;
	}

	/**
	 * The name of the repository that is considered in the test class. <br>
	 * <br>
	 * Use this, if only one repository/commit is considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withRepositoryName(String repositoryName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.REPOSITORY_NAME, repositoryName);
		return this;
	}

	/**
	 * The URI to the repository that is considered in the test class. <br>
	 * <br>
	 * Use this, if only one repository/commit is considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withRepositoryURI(String repositoryURI) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.REPOSITORY_URI, repositoryURI);
		return this;
	}

	/**
	 * The hash of the commit that is currently considered. <br>
	 * <br>
	 * Use this, if only one commit is considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withCommitID(String commitID) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.COMMIT_ID, commitID);
		return this;
	}

	/**
	 * The name of the (left-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withLeftRepositoryName(String leftRepositoryName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.LEFT_REPOSITORY_NAME, leftRepositoryName);
		return this;
	}

	/**
	 * The URI to the (left-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withLeftRepositoryURI(String leftRepositoryURI) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.LEFT_REPOSITORY_URI, leftRepositoryURI);
		return this;
	}

	/**
	 * The name of the (right-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withRightRepositoryName(String rightRepositoryName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RIGHT_REPOSITORY_NAME, rightRepositoryName);
		return this;
	}

	/**
	 * The URI to the (right-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withRightRepositoryURI(String rightRepositoryURI) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RIGHT_REPOSITORY_URI, rightRepositoryURI);
		return this;
	}

	/**
	 * The hash of the (left-hand-side) commit that is currently considered. <br>
	 * <br>
	 * Use this, if two commits are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withLeftCommitID(String leftCommitID) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.LEFT_COMMIT_ID, leftCommitID);
		return this;
	}

	/**
	 * The hash of the (right-hand-side) commit that is currently considered. <br>
	 * <br>
	 * Use this, if two commits are considered.
	 */
	public ParserTestTimeMeasurementKeyBuilder withRightCommitID(String rightCommitID) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RIGHT_COMMIT_ID, rightCommitID);
		return this;
	}

	/**
	 * Resets the building process of a {@link ParserTestTimeMeasurementKey}
	 * instance by initialising all status information of this instance, effectively
	 * resetting them.<br>
	 * <br>
	 * If this method is called before {@link #createKey()}, the building process
	 * will be reset and must be started anew.
	 */
	public ParserTestTimeMeasurementKeyBuilder reset() {
		this.keyMap = new HashMap<ParserTestTimeMeasurerKeyType, String>();
		return this;
	}

	/**
	 * Finalises the current building process by actually creating the
	 * {@link ParserTestTimeMeasurementKey} instance. Resets (via {@link #reset()})
	 * all status information in this instance afterward. <br>
	 * <br>
	 * If this method is overridden, {@link #reset()} should be called after the key
	 * instance is created.
	 * 
	 * @return The built {@link ParserTestTimeMeasurementKey} instance
	 */
	public ParserTestTimeMeasurementKey createKey() {
		var key = new ParserTestTimeMeasurementKey(this.keyMap);
		this.reset();
		return key;
	}
}
