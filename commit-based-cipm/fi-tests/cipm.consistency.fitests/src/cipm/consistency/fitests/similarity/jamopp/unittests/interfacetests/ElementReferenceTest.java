package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.references.ElementReference;
import org.emftext.language.java.references.ReferenceableElement;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.initialisers.jamopp.references.IElementReferenceInitialiser;

public class ElementReferenceTest extends AbstractJaMoPPSimilarityTest implements UsesConcreteClassifiers {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IElementReferenceInitialiser.class);
	}

	protected ElementReference initElement(IElementReferenceInitialiser init, ReferenceableElement target,
			ReferenceableElement cTarget) {
		ElementReference result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setTarget(result, target));
		Assertions.assertTrue(init.setContainedTarget(result, cTarget));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTarget(IElementReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createMinimalClass("cls1"), null);
		var objTwo = this.initElement(init, this.createMinimalClass("cls2"), null);

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNullCheck(IElementReferenceInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalClass("cls1"), null), init, true,
				ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET);
	}

	/**
	 * Makes sure that not providing a container for the created element reference
	 * does not result in an exception.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNoException(IElementReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createMinimalClass("cls1"), null);
		var objTwo = this.initElement(init, this.createMinimalClass("cls2"), null);

		Assertions.assertDoesNotThrow(
				() -> this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET));
	}

	/**
	 * Makes sure that not providing a container for the created element reference
	 * does not result in an exception, if it is compared to an uninitialised
	 * element reference.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNoExceptionNullCheck(IElementReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createMinimalClass("cls1"), null);
		var objTwo = init.instantiate();
		Assertions.assertTrue(init.initialise(objTwo));

		Assertions.assertDoesNotThrow(
				() -> this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET));
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testContainedTarget(IElementReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, null, this.createMinimalClass("cls1"));
		var objTwo = this.initElement(init, null, this.createMinimalClass("cls2"));

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testContainedTargetNullCheck(IElementReferenceInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, null, this.createMinimalClass("cls1")), init, true,
				ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET);
	}
}
