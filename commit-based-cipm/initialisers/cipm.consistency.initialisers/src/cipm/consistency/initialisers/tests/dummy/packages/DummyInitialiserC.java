package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.AbstractInitialiserBase;
import cipm.consistency.initialisers.IInitialiserAdapterStrategy;

/**
 * A dummy initialiser for {@link DummyObjC}.
 * 
 * @author Alp Torac Genc
 * @see {@link ObjCFirstInitStepStrat}
 * @see {@link ObjCSecondInitStepStrat}
 */
public class DummyInitialiserC extends AbstractInitialiserBase {

	/**
	 * @see {@link AbstractInitialiserBase#AbstractInitialiserBase()}
	 */
	public DummyInitialiserC() {
		super();
	}

	/**
	 * @see {@link AbstractInitialiserBase#AbstractInitialiserBase(IInitialiserAdapterStrategy[])}
	 */
	public DummyInitialiserC(IInitialiserAdapterStrategy[] adaptingInits) {
		super(adaptingInits);
	}

	@Override
	public DummyInitialiserC newInitialiser() {
		return new DummyInitialiserC();
	}

	@Override
	public DummyObjC instantiate() {
		return new DummyObjC();
	}
}
