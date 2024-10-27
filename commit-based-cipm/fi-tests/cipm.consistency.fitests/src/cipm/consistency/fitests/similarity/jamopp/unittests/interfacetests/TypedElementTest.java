package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypedElement;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public class TypedElementTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(ITypedElementInitialiser.class);
	}

	protected TypedElement initElement(ITypedElementInitialiser init, TypeReference tRef) {
		TypedElement result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setTypeReference(result, tRef));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeReference(ITypedElementInitialiser init) {
		var objOne = this.initElement(init, this.createMinimalClsRef("cls1"));
		var objTwo = this.initElement(init, this.createMinimalClsRef("cls2"));

		this.testSimilarity(objOne, objTwo, TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeReferenceNullCheck(ITypedElementInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalClsRef("cls1")), init, true,
				TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE);
	}
}
