package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ConcreteClassifierTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<org.emftext.language.java.containers.Package> package1 = () -> getAPI().newPackage()
			.withAddedNamespaces("ns1").createNow();
	private final Supplier<org.emftext.language.java.containers.Package> package2 = () -> getAPI().newPackage()
			.withAddedNamespaces("ns2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ConcreteClassifier.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testPackage(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE, package1.get())
						.createNow(),
				getAPI().newX(cls).xWithFeat(ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE, package2.get())
						.createNow(),
				ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testPackageNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE, package1.get()).createNow(),
				ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE);
	}
}
