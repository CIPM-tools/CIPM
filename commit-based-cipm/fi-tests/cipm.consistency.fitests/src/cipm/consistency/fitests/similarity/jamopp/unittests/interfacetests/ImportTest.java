package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesImports;
import cipm.consistency.initialisers.jamopp.imports.IImportInitialiser;

public class ImportTest extends AbstractJaMoPPSimilarityTest implements UsesImports {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IImportInitialiser.class);
	}

	protected Import initElement(IImportInitialiser init, ConcreteClassifier cls) {
		Import result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setClassifier(result, cls));

		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testClassifier(IImportInitialiser init) {
		var objOne = this.initElement(init, this.createMinimalClass("cls1Name"));
		var objTwo = this.initElement(init, this.createMinimalClass("cls2Name"));

		this.testSimilarity(objOne, objTwo, ImportsPackage.Literals.IMPORT__CLASSIFIER);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testClassifierNullCheck(IImportInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalClass("cls1Name")), init, true,
				ImportsPackage.Literals.IMPORT__CLASSIFIER);
	}
}
