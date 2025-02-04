package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamedElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;

public class NamedElementTest extends AbstractJaMoPPSimilarityTest {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(INamedElementInitialiser.class);
	}

	protected NamedElement initElement(INamedElementInitialiser init, String name) {
		NamedElement result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertEquals(init.canSetName(result) || name == null, init.setName(result, name));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testName(INamedElementInitialiser init, String displayName) {
		var objOne = this.initElement(init, "name11");
		var objTwo = this.initElement(init, "name22");

		this.testSimilarity(objOne, objTwo, CommonsPackage.Literals.NAMED_ELEMENT__NAME);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNameNullCheck(INamedElementInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, "name11"), init, true,
				CommonsPackage.Literals.NAMED_ELEMENT__NAME);
	}
}
