package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.expressions.Expression;

import cipm.consistency.initialisers.jamopp.arrays.ArraySelectorInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ArraySelector} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ArraySelector} instances.
 */
public interface UsesArraySelectors extends UsesExpressions {
	/**
	 * @param asPos The position of the instance to be constructed
	 * @return An {@link ArraySelector} instance with the given parameter.
	 */
	public default ArraySelector createAS(Expression asPos) {
		var init = new ArraySelectorInitialiser();
		var as = init.instantiate();
		init.setPosition(as, asPos);
		return as;
	}

	/**
	 * A variant of {@link #createAS(Expression)}, where the parameter is wrapped
	 * using {@link #createDecimalIntegerLiteral(int)}.
	 */
	public default ArraySelector createMinimalAS(int asPos) {
		return this.createAS(this.createDecimalIntegerLiteral(asPos));
	}
}
