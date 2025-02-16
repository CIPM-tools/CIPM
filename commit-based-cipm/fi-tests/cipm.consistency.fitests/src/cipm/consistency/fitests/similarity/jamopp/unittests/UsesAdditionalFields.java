package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.members.AdditionalField;

import cipm.consistency.initialisers.jamopp.members.AdditionalFieldInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link AdditionalField} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link AdditionalField}
 * instances.
 */
public interface UsesAdditionalFields {
	/**
	 * @param The name of the instance to be constructed
	 * @return A minimal {@link AdditionalField} instance with the given parameter.
	 */
	public default AdditionalField createMinimalAF(String afName) {
		var afInit = new AdditionalFieldInitialiser();
		var af = afInit.instantiate();
		afInit.setName(af, afName);
		return af;
	}
}
