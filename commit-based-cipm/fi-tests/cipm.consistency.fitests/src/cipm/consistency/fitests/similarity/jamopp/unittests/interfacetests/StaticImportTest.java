package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.imports.ImportsPackage;
import org.emftext.language.java.imports.StaticImport;
import org.emftext.language.java.modifiers.Static;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class StaticImportTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Static> static1 = () -> getAPI().newStatic();
	private final Supplier<Static> static2 = () -> null;

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(StaticImport.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStatic(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ImportsPackage.Literals.STATIC_IMPORT__STATIC, static1.get()).createNow(),
				getAPI().newX(cls).xWithFeat(ImportsPackage.Literals.STATIC_IMPORT__STATIC, static2.get()).createNow(),
				ImportsPackage.Literals.STATIC_IMPORT__STATIC);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStaticNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls).xWithFeat(ImportsPackage.Literals.STATIC_IMPORT__STATIC, static1.get()).createNow(),
				ImportsPackage.Literals.STATIC_IMPORT__STATIC);
	}
}
