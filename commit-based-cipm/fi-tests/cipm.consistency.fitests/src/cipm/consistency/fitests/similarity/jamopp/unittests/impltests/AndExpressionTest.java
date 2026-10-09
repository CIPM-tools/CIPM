package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.AndExpressionChild;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AndExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AndExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AndExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newAndExpression(child1.get()), getAPI().newAndExpression(child2.get()),
				ExpressionsPackage.Literals.AND_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildSize() {
		this.testSimilarity(getAPI().newAndExpression(new AndExpressionChild[] { child1.get(), child2.get() }),
				getAPI().newAndExpression(child1.get()), ExpressionsPackage.Literals.AND_EXPRESSION__CHILDREN);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAndExpression(child1.get()),
				ExpressionsPackage.Literals.AND_EXPRESSION__CHILDREN);
	}
}
