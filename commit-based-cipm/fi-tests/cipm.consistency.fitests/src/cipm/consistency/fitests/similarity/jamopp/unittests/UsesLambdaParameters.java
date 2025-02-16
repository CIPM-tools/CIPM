package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.expressions.ExplicitlyTypedLambdaParameters;
import org.emftext.language.java.parameters.Parameter;

import cipm.consistency.initialisers.jamopp.expressions.ExplicitlyTypedLambdaParametersInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link LambdaParameters} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link LambdaParameters}
 * instances.
 */
public interface UsesLambdaParameters extends UsesParameters {
	/**
	 * @param params The parameters that will be added to the constructed instance
	 * @return An {@link ExplicitlyTypedLambdaParameters} instance with the given
	 *         parameters
	 */
	public default ExplicitlyTypedLambdaParameters createETLP(Parameter[] params) {
		var init = new ExplicitlyTypedLambdaParametersInitialiser();

		var result = init.instantiate();
		init.addParameters(result, params);

		return result;
	}

	/**
	 * A variant of {@link #createETLP(Parameter[])}, where a single
	 * {@link Parameter} instance is constructed and used.
	 * 
	 * @param paramName  See
	 *                   {@link #createMinimalOrdParamWithClsTarget(String, String)}
	 * @param targetName See
	 *                   {@link #createMinimalOrdParamWithClsTarget(String, String)}
	 */
	public default ExplicitlyTypedLambdaParameters createMinimalETLP(String paramName, String targetName) {
		return this.createETLP(new Parameter[] { this.createMinimalOrdParamWithClsTarget(paramName, targetName) });
	}
}
