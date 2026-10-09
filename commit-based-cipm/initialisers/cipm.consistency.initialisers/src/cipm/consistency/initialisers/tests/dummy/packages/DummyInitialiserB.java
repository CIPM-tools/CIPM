package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy initialiser for {@link DummyObjB}, whose {@link #initialise(Object)}
 * method always returns true.
 * 
 * @author Alp Torac Genc
 */
public class DummyInitialiserB implements IInitialiser {

	@Override
	public DummyInitialiserB newInitialiser() {
		return new DummyInitialiserB();
	}

	@Override
	public DummyObjB instantiate() {
		return new DummyObjB();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * For this implementor, does nothing and returns true.
	 */
	@Override
	public boolean initialise(Object obj) {
		return true;
	}
}
