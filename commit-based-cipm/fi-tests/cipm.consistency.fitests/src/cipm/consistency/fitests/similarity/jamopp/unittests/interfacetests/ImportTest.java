package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ImportTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ConcreteClassifier> classifier1 = () -> getAPI().newClass().withName("cls1").createNow();
	private final Supplier<ConcreteClassifier> classifier2 = () -> getAPI().newClass().withName("cls2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Import.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testClassifier(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ImportsPackage.Literals.IMPORT__CLASSIFIER, classifier1.get()).createNow(),
				getAPI().newX(cls).xWithFeat(ImportsPackage.Literals.IMPORT__CLASSIFIER, classifier2.get()).createNow(),
				ImportsPackage.Literals.IMPORT__CLASSIFIER);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testClassifierNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls).xWithFeat(ImportsPackage.Literals.IMPORT__CLASSIFIER, classifier1.get()).createNow(),
				ImportsPackage.Literals.IMPORT__CLASSIFIER);
	}
}
