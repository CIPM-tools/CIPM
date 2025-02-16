package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.variables.LocalVariable;

import cipm.consistency.initialisers.jamopp.variables.LocalVariableInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link LocalVariable} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link LocalVariable} instances.
 */
public interface UsesLocalVariables {
	/**
	 * @param lvName The name of the instance to be constructed
	 * @return A {@link LocalVariable} instance with the given parameter
	 */
	public default LocalVariable createMinimalLV(String lvName) {
		var lvInit = new LocalVariableInitialiser();
		var lv = lvInit.instantiate();
		lvInit.setName(lv, lvName);
		return lv;
	}
}
