package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ClassifierReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Classifier> target1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<Classifier> target2 = () -> getAPI().newClass().withName("cls2").createNow();

	@Test
	public void testTarget() {
		this.testSimilarity(getAPI().newClassifierReference().withTarget(target1.get()).createNow(),
				getAPI().newClassifierReference().withTarget(target2.get()).createNow(),
				TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}

	@Test
	public void testTargetNullCheck() {
		this.testSimilarityNullCheck(getAPI().newClassifierReference().withTarget(target1.get()).createNow(),
				TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}
}
