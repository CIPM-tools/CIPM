package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypedElementExtension;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class TypedElementExtensionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> actualTarget1 = () -> getAPI().createNewClassifierReference();
	private final Supplier<TypeReference> actualTarget2 = () -> getAPI().createNewNamespaceClassifierReference();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(TypedElementExtension.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testActualTarget(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS,
								actualTarget1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS,
								actualTarget2.get())
						.createNow(),
				TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testActualTargets(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS,
								new TypeReference[] { actualTarget1.get(), actualTarget2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS,
								actualTarget1.get())
						.createNow(),
				TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS);
	}

	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testActualTargetNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS,
								actualTarget1.get())
						.createNow(),
				(Class<? extends EObject>) cls, TypesPackage.Literals.TYPED_ELEMENT_EXTENSION__ACTUAL_TARGETS);
	}
}
