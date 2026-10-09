package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.imports.Import;
import org.emftext.language.java.imports.ImportingElement;
import org.emftext.language.java.imports.ImportsPackage;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ImportingElementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Import> imports1 = () -> getAPI().createNewClassifierImport();
	private final Supplier<Import> imports2 = () -> getAPI().createNewStaticClassifierImport();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ImportingElement.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testImports(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithAddedFeat(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS, imports1.get())
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS, imports2.get())
						.createNow(),
				ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testImportsSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS,
								new Import[] { imports1.get(), imports2.get() })
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS, imports1.get())
						.createNow(),
				ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testImportsNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS, imports1.get()).createNow(),
				ImportsPackage.Literals.IMPORTING_ELEMENT__IMPORTS);
	}
}
