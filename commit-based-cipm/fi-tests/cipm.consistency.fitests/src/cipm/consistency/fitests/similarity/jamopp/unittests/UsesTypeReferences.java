package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.types.ClassifierReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypeReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.types.NamespaceClassifierReferenceInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link TypeReference} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link TypeReference} instances.
 */
public interface UsesTypeReferences extends UsesConcreteClassifiers {
	/**
	 * @param init The initialiser that will be used to construct the instance
	 * @param cls  The target of the instance to be constructed
	 * @return A {@link TypeReference} instance with the given parameters
	 */
	public default TypeReference createMinimalTypeReference(ITypeReferenceInitialiser init, Classifier cls) {
		TypeReference result = init.instantiate();
		init.setTarget(result, cls);
		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalTypeReference(ITypeReferenceInitialiser, Classifier)},
	 * the {@link Classifier} parameter is constructed with
	 * {@link #createMinimalClassWithCU(String)}.
	 * 
	 * @param clsName See {@link #createMinimalClassWithCU(String)}
	 */
	public default ClassifierReference createMinimalClsRef(String clsName) {
		return (ClassifierReference) this.createMinimalTypeReference(new ClassifierReferenceInitialiser(),
				this.createMinimalClassWithCU(clsName));
	}

	/**
	 * A variant of
	 * {@link #createMinimalTypeReference(ITypeReferenceInitialiser, Classifier)},
	 * where the {@link Classifier} parameter is constructed using
	 * {@link #createMinimalClassWithCU(String)}.
	 * 
	 * @param clsName See {@link #createMinimalClassWithCU(String)}
	 */
	public default TypeReference createMinimalClsRef(ITypeReferenceInitialiser init, String clsName) {
		return this.createMinimalTypeReference(init, this.createMinimalClassWithCU(clsName));
	}

	/**
	 * @param clsName See {@link #createMinimalClsRef(String)}
	 * 
	 * @return A {@link NamespaceClassifierReference} instance with a
	 *         {@link ClassifierReference} constructed with
	 *         {@link #createMinimalClsRef(String)}.
	 */
	public default NamespaceClassifierReference createMinimalCNR(String clsName) {
		var init = new NamespaceClassifierReferenceInitialiser();
		NamespaceClassifierReference result = init.instantiate();
		init.addClassifierReference(result, this.createMinimalClsRef(clsName));
		return result;
	}
}
