package cipm.consistency.initialisers.tests.dummy.packages;

/**
 * A dummy object that can be initialised via 2 methods {@link #initStepOne()}
 * and {@link #initStepTwo()}. Note that the said initialisation methods do not
 * do any vital changes on the object, they only simulate an initialisation with
 * multiple steps. The order of the steps do not matter.
 * 
 * @author Alp Torac Genc
 */
public class DummyObjC {
	/**
	 * @see {@link #isInitialisationStepOneDone()}
	 */
	private boolean initialisationStepOneDone = false;
	/**
	 * @see {@link #isInitialisationStepTwoDone()}
	 */
	private boolean initialisationStepTwoDone = false;

	/**
	 * Performs the step one of the initialisation.
	 */
	public void initStepOne() {
		this.initialisationStepOneDone = true;
	}

	/**
	 * Performs the step two of the initialisation.
	 */
	public void initStepTwo() {
		this.initialisationStepTwoDone = true;
	}

	/**
	 * @return Whether the step one of the initialisation is performed.
	 * @see {@link #initStepOne()}
	 */
	public boolean isInitialisationStepOneDone() {
		return initialisationStepOneDone;
	}

	/**
	 * @return Whether the step two of the initialisation is performed.
	 * @see {@link #initStepTwo()}
	 */
	public boolean isInitialisationStepTwoDone() {
		return initialisationStepTwoDone;
	}
}
