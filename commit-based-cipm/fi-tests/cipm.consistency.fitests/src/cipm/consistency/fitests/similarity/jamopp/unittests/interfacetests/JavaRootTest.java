package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.containers.ContainersPackage;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class JavaRootTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Origin> origin1 = () -> Origin.BINDING;
	private final Supplier<Origin> origin2 = () -> Origin.CLASS;

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(JavaRoot.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOrigin(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ContainersPackage.Literals.JAVA_ROOT__ORIGIN, origin1.get()).createNow(),
				getAPI().newX(cls).xWithFeat(ContainersPackage.Literals.JAVA_ROOT__ORIGIN, origin2.get()).createNow(),
				ContainersPackage.Literals.JAVA_ROOT__ORIGIN);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOriginNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls).xWithFeat(ContainersPackage.Literals.JAVA_ROOT__ORIGIN, origin1.get()).createNow(),
				ContainersPackage.Literals.JAVA_ROOT__ORIGIN);
	}
}
