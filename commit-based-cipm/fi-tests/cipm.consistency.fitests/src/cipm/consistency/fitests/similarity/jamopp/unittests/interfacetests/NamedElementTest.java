package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamedElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;

public class NamedElementTest extends AbstractJaMoPPSimilarityTest {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(INamedElementInitialiser.class);
	}

	protected NamedElement initElement(INamedElementInitialiser init, String name) {
		NamedElement result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertEquals(init.canSetName(result) || name == null, init.setName(result, name));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testName(INamedElementInitialiser init) {
		var objOne = this.initElement(init, "name11");
		var objTwo = this.initElement(init, "name22");

		this.testSimilarity(objOne, objTwo, CommonsPackage.Literals.NAMED_ELEMENT__NAME);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testNameNullCheck(INamedElementInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, "name11"), init, false,
				CommonsPackage.Literals.NAMED_ELEMENT__NAME);
	}
}
