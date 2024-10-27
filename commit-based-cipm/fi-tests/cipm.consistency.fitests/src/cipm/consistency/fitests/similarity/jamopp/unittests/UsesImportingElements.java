package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportingElement;

import cipm.consistency.initialisers.jamopp.imports.IImportingElementInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ImportingElement} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ImportingElement}
 * instances.
 */
public interface UsesImportingElements extends UsesImports {
	/**
	 * @param init The initialiser that will be used to construct the instance
	 * @param imp  The {@link Import} that will be added to the constructed instance
	 * @return An {@link ImportingElement} instance with the given parameters
	 */
	public default ImportingElement createMinimalImportingElement(IImportingElementInitialiser init, Import imp) {
		ImportingElement result = init.instantiate();
		init.addImport(result, imp);

		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalImportingElement(IImportingElementInitialiser, Import)},
	 * where the {@link Import} parameter is constructed using
	 * {@link #createMinimalClsImport(String)}.
	 * 
	 * @param clsName See {@link #createMinimalClsImport(String)}
	 */
	public default ImportingElement createMinimalImportingElement(IImportingElementInitialiser init, String clsName) {
		return this.createMinimalImportingElement(init, this.createMinimalClsImport(clsName));
	}
}
