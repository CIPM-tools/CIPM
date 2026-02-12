package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiser;

/**
 * An {@link AbstractDummyAdaptationStrategy} implementation that partially
 * initialises {@link DummyObjC} instances, provided its constructor is given
 * parameters that allow it. This implementation is complemented with
 * {@link ObjCFirstInitStepStrat}, which performs the remaining part of the said
 * initialisation.
 * 
 * @author Alp Torac Genc
 */
public class ObjCSecondInitStepStrat extends AbstractDummyAdaptationStrategy {
	/**
	 * A variant of {@link #ObjCSecondInitStepStrat(boolean)} with "true" as the
	 * boolean parameter.
	 */
	public ObjCSecondInitStepStrat() {
		super();
	}

	/**
	 * @see {@link AbstractDummyAdaptationStrategy#AbstractDummyAdaptationStrategy(boolean)}
	 */
	public ObjCSecondInitStepStrat(boolean initSuccessfully) {
		super(initSuccessfully);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Performs {@link DummyObjC#initStepTwo()} on the given {@link DummyObjC}
	 * instance, if {@link #doesInitialiseSuccessfully()} is true.
	 */
	@Override
	public boolean apply(IInitialiser init, Object obj) {
		if (obj instanceof DummyObjC) {
			var castedO = (DummyObjC) obj;

			if (this.doesInitialiseSuccessfully()) {
				castedO.initStepTwo();
			}

			return castedO.isInitialisationStepTwoDone();
		}

		return true;
	}

	@Override
	public ObjCSecondInitStepStrat newStrategy() {
		return new ObjCSecondInitStepStrat();
	}
}