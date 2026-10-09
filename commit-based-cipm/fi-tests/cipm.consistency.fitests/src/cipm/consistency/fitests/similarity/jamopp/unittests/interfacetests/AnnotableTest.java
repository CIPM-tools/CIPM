package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.annotations.Annotable;
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class AnnotableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AnnotationInstance> annotations1 = () -> getAPI().newAnnotationInstance()
			.withAddedNamespaces("ns1").createNow();
	private final Supplier<AnnotationInstance> annotations2 = () -> getAPI().newAnnotationInstance()
			.withAddedNamespaces("ns2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Annotable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAnnotations(Class<?> cls, String displayName) {
		this.testSimilarity(getAPI().newX(cls)
				.xWithAddedFeat(AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS, annotations1.get()).createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS, annotations2.get())
						.createNow(),
				AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAnnotationsSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS,
								new AnnotationInstance[] { annotations1.get(), annotations2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS, annotations1.get())
						.createNow(),
				AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testAnnotationsNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS, annotations1.get()).createNow(),
				AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS);
	}
}
