package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.InstanceOfExpressionChild;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class InstanceOfExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<InstanceOfExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<InstanceOfExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testChild() {
		this.testSimilarity(getAPI().newInstanceOfExpression().withChild(child1.get()).createNow(),
				getAPI().newInstanceOfExpression().withChild(child2.get()).createNow(),
				ExpressionsPackage.Literals.INSTANCE_OF_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newInstanceOfExpression().withChild(child1.get()).createNow(),
				ExpressionsPackage.Literals.INSTANCE_OF_EXPRESSION__CHILD);
	}
}
