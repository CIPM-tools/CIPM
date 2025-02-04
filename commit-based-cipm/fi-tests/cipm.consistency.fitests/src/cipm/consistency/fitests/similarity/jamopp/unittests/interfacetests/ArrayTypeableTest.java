package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArrayDimension;
import org.emftext.language.java.arrays.ArrayTypeable;
import org.emftext.language.java.arrays.ArraysPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesArrayDimensions;
import cipm.consistency.initialisers.jamopp.arrays.IArrayTypeableInitialiser;

public class ArrayTypeableTest extends AbstractJaMoPPSimilarityTest implements UsesArrayDimensions {
	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IArrayTypeableInitialiser.class);
	}

	protected ArrayTypeable initElement(IArrayTypeableInitialiser init, ArrayDimension[] arrDimsBefore,
			ArrayDimension[] arrDimsAfter) {
		ArrayTypeable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addArrayDimensionsBefore(result, arrDimsBefore));
		Assertions.assertTrue(init.addArrayDimensionsAfter(result, arrDimsAfter));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsBefore(IArrayTypeableInitialiser init, String displayName) {
		var objOne = this.initElement(init,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1") }, null);
		var objTwo = this.initElement(init,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns2" }, "ai2") }, null);

		this.testSimilarity(objOne, objTwo, ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsBeforeSize(IArrayTypeableInitialiser init, String displayName) {
		var objOne = this.initElement(init,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1"),
						this.createArrayDimension(new String[] { "ns2" }, "ai2") },
				null);
		var objTwo = this.initElement(init,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1") }, null);

		this.testSimilarity(objOne, objTwo, ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsBeforeNullCheck(IArrayTypeableInitialiser init, String displayName) {
		this.testSimilarityNullCheck(
				this.initElement(init,
						new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1") }, null),
				init, true, ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsAfter(IArrayTypeableInitialiser init, String displayName) {
		var objOne = this.initElement(init, null,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1") });
		var objTwo = this.initElement(init, null,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns2" }, "ai2") });

		this.testSimilarity(objOne, objTwo, ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsAfterSize(IArrayTypeableInitialiser init, String displayName) {
		var objOne = this.initElement(init, null,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1"),
						this.createArrayDimension(new String[] { "ns2" }, "ai2") });
		var objTwo = this.initElement(init, null,
				new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1") });

		this.testSimilarity(objOne, objTwo, ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsAfterNullCheck(IArrayTypeableInitialiser init, String displayName) {
		this.testSimilarityNullCheck(
				this.initElement(init, null,
						new ArrayDimension[] { this.createArrayDimension(new String[] { "ns1" }, "ai1") }),
				init, true, ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER);
	}
}
