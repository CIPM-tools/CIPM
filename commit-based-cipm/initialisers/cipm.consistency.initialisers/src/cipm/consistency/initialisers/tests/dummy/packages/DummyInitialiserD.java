package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy initialiser for {@link DummyObjD}, whose {@link #initialise(Object)}
 * method always returns true.
 * 
 * @author Alp Torac Genc
 */
public class DummyInitialiserD implements IInitialiser {

	@Override
	public DummyInitialiserD newInitialiser() {
		return new DummyInitialiserD();
	}

	@Override
	public DummyObjD instantiate() {
		return new DummyObjD();
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
