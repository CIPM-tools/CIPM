package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

/**
 * An enum that can be used to associate time measurement related information
 * with descriptions, in form of enum constants.
 * 
 * @author Alp Torac Genc
 */
public enum ParserTestTimeMeasurerKeyType {
	/**
	 * A key type meant for paths, where discovery of model source file directories
	 * and model source parent directories start.
	 */
	MODEL_DISCOVERY_PATH,
	/**
	 * A key type meant for the name of the class encapsulating how model source
	 * file directories and model source parent directories are to be discovered.
	 */
	MODEL_DISCOVERY_CLASS_NAME,

	/**
	 * A key type meant for model source file directory locations. Use this, if time
	 * measurement is from an operation involving one model resource.
	 */
	MODEL_SOURCE_FILE_DIR_LOCATION,
	/**
	 * A key type meant for model resource locations. Use this, if time measurement
	 * is from an operation involving one model resource.
	 */
	MODEL_RESOURCE_LOCATION,

	/**
	 * A key type meant for model source file directory locations for the left hand
	 * side model resource. Use this, if time measurement is from an operation
	 * involving two model resources.
	 */
	LEFT_MODEL_SOURCE_FILE_DIR_LOCATION,
	/**
	 * A key type meant for model resource locations for the left hand side model
	 * resource. Use this, if time measurement is from an operation involving two
	 * model resources.
	 */
	LEFT_MODEL_RESOURCE_LOCATION,
	/**
	 * A key type meant for model source file directory locations for the right hand
	 * side model resource. Use this, if time measurement is from an operation
	 * involving two model resources.
	 */
	RIGHT_MODEL_SOURCE_FILE_DIR_LOCATION,
	/**
	 * A key type meant for model resource locations for the right hand side model
	 * resource. Use this, if time measurement is from an operation involving two
	 * model resources.
	 */
	RIGHT_MODEL_RESOURCE_LOCATION,

	/**
	 * A key type meant for the name of the concrete resource parsing strategy
	 * class, which was used to parse a model resource.
	 */
	RESOURCE_PARSING_STRATEGY_CLASS_NAME,

	/**
	 * A key type meant for the name of the concrete model comparison class, which
	 * was used to perform model comparison on model resources.
	 */
	MODEL_COMPARISON_CLASS_NAME,

	/**
	 * A key type meant for the name of the currently running test class.
	 */
	TEST_CLASS_NAME,
	/**
	 * A key type meant for the name of the test factory class, which created the
	 * currently running dynamic test.
	 */
	TEST_FACTORY_CLASS_NAME,

	/**
	 * A key type meant for the name of the concrete expected similarity result
	 * provider class.
	 */
	EXPECTED_SIMILARITY_RESULT_PROVIDER_CLASS_NAME,

	/**
	 * A key type meant for the name of the repository. Use this, if time
	 * measurement is from an operation involving one model resource.
	 */
	REPOSITORY_NAME,
	/**
	 * A key type meant for the URI of the repository. Use this, if time measurement
	 * is from an operation involving one model resource.
	 */
	REPOSITORY_URI,
	/**
	 * A key type meant for commit IDs from the repository. Use this, if time
	 * measurement is from an operation involving one model resource.
	 */
	COMMIT_ID,

	/**
	 * A key type meant for the name of the repository for the left hand side model
	 * resource. Use this, if time measurement is from an operation involving two
	 * model resources.
	 */
	LEFT_REPOSITORY_NAME,
	/**
	 * A key type meant for the URI of the repository for the left hand side model
	 * resource. Use this, if time measurement is from an operation involving two
	 * model resources.
	 */
	LEFT_REPOSITORY_URI,
	/**
	 * A key type meant for the name of the repository for the right hand side model
	 * resource. Use this, if time measurement is from an operation involving two
	 * model resources.
	 */
	RIGHT_REPOSITORY_NAME,
	/**
	 * A key type meant for the URI of the repository for the right hand side model
	 * resource. Use this, if time measurement is from an operation involving two
	 * model resources.
	 */
	RIGHT_REPOSITORY_URI,
	/**
	 * A key type meant for commit IDs of the repository for the left hand side
	 * model resource. Use this, if time measurement is from an operation involving
	 * two model resources.
	 */
	LEFT_COMMIT_ID,
	/**
	 * A key type meant for commit IDs of the repository for the right hand side
	 * model resource. Use this, if time measurement is from an operation involving
	 * two model resources.
	 */
	RIGHT_COMMIT_ID;
}