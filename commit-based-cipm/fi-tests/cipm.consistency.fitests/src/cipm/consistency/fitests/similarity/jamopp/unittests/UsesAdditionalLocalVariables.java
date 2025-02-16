package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.variables.AdditionalLocalVariable;

import cipm.consistency.initialisers.jamopp.variables.AdditionalLocalVariableInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link AdditionalLocalVariable} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link AdditionalLocalVariable}
 * instances.
 */
public interface UsesAdditionalLocalVariables {
	/**
	 * @param alvName The name of the instance to be constructed
	 * @return A minimal {@link AdditionalLocalVariable} instance with the given
	 *         parameter.
	 */
	public default AdditionalLocalVariable createMinimalALV(String alvName) {
		var alvInit = new AdditionalLocalVariableInitialiser();
		var alv = alvInit.instantiate();
		alvInit.setName(alv, alvName);
		return alv;
	}
}
