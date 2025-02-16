package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.AssignmentExpressionChild;
import org.emftext.language.java.expressions.ConditionalExpression;
import org.emftext.language.java.expressions.ConditionalExpressionChild;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.ConditionalExpressionInitialiser;

public class ConditionalExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected ConditionalExpression initElement(ConditionalExpressionChild child, AssignmentExpressionChild exprChild,
			Expression exprIf, Expression generalExprElse) {
		var ceInit = new ConditionalExpressionInitialiser();
		var ce = ceInit.instantiate();
		Assertions.assertTrue(ceInit.setChild(ce, child));
		Assertions.assertTrue(ceInit.setExpressionChild(ce, exprChild));
		Assertions.assertTrue(ceInit.setExpressionIf(ce, exprIf));
		Assertions.assertTrue(ceInit.setGeneralExpressionElse(ce, generalExprElse));
		return ce;
	}

	@Test
	public void testChild() {
		this.testSimilarity(this.initElement(this.createDecimalIntegerLiteral(1), null, null, null),
				this.initElement(this.createDecimalIntegerLiteral(2), null, null, null),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createDecimalIntegerLiteral(1), null, null, null),
				new ConditionalExpressionInitialiser(), false,
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD);
	}

	@Test
	public void testExpressionChild() {
		this.testSimilarity(this.initElement(null, this.createDecimalIntegerLiteral(1), null, null),
				this.initElement(null, this.createDecimalIntegerLiteral(2), null, null),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD);
	}

	@Test
	public void testExpressionChildNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createDecimalIntegerLiteral(1), null, null),
				new ConditionalExpressionInitialiser(), false,
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__CHILD);
	}

	@Test
	public void testExpressionIf() {
		this.testSimilarity(this.initElement(null, null, this.createDecimalIntegerLiteral(1), null),
				this.initElement(null, null, this.createDecimalIntegerLiteral(2), null),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__EXPRESSION_IF);
	}

	@Test
	public void testExpressionIfNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, null, this.createDecimalIntegerLiteral(1), null),
				new ConditionalExpressionInitialiser(), false,
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__EXPRESSION_IF);
	}

	@Test
	public void testGeneralExpressionElse() {
		this.testSimilarity(this.initElement(null, null, null, this.createDecimalIntegerLiteral(1)),
				this.initElement(null, null, null, this.createDecimalIntegerLiteral(2)),
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__GENERAL_EXPRESSION_ELSE);
	}

	@Test
	public void testGeneralExpressionElseNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, null, null, this.createDecimalIntegerLiteral(1)),
				new ConditionalExpressionInitialiser(), false,
				ExpressionsPackage.Literals.CONDITIONAL_EXPRESSION__GENERAL_EXPRESSION_ELSE);
	}
}
