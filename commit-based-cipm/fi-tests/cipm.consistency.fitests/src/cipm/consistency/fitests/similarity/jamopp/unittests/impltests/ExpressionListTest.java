package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ExpressionListTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> expression1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> expression2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testExpression() {
		this.testSimilarity(getAPI().newExpressionList(expression1.get()),
				getAPI().newExpressionList(expression2.get()),
				ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS);
	}

	@Test
	public void testExpressionSize() {
		this.testSimilarity(getAPI().newExpressionList(new Expression[] { expression1.get(), expression2.get() }),
				getAPI().newExpressionList(expression1.get()),
				ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS);
	}

	@Test
	public void testExpressionNullCheck() {
		this.testSimilarityNullCheck(getAPI().newExpressionList(expression1.get()),
				ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS);
	}
}
