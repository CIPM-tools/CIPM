package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArrayInitializer;
import org.emftext.language.java.arrays.ArrayInstantiationByValues;
import org.emftext.language.java.arrays.ArraysPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ArrayInstantiationByValuesTest extends AbstractJaMoPPSimilarityTest {

	private final Supplier<ArrayInitializer> arrayInitialiser1 = () -> getAPI()
			.newArrayInitializer(getAPI().newDecimalIntegerLiteral(1));
	private final Supplier<ArrayInitializer> arrayInitialiser2 = () -> getAPI()
			.newArrayInitializer(getAPI().newDecimalIntegerLiteral(2));

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ArrayInstantiationByValues.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayInitialiser(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithFeat(ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_VALUES__ARRAY_INITIALIZER,
								arrayInitialiser1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithFeat(ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_VALUES__ARRAY_INITIALIZER,
								arrayInitialiser2.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_VALUES__ARRAY_INITIALIZER);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArrayInitialiserNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithFeat(ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_VALUES__ARRAY_INITIALIZER,
								arrayInitialiser1.get())
						.createNow(),
				ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_VALUES__ARRAY_INITIALIZER);
	}
}
