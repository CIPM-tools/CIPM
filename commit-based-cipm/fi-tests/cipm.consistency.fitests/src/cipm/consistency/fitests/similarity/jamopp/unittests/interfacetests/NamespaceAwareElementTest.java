package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamespaceAwareElement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class NamespaceAwareElementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<String> namespaces1 = () -> "ns1";
	private final Supplier<String> namespaces2 = () -> "ns2";

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(NamespaceAwareElement.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNamespace(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, namespaces1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, namespaces2.get())
						.createNow(),
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNamespaceNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, namespaces1.get())
				.createNow(), CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
	}

}
