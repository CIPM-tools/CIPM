package cipm.consistency.initialisers.tests.dummy.types.flathierarchy;

import cipm.consistency.initialisers.tests.dummy.types.AbstractDummyTriviallyInitialisingInitialiser;

/**
 * A dummy initialiser implementation of {@link IDummyObjFourInitialiser} that
 * introduces some further utility methods:
 * <ul>
 * <li>One with an object parameter
 * <li>Another one without parameters
 * </ul>
 * 
 * @author Alp Torac Genc
 */
public class DummyObjFourInitialiser extends AbstractDummyTriviallyInitialisingInitialiser
		implements IDummyObjFourInitialiser {

	public DummyObjFourInitialiser() {
		super();
	}

	public DummyObjFourInitialiser(boolean isInitialiseSuccessful) {
		super(isInitialiseSuccessful);
	}

	@Override
	public DummyObjFourInitialiser newInitialiser() {
		return new DummyObjFourInitialiser();
	}

	@Override
	public DummyObjFour instantiate() {
		return new DummyObjFour();
	}

	public boolean someUtilityMethodInClass(Object param) {
		return true;
	}

	public boolean someUtilityMethodInClassWithoutParam() {
		return true;
	}
}
