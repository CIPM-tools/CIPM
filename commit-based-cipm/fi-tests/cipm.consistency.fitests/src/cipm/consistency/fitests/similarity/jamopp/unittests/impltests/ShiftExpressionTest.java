package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.ShiftExpression;
import org.emftext.language.java.expressions.ShiftExpressionChild;
import org.emftext.language.java.operators.ShiftOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.ShiftExpressionInitialiser;

public class ShiftExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected ShiftExpression initElement(ShiftExpressionChild[] children, ShiftOperator[] ops) {
		var seInit = new ShiftExpressionInitialiser();
		var se = seInit.instantiate();
		Assertions.assertTrue(seInit.addChildren(se, children));
		Assertions.assertTrue(seInit.addShiftOperators(se, ops));
		return se;
	}

	@Test
	public void testChild() {
		this.testSimilarity(this.initElement(new ShiftExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				this.initElement(new ShiftExpressionChild[] { this.createDecimalIntegerLiteral(2) }, null),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				this.initElement(new ShiftExpressionChild[] { this.createDecimalIntegerLiteral(1),
						this.createDecimalIntegerLiteral(2) }, null),
				this.initElement(new ShiftExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(new ShiftExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				new ShiftExpressionInitialiser(), false, ExpressionsPackage.Literals.SHIFT_EXPRESSION__CHILDREN);
	}

	@Test
	public void testShiftOperator() {
		this.testSimilarity(this.initElement(null, new ShiftOperator[] { this.createLeftShiftOperator() }),
				this.initElement(null, new ShiftOperator[] { this.createRightShiftOperator() }),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS);
	}

	@Test
	public void testShiftOperatorSize() {
		this.testSimilarity(
				this.initElement(null,
						new ShiftOperator[] { this.createLeftShiftOperator(), this.createRightShiftOperator() }),
				this.initElement(null, new ShiftOperator[] { this.createLeftShiftOperator() }),
				ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS);
	}

	@Test
	public void testShiftOperatorNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, new ShiftOperator[] { this.createLeftShiftOperator() }),
				new ShiftExpressionInitialiser(), false, ExpressionsPackage.Literals.SHIFT_EXPRESSION__SHIFT_OPERATORS);
	}
}
