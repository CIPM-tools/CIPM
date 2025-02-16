package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.TypeParameter;
import org.emftext.language.java.generics.TypeParametrizable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeParameters;
import cipm.consistency.initialisers.jamopp.generics.ITypeParametrizableInitialiser;

public class TypeParametrizableTest extends AbstractJaMoPPSimilarityTest implements UsesTypeParameters {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(ITypeParametrizableInitialiser.class);
	}

	protected TypeParametrizable initElement(ITypeParametrizableInitialiser init, TypeParameter[] tParams) {
		TypeParametrizable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addTypeParameters(result, tParams));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeParameters(ITypeParametrizableInitialiser init) {
		var objOne = this.initElement(init, new TypeParameter[] { this.createMinimalTypeParamWithClsRef("cls1") });
		var objTwo = this.initElement(init, new TypeParameter[] { this.createMinimalTypeParamWithClsRef("cls2") });

		this.testSimilarity(objOne, objTwo, GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeParametersSize(ITypeParametrizableInitialiser init) {
		var objOne = this.initElement(init, new TypeParameter[] { this.createMinimalTypeParamWithClsRef("cls1"),
				this.createMinimalTypeParamWithClsRef("cls2") });
		var objTwo = this.initElement(init, new TypeParameter[] { this.createMinimalTypeParamWithClsRef("cls1") });

		this.testSimilarity(objOne, objTwo, GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeParametersNullCheck(ITypeParametrizableInitialiser init) {
		this.testSimilarityNullCheck(
				this.initElement(init, new TypeParameter[] { this.createMinimalTypeParamWithClsRef("cls1") }), init,
				true, GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS);
	}
}
