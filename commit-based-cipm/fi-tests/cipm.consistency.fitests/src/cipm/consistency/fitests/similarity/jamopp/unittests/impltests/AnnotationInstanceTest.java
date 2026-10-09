package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.annotations.AnnotationsPackage;
import org.emftext.language.java.annotations.SingleAnnotationParameter;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AnnotationInstanceTest extends AbstractJaMoPPSimilarityTest {

	private final Supplier<ConcreteClassifier> annotation1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<ConcreteClassifier> annotation2 = () -> getAPI().newClass().withName("cls2").createNow();

	private final Supplier<SingleAnnotationParameter> parameter1 = () -> getAPI()
			.newSingleAnnotationParameter(getAPI().newNullLiteral());
	private final Supplier<SingleAnnotationParameter> parameter2 = () -> getAPI()
			.newSingleAnnotationParameter(getAPI().newDecimalIntegerLiteral(1));

	@Test
	public void testAnnotation() {
		this.testSimilarity(getAPI().newAnnotationInstance().withAnnotation(annotation1.get()).createNow(),
				getAPI().newAnnotationInstance().withAnnotation(annotation2.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_INSTANCE__ANNOTATION);
	}

	@Test
	public void testAnnotationNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAnnotationInstance().withAnnotation(annotation1.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_INSTANCE__ANNOTATION);
	}

	@Test
	public void testParameter() {
		this.testSimilarity(getAPI().newAnnotationInstance().withParameter(parameter1.get()).createNow(),
				getAPI().newAnnotationInstance().withParameter(parameter2.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_INSTANCE__PARAMETER);
	}

	@Test
	public void testParameterNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAnnotationInstance().withParameter(parameter1.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTATION_INSTANCE__PARAMETER);
	}
}
