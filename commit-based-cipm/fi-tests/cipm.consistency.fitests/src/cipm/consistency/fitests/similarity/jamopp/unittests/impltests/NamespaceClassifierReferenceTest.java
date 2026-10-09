package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

/**
 * The "target" of a NamespaceClassifierReference is stored in
 * "NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES".
 * <p>
 * TypeReference.setTarget(...) does NOT belong to an actual feature, but
 * returns a derived value.
 */
public class NamespaceClassifierReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ClassifierReference> classifierReferences1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<ClassifierReference> classifierReferences2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testClassifierReferences() {
		this.testSimilarity(
				getAPI().newNamespaceClassifierReference().withAddedClassifierReferences(classifierReferences1.get())
						.createNow(),
				getAPI().newNamespaceClassifierReference().withAddedClassifierReferences(classifierReferences2.get())
						.createNow(),
				TypesPackage.Literals.NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES);
	}

	@Test
	public void testClassifierReferencesSize() {
		this.testSimilarity(
				getAPI().newNamespaceClassifierReference()
						.withAddedClassifierReferences(
								new ClassifierReference[] { classifierReferences1.get(), classifierReferences2.get() })
						.createNow(),
				getAPI().newNamespaceClassifierReference().withAddedClassifierReferences(classifierReferences1.get())
						.createNow(),
				TypesPackage.Literals.NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES);
	}

	@Test
	public void testClassifierReferencesNullCheck() {
		this.testSimilarityNullCheck(getAPI().newNamespaceClassifierReference()
				.withAddedClassifierReferences(classifierReferences1.get()).createNow(),
				TypesPackage.Literals.NAMESPACE_CLASSIFIER_REFERENCE__CLASSIFIER_REFERENCES);
	}
}
