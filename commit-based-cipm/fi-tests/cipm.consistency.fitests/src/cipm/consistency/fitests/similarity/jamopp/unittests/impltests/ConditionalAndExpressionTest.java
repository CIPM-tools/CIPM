package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ConditionalAndExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ConditionalAndExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ConditionalAndExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<ConditionalAndExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newConditionalAndExpression(child1.get()),
				getAPI().newConditionalAndExpression(child2.get()),
				ExpressionsPackage.Literals.CONDITIONAL_AND_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(
				getAPI().newConditionalAndExpression(
						new ConditionalAndExpressionChild[] { child1.get(), child2.get() }),
				getAPI().newConditionalAndExpression(child1.get()),
				ExpressionsPackage.Literals.CONDITIONAL_AND_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newConditionalAndExpression(child1.get()),
				ExpressionsPackage.Literals.CONDITIONAL_AND_EXPRESSION__CHILDREN);
	}
}
