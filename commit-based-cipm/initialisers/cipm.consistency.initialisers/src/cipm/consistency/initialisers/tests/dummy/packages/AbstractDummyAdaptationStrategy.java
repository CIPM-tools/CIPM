package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiserAdapterStrategy;

/**
 * An abstract class for simple dummy implementations of
 * {@link IInitialiserAdapterStrategy} that are used in tests.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractDummyAdaptationStrategy implements IInitialiserAdapterStrategy {
	/**
	 * @see {@link #doesInitialiseSuccessfully()}
	 */
	private boolean initSuccessfully;

	/**
	 * A variant of {@link #AbstractDummyAdaptationStrategy(boolean)} with "true" as
	 * boolean parameter.
	 */
	public AbstractDummyAdaptationStrategy() {
		this(true);
	}

	/**
	 * Constructs an instance with the given parameter that determines the success
	 * of the adaptation strategy.
	 * 
	 * @param initSuccessfully Whether
	 *                         {@link #apply(cipm.consistency.initialisers.IInitialiser, Object)}
	 *                         performs the initialisation as expected.
	 */
	public AbstractDummyAdaptationStrategy(boolean initSuccessfully) {
		this.initSuccessfully = initSuccessfully;
	}

	/**
	 * @return Whether
	 *         {@link #apply(cipm.consistency.initialisers.IInitialiser, Object)}
	 *         performs the initialisation as expected.
	 */
	public boolean doesInitialiseSuccessfully() {
		return this.initSuccessfully;
	}
}
