package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.types.ClassifierReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.types.NamespaceClassifierReferenceInitialiser;

public class NamespaceClassifierReferenceTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {
	protected NamespaceClassifierReference initElement(Classifier target, ClassifierReference[] clsRefs) {
		var ncrInit = new NamespaceClassifierReferenceInitialiser();
		var ncr = ncrInit.instantiate();
		Assertions.assertTrue(ncrInit.setTarget(ncr, target));
		Assertions.assertTrue(ncrInit.addClassifierReferences(ncr, clsRefs));
		return ncr;
	}

	@Test
	public void testTarget() {
		var objOne = this.initElement(this.createMinimalClassWithCU("cls1"), null);
		var objTwo = this.initElement(this.createMinimalClassWithCU("cls2"), null);

		this.testSimilarity(objOne, objTwo, NamespaceClassifierReference.class,
				TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}

	@Test
	public void testTargetNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalClassWithCU("cls1"), null),
				new ClassifierReferenceInitialiser(), false, NamespaceClassifierReference.class,
				TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}

	@Test
	public void testClassifierReference() {
		var objOne = this.initElement(null, new ClassifierReference[] { this.createMinimalClsRef("cls1") });
		var objTwo = this.initElement(null, new ClassifierReference[] { this.createMinimalClsRef("cls2") });

		this.testSimilarity(objOne, objTwo,
				TypesPackage.Literals.NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES);
	}

	@Test
	public void testClassifierReferenceSize() {
		var objOne = this.initElement(null,
				new ClassifierReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") });
		var objTwo = this.initElement(null, new ClassifierReference[] { this.createMinimalClsRef("cls1") });

		this.testSimilarity(objOne, objTwo,
				TypesPackage.Literals.NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES);
	}

	@Test
	public void testClassifierReferenceNullCheck() {
		this.testSimilarityNullCheck(
				this.initElement(null, new ClassifierReference[] { this.createMinimalClsRef("cls1") }),
				new NamespaceClassifierReferenceInitialiser(), false,
				TypesPackage.Literals.NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES);
	}
}
