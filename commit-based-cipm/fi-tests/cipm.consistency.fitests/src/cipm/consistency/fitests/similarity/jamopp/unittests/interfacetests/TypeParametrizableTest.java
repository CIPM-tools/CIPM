package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.TypeParameter;
import org.emftext.language.java.generics.TypeParametrizable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class TypeParametrizableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeParameter> typeParameters1 = () -> getAPI().newTypeParameter().withName("tp1")
			.createNow();
	private final Supplier<TypeParameter> typeParameters2 = () -> getAPI().newTypeParameter().withName("tp2")
			.createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(TypeParametrizable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeParameters(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS,
								typeParameters1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS,
								typeParameters2.get())
						.createNow(),
				GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeParametersSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS,
								new TypeParameter[] { typeParameters1.get(), typeParameters2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS,
								typeParameters1.get())
						.createNow(),
				GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeParametersNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS,
								typeParameters1.get())
						.createNow(),
				GenericsPackage.Literals.TYPE_PARAMETRIZABLE__TYPE_PARAMETERS);
	}
}
