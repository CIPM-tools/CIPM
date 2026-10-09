package cipm.consistency.initialisers.tests.dummy.packages;

/**
 * A dummy object that can be initialised via {@link #initStep()}. Note that the
 * said initialisation does not do any vital changes on the object, it only
 * simulates an initialisation.
 * 
 * @author Alp Torac Genc
 */
public class DummyObjA {
	/**
	 * @see {@link #isInitialisationStepDone()}
	 */
	private boolean initialisationStepDone = false;

	/**
	 * Performs the initialisation
	 */
	public void initStep() {
		this.initialisationStepDone = true;
	}

	/**
	 * @return Whether initialisation was done
	 * @see {@link #initStep()}
	 */
	public boolean isInitialisationStepDone() {
		return initialisationStepDone;
	}
}
