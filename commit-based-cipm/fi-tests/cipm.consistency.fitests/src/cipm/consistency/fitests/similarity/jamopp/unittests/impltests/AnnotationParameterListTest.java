package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AnnotationParameterListTest extends AbstractJaMoPPSimilarityTest {

	private final Supplier<AnnotationAttributeSetting> setting1 = () -> getAPI().newAnnotationAttributeSetting()
			.withValue(getAPI().newDecimalIntegerLiteral(1)).createNow();
	private final Supplier<AnnotationAttributeSetting> setting2 = () -> getAPI().newAnnotationAttributeSetting()
			.withValue(getAPI().newDecimalIntegerLiteral(2)).createNow();

	@Test
	public void testSetting() {
		this.testSimilarity(getAPI().newAnnotationParameterList(setting1.get()),
				getAPI().newAnnotationParameterList(setting2.get()),
				AnnotationsPackage.Literals.ANNOTATION_PARAMETER_LIST__SETTINGS);
	}

	@Test
	public void testSettingSize() {
		this.testSimilarity(
				getAPI().newAnnotationParameterList(new AnnotationAttributeSetting[] { setting1.get(), setting2.get() }),
				getAPI().newAnnotationParameterList(setting1.get()),
				AnnotationsPackage.Literals.ANNOTATION_PARAMETER_LIST__SETTINGS);
	}

	@Test
	public void testSettingNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAnnotationParameterList(setting1.get()),
				AnnotationsPackage.Literals.ANNOTATION_PARAMETER_LIST__SETTINGS);
	}
}
