package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArrayDimension;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.ParametersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

/**
 * A test class to cover some interactions of similarity checking with
 * {@link Method}s' {@link Parameter}s, which are not addressed in other tests.
 * 
 * @author Alp Torac Genc
 */
public class MethodParameterTRefTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * @return Parameters for the test methods in this test class. See the
	 *         documentation of parameterized test methods.
	 */
	private static Stream<Arguments> getTestParams() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Method.class);
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
	public void test_SimilarParameters_OneParameterNullTypeReference(Class<? extends Method> metCls,
			String displayName) {
		var met1 = getAPI().createNewX(metCls);
		var met2 = getAPI().createNewX(metCls);

		var param1 = getAPI().newOrdinaryParameter().withTypeReference(getAPI().newClassifierReference()
				.withTarget(getAPI().newClass().withName("cls").createNow()).createNow()).createNow();
		var param2 = getAPI().createNewOrdinaryParameter();

		// Ensure that parameters are similar
		this.assertSimilarityResult(param1, param2, true);

		// Ensure that param2 has no type reference set
		Assertions.assertNull(param2.getTypeReference());

		getAPI().modifyX(met1).xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, param1);
		getAPI().modifyX(met2).xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, param2);

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
	public void test_SimilarParameters_SameTypeReference_DifferentArrayDimension(Class<? extends Method> metCls,
			String displayName) {
		var met1 = getAPI().createNewX(metCls);
		var met2 = getAPI().createNewX(metCls);

		var clsRef1 = getAPI().newClassifierReference()
				.withAddedArrayDimensionsAfter(getAPI().createNewArrayDimension()).createNow();
		var clsRef2 = getAPI().newClassifierReference()
				.withAddedArrayDimensionsAfter(
						new ArrayDimension[] { getAPI().createNewArrayDimension(), getAPI().createNewArrayDimension() })
				.createNow();

		// Ensure that clsRefs are similar
		this.assertSimilarityResult(clsRef1, clsRef2, true);

		// Ensure that clsRefs' array dimensions differ
		Assertions.assertNotEquals(clsRef1.getArrayDimension(), clsRef2.getArrayDimension());

		var param1 = getAPI().newOrdinaryParameter().withTypeReference(clsRef1).createNow();
		var param2 = getAPI().newOrdinaryParameter().withTypeReference(clsRef2).createNow();

		// Ensure that params are similar
		this.assertSimilarityResult(param1, param2, true);

		getAPI().modifyX(met1).xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, param1);
		getAPI().modifyX(met2).xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, param2);

		this.testSimilarity(met1, met2, false);
	}
}
