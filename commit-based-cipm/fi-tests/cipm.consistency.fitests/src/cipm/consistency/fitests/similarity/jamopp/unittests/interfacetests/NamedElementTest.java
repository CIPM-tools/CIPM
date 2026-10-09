package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamedElement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class NamedElementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<String> name1 = () -> "a";
	private final Supplier<String> name2 = () -> "b";

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(NamedElement.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testName(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(CommonsPackage.Literals.NAMED_ELEMENT__NAME, name1.get()).createNow(),
				getAPI().newX(cls).xWithFeat(CommonsPackage.Literals.NAMED_ELEMENT__NAME, name2.get()).createNow(),
				CommonsPackage.Literals.NAMED_ELEMENT__NAME);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNameNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls).xWithFeat(CommonsPackage.Literals.NAMED_ELEMENT__NAME, name1.get()).createNow(),
				CommonsPackage.Literals.NAMED_ELEMENT__NAME);
	}
}
