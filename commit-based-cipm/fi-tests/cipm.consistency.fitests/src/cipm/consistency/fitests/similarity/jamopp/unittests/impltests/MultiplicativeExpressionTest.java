package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.MultiplicativeExpressionChild;
import org.emftext.language.java.operators.MultiplicativeOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class MultiplicativeExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<MultiplicativeExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<MultiplicativeExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<MultiplicativeOperator> multiplicativeOperator1 = () -> getAPI().newDivision();
	private final Supplier<MultiplicativeOperator> multiplicativeOperator2 = () -> getAPI().newMultiplication();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newMultiplicativeExpression().withAddedChildren(child1.get()).createNow(),
				getAPI().newMultiplicativeExpression().withAddedChildren(child2.get()).createNow(),
				ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(getAPI().newMultiplicativeExpression()
				.withAddedChildren(new MultiplicativeExpressionChild[] { child1.get(), child2.get() }).createNow(),
				getAPI().newMultiplicativeExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newMultiplicativeExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testMultiplicativeOperator() {
		this.testSimilarity(
				getAPI().newMultiplicativeExpression().withAddedMultiplicativeOperators(multiplicativeOperator1.get())
						.createNow(),
				getAPI().newMultiplicativeExpression().withAddedMultiplicativeOperators(multiplicativeOperator2.get())
						.createNow(),
				ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__MULTIPLICATIVE_OPERATORS);
	}

	@Test
	public void testMultiplicativeOperatorSize() {
		this.testSimilarity(
				getAPI().newMultiplicativeExpression()
						.withAddedMultiplicativeOperators(new MultiplicativeOperator[] { multiplicativeOperator1.get(),
								multiplicativeOperator2.get() })
						.createNow(),
				getAPI().newMultiplicativeExpression().withAddedMultiplicativeOperators(multiplicativeOperator1.get())
						.createNow(),
				ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__MULTIPLICATIVE_OPERATORS);
	}

	@Test
	public void testMultiplicativeOperatorNullCheck() {
		this.testSimilarityNullCheck(getAPI().newMultiplicativeExpression()
				.withAddedMultiplicativeOperators(multiplicativeOperator1.get()).createNow(),
				ExpressionsPackage.Literals.MULTIPLICATIVE_EXPRESSION__MULTIPLICATIVE_OPERATORS);
	}
}
