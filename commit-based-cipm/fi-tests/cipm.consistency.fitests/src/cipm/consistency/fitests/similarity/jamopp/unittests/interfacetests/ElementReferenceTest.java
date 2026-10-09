package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.references.ElementReference;
import org.emftext.language.java.references.ReferenceableElement;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ElementReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ReferenceableElement> target1 = () -> getAPI().createNewClass();
	private final Supplier<ReferenceableElement> target2 = () -> getAPI().createNewInterface();

	private final Supplier<ReferenceableElement> containedTarget1 = () -> getAPI().createNewClass();
	private final Supplier<ReferenceableElement> containedTarget2 = () -> getAPI().createNewInterface();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ElementReference.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTarget(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET, target1.get())
						.createNow(),
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET, target2.get())
						.createNow(),
				ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET, target1.get()).createNow(),
				ReferencesPackage.Literals.ELEMENT_REFERENCE__TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testContainedTarget(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET,
								containedTarget1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET,
								containedTarget2.get())
						.createNow(),
				ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testContainedTargetNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithFeat(ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET,
								containedTarget1.get())
						.createNow(),
				ReferencesPackage.Literals.ELEMENT_REFERENCE__CONTAINED_TARGET);
	}
}
