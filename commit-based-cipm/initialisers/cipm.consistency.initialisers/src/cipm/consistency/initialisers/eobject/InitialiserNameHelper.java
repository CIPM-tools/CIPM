package cipm.consistency.initialisers.eobject;

/**
 * A utility class that contains information about the naming convention used
 * within the sub-packages of this package. <br>
 * <br>
 * This class is intended to be used from tests, which ensure that all necessary
 * initialisers are implemented and are accessible.
 * 
 * @author Alp Torac Genc
 */
public class InitialiserNameHelper {

	/**
	 * @return The name of the concrete initialiser corresponding to cls.
	 */
	public String getConcreteInitialiserName(Class<?> cls) {
		return cls.getSimpleName() + this.getInitialiserSuffix();
	}

	/**
	 * @return The name of the initialiser interface corresponding to cls.
	 */
	public String getInitialiserInterfaceName(Class<?> cls) {
		return getInitialiserInterfacePrefix() + cls.getSimpleName() + this.getInitialiserSuffix();
	}

	/**
	 * The prefix used in initialiser interfaces.
	 */
	public String getInitialiserInterfacePrefix() {
		return "I";
	}

	/**
	 * The suffix used in initialisers.
	 */
	public String getInitialiserSuffix() {
		return "Initialiser";
	}
}
