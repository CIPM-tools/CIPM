package cipm.consistency.fitests.similarity.params;

import cipm.consistency.initialisers.IInitialiserPackage;

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
	 * An {@link IInitialiserParameters} instance, which has the means to supply
	 * parameterised tests with initialiser instances.
	 */
	private IInitialiserParameters params;

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
	 * @return An {@link IInitialiserParameters} instance, which has the means to
	 *         supply parameterised tests with initialiser instances.
	 */
	public IInitialiserParameters getParameters() {
		return this.params;
	}

	/**
	 * Sets the {@link IInitialiserParameters} instance, which has the means to
	 * supply parameterised tests with initialiser instances.
	 */
	public void setParameters(IInitialiserParameters prms) {
		this.params = prms;
	}

	/**
	 * Change the stored {@link IInitialiserParameters} instance to change the
	 * return value.
	 * 
	 * @return The {@link IInitialiserPackage} used in the stored
	 *         {@link IInitialiserParameters}
	 */
	public IInitialiserPackage getUsedInitialiserPackage() {
		var params = this.getParameters();

		if (params != null) {
			return params.getUsedInitialiserPackage();
		}

		return null;
	}

	/**
	 * Sets everything provided by this class, except its only instance, to null.
	 */
	public void reset() {
		this.params = null;
		this.simVals = null;
	}
}
