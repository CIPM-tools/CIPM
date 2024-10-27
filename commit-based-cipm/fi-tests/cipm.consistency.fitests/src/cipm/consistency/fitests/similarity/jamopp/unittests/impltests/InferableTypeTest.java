package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.initialisers.jamopp.types.InferableTypeInitialiser;

public class InferableTypeTest extends AbstractJaMoPPSimilarityTest implements UsesConcreteClassifiers {
	protected InferableType initElement(Classifier target) {
		var init = new InferableTypeInitialiser();
		var res = init.instantiate();

		Assertions.assertTrue(init.setTarget(res, target));
		return res;
	}

	@Test
	public void testTarget() {
		var objOne = this.initElement(this.createMinimalClass("cls1"));
		var objTwo = this.initElement(this.createMinimalClass("cls2"));

		this.testSimilarity(objOne, objTwo, InferableType.class, TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}

	@Test
	public void testTargetNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalClass("cls1")), new InferableTypeInitialiser(),
				false, InferableType.class, TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}
}
