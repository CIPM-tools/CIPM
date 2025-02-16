package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.emftext.language.java.members.InterfaceMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesAnnotationValues;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesMethods;
import cipm.consistency.initialisers.jamopp.annotations.AnnotationAttributeSettingInitialiser;

public class AnnotationAttributeSettingTest extends AbstractJaMoPPSimilarityTest implements UsesMethods, UsesAnnotationValues {
	protected AnnotationAttributeSetting initElement(InterfaceMethod attr, AnnotationValue val) {
		var initialiser = new AnnotationAttributeSettingInitialiser();
		AnnotationAttributeSetting result = initialiser.instantiate();
		Assertions.assertTrue(initialiser.setAttribute(result, attr));
		Assertions.assertTrue(initialiser.setValue(result, val));

		return result;
	}

	@Test
	public void testAttribute() {
		var objOne = this.initElement(this.createMinimalInterfaceMethodWithNullReturn("im1Name"), null);
		var objTwo = this.initElement(this.createMinimalInterfaceMethodWithNullReturn("im2Name"), null);

		this.testSimilarity(objOne, objTwo, AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__ATTRIBUTE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testAttributeNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalInterfaceMethodWithNullReturn("im1Name"), null),
				new AnnotationAttributeSettingInitialiser(), false,
				AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__ATTRIBUTE);
	}

	@Test
	public void testValue() {
		var objOne = this.initElement(null, this.createNullLiteral());
		var objTwo = this.initElement(null, this.createMinimalSR("val"));

		this.testSimilarity(objOne, objTwo, AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__VALUE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createNullLiteral()),
				new AnnotationAttributeSettingInitialiser(), false,
				AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__VALUE);
	}
}
