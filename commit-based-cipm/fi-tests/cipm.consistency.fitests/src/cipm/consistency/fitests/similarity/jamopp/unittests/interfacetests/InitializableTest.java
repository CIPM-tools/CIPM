package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.instantiations.Initializable;
import org.emftext.language.java.instantiations.InstantiationsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLiterals;
import cipm.consistency.initialisers.jamopp.instantiations.IInitializableInitialiser;

public class InitializableTest extends AbstractJaMoPPSimilarityTest implements UsesLiterals {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IInitializableInitialiser.class);
	}

	protected Initializable initElement(IInitializableInitialiser init, Expression initVal) {
		Initializable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setInitialValue(result, initVal));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testInitialValue(IInitializableInitialiser init) {
		var objOne = this.initElement(init, this.createDecimalIntegerLiteral(5));
		var objTwo = this.initElement(init, this.createBooleanLiteral(false));

		this.testSimilarity(objOne, objTwo, InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testInitialValueNullCheck(IInitializableInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, this.createDecimalIntegerLiteral(5)), init, true,
				InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE);
	}
}
