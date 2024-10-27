package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.MethodReferenceExpressionChild;
import org.emftext.language.java.expressions.PrimaryExpressionReferenceExpression;
import org.emftext.language.java.references.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.expressions.PrimaryExpressionReferenceExpressionInitialiser;

public class PrimaryExpressionReferenceExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected PrimaryExpressionReferenceExpression initElement(MethodReferenceExpressionChild child, Reference metRef) {
		var pereInit = new PrimaryExpressionReferenceExpressionInitialiser();
		var pere = pereInit.instantiate();
		Assertions.assertTrue(pereInit.setChild(pere, child));
		Assertions.assertTrue(pereInit.setMethodReference(pere, metRef));
		return pere;
	}

	@Test
	public void testChild() {
		this.testSimilarity(this.initElement(this.createDecimalIntegerLiteral(1), null),
				this.initElement(this.createDecimalIntegerLiteral(2), null),
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__CHILD);
	}

	@Test
	public void testChildNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createDecimalIntegerLiteral(1), null),
				new PrimaryExpressionReferenceExpressionInitialiser(), false,
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__CHILD);
	}

	@Test
	public void testMethodReference() {
		this.testSimilarity(this.initElement(null, this.createMinimalSR("str1")),
				this.initElement(null, this.createMinimalSR("str2")),
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__METHOD_REFERENCE);
	}

	@Test
	public void testMethodReferenceNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createMinimalSR("str1")),
				new PrimaryExpressionReferenceExpressionInitialiser(), false,
				ExpressionsPackage.Literals.PRIMARY_EXPRESSION_REFERENCE_EXPRESSION__METHOD_REFERENCE);
	}
}
