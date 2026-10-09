package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ConditionalExpressionChild;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ConditionalExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ConditionalExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<ConditionalExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<Expression> expressionIf1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> expressionIf2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<Expression> generalExpressionElse1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> generalExpressionElse2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newConditionalExpression().withChild(child1.get()).createNow(),
				getAPI().newConditionalExpression().withChild(child2.get()).createNow(),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newConditionalExpression().withChild(child1.get()).createNow(),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD);
	}

	@Test
	public void testExpressionIf() {
		this.testSimilarity(getAPI().newConditionalExpression().withExpressionIf(expressionIf1.get()).createNow(),
				getAPI().newConditionalExpression().withExpressionIf(expressionIf2.get()).createNow(),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__EXPRESSION_IF);
	}

	@Test
	public void testExpressionIfNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newConditionalExpression().withExpressionIf(expressionIf1.get()).createNow(),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__EXPRESSION_IF);
	}

	@Test
	public void testGeneralExpressionElse() {
		this.testSimilarity(
				getAPI().newConditionalExpression().withGeneralExpressionElse(generalExpressionElse1.get()).createNow(),
				getAPI().newConditionalExpression().withGeneralExpressionElse(generalExpressionElse2.get()).createNow(),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__GENERAL_EXPRESSION_ELSE);
	}

	@Test
	public void testGeneralExpressionElseNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newConditionalExpression().withGeneralExpressionElse(generalExpressionElse1.get()).createNow(),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__GENERAL_EXPRESSION_ELSE);
	}
}
