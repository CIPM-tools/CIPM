package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.classifiers.Implementor;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ImplementorTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> implements1 = () -> getAPI().createNewClassifierReference();
	private final Supplier<TypeReference> implements2 = () -> getAPI().createNewNamespaceClassifierReference();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Implementor.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testImplements(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithAddedFeat(ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS,
						implements1.get()).createNow(),
				getAPI().newX(cls).xWithAddedFeat(ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS,
						implements2.get()).createNow(),
				ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testImplementsSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithAddedFeat(ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS,
						new TypeReference[] { implements1.get(), implements2.get() }).createNow(),
				getAPI().newX(cls).xWithAddedFeat(ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS,
						implements1.get()).createNow(),
				ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testImplementsNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS, implements1.get()).createNow(),
				ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS);
	}
}
