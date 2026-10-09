package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.emftext.language.java.members.InterfaceMethod;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AnnotationAttributeSettingTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<InterfaceMethod> attribute1 = () -> getAPI().newInterfaceMethod().withName("im1Name")
			.createNow();
	private final Supplier<InterfaceMethod> attribute2 = () -> getAPI().newInterfaceMethod().withName("im2Name")
			.createNow();

	private final Supplier<AnnotationValue> value1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AnnotationValue> value2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testAttribute() {
		this.testSimilarity(getAPI().newAnnotationAttributeSetting().withAttribute(attribute1.get()).createNow(),
				getAPI().newAnnotationAttributeSetting().withAttribute(attribute2.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__ATTRIBUTE);
	}

	@Test
	public void testAttributeNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAnnotationAttributeSetting().withAttribute(attribute1.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__ATTRIBUTE);
	}

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newAnnotationAttributeSetting().withValue(value1.get()).createNow(),
				getAPI().newAnnotationAttributeSetting().withValue(value2.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAnnotationAttributeSetting().withValue(value1.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_ATTRIBUTE_SETTING__VALUE);
	}
}
