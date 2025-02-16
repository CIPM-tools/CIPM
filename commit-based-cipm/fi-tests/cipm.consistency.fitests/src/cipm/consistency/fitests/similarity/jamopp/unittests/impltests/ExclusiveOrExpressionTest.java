package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.ExclusiveOrExpression;
import org.emftext.language.java.expressions.ExclusiveOrExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.ExclusiveOrExpressionInitialiser;

public class ExclusiveOrExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected ExclusiveOrExpression initElement(ExclusiveOrExpressionChild[] children) {
		var eoeInit = new ExclusiveOrExpressionInitialiser();
		var eoe = eoeInit.instantiate();
		Assertions.assertTrue(eoeInit.addChildren(eoe, children));
		return eoe;
	}

	@Test
	public void testChild() {
		this.testSimilarity(this.initElement(new ExclusiveOrExpressionChild[] { this.createDecimalIntegerLiteral(1) }),
				this.initElement(new ExclusiveOrExpressionChild[] { this.createDecimalIntegerLiteral(2) }),
				ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				this.initElement(new ExclusiveOrExpressionChild[] { this.createDecimalIntegerLiteral(1),
						this.createDecimalIntegerLiteral(2) }),
				this.initElement(new ExclusiveOrExpressionChild[] { this.createDecimalIntegerLiteral(1) }),
				ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(new ExclusiveOrExpressionChild[] { this.createDecimalIntegerLiteral(1) }),
				new ExclusiveOrExpressionInitialiser(), false,
				ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN);
	}
}
