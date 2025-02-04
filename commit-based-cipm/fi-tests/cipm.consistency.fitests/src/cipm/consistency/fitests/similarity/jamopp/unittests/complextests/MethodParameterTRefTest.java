package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArrayDimension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesArrayDimensions;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesParameters;
import cipm.consistency.initialisers.jamopp.members.IMethodInitialiser;
import cipm.consistency.initialisers.jamopp.parameters.OrdinaryParameterInitialiser;
import cipm.consistency.initialisers.jamopp.types.ClassifierReferenceInitialiser;

/**
 * A test class to cover some interactions of similarity checking with
 * {@link Method}s' {@link Parameter}s, which are not addressed in other tests.
 * 
 * @author Alp Torac Genc
 */
public class MethodParameterTRefTest extends AbstractJaMoPPSimilarityTest
		implements UsesParameters, UsesArrayDimensions {
	/**
	 * @return Parameters for the test methods in this test class. See the
	 *         documentation of parameterized test methods.
	 */
	private static Stream<Arguments> getTestParams() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IMethodInitialiser.class);
	}

	/**
	 * Tests whether 2 {@link Method} instances are not similar, if they have
	 * similar {@link Parameter}s yet one of them has no {@link TypeReference} set.
	 * 
	 * @param metInit The initialiser that will instantiate the {@link Method}
	 *                implementor under test
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("getTestParams")
	public void test_SimilarParameters_OneParameterNullTypeReference(IMethodInitialiser metInit, String displayName) {
		var met1 = metInit.instantiate();
		Assertions.assertTrue(metInit.initialise(met1));
		var met2 = metInit.instantiate();
		Assertions.assertTrue(metInit.initialise(met2));

		var ordParamInit = new OrdinaryParameterInitialiser();
		var param1 = this.createMinimalOrdParamWithClsTarget(null, "cls");
		var param2 = ordParamInit.instantiate();

		// Ensure that parameters are similar
		this.assertSimilarityResult(param1, param2, true);

		// Ensure that param2 has no type reference set
		Assertions.assertNull(param2.getTypeReference());

		Assertions.assertTrue(metInit.addParameter(met1, param1));
		Assertions.assertTrue(metInit.addParameter(met2, param2));

		this.testSimilarity(met1, met2, false);
	}

	/**
	 * Tests whether 2 {@link Method} instances are not similar, if they have
	 * similar {@link Parameter}s with similar {@link TypeReference}s, yet type
	 * references' {@link ArrayDimension}s differ.
	 * 
	 * @param metInit The initialiser that will instantiate the {@link Method}
	 *                implementor under test
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("getTestParams")
	public void test_SimilarParameters_SameTypeReference_DifferentArrayDimension(IMethodInitialiser metInit, String displayName) {
		var met1 = metInit.instantiate();
		Assertions.assertTrue(metInit.initialise(met1));
		var met2 = metInit.instantiate();
		Assertions.assertTrue(metInit.initialise(met2));

		var clsRefInit = new ClassifierReferenceInitialiser();

		var clsRef1 = clsRefInit.instantiate();
		Assertions.assertTrue(clsRefInit.addArrayDimensionAfter(clsRef1, this.createMinimalArrayDimension()));
		var clsRef2 = clsRefInit.instantiate();
		Assertions.assertTrue(clsRefInit.addArrayDimensionsAfter(clsRef2,
				new ArrayDimension[] { this.createMinimalArrayDimension(), this.createMinimalArrayDimension() }));

		// Ensure that clsRefs are similar
		this.assertSimilarityResult(clsRef1, clsRef2, true);

		// Ensure that clsRefs' array dimensions differ
		Assertions.assertNotEquals(clsRef1.getArrayDimension(), clsRef2.getArrayDimension());

		var ordParamInit = new OrdinaryParameterInitialiser();

		var param1 = ordParamInit.instantiate();
		Assertions.assertTrue(ordParamInit.setTypeReference(param1, clsRef1));
		var param2 = ordParamInit.instantiate();
		Assertions.assertTrue(ordParamInit.setTypeReference(param2, clsRef2));

		// Ensure that params are similar
		this.assertSimilarityResult(param1, param2, true);

		Assertions.assertTrue(metInit.addParameter(met1, param1));
		Assertions.assertTrue(metInit.addParameter(met2, param2));

		this.testSimilarity(met1, met2, false);
	}
}
