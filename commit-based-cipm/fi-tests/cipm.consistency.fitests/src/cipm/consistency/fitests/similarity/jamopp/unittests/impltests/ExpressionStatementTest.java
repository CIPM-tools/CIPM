package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ExpressionStatementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> expression1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> expression2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testExpression() {
		this.testSimilarity(getAPI().newExpressionStatement(expression1.get()),
				getAPI().newExpressionStatement(expression2.get()),
				StatementsPackage.Literals.EXPRESSION_STATEMENT__EXPRESSION);
	}

	@Test
	public void testExpressionNullCheck() {
		this.testSimilarityNullCheck(getAPI().newExpressionStatement(expression1.get()),
				StatementsPackage.Literals.EXPRESSION_STATEMENT__EXPRESSION);
	}
}
