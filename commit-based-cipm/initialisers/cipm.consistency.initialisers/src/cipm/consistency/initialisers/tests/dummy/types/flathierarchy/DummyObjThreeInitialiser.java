package cipm.consistency.initialisers.tests.dummy.types.flathierarchy;

import cipm.consistency.initialisers.tests.dummy.types.AbstractDummyTriviallyInitialisingInitialiser;

/**
 * A dummy initialiser implementation of {@link IDummyObjThreeInitialiser} that
 * introduces no new methods.
 * 
 * @author Alp Torac Genc
 */
public class DummyObjThreeInitialiser extends AbstractDummyTriviallyInitialisingInitialiser
		implements IDummyObjThreeInitialiser {

	public DummyObjThreeInitialiser() {
		super();
	}

	public DummyObjThreeInitialiser(boolean isInitialiseSuccessful) {
		super(isInitialiseSuccessful);
	}

	@Override
	public DummyObjThreeInitialiser newInitialiser() {
		return new DummyObjThreeInitialiser();
	}

	@Override
	public DummyObjThree instantiate() {
		return new DummyObjThree();
	}
}
