package cipm.consistency.initialisers.tests.dummy.types.flathierarchy;

import cipm.consistency.initialisers.IInitialiser;

/**
 * A dummy initialiser interface with some default utility methods:
 * <ul>
 * <li>One with an object parameter
 * <li>Another one without parameters
 * </ul>
 * 
 * @author Alp Torac Genc
 */
public interface IDummyObjFourInitialiser extends IInitialiser {
	@Override
	public DummyObjFour instantiate();

	public default boolean someUtilityMethodInInterface(Object someParam) {
		return true;
	}

	public default boolean someUtilityMethodInInterfaceWithoutParam() {
		return true;
	}
}
