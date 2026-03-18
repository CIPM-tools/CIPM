package cipm.consistency.commitintegration.settings;

/**
 * Contains known keys for the settings.
 * 
 * @author Martin Armbruster
 */
public final class SettingKeys {
    /**
     * Base for the keys.
     */
    private static final String KEY_BASE = "cipm.consistency.settings.";
    /**
     * A setting which controls if an adaptive or full instrumentation is performed.
     */
    public static final String PERFORM_FULL_INSTRUMENTATION = KEY_BASE + "instrumentation.full";
    /**
     * A setting which controls if the incremental fine-grained SEFF reconstruction is performed.
     */
    public static final String PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION = KEY_BASE + "reconstruction.finegrained";
    /**
     * A setting which controls if the CPRs between the PCM and extended IM should be used.
     */
    public static final String USE_PCM_IM_CPRS = KEY_BASE + "cpr.pcmim";
    /**
     * A list of regular expressions for the exclusion of files during the JaMoPP parsing.
     */
    public static final String JAVA_PARSER_EXCLUSION_PATTERNS = KEY_BASE + "parser.excludes";
    /**
     * Path to a script for the preprocessing of a commit.
     */
    public static final String PATH_TO_PREPROCESSING_SCRIPT = KEY_BASE + "path.preprocess";
    /**
     * Path to a script for the compilation of the instrumented code.
     */
    public static final String PATH_TO_COMPILATION_SCRIPT = KEY_BASE + "path.compile";
    /**
     * Path to a directory in which the prepared artifacts with the instrumented code are deployed.
     */
    public static final String DEPLOYMENT_PATH = KEY_BASE + "path.deployment";
    /**
     * A list of packages which realize a REST Client to identify REST calls.
     */
    public static final String REST_CLIENT_API_PACKAGES = KEY_BASE + "rest.packages";

    private SettingKeys() {
    }
}
