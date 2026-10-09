package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.MultiplicativeExpressionChild;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

/**
 * 
 * General child and child are the same. Therefore testChild is left out.
 * 
 * @author Alp Torac Genc
 */
public class CastExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> additionalBound1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> additionalBound2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	private final Supplier<MultiplicativeExpressionChild> generalChild1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<MultiplicativeExpressionChild> generalChild2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testAdditionalBound() {
		this.testSimilarity(getAPI().newCastExpression().withAddedAdditionalBounds(additionalBound1.get()).createNow(),
				getAPI().newCastExpression().withAddedAdditionalBounds(additionalBound2.get()).createNow(),
				ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS);
	}

	@Test
	public void testAdditionalBoundSize() {
		this.testSimilarity(
				getAPI().newCastExpression()
						.withAddedAdditionalBounds(
								new TypeReference[] { additionalBound1.get(), additionalBound2.get() })
						.createNow(),
				getAPI().newCastExpression().withAddedAdditionalBounds(additionalBound1.get()).createNow(),
				ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS);
	}

	@Test
	public void testAdditionalBoundNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newCastExpression().withAddedAdditionalBounds(additionalBound1.get()).createNow(),
				ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS);
	}

	@Test
	public void testGeneralChild() {
		this.testSimilarity(getAPI().newCastExpression().withGeneralChild(generalChild1.get()).createNow(),
				getAPI().newCastExpression().withGeneralChild(generalChild2.get()).createNow(),
				ExpressionsPackage.Literals.CAST_EXPRESSION__GENERAL_CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(getAPI().newCastExpression().withGeneralChild(generalChild1.get()).createNow(),
				ExpressionsPackage.Literals.CAST_EXPRESSION__GENERAL_CHILD);
	}
}
