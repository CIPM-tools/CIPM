package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypedElement;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class TypedElementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<TypeReference> typeReference1 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<TypeReference> typeReference2 = () -> getAPI().newClassifierReference()
			.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(TypedElement.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeReference(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE, typeReference1.get())
						.createNow(),
				getAPI().newX(cls).xWithFeat(TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE, typeReference2.get())
						.createNow(),
				TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTypeReferenceNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE, typeReference1.get()).createNow(),
				TypesPackage.Literals.TYPED_ELEMENT__TYPE_REFERENCE);
	}
}
