package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.MethodReferenceExpressionChild;
import org.emftext.language.java.references.Reference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class PrimaryExpressionReferenceExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<MethodReferenceExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<MethodReferenceExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<Reference> methodReference1 = () -> getAPI().newStringReference().withValue("a").createNow();
	private final Supplier<Reference> methodReference2 = () -> getAPI().newStringReference().withValue("b").createNow();

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newPrimaryExpressionReferenceExpression().withChild(child1.get()).createNow(),
				getAPI().newPrimaryExpressionReferenceExpression().withChild(child2.get()).createNow(),
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newPrimaryExpressionReferenceExpression().withChild(child1.get()).createNow(),
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__CHILD);
	}

	@Test
	public void testMethodReference() {
		this.testSimilarity(
				getAPI().newPrimaryExpressionReferenceExpression().withMethodReference(methodReference1.get())
						.createNow(),
				getAPI().newPrimaryExpressionReferenceExpression().withMethodReference(methodReference2.get())
						.createNow(),
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__METHOD_REFERENCE);
	}

	@Test
	public void testMethodReferenceNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newPrimaryExpressionReferenceExpression().withMethodReference(methodReference1.get())
						.createNow(),
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__METHOD_REFERENCE);
	}
}
