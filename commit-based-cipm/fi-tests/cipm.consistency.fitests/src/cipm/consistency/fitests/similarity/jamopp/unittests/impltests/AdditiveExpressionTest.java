package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.AdditiveExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.operators.AdditiveOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AdditiveExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AdditiveExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AdditiveExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<AdditiveOperator> additiveOperator1 = () -> getAPI().newAddition();
	private final Supplier<AdditiveOperator> additiveOperator2 = () -> getAPI().newSubtraction();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newAdditiveExpression().withAddedChildren(child1.get()).createNow(),
				getAPI().newAdditiveExpression().withAddedChildren(child2.get()).createNow(),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newAdditiveExpression()
						.withAddedChildren(new AdditiveExpressionChild[] { child1.get(), child2.get() }).createNow(),
				getAPI().newAdditiveExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAdditiveExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testAdditiveOperator() {
		this.testSimilarity(getAPI().newAdditiveExpression().withAddedAdditiveOperators(additiveOperator1.get()).createNow(),
				getAPI().newAdditiveExpression().withAddedAdditiveOperators(additiveOperator2.get()).createNow(),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__ADDITIVE_OPERATORS);
	}

	@Test
	public void testAdditiveOperatorSize() {
		this.testSimilarity(
				getAPI().newAdditiveExpression()
						.withAddedAdditiveOperators(new AdditiveOperator[] { additiveOperator1.get(), additiveOperator2.get() }).createNow(),
				getAPI().newAdditiveExpression().withAddedAdditiveOperators(additiveOperator1.get()).createNow(),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__ADDITIVE_OPERATORS);
	}

	@Test
	public void testAdditiveOperatorNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAdditiveExpression().withAddedAdditiveOperators(additiveOperator1.get()).createNow(),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__ADDITIVE_OPERATORS);
	}
}
