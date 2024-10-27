package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.generics.TypeParameter;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.generics.ITypeParameterInitialiser;
import cipm.consistency.initialisers.jamopp.generics.TypeParameterInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link TypeParameter} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link TypeParameter} instances.
 */
public interface UsesTypeParameters extends UsesTypeReferences {
	/**
	 * @param init     The initialiser that will be used to construct the instance
	 * @param extTypes The extend types of the instance to be constructed
	 * @return A {@link TypeParameter} with the given parameters
	 */
	public default TypeParameter createMinimalTypeParam(ITypeParameterInitialiser init, TypeReference[] extTypes) {
		TypeParameter result = init.instantiate();
		init.addExtendTypes(result, extTypes);
		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalTypeParam(ITypeParameterInitialiser, TypeReference[])}
	 * that uses {@link #createMinimalClsRef(String)} to generate the given amount
	 * of extend types. <br>
	 * <br>
	 * The each generated extend type will point at a {@link Classifier} (will be
	 * constructed in the process) with the name "cls"+i, where i = 0, 1, ...,
	 * extTypeCount - 1.
	 * 
	 * @param extTypeCount The amount of extend types that will be generated in the
	 *                     process
	 */
	public default TypeParameter createMinimalTypeParamWithClsRefs(ITypeParameterInitialiser init, int extTypeCount) {
		var arr = new TypeReference[extTypeCount];

		for (int i = 0; i < extTypeCount; i++) {
			arr[i] = this.createMinimalClsRef("cls" + i);
		}

		return this.createMinimalTypeParam(init, arr);
	}

	/**
	 * A variant of
	 * {@link #createMinimalTypeParam(ITypeParameterInitialiser, TypeReference[])}
	 * that generates a {@link TypeParameter} instance with only one extend type
	 * that points at a {@link Classifier} generated with
	 * {@link #createMinimalClsRef(String)} using the given parameter
	 * 
	 * @param clsName See {@link #createMinimalClsRef(String)}
	 */
	public default TypeParameter createMinimalTypeParamWithClsRef(String clsName) {
		return this.createMinimalTypeParam(new TypeParameterInitialiser(),
				new TypeReference[] { this.createMinimalClsRef(clsName) });
	}
}
