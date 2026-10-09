package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExclusiveOrExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ExclusiveOrExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ExclusiveOrExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<ExclusiveOrExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newExclusiveOrExpression(child1.get()),
				getAPI().newExclusiveOrExpression(child2.get()),
				ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newExclusiveOrExpression(new ExclusiveOrExpressionChild[] { child1.get(), child2.get() }),
				getAPI().newExclusiveOrExpression(child1.get()),
				ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newExclusiveOrExpression(child1.get()),
				ExpressionsPackage.Literals.EXCLUSIVE_OR_EXPRESSION__CHILDREN);
	}
}
