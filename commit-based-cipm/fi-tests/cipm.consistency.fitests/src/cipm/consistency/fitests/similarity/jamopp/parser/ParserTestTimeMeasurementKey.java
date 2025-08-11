package cipm.consistency.fitests.similarity.jamopp.parser;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

/**
 * A class that contains information about time measurements. Instances of this
 * class can be filled in by using the {@code with...(...)} methods it offers.
 * For convenience, those methods return this instance. <br>
 * <br>
 * There is no mandatory information that should be given to this class.
 * 
 * @author Alp Torac Genc
 */
public class ParserTestTimeMeasurementKey {
	@Expose
	private final Map<ParserTestTimeMeasurerKeyType, String> keyMap = new HashMap<ParserTestTimeMeasurerKeyType, String>();

	/**
	 * The class name of the instance, which is used for hierarchical model
	 * comparison (ex: JavaModelComparator)
	 */
	public ParserTestTimeMeasurementKey withModelComparisonClassName(String modelComparisonClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.MODEL_COMPARISON_CLASS_NAME, modelComparisonClassName);
		return this;
	}

	/**
	 * The path, from which original model files are explored. Model resources will
	 * then be parsed from them.
	 */
	public ParserTestTimeMeasurementKey withModelDiscoveryPath(String modelDiscoveryPath) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.MODEL_DISCOVERY_PATH, modelDiscoveryPath);
		return this;
	}

	/**
	 * The class name of the instance, which is used for discovering original model
	 * files (ex: ModelDirDiscoveryStrategy)
	 */
	public ParserTestTimeMeasurementKey withModelDiscoveryClassName(String modelDiscoveryClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.MODEL_DISCOVERY_CLASS_NAME, modelDiscoveryClassName);
		return this;
	}

	/**
	 * The class name of the instance, which is used to parse model resources (ex:
	 * JaMoPPResourceParsingStrategy)
	 */
	public ParserTestTimeMeasurementKey withResourceParsingStrategyClassName(String resourceParsingStrategyClassName) {
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
	public ParserTestTimeMeasurementKey withOriginalModelLocation(String originalModelLocation) {
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
	public ParserTestTimeMeasurementKey withParsedModelLocation(String parsedModelLocation) {
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
	public ParserTestTimeMeasurementKey withOriginalLeftModelLocation(String originalModelLocation) {
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
	public ParserTestTimeMeasurementKey withParsedLeftModelLocation(String parsedModelLocation) {
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
	public ParserTestTimeMeasurementKey withOriginalRightModelLocation(String originalModelLocation) {
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
	public ParserTestTimeMeasurementKey withParsedRightModelLocation(String parsedModelLocation) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.PARSED_RIGHT_MODEL_LOCATION, parsedModelLocation);
		return this;
	}

	/**
	 * The name of the test class (ex: CWARepoTest)
	 */
	public ParserTestTimeMeasurementKey withTestClassName(String testClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.TEST_CLASS_NAME, testClassName);
		return this;
	}

	/**
	 * The name of the test factory class, which was used to create the dynamic
	 * tests (ex: ModelComparisonTestFactory)
	 */
	public ParserTestTimeMeasurementKey withTestFactoryClassName(String testFactoryClassName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.TEST_FACTORY_CLASS_NAME, testFactoryClassName);
		return this;
	}

	/**
	 * The class name of the instance, which provides expected similarity results to
	 * dynamic tests (ex: ResourceReferenceEqualitySimilarityResultProvider)
	 */
	public ParserTestTimeMeasurementKey withExpectedSimilarityResultProviderClassName(
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
	public ParserTestTimeMeasurementKey withRepositoryName(String repositoryName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.REPOSITORY_NAME, repositoryName);
		return this;
	}

	/**
	 * The URI to the repository that is considered in the test class. <br>
	 * <br>
	 * Use this, if only one repository/commit is considered.
	 */
	public ParserTestTimeMeasurementKey withRepositoryURI(String repositoryURI) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.REPOSITORY_URI, repositoryURI);
		return this;
	}

	/**
	 * The hash of the commit that is currently considered. <br>
	 * <br>
	 * Use this, if only one commit is considered.
	 */
	public ParserTestTimeMeasurementKey withCommitID(String commitID) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.COMMIT_ID, commitID);
		return this;
	}

	/**
	 * The name of the (left-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKey withLeftRepositoryName(String leftRepositoryName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.LEFT_REPOSITORY_NAME, leftRepositoryName);
		return this;
	}

	/**
	 * The URI to the (left-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKey withLeftRepositoryURI(String leftRepositoryURI) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.LEFT_REPOSITORY_URI, leftRepositoryURI);
		return this;
	}

	/**
	 * The name of the (right-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKey withRightRepositoryName(String rightRepositoryName) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RIGHT_REPOSITORY_NAME, rightRepositoryName);
		return this;
	}

	/**
	 * The URI to the (right-hand-side) repository. <br>
	 * <br>
	 * Use this, if two repositories/commits are considered.
	 */
	public ParserTestTimeMeasurementKey withRightRepositoryURI(String rightRepositoryURI) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RIGHT_REPOSITORY_URI, rightRepositoryURI);
		return this;
	}

	/**
	 * The hash of the (left-hand-side) commit that is currently considered. <br>
	 * <br>
	 * Use this, if two commits are considered.
	 */
	public ParserTestTimeMeasurementKey withLeftCommitID(String leftCommitID) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.LEFT_COMMIT_ID, leftCommitID);
		return this;
	}

	/**
	 * The hash of the (right-hand-side) commit that is currently considered. <br>
	 * <br>
	 * Use this, if two commits are considered.
	 */
	public ParserTestTimeMeasurementKey withRightCommitID(String rightCommitID) {
		this.keyMap.put(ParserTestTimeMeasurerKeyType.RIGHT_COMMIT_ID, rightCommitID);
		return this;
	}

	public enum ParserTestTimeMeasurerKeyType {
		MODEL_DISCOVERY_PATH,

		MODEL_DISCOVERY_CLASS_NAME,

		ORIGINAL_MODEL_LOCATION, PARSED_MODEL_LOCATION,

		ORIGINAL_LEFT_MODEL_LOCATION, PARSED_LEFT_MODEL_LOCATION, ORIGINAL_RIGHT_MODEL_LOCATION,
		PARSED_RIGHT_MODEL_LOCATION,

		RESOURCE_PARSING_STRATEGY_CLASS_NAME,

		MODEL_COMPARISON_CLASS_NAME,

		TEST_CLASS_NAME, TEST_FACTORY_CLASS_NAME,

		EXPECTED_SIMILARITY_RESULT_PROVIDER_CLASS_NAME,

		REPOSITORY_NAME, REPOSITORY_URI, COMMIT_ID,

		LEFT_REPOSITORY_NAME, LEFT_REPOSITORY_URI,

		RIGHT_REPOSITORY_NAME, RIGHT_REPOSITORY_URI,

		LEFT_COMMIT_ID, RIGHT_COMMIT_ID;
	}
}
