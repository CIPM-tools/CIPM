package cipm.consistency.initialisers.eobject;

import cipm.consistency.initialisers.IInitialiserUtilityTest;

/**
 * An interface that extends {@link IInitialiserUtilityTest} with further
 * utility methods, focusing on EObject.
 * 
 * @author Alp Torac Genc
 */
public interface IEObjectUtilityTest extends IInitialiserUtilityTest {
	/**
	 * @return The {@link InitialiserNameHelper} instance that will be used in the
	 *         other methods.
	 */
	public default InitialiserNameHelper getInitialiserNameHelper() {
		return new InitialiserNameHelper();
	}

	/**
	 * {@link InitialiserNameHelper#getInitialiserInterfaceName(Class)}
	 */
	public default String getInitialiserInterfaceName(Class<?> cls) {
		return this.getInitialiserNameHelper().getInitialiserInterfaceName(cls);
	}

	/**
	 * {@link InitialiserNameHelper#getConcreteInitialiserName(Class)}
	 */
	public default String getConcreteInitialiserName(Class<?> ifc) {
		return this.getInitialiserNameHelper().getConcreteInitialiserName(ifc);
	}
}