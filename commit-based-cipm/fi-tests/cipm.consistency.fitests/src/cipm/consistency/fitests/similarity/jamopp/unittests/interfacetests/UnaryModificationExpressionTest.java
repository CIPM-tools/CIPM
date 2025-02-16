package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.UnaryModificationExpression;
import org.emftext.language.java.expressions.UnaryModificationExpressionChild;
import org.emftext.language.java.operators.UnaryModificationOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.IUnaryModificationExpressionInitialiser;

public class UnaryModificationExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IUnaryModificationExpressionInitialiser.class);
	}

	protected UnaryModificationExpression initElement(IUnaryModificationExpressionInitialiser init,
			UnaryModificationExpressionChild child, UnaryModificationOperator op) {
		UnaryModificationExpression result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setChild(result, child));
		Assertions.assertTrue(init.setOperator(result, op));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testChild(IUnaryModificationExpressionInitialiser init, String displayName) {
		this.testSimilarity(this.initElement(init, this.createDecimalIntegerLiteral(1), null),
				this.initElement(init, this.createDecimalIntegerLiteral(2), null),
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testChildNullCheck(IUnaryModificationExpressionInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, this.createDecimalIntegerLiteral(1), null), init, true,
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOperator(IUnaryModificationExpressionInitialiser init, String displayName) {
		this.testSimilarity(this.initElement(init, null, this.createPlusPlusOperator()),
				this.initElement(init, null, this.createMinusMinusOperator()),
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOperatorNullCheck(IUnaryModificationExpressionInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, null, this.createPlusPlusOperator()), init, true,
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR);
	}
}
