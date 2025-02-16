package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiser;

/**
 * An {@link AbstractDummyAdaptationStrategy} implementation that partially
 * initialises {@link DummyObjC} instances, provided its constructor is given
 * parameters that allow it. This implementation is complemented with
 * {@link ObjCSecondInitStepStrat}, which performs the remaining part of the
 * said initialisation.
 * 
 * @author Alp Torac Genc
 */
public class ObjCFirstInitStepStrat extends AbstractDummyAdaptationStrategy {
	/**
	 * A variant of {@link #ObjCFirstInitStepStrat(boolean)} with "true" as the
	 * boolean parameter.
	 */
	public ObjCFirstInitStepStrat() {
		super();
	}

	/**
	 * @see {@link AbstractDummyAdaptationStrategy#AbstractDummyAdaptationStrategy(boolean)}
	 */
	public ObjCFirstInitStepStrat(boolean initSuccessfully) {
		super(initSuccessfully);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Performs {@link DummyObjC#initStepOne()} on the given {@link DummyObjC}
	 * instance, if {@link #doesInitialiseSuccessfully()} is true.
	 */
	@Override
	public boolean apply(IInitialiser init, Object obj) {
		if (obj instanceof DummyObjC) {
			var castedO = (DummyObjC) obj;

			if (this.doesInitialiseSuccessfully()) {
				castedO.initStepOne();
			}

			return castedO.isInitialisationStepOneDone();
		}

		return true;
	}

	@Override
	public ObjCFirstInitStepStrat newStrategy() {
		return new ObjCFirstInitStepStrat();
	}
}
