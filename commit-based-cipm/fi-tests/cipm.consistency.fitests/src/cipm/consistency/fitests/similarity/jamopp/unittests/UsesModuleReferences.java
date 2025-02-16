package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.modules.ModuleReference;

import cipm.consistency.initialisers.jamopp.modules.ModuleReferenceInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ModuleReference} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ModuleReference}
 * instances.
 */
public interface UsesModuleReferences extends UsesModules {
	/**
	 * @param modName   See {@link #createMinimalModule(String)}
	 * @param modRefNss The namespaces of the {@link Module}, at which the
	 *                  constructed instance will point
	 * @return A {@link ModuleReference} instance that points at a {@link Module}
	 *         constructed with {@link #createMinimalModule(String)}.
	 */
	public default ModuleReference createMinimalMR(String modName, String[] modRefNss) {
		var mrInit = new ModuleReferenceInitialiser();
		var mr = mrInit.instantiate();
		mrInit.setTarget(mr, this.createMinimalModule(modName));
		mrInit.addNamespaces(mr, modRefNss);
		return mr;
	}

	/**
	 * A variant of {@link #createMinimalModule(String, String[])}, where the
	 * {@link Module} constructed in the process has no namespaces (second parameter
	 * is null).
	 */
	public default ModuleReference createMinimalMR(String modName) {
		return this.createMinimalMR(modName, null);
	}

}
