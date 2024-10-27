package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.imports.ClassifierImport;
import org.emftext.language.java.imports.Import;

import cipm.consistency.initialisers.jamopp.imports.ClassifierImportInitialiser;
import cipm.consistency.initialisers.jamopp.imports.IImportInitialiser;

/**
 * An interface that can be implemented by tests, which work with {@link Import}
 * instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Import} instances.
 */
public interface UsesImports extends UsesConcreteClassifiers {
	/**
	 * @param initialiser The initialiser that will be used to construct the
	 *                    instance
	 * @param cls         The classifier of the instance to be constructed (what it
	 *                    will point at)
	 * @return An {@link Import} instance with the given parameters
	 */
	public default Import createMinimalImport(IImportInitialiser initialiser, ConcreteClassifier cls) {
		Import result = initialiser.instantiate();
		initialiser.setClassifier(result, cls);

		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalImport(IImportInitialiser, ConcreteClassifier)}, where
	 * the constructed instance is of type {@link ClassifierImport}.
	 */
	public default ClassifierImport createMinimalClsImport(ConcreteClassifier cls) {
		return (ClassifierImport) this.createMinimalImport(new ClassifierImportInitialiser(), cls);
	}

	/**
	 * A variant of
	 * {@link #createMinimalImport(IImportInitialiser, ConcreteClassifier)}, where
	 * the constructed instance is of type {@link ClassifierImport} that points at a
	 * {@link Classifier} constructed with
	 * {@link #createMinimalClassWithCU(String)}.
	 * 
	 * @param clsName See {@link #createMinimalClassWithCU(String)}
	 */
	public default ClassifierImport createMinimalClsImport(String clsName) {
		return this.createMinimalClsImport(this.createMinimalClassWithCU(clsName));
	}
}
