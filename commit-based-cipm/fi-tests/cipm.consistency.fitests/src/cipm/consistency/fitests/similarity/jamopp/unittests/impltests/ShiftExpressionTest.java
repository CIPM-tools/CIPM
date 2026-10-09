package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.ShiftExpressionChild;
import org.emftext.language.java.operators.ShiftOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ShiftExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ShiftExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<ShiftExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<ShiftOperator> shiftOperator1 = () -> getAPI().newLeftShift();
	private final Supplier<ShiftOperator> shiftOperator2 = () -> getAPI().newRightShift();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newShiftExpression().withAddedChildren(child1.get()).createNow(),
				getAPI().newShiftExpression().withAddedChildren(child2.get()).createNow(),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newShiftExpression()
						.withAddedChildren(new ShiftExpressionChild[] { child1.get(), child2.get() }).createNow(),
				getAPI().newShiftExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newShiftExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN);
	}

	@Test
	public void testShiftOperator() {
		this.testSimilarity(getAPI().newShiftExpression().withAddedShiftOperators(shiftOperator1.get()).createNow(),
				getAPI().newShiftExpression().withAddedShiftOperators(shiftOperator2.get()).createNow(),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS);
	}

	@Test
	public void testShiftOperatorSize() {
		this.testSimilarity(
				getAPI().newShiftExpression()
						.withAddedShiftOperators(new ShiftOperator[] { shiftOperator1.get(), shiftOperator2.get() })
						.createNow(),
				getAPI().newShiftExpression().withAddedShiftOperators(shiftOperator1.get()).createNow(),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS);
	}

	@Test
	public void testShiftOperatorNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newShiftExpression().withAddedShiftOperators(shiftOperator1.get()).createNow(),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS);
	}
}
