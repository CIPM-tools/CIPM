package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.generics.ExtendsTypeArgument;
import org.emftext.language.java.generics.SuperTypeArgument;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.generics.ExtendsTypeArgumentInitialiser;
import cipm.consistency.initialisers.jamopp.generics.SuperTypeArgumentInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link TypeArgument} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link TypeArgument} instances.
 */
public interface UsesTypeArguments extends UsesTypeReferences {
	/**
	 * @param extType The extend type of the instance to be constructed
	 * @return An {@link ExtendsTypeArgument} instance with the given parameter
	 */
	public default ExtendsTypeArgument createMinimalExtendsTA(TypeReference extType) {
		var init = new ExtendsTypeArgumentInitialiser();
		ExtendsTypeArgument result = init.instantiate();
		init.setExtendType(result, extType);
		return result;
	}

	/**
	 * @param supType The super type of the instance to be constructed
	 * @return An {@link SuperTypeArgument} instance with the given parameter
	 */
	public default SuperTypeArgument createMinimalSuperTA(TypeReference supType) {
		var init = new SuperTypeArgumentInitialiser();
		SuperTypeArgument result = init.instantiate();
		init.setSuperType(result, supType);
		return result;
	}

	/**
	 * A variant of {@link #createMinimalExtendsTA(TypeReference)}, where the
	 * parameter is constructed with {@link #createMinimalClsRef(String)}.
	 * 
	 * @param clsName See {@link #createMinimalClsRef(String)}
	 */
	public default ExtendsTypeArgument createMinimalExtendsTAWithCls(String clsName) {
		return this.createMinimalExtendsTA(this.createMinimalClsRef(clsName));
	}

	/**
	 * A variant of {@link #createMinimalSuperTA(TypeReference)}, where the
	 * parameter is constructed with {@link #createMinimalClsRef(String)}.
	 * 
	 * @param clsName See {@link #createMinimalClsRef(String)}
	 */
	public default SuperTypeArgument createMinimalSuperTAWithCls(String clsName) {
		return this.createMinimalSuperTA(this.createMinimalClsRef(clsName));
	}
}
