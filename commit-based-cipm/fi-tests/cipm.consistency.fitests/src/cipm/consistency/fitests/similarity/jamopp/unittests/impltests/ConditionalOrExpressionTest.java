package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ConditionalOrExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ConditionalOrExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ConditionalOrExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<ConditionalOrExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newConditionalOrExpression(child1.get()),
				getAPI().newConditionalOrExpression(child2.get()),
				ExpressionsPackage.Literals.CONDITIONAL_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newConditionalOrExpression(new ConditionalOrExpressionChild[] { child1.get(), child2.get() }),
				getAPI().newConditionalOrExpression(child1.get()),
				ExpressionsPackage.Literals.CONDITIONAL_OR_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newConditionalOrExpression(child1.get()),
				ExpressionsPackage.Literals.CONDITIONAL_OR_EXPRESSION__CHILDREN);
	}
}
