package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy initialiser for {@link DummyObjE}, whose {@link #initialise(Object)}
 * method always returns false.
 * 
 * @author Alp Torac Genc
 */
public class DummyInitialiserE implements IInitialiser {

	@Override
	public DummyInitialiserE newInitialiser() {
		return new DummyInitialiserE();
	}

	@Override
	public DummyObjE instantiate() {
		return new DummyObjE();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * For this implementor, does nothing and returns false.
	 */
	@Override
	public boolean initialise(Object obj) {
		return false;
	}
}
