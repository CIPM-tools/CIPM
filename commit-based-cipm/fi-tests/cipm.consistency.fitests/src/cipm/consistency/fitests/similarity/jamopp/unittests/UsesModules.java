package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.containers.Module;

import cipm.consistency.initialisers.jamopp.containers.ModuleInitialiser;

/**
 * An interface that can be implemented by tests, which work with {@link Module}
 * instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Module} instances.
 */
public interface UsesModules {
	/**
	 * @param modName The name of the instance to be constructed
	 * @param modNss  The namespaces of the instance to be constructed
	 * @return A {@link Module} instance with the given parameters
	 */
	public default Module createMinimalModule(String modName, String[] modNss) {
		var modInit = new ModuleInitialiser();
		var mod = modInit.instantiate();
		modInit.setName(mod, modName);
		modInit.addNamespaces(mod, modNss);
		return mod;
	}

	/**
	 * A variation of {@link #createMinimalModule(String, String[])}, where the
	 * constructed instance has no namespaces.
	 */
	public default Module createMinimalModule(String modName) {
		return this.createMinimalModule(modName, null);
	}

}
