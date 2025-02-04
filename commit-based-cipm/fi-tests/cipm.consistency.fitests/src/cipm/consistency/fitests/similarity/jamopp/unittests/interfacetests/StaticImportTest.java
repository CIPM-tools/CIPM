package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.imports.ImportsPackage;
import org.emftext.language.java.imports.StaticImport;
import org.emftext.language.java.modifiers.Static;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesImports;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesModifiers;
import cipm.consistency.initialisers.jamopp.imports.IStaticImportInitialiser;

public class StaticImportTest extends AbstractJaMoPPSimilarityTest implements UsesImports, UsesModifiers {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IStaticImportInitialiser.class);
	}

	protected StaticImport initElement(IStaticImportInitialiser init, Static st) {
		StaticImport result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setStatic(result, st));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStatic(IStaticImportInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createStatic());
		var objTwo = this.initElement(init, null);

		this.testSimilarity(objOne, objTwo, ImportsPackage.Literals.STATIC_IMPORT__STATIC);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStaticNullCheck(IStaticImportInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, this.createStatic()), init, true,
				ImportsPackage.Literals.STATIC_IMPORT__STATIC);
	}
}
