package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.AdditiveExpression;
import org.emftext.language.java.expressions.AdditiveExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.operators.AdditiveOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.AdditiveExpressionInitialiser;

public class AdditiveExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected AdditiveExpression initElement(AdditiveExpressionChild[] children, AdditiveOperator[] ops) {
		var aeInit = new AdditiveExpressionInitialiser();
		var ae = aeInit.instantiate();
		Assertions.assertTrue(aeInit.addChildren(ae, children));
		Assertions.assertTrue(aeInit.addAdditiveOperators(ae, ops));
		return ae;
	}

	@Test
	public void testChild() {
		this.testSimilarity(
				this.initElement(new AdditiveExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				this.initElement(new AdditiveExpressionChild[] { this.createDecimalIntegerLiteral(2) }, null),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				this.initElement(new AdditiveExpressionChild[] { this.createDecimalIntegerLiteral(1),
						this.createDecimalIntegerLiteral(2) }, null),
				this.initElement(new AdditiveExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(new AdditiveExpressionChild[] { this.createDecimalIntegerLiteral(1) }, null),
				new AdditiveExpressionInitialiser(), false, ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__CHILDREN);
	}

	@Test
	public void testAdditiveOperator() {
		this.testSimilarity(this.initElement(null, new AdditiveOperator[] { this.createAdditionOperator() }),
				this.initElement(null, new AdditiveOperator[] { this.createSubtractionOperator() }),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__ADDITIVE_OPERATORS);
	}

	@Test
	public void testAdditiveOperatorSize() {
		this.testSimilarity(
				this.initElement(null,
						new AdditiveOperator[] { this.createAdditionOperator(), this.createSubtractionOperator() }),
				this.initElement(null, new AdditiveOperator[] { this.createAdditionOperator() }),
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__ADDITIVE_OPERATORS);
	}

	@Test
	public void testAdditiveOperatorNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, new AdditiveOperator[] { this.createAdditionOperator() }),
				new AdditiveExpressionInitialiser(), false,
				ExpressionsPackage.Literals.ADDITIVE_EXPRESSION__ADDITIVE_OPERATORS);
	}
}
