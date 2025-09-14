package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

/**
 * An enum that can be used to associate time measurement related information
 * with descriptions, in form of enum constants.
 * 
 * TODO Decide whether all enum constants require commentary
 * 
 * @author Alp Torac Genc
 */
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