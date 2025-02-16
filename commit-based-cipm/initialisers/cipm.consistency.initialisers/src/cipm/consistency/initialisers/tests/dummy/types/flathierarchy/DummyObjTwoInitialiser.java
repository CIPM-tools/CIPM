package cipm.consistency.initialisers.tests.dummy.types.flathierarchy;

import cipm.consistency.initialisers.tests.dummy.types.AbstractDummyTriviallyInitialisingInitialiser;

/**
 * A dummy initialiser implementation of {@link IDummyObjTwoInitialiser} that
 * introduces a new modification method, which takes an object parameter as well
 * as the object to be modified.
 * 
 * @author Alp Torac Genc
 */
public class DummyObjTwoInitialiser extends AbstractDummyTriviallyInitialisingInitialiser
		implements IDummyObjTwoInitialiser {

	public DummyObjTwoInitialiser() {
		super();
	}

	public DummyObjTwoInitialiser(boolean isInitialiseSuccessful) {
		super(isInitialiseSuccessful);
	}

	@Override
	public DummyObjTwoInitialiser newInitialiser() {
		return new DummyObjTwoInitialiser();
	}

	@Override
	public DummyObjTwo instantiate() {
		return new DummyObjTwo();
	}

	public boolean modificationMethodInClass(DummyObjTwo obj, Object param) {
		return true;
	}
}
