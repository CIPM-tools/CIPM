package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.TypeArgument;
import org.emftext.language.java.generics.TypeArgumentable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class TypeArgumentableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeArgument> typeArguments1 = () -> getAPI().createNewExtendsTypeArgument();
	private final Supplier<TypeArgument> typeArguments2 = () -> getAPI().createNewQualifiedTypeArgument();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(TypeArgumentable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeArgument(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS,
								typeArguments1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS,
								typeArguments2.get())
						.createNow(),
				GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeArgumentSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS,
								new TypeArgument[] { typeArguments1.get(), typeArguments2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS,
								typeArguments1.get())
						.createNow(),
				GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeArgumentNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS,
								typeArguments1.get())
						.createNow(),
				GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS);
	}
}
