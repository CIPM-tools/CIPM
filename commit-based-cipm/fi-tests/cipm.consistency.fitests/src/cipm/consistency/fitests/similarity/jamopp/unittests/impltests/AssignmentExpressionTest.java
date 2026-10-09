package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.AssignmentExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.operators.AssignmentOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AssignmentExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AssignmentOperator> assignmentOperator1 = () -> getAPI().newAssignmentAnd();
	private final Supplier<AssignmentOperator> assignmentOperator2 = () -> getAPI().newAssignmentOr();

	private final Supplier<AssignmentExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AssignmentExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<AssignmentExpressionChild> value1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AssignmentExpressionChild> value2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testAssignmentOperator() {
		this.testSimilarity(
				getAPI().newAssignmentExpression().withAssignmentOperator(assignmentOperator1.get()).createNow(),
				getAPI().newAssignmentExpression().withAssignmentOperator(assignmentOperator2.get()).createNow(),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__ASSIGNMENT_OPERATOR);
	}

	@Test
	public void testAssignmentOperatorNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newAssignmentExpression().withAssignmentOperator(assignmentOperator1.get()).createNow(),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__ASSIGNMENT_OPERATOR);
	}

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newAssignmentExpression().withChild(child1.get()).createNow(),
				getAPI().newAssignmentExpression().withChild(child2.get()).createNow(),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAssignmentExpression().withChild(child1.get()).createNow(),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__CHILD);
	}

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newAssignmentExpression().withValue(value1.get()).createNow(),
				getAPI().newAssignmentExpression().withValue(value2.get()).createNow(),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAssignmentExpression().withValue(value1.get()).createNow(),
				ExpressionsPackage.Literals.ASSIGNMENT_EXPRESSION__VALUE);
	}
}
