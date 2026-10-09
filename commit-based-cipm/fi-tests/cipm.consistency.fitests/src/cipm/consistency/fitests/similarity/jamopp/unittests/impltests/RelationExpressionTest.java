package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.RelationExpressionChild;
import org.emftext.language.java.operators.RelationOperator;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class RelationExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<RelationExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<RelationExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<RelationOperator> relationOperator1 = () -> getAPI().newGreaterThan();
	private final Supplier<RelationOperator> relationOperator2 = () -> getAPI().newLessThan();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newRelationExpression().withAddedChildren(child1.get()).createNow(),
				getAPI().newRelationExpression().withAddedChildren(child2.get()).createNow(),
				ExpressionsPackage.Literals.RELATION_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newRelationExpression()
						.withAddedChildren(new RelationExpressionChild[] { child1.get(), child2.get() }).createNow(),
				getAPI().newRelationExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.RELATION_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newRelationExpression().withAddedChildren(child1.get()).createNow(),
				ExpressionsPackage.Literals.RELATION_EXPRESSION__CHILDREN);
	}

	@Test
	public void testRelationOperator() {
		this.testSimilarity(
				getAPI().newRelationExpression().withAddedRelationOperators(relationOperator1.get()).createNow(),
				getAPI().newRelationExpression().withAddedRelationOperators(relationOperator2.get()).createNow(),
				ExpressionsPackage.Literals.RELATION_EXPRESSION__RELATION_OPERATORS);
	}

	@Test
	public void testRelationOperatorSize() {
		this.testSimilarity(
				getAPI().newRelationExpression()
						.withAddedRelationOperators(
								new RelationOperator[] { relationOperator1.get(), relationOperator2.get() })
						.createNow(),
				getAPI().newRelationExpression().withAddedRelationOperators(relationOperator1.get()).createNow(),
				ExpressionsPackage.Literals.RELATION_EXPRESSION__RELATION_OPERATORS);
	}

	@Test
	public void testRelationOperatorNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newRelationExpression().withAddedRelationOperators(relationOperator1.get()).createNow(),
				ExpressionsPackage.Literals.RELATION_EXPRESSION__RELATION_OPERATORS);
	}
}
