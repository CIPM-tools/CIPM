package cipm.consistency.fitests.similarity.jamopp.unittests.mocktests;

import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.emftext.language.java.commons.NamespaceAwareElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

/**
 * Tests whether namespace checking within similarity checking process can
 * handle inordinary/problematic namespaces.
 * 
 * @author Alp Torac Genc
 */
public class NamespaceComparisonMockTest extends AbstractJaMoPPSimilarityTest implements IMockTest {
	/**
	 * @return Class object of each non-abstract {@link NamespaceAwareElement}
	 *         sub-type.
	 */
	private static Stream<Arguments> genTestParams() {
		return IMockTest.getAllClasses(
				(cls) -> NamespaceAwareElement.class.isAssignableFrom(cls.getInstanceClass()) && !cls.isAbstract())
				.stream().map((cls) -> Arguments.of(cls, cls.getSimpleName()));
	}

	/**
	 * Ensures that similarity checking can handle cases, where both sides have the
	 * namespace "$".
	 * 
	 * @param cls A {@link NamespaceAwareElement} sub-type to be spied on.
	 */
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genTestParams")
	public <T extends NamespaceAwareElement> void testNamespaceComparison_BothSides_JustSentinel(Class<T> cls, String displayName) {
		var init = this.getUsedInitialiserPackage().getInitialiserInstanceFor(cls);

		var spy1 = this.spyEObject((T) init.instantiate());
		when(spy1.getNamespacesAsString()).thenReturn("$");

		var spy2 = this.spyEObject((T) init.instantiate());
		when(spy2.getNamespacesAsString()).thenReturn("$");

		Assertions.assertTrue(this.isSimilar(spy1, spy2));
		Assertions.assertTrue(this.isSimilar(spy2, spy1));
	}

	/**
	 * Ensures that similarity checking can handle cases, where only one side has
	 * the namespace "$".
	 * 
	 * @param cls A {@link NamespaceAwareElement} sub-type to be spied on.
	 */
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genTestParams")
	public <T extends NamespaceAwareElement> void testNamespaceComparison_OneSide_JustSentinel(Class<T> cls, String displayName) {
		var init = this.getUsedInitialiserPackage().getInitialiserInstanceFor(cls);

		var spy1 = this.spyEObject((T) init.instantiate());
		when(spy1.getNamespacesAsString()).thenReturn("$");

		var spy2 = this.spyEObject((T) init.instantiate());
		when(spy2.getNamespacesAsString()).thenReturn("");

		Assertions.assertEquals(this.isSimilar(spy1, spy2), this.isSimilar(spy2, spy1), "isSimilar is not symmetric");
	}

	/**
	 * Ensures that similarity checking can handle cases, where both sides have the
	 * same namespace "a", which does not contain any dots.
	 * 
	 * @param cls A {@link NamespaceAwareElement} sub-type to be spied on.
	 */
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genTestParams")
	public <T extends NamespaceAwareElement> void testNamespaceComparison_BothSides_WithoutDot(Class<T> cls, String displayName) {
		var init = this.getUsedInitialiserPackage().getInitialiserInstanceFor(cls);

		var spy1 = this.spyEObject((T) init.instantiate());
		when(spy1.getNamespacesAsString()).thenReturn("a");

		var spy2 = this.spyEObject((T) init.instantiate());
		when(spy2.getNamespacesAsString()).thenReturn("a");

		Assertions.assertTrue(this.isSimilar(spy1, spy2));
		Assertions.assertTrue(this.isSimilar(spy2, spy1));
	}

	/**
	 * Ensures that similarity checking can handle cases, where only one side has a
	 * namespace, which contains no dots.
	 * 
	 * @param cls A {@link NamespaceAwareElement} sub-type to be spied on.
	 */
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genTestParams")
	public <T extends NamespaceAwareElement> void testNamespaceComparison_OneSide_WithoutDot(Class<T> cls, String displayName) {
		var init = this.getUsedInitialiserPackage().getInitialiserInstanceFor(cls);

		var spy1 = this.spyEObject((T) init.instantiate());
		when(spy1.getNamespacesAsString()).thenReturn("a");

		var spy2 = this.spyEObject((T) init.instantiate());
		when(spy2.getNamespacesAsString()).thenReturn(".");

		Assertions.assertEquals(this.isSimilar(spy1, spy2), this.isSimilar(spy2, spy1), "isSimilar is not symmetric");
	}
}
