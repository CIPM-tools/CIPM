package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.members.EnumConstant;

import cipm.consistency.initialisers.jamopp.members.EnumConstantInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link EnumConstant} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link EnumConstant} instances.
 */
public interface UsesEnumConstants {
	/**
	 * @param ecName The name of the instance to be created
	 * @return An {@link EnumConstant} with the given parameter
	 */
	public default EnumConstant createMinimalEnumConstant(String ecName) {
		var ecInit = new EnumConstantInitialiser();
		var ec = ecInit.instantiate();
		ecInit.setName(ec, ecName);
		return ec;
	}
}