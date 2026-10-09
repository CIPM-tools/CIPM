package cipm.consistency.initialisers.tests.dummy.packages;

import cipm.consistency.initialisers.IInitialiser;

/**
 * An {@link AbstractDummyAdaptationStrategy} implementation that initialises
 * {@link DummyObjA} instances, provided its constructor is given parameters
 * that allow it.
 * 
 * @author Alp Torac Genc
 */
public class ObjAInitStrat extends AbstractDummyAdaptationStrategy {
	/**
	 * A variant of {@link #ObjAInitStrat(boolean)} with "true" as the boolean
	 * parameter.
	 */
	public ObjAInitStrat() {
		super();
	}

	/**
	 * @see {@link AbstractDummyAdaptationStrategy#AbstractDummyAdaptationStrategy(boolean)}
	 */
	public ObjAInitStrat(boolean initSuccessfully) {
		super(initSuccessfully);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Initialises the given {@link DummyObjA} instance, if
	 * {@link #doesInitialiseSuccessfully()} is true.
	 */
	@Override
	public boolean apply(IInitialiser init, Object obj) {
		if (obj instanceof DummyObjA) {
			var castedO = (DummyObjA) obj;

			if (this.doesInitialiseSuccessfully()) {
				castedO.initStep();
			}

			return castedO.isInitialisationStepDone();
		}

		return true;
	}

	@Override
	public ObjAInitStrat newStrategy() {
		return new ObjAInitStrat();
	}
}
