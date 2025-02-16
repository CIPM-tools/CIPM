package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.EqualityExpression;
import org.emftext.language.java.expressions.EqualityExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.operators.EqualityOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.EqualityExpressionInitialiser;

public class EqualityExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected EqualityExpression initElement(EqualityExpressionChild[] children, EqualityOperator[] ops) {
		var eeInit = new EqualityExpressionInitialiser();
		var ee = eeInit.instantiate();
		Assertions.assertTrue(eeInit.addChildren(ee, children));
		Assertions.assertTrue(eeInit.addEqualityOperators(ee, ops));
		return ee;
	}

	@Test
	public void testChild() {
		this.testSimilarity(
				this.initElement(new EqualityExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				this.initElement(new EqualityExpressionChild[] { this.createDecimalIntegerLiteral(2) }, null),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				this.initElement(new EqualityExpressionChild[] { this.createDecimalIntegerLiteral(1),
						this.createDecimalIntegerLiteral(2) }, null),
				this.initElement(new EqualityExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(new EqualityExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				new EqualityExpressionInitialiser(), false, ExpressionsPackage.Literals.EQUALITY_EXPRESSION__CHILDREN);
	}

	@Test
	public void testEqualityOperator() {
		this.testSimilarity(this.initElement(null, new EqualityOperator[] { this.createEqualityOperator() }),
				this.initElement(null, new EqualityOperator[] { this.createNotEqualOperator() }),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__EQUALITY_OPERATORS);
	}

	@Test
	public void testEqualityOperatorSize() {
		this.testSimilarity(
				this.initElement(null,
						new EqualityOperator[] { this.createEqualityOperator(), this.createNotEqualOperator() }),
				this.initElement(null, new EqualityOperator[] { this.createEqualityOperator() }),
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__EQUALITY_OPERATORS);
	}

	@Test
	public void testEqualityOperatorNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, new EqualityOperator[] { this.createEqualityOperator() }),
				new EqualityExpressionInitialiser(), false,
				ExpressionsPackage.Literals.EQUALITY_EXPRESSION__EQUALITY_OPERATORS);
	}
}
