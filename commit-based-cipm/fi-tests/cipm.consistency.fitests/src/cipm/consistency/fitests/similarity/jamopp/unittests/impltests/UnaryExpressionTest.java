package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.UnaryExpressionChild;
import org.emftext.language.java.operators.UnaryOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class UnaryExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<UnaryExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<UnaryExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<UnaryOperator> operator1 = () -> getAPI().newAddition();
	private final Supplier<UnaryOperator> operator2 = () -> getAPI().newSubtraction();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newUnaryExpression().withChild(child1.get()).createNow(),
				getAPI().newUnaryExpression().withChild(child2.get()).createNow(),
				ExpressionsPackage.Literals.UNARY_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newUnaryExpression().withChild(child1.get()).createNow(),
				ExpressionsPackage.Literals.UNARY_EXPRESSION__CHILD);
	}

	@Test
	public void testOperator() {
		this.testSimilarity(getAPI().newUnaryExpression().withAddedOperators(operator1.get()).createNow(),
				getAPI().newUnaryExpression().withAddedOperators(operator2.get()).createNow(),
				ExpressionsPackage.Literals.UNARY_EXPRESSION__OPERATORS);
	}

	@Test
	public void testOperatorSize() {
		this.testSimilarity(
				getAPI().newUnaryExpression()
						.withAddedOperators(new UnaryOperator[] { operator1.get(), operator2.get() }).createNow(),
				getAPI().newUnaryExpression().withAddedOperators(operator1.get()).createNow(),
				ExpressionsPackage.Literals.UNARY_EXPRESSION__OPERATORS);
	}

	@Test
	public void testOperatorNullCheck() {
		this.testSimilarityNullCheck(getAPI().newUnaryExpression().withAddedOperators(operator1.get()).createNow(),
				ExpressionsPackage.Literals.UNARY_EXPRESSION__OPERATORS);
	}
}
