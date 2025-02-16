package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.CastExpression;
import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.MultiplicativeExpressionChild;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.expressions.CastExpressionInitialiser;

/**
 * 
 * General child and child are the same. Therefore testChild is left out.
 * 
 * @author Alp Torac Genc
 */
public class CastExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions, UsesTypeReferences {
	protected CastExpression initElement(TypeReference[] additionalBoundsArr, MultiplicativeExpressionChild child) {
		var ceInit = new CastExpressionInitialiser();
		var ce = ceInit.instantiate();
		Assertions.assertTrue(ceInit.addAdditionalBounds(ce, additionalBoundsArr));
		Assertions.assertTrue(ceInit.setChild(ce, child));
		return ce;
	}

	@Test
	public void testAdditionalBound() {
		this.testSimilarity(this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }, null),
				this.initElement(new TypeReference[] { this.createMinimalClsRef("cls2") }, null),
				ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS);
	}

	@Test
	public void testAdditionalBoundSize() {
		this.testSimilarity(
				this.initElement(
						new TypeReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") },
						null),
				this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }, null),
				ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS);
	}

	@Test
	public void testAdditionalBoundNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new TypeReference[] { this.createMinimalClsRef("cls1") }, null),
				new CastExpressionInitialiser(), false, ExpressionsPackage.Literals.CAST_EXPRESSION__ADDITIONAL_BOUNDS);
	}

	@Test
	public void testChild() {
		this.testSimilarity(this.initElement(null, this.createDecimalIntegerLiteral(1)),
				this.initElement(null, this.createDecimalIntegerLiteral(2)),
				ExpressionsPackage.Literals.CAST_EXPRESSION__GENERAL_CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createDecimalIntegerLiteral(1)),
				new CastExpressionInitialiser(), false, ExpressionsPackage.Literals.CAST_EXPRESSION__GENERAL_CHILD);
	}
}
