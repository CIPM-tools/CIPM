package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.InclusiveOrExpressionChild;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class InclusiveOrExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<InclusiveOrExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<InclusiveOrExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newInclusiveOrExpression(child1.get()),
				getAPI().newInclusiveOrExpression(child2.get()),
				ExpressionsPackage.Literals.INCLUSIVE_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newInclusiveOrExpression(new InclusiveOrExpressionChild[] { child1.get(), child2.get() }),
				getAPI().newInclusiveOrExpression(child1.get()),
				ExpressionsPackage.Literals.INCLUSIVE_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newInclusiveOrExpression(child1.get()),
				ExpressionsPackage.Literals.INCLUSIVE_OR_EXPRESSION__CHILDREN);
	}
}
