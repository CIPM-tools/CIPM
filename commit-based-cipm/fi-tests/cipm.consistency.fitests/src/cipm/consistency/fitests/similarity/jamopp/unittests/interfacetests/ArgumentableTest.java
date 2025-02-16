package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.Argumentable;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLiterals;
import cipm.consistency.initialisers.jamopp.references.IArgumentableInitialiser;

public class ArgumentableTest extends AbstractJaMoPPSimilarityTest
		implements UsesConcreteClassifiers, UsesExpressions, UsesLiterals {
	
	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IArgumentableInitialiser.class);
	}
	
	protected Argumentable initElement(IArgumentableInitialiser init, Expression[] args) {
		var result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addArguments(result, args));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArguments(IArgumentableInitialiser init, String displayName) {
		var objOne = this.initElement(init, new Expression[] { this.createDecimalIntegerLiteral(1) });
		var objTwo = this.initElement(init, new Expression[] { this.createDecimalIntegerLiteral(0) });

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArgumentsSize(IArgumentableInitialiser init, String displayName) {
		var objOne = this.initElement(init,
				new Expression[] { this.createDecimalIntegerLiteral(1), this.createDecimalIntegerLiteral(2) });
		var objTwo = this.initElement(init, new Expression[] { this.createDecimalIntegerLiteral(1) });

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArgumentsNullCheck(IArgumentableInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, new Expression[] { this.createDecimalIntegerLiteral(1) }),
				init, true, ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS);
	}
}
