package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.arrays.ArrayInitializationValue;
import org.emftext.language.java.arrays.ArrayInitializer;
import org.emftext.language.java.expressions.Expression;

import cipm.consistency.initialisers.jamopp.arrays.ArrayInitializerInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ArrayInitializer} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ArrayInitializer}
 * instances.
 */
public interface UsesArrayInitializers {
	/**
	 * @param aivs The values of the instance to be constructed
	 * @return An {@link ArrayInitializer} with the given parameter
	 */
	public default ArrayInitializer createMinimalArrayInitializer(ArrayInitializationValue[] aivs) {
		var init = new ArrayInitializerInitialiser();
		ArrayInitializer result = init.instantiate();
		init.addInitialValues(result, aivs);
		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalArrayInitializer(ArrayInitializationValue[])}, where the
	 * parameter is an array containing only one {@link ArrayInitializationValue}.
	 */
	public default ArrayInitializer createMinimalArrayInitializer(Expression aiv) {
		return this.createMinimalArrayInitializer(new ArrayInitializationValue[] { aiv });
	}
}
