package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionList;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.ExpressionListInitialiser;

public class ExpressionListTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected ExpressionList initElement(Expression[] exprs) {
		var elInit = new ExpressionListInitialiser();
		var el = elInit.instantiate();
		Assertions.assertTrue(elInit.addExpressions(el, exprs));
		return el;
	}

	@Test
	public void testExpression() {
		var objOne = this.initElement(new Expression[] { this.createMinimalFalseEE() });
		var objTwo = this.initElement(new Expression[] { this.createMinimalTrueNEE() });

		this.testSimilarity(objOne, objTwo, ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS);
	}

	@Test
	public void testExpressionSize() {
		var objOne = this.initElement(new Expression[] { this.createMinimalFalseEE(), this.createMinimalTrueNEE() });
		var objTwo = this.initElement(new Expression[] { this.createMinimalFalseEE() });

		this.testSimilarity(objOne, objTwo, ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS);
	}

	@Test
	public void testExpressionNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new Expression[] { this.createMinimalFalseEE() }),
				new ExpressionListInitialiser(), false, ExpressionsPackage.Literals.EXPRESSION_LIST__EXPRESSIONS);
	}
}
