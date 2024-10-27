package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportingElement;
import org.emftext.language.java.imports.ImportsPackage;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesImportingElements;
import cipm.consistency.initialisers.jamopp.imports.IImportingElementInitialiser;

public class ImportingElementTest extends AbstractJaMoPPSimilarityTest implements UsesImportingElements {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IImportingElementInitialiser.class);
	}

	protected ImportingElement initElement(IImportingElementInitialiser init, Import[] imps) {
		ImportingElement result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addImports(result, imps));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImports(IImportingElementInitialiser init) {
		var objOne = this.initElement(init, new Import[] { this.createMinimalClsImport("cls1") });
		var objTwo = this.initElement(init, new Import[] { this.createMinimalClsImport("cls2") });

		this.testSimilarity(objOne, objTwo, ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImportsSize(IImportingElementInitialiser init) {
		var objOne = this.initElement(init,
				new Import[] { this.createMinimalClsImport("cls1"), this.createMinimalClsImport("cls2") });
		var objTwo = this.initElement(init, new Import[] { this.createMinimalClsImport("cls1") });

		this.testSimilarity(objOne, objTwo, ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImportsNullCheck(IImportingElementInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, new Import[] { this.createMinimalClsImport("cls1") }), init,
				true, ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS);
	}
}
