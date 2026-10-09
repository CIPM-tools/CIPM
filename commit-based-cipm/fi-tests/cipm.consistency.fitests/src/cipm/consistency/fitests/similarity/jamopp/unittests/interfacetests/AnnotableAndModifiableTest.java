package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.modifiers.ModifiersPackage;
import org.emftext.language.java.modifiers.AnnotableAndModifiable;
import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class AnnotableAndModifiableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AnnotationInstanceOrModifier> annotationsAndModifiers1 = () -> getAPI().newVolatile();
	private final Supplier<AnnotationInstanceOrModifier> annotationsAndModifiers2 = () -> getAPI().newAbstract();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(AnnotableAndModifiable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAnnotationsAndModifiers(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS,
								annotationsAndModifiers1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS,
								annotationsAndModifiers2.get())
						.createNow(),
				ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAnnotationsAndModifiersSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS,
								new AnnotationInstanceOrModifier[] { annotationsAndModifiers1.get(),
										annotationsAndModifiers2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS,
								annotationsAndModifiers1.get())
						.createNow(),
				ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAnnotationsAndModifiersNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls)
						.xWithAddedFeat(ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS,
								annotationsAndModifiers1.get())
						.createNow(),
				ModifiersPackage.Literals.ANNOTABLE_AND_MODIFIABLE__ANNOTATIONS_AND_MODIFIERS);
	}
}
