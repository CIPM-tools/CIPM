package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArrayDimension;
import org.emftext.language.java.arrays.ArrayTypeable;
import org.emftext.language.java.arrays.ArraysPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ArrayTypeableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ArrayDimension> arrayDimensionsBefore1 = () -> getAPI()
			.newArrayDimension(getAPI().newAnnotationInstance().withAddedNamespaces("ns1").createNow());
	private final Supplier<ArrayDimension> arrayDimensionsBefore2 = () -> getAPI()
			.newArrayDimension(getAPI().newAnnotationInstance().withAddedNamespaces("ns2").createNow());

	private final Supplier<ArrayDimension> arrayDimensionsAfter1 = () -> getAPI()
			.newArrayDimension(getAPI().newAnnotationInstance().withAddedNamespaces("ns1").createNow());
	private final Supplier<ArrayDimension> arrayDimensionsAfter2 = () -> getAPI()
			.newArrayDimension(getAPI().newAnnotationInstance().withAddedNamespaces("ns2").createNow());

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ArrayTypeable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsBefore(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE,
								arrayDimensionsBefore1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE,
								arrayDimensionsBefore2.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsBeforeSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE,
								new ArrayDimension[] { arrayDimensionsBefore1.get(), arrayDimensionsBefore2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE,
								arrayDimensionsBefore1.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsBeforeNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE,
								arrayDimensionsBefore1.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_BEFORE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsAfter(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER,
								arrayDimensionsAfter1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER,
								arrayDimensionsAfter2.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsAfterSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER,
								new ArrayDimension[] { arrayDimensionsAfter1.get(), arrayDimensionsAfter2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER,
								arrayDimensionsAfter1.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayDimensionsAfterNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER,
								arrayDimensionsAfter1.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_TYPEABLE__ARRAY_DIMENSIONS_AFTER);
	}
}
