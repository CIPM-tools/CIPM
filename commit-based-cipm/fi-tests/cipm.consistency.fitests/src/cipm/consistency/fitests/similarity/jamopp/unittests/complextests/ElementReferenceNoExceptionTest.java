package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.references.ElementReference;
import org.emftext.language.java.references.ReferenceableElement;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ElementReferenceNoExceptionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ReferenceableElement> target1 = () -> getAPI().createNewClass();
	private final Supplier<ReferenceableElement> target2 = () -> getAPI().createNewInterface();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ElementReference.class);
	}

	/**
	 * Makes sure that not providing a container for the created element reference
	 * does not result in an exception.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNoException(Class<?> cls, String displayName) {
		Assertions.assertDoesNotThrow(() -> this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET, target1.get())
						.createNow(),
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET, target2.get())
						.createNow(),
				ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET));
	}

	/**
	 * Makes sure that not providing a container for the created element reference
	 * does not result in an exception, if it is compared to an uninitialised
	 * element reference.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNoExceptionNullCheck(Class<?> cls, String displayName) {
		Assertions
				.assertDoesNotThrow(
						() -> this
								.testSimilarityNullCheck(
										getAPI().newX(cls)
												.xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET,
														target1.get())
												.createNow(),
										ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET));
	}
}
