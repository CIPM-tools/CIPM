package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class SingleAnnotationParameterTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AnnotationValue> value1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AnnotationValue> value2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newSingleAnnotationParameter(value1.get()),
				getAPI().newSingleAnnotationParameter(value2.get()),
				AnnotationsPackage.Literals.SINGLE_ANNOTATION_PARAMETER__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newSingleAnnotationParameter(value1.get()),
				AnnotationsPackage.Literals.SINGLE_ANNOTATION_PARAMETER__VALUE);
	}
}
