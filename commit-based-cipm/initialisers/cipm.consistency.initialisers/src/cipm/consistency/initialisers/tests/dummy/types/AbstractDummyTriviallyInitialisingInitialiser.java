package cipm.consistency.initialisers.tests.dummy.types;

import cipm.consistency.initialisers.IInitialiser;

/**
 * An abstract dummy {@link IInitialiser}, which either always succeeds or
 * always fails initialising via {@link #initialise(Object)}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractDummyTriviallyInitialisingInitialiser implements IInitialiser {

	/**
	 * @see {@link AbstractDummyTriviallyInitialisingInitialiser#AbstractDummyTriviallyInitialisingInitialiser(boolean)}
	 */
	private boolean isInitialiseSuccessful;

	/**
	 * A variant of
	 * {@link AbstractDummyTriviallyInitialisingInitialiser#AbstractDummyTriviallyInitialisingInitialiser(boolean)}
	 * with "true" as the boolean parameter.
	 */
	public AbstractDummyTriviallyInitialisingInitialiser() {
		this(true);
	}

	/**
	 * Constructs an instance, which either always succeeds or always fails
	 * initialising via {@link #initialise(Object)}.
	 * 
	 * @param isInitialiseSuccessful The return value of {@link #initialise(Object)}
	 *                               (i.e. whether initialisation succeeds).
	 */
	public AbstractDummyTriviallyInitialisingInitialiser(boolean isInitialiseSuccessful) {
		this.isInitialiseSuccessful = isInitialiseSuccessful;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * For this implementation, does nothing to the given object. Check
	 * {@link AbstractDummyTriviallyInitialisingInitialiser#AbstractDummyTriviallyInitialisingInitialiser(boolean)}
	 * for the return value.
	 */
	@Override
	public boolean initialise(Object obj) {
		return this.isInitialiseSuccessful;
	}
}
