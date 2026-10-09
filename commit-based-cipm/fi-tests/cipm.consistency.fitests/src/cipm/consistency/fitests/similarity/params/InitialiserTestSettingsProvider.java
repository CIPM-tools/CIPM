package cipm.consistency.fitests.similarity.params;

/**
 * A singleton class that provides central access to
 * {@link IInitialiserParameters} and {@link ISimilarityValues} instance
 * required by tests.
 * 
 * @author Alp Torac Genc
 */
public class InitialiserTestSettingsProvider {
	/**
	 * The only instance of this class.
	 */
	private static InitialiserTestSettingsProvider instance;

	/**
	 * An {@link ISimilarityValues} instance, which contains expected similarity
	 * values required by similarity checking tests.
	 */
	private ISimilarityValues simVals;

	private InitialiserTestSettingsProvider() {
	};

	/**
	 * Creates an instance of this class, if there is none.
	 */
	public static void initialise() {
		if (instance == null)
			instance = new InitialiserTestSettingsProvider();
	}

	/**
	 * @return The only instance of this class. If there is no such instance,
	 *         creates an instance first. That will be the only instance of this
	 *         class.
	 */
	public static InitialiserTestSettingsProvider getInstance() {
		initialise();
		return instance;
	}

	/**
	 * @return An {@link ISimilarityValues} instance, which contains expected
	 *         similarity values required by similarity checking tests.
	 */
	public ISimilarityValues getSimilarityValues() {
		return this.simVals;
	}

	/**
	 * @param similarityValues Sets the {@link ISimilarityValues} instance, which
	 *                         contains expected similarity values required by
	 *                         similarity checking tests.
	 */
	public void setSimilarityValues(ISimilarityValues similarityValues) {
		this.simVals = similarityValues;
	}

	/**
	 * Sets everything provided by this class, except its only instance, to null.
	 */
	public void reset() {
		this.simVals = null;
	}
}
