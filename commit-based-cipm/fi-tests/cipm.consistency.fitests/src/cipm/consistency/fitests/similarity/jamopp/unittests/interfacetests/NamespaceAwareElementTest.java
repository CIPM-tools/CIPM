package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamespaceAwareElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.commons.INamespaceAwareElementInitialiser;

public class NamespaceAwareElementTest extends AbstractJaMoPPSimilarityTest {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(INamespaceAwareElementInitialiser.class);
	}

	private final String[] nss1 = new String[] { "ns11", "ns12", "ns13" };
	private final String[] nss2 = new String[] { "ns21", "ns22", "ns23" };

	protected NamespaceAwareElement initElement(INamespaceAwareElementInitialiser init, String[] nss) {
		NamespaceAwareElement result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addNamespaces(result, nss));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testNamespace(INamespaceAwareElementInitialiser init) {
		var objOne = this.initElement(init, nss1);
		var objTwo = this.initElement(init, nss2);

		this.testSimilarity(objOne, objTwo, CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
	}

	/**
	 * Tests whether longer namespaces with the same prefix are different.
	 */
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testNamespaceScope(INamespaceAwareElementInitialiser init) {
		for (int i = 0; i < nss1.length; i++) {
			var newNss = new String[i];

			for (int j = 0; j < i; j++) {
				newNss[j] = nss1[j];
			}

			var objOne = this.initElement(init, newNss);
			var objTwo = this.initElement(init, nss1);

			this.testSimilarity(objOne, objTwo, CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
		}
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testNamespaceNullCheck(INamespaceAwareElementInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, nss1), init, true,
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
	}

}
