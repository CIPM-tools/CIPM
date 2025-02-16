package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.parameters.Parametrizable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesParameters;
import cipm.consistency.initialisers.jamopp.parameters.IParametrizableInitialiser;

public class ParametrizableTest extends AbstractJaMoPPSimilarityTest implements UsesParameters {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(IParametrizableInitialiser.class);
	}

	protected Parametrizable initElement(IParametrizableInitialiser init, Parameter[] params) {
		Parametrizable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addParameters(result, params));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testParameters(IParametrizableInitialiser init) {
		var objOne = this.initElement(init, new Parameter[] { this.createMinimalOrdParamWithClsTarget("p1", "t1") });
		var objTwo = this.initElement(init, new Parameter[] { this.createMinimalOrdParamWithClsTarget("p2", "t2") });

		this.testSimilarity(objOne, objTwo, ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testParametersSize(IParametrizableInitialiser init) {
		var objOne = this.initElement(init, new Parameter[] { this.createMinimalOrdParamWithClsTarget("p1", "t1"),
				this.createMinimalOrdParamWithClsTarget("p2", "t2") });
		var objTwo = this.initElement(init, new Parameter[] { this.createMinimalOrdParamWithClsTarget("p1", "t1") });

		this.testSimilarity(objOne, objTwo, ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testParametersNullCheck(IParametrizableInitialiser init) {
		this.testSimilarityNullCheck(
				this.initElement(init, new Parameter[] { this.createMinimalOrdParamWithClsTarget("p1", "t1") }), init,
				true, ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS);
	}
}
