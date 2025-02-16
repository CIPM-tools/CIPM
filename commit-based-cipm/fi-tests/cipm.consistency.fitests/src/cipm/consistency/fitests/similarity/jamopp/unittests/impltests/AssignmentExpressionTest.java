package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.AssignmentExpression;
import org.emftext.language.java.expressions.AssignmentExpressionChild;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.operators.AssignmentOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.AssignmentExpressionInitialiser;

public class AssignmentExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected AssignmentExpression initElement(AssignmentOperator op, AssignmentExpressionChild child, Expression val) {
		var aeInit = new AssignmentExpressionInitialiser();
		var ae = aeInit.instantiate();
		Assertions.assertTrue(aeInit.setAssignmentOperator(ae, op));
		Assertions.assertTrue(aeInit.setChild(ae, child));
		Assertions.assertTrue(aeInit.setValue(ae, val));
		return ae;
	}

	@Test
	public void testAssignmentOperator() {
		this.testSimilarity(this.initElement(this.createAssignmentOperator(), null, null),
				this.initElement(this.createAssignmentOrOperator(), null, null),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__ASSIGNMENT_OPERATOR);
	}

	@Test
	public void testAssignmentOperatorNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createAssignmentOperator(), null, null),
				new AssignmentExpressionInitialiser(), false,
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__ASSIGNMENT_OPERATOR);
	}

	@Test
	public void testChild() {
		this.testSimilarity(this.initElement(null, this.createDecimalIntegerLiteral(1), null),
				this.initElement(null, this.createDecimalIntegerLiteral(2), null),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createDecimalIntegerLiteral(1), null),
				new AssignmentExpressionInitialiser(), false, ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__CHILD);
	}

	@Test
	public void testValue() {
		this.testSimilarity(this.initElement(null, null, this.createDecimalIntegerLiteral(1)),
				this.initElement(null, null, this.createDecimalIntegerLiteral(2)),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, null, this.createDecimalIntegerLiteral(1)),
				new AssignmentExpressionInitialiser(), false, ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__VALUE);
	}
}
