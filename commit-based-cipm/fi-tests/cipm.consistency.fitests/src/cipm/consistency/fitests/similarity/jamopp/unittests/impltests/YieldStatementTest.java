package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class YieldStatementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> yieldExpression1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> yieldExpression2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testYieldExpression() {
		this.testSimilarity(getAPI().newYieldStatement(yieldExpression1.get()),
				getAPI().newYieldStatement(yieldExpression2.get()),
				StatementsPackage.Literals.YIELD_STATEMENT__YIELD_EXPRESSION);
	}

	@Test
	public void testYieldExpressionNullCheck() {
		this.testSimilarityNullCheck(getAPI().newYieldStatement(yieldExpression1.get()),
				StatementsPackage.Literals.YIELD_STATEMENT__YIELD_EXPRESSION);
	}
}
