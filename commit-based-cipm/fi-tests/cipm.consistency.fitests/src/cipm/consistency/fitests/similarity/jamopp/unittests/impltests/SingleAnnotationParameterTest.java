package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.emftext.language.java.annotations.SingleAnnotationParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesAnnotationValues;
import cipm.consistency.initialisers.jamopp.annotations.SingleAnnotationParameterInitialiser;

public class SingleAnnotationParameterTest extends AbstractJaMoPPSimilarityTest implements UsesAnnotationValues {
	protected SingleAnnotationParameter initElement(AnnotationValue val) {
		var sapInit = new SingleAnnotationParameterInitialiser();
		var sap = sapInit.instantiate();
		Assertions.assertTrue(sapInit.setValue(sap, val));
		return sap;
	}

	@Test
	public void testValue() {
		var objOne = this.initElement(this.createNullLiteral());
		var objTwo = this.initElement(this.createMinimalSR("strVal"));

		this.testSimilarity(objOne, objTwo, AnnotationsPackage.Literals.SINGLE_ANNOTATION_PARAMETER__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createNullLiteral()),
				new SingleAnnotationParameterInitialiser(), false,
				AnnotationsPackage.Literals.SINGLE_ANNOTATION_PARAMETER__VALUE);
	}
}
