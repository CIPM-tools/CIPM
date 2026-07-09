package cipm.consistency.fluentapi.gen;

import java.util.Map;

/**
 * An interface meant to be implemented by generators of EOperations that
 * represent methods of classes within the fluent API model. Implemented to
 * facilitate generating overviews of methods for each fluent API class.
 * 
 * @author Alp Torac Genc
 */
public interface IFluentAPIMethodGenerator {
	/**
	 * @return A map containing (method name, method description) pairs that this
	 *         method generator creates. Note that method descriptions are only
	 *         meant for methods, which are intended to be used from the outside the
	 *         class declaring it.
	 */
	public Map<String, String> getMethodNamesToDescriptions();
}
