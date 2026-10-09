package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class NestedExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> expression1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> expression2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testExpression() {
		this.testSimilarity(getAPI().newNestedExpression().withExpression(expression1.get()).createNow(),
				getAPI().newNestedExpression().withExpression(expression2.get()).createNow(),
				ExpressionsPackage.Literals.NESTED_EXPRESSION__EXPRESSION);
	}

	@Test
	public void testExpressionNullCheck() {
		this.testSimilarityNullCheck(getAPI().newNestedExpression().withExpression(expression1.get()).createNow(),
				ExpressionsPackage.Literals.NESTED_EXPRESSION__EXPRESSION);
	}
}
