package cipm.consistency.fitests.similarity.jamopp.parser;

/**
 * An enum containing various general-purpose tags that can be used while taking
 * time measurements.
 * 
 * @author Alp Torac Genc
 */
public enum GeneralTimeMeasurementTag implements ITimeMeasurementTag {
	/**
	 * A tag meant for time measurements from discovering model source files
	 */
	DISCOVER_MODEL_RESOURCES,
	/**
	 * A tag meant for time measurements from parsing model resources from model
	 * source files
	 */
	PARSE_MODEL_RESOURCE,
	/**
	 * A tag meant for time measurements from loading previously parsed model
	 * resource files
	 */
	LOAD_MODEL_RESOURCE,
	/**
	 * A tag meant for time measurements from unloading model resources that are
	 * loaded
	 */
	UNLOAD_MODEL_RESOURCE,
	/**
	 * A tag meant for time measurements from saving model resources to physical
	 * files
	 */
	SAVE_MODEL_RESOURCE,
	/**
	 * A tag meant for time measurements from deleting previously parsed model
	 * resource files
	 */
	DELETE_MODEL_RESOURCE,
	/**
	 * A tag meant for time measurements from accessing model resource instances
	 * from their cache, or making queries to their cache (such as checking if a
	 * particular model resource exists)
	 */
	MODEL_RESOURCE_CACHE_ACCESS,

	/**
	 * A tag meant for time measurements from the test methods annotated with
	 * {@link org.junit.jupiter.api.BeforeEach}
	 */
	TEST_BEFOREEACH,
	/**
	 * A tag meant for time measurements from the test methods annotated with
	 * {@link org.junit.jupiter.api.AfterEach}
	 */
	TEST_AFTEREACH,
	/**
	 * A tag meant for time measurements from creating dynamic tests (but not
	 * running them)
	 */
	DYNAMIC_TEST_CREATION,
	/**
	 * A tag meant for time measurements from miscellaneous preparation operations
	 * (except those from {@link #TEST_BEFOREEACH} and {@link #TEST_AFTEREACH} ) in
	 * tests that cannot be reasonably assigned to a more accurate tag
	 */
	TEST_OVERHEAD,

	/**
	 * A tag meant for time measurements from similarity checking. Primarily
	 * intended for using a similarity checking mechanism on model resource
	 * instances
	 */
	SIMILARITY_CHECKING,
	/**
	 * A tag meant for time measurements from comparing model resources, i.e.
	 * generating a {@link org.eclipse.emf.compare.Comparison} instance for 2 model
	 * resources.
	 */
	MODEL_RESOURCE_COMPARISON,
	/**
	 * A tag meant for time measurements from computing expected similarity results
	 */
	EXPECTED_SIMILARITY_RESULT_COMPUTATION

	;
}