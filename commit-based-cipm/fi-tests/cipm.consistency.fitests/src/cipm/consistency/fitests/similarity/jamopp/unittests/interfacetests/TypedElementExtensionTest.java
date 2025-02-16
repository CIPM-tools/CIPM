package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypedElementExtension;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.types.ITypedElementExtensionInitialiser;

public class TypedElementExtensionTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(ITypedElementExtensionInitialiser.class);
	}

	protected TypedElementExtension initElement(ITypedElementExtensionInitialiser init, TypeReference[] actualTargets) {
		TypedElementExtension result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addActualTargets(result, actualTargets));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testActualTarget(ITypedElementExtensionInitialiser init, String displayName) {
		var objOne = this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls1") });
		var objTwo = this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls2") });

		this.testSimilarity(objOne, objTwo, TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testActualTargets(ITypedElementExtensionInitialiser init, String displayName) {
		var objOne = this.initElement(init,
				new TypeReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") });
		var objTwo = this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls1") });

		this.testSimilarity(objOne, objTwo, TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testActualTargetNullCheck(ITypedElementExtensionInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls1") }),
				init, true, TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS);
	}
}
