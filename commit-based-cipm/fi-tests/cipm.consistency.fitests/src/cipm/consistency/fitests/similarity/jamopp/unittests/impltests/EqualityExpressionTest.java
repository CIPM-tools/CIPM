package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.EqualityExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.operators.EqualityOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class EqualityExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<EqualityExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<EqualityExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<EqualityOperator> equalityOperator1 = () -> getAPI().newEqual();
	private final Supplier<EqualityOperator> equalityOperator2 = () -> getAPI().newNotEqual();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newEqualityExpression().withAddedChildren(child1.get()).createNow(),
				getAPI().newEqualityExpression().withAddedChildren(child2.get()).createNow(),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newEqualityExpression()
						.withAddedChildren(new EqualityExpressionChild[] { child1.get(), child2.get() }).createNow(),
				getAPI().newEqualityExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newEqualityExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__CHILDREN);
	}

	@Test
	public void testEqualityOperator() {
		this.testSimilarity(
				getAPI().newEqualityExpression().withAddedEqualityOperators(equalityOperator1.get()).createNow(),
				getAPI().newEqualityExpression().withAddedEqualityOperators(equalityOperator2.get()).createNow(),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__EQUALITY_OPERATORS);
	}

	@Test
	public void testEqualityOperatorSize() {
		this.testSimilarity(
				getAPI().newEqualityExpression()
						.withAddedEqualityOperators(
								new EqualityOperator[] { equalityOperator1.get(), equalityOperator2.get() })
						.createNow(),
				getAPI().newEqualityExpression().withAddedEqualityOperators(equalityOperator1.get()).createNow(),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__EQUALITY_OPERATORS);
	}

	@Test
	public void testEqualityOperatorNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newEqualityExpression().withAddedEqualityOperators(equalityOperator1.get()).createNow(),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__EQUALITY_OPERATORS);
	}
}
