package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.ConditionalAndExpression;
import org.emftext.language.java.expressions.ConditionalAndExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.ConditionalAndExpressionInitialiser;

public class ConditionalAndExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected ConditionalAndExpression initElement(ConditionalAndExpressionChild[] children) {
		var caeInit = new ConditionalAndExpressionInitialiser();
		var cae = caeInit.instantiate();
		Assertions.assertTrue(caeInit.addChildren(cae, children));
		return cae;
	}

	@Test
	public void testChild() {
		this.testSimilarity(
				this.initElement(new ConditionalAndExpressionChild[] { this.createDecimalIntegerLiteral(1) }),
				this.initElement(new ConditionalAndExpressionChild[] { this.createDecimalIntegerLiteral(2) }),
				ExpressionsPackage.Literals.CONDITIONAL_AND_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				this.initElement(new ConditionalAndExpressionChild[] { this.createDecimalIntegerLiteral(1),
						this.createDecimalIntegerLiteral(2) }),
				this.initElement(new ConditionalAndExpressionChild[] { this.createDecimalIntegerLiteral(1) }),
				ExpressionsPackage.Literals.CONDITIONAL_AND_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(new ConditionalAndExpressionChild[] { this.createDecimalIntegerLiteral(1) }),
				new ConditionalAndExpressionInitialiser(), false,
				ExpressionsPackage.Literals.CONDITIONAL_AND_EXPRESSION__CHILDREN);
	}
}
