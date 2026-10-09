package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.AbstractInitialiserBase;

/**
 * A dummy initialiser for {@link DummyObjA}.
 * 
 * @author Alp Torac Genc
 * @see {@link ObjAInitStrat}
 */
public class DummyInitialiserA extends AbstractInitialiserBase {

	@Override
	public DummyInitialiserA newInitialiser() {
		return new DummyInitialiserA();
	}

	@Override
	public DummyObjA instantiate() {
		return new DummyObjA();
	}
}
