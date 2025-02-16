package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.NestedExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.NestedExpressionInitialiser;

public class NestedExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected NestedExpression initElement(Expression expr) {
		var init = new NestedExpressionInitialiser();
		var result = init.instantiate();
		Assertions.assertTrue(init.setExpression(result, expr));
		return result;
	}

	@Test
	public void testExpression() {
		var objOne = this.initElement(this.createMinimalFalseEE());
		var objTwo = this.initElement(this.createMinimalTrueEE());

		this.testSimilarity(objOne, objTwo, ExpressionsPackage.Literals.NESTED_EXPRESSION__EXPRESSION);
	}

	@Test
	public void testExpressionNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalFalseEE()), new NestedExpressionInitialiser(),
				false, ExpressionsPackage.Literals.NESTED_EXPRESSION__EXPRESSION);
	}
}
