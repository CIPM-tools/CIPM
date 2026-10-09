package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.generics.CallTypeArgumentable;
import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.TypeArgument;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class CallTypeArgumentableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeArgument> callTypeArguments1 = () -> getAPI().createNewExtendsTypeArgument();
	private final Supplier<TypeArgument> callTypeArguments2 = () -> getAPI().createNewQualifiedTypeArgument();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(CallTypeArgumentable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testCallTypeArguments(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS,
								callTypeArguments1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS,
								callTypeArguments2.get())
						.createNow(),
				GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testCallTypeArgumentsSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS,
								new TypeArgument[] { callTypeArguments1.get(), callTypeArguments2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS,
								callTypeArguments1.get())
						.createNow(),
				GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testCallTypeArgumentsNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS,
								callTypeArguments1.get())
						.createNow(),
				GenericsPackage.Literals.CALL_TYPE_ARGUMENTABLE__CALL_TYPE_ARGUMENTS);
	}
}
