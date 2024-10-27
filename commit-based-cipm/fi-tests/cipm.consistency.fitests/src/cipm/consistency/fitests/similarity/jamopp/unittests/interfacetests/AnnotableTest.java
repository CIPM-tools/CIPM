package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.annotations.Annotable;
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.annotations.AnnotationsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesAnnotationInstances;
import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

public class AnnotableTest extends AbstractJaMoPPSimilarityTest implements UsesAnnotationInstances {
	
	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IAnnotableInitialiser.class);
	}
	
	protected Annotable initElement(IAnnotableInitialiser init, AnnotationInstance[] annotations) {
		Annotable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addAnnotations(result, annotations));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testAnnotation(IAnnotableInitialiser init) {
		var objOne = this.initElement(init,
				new AnnotationInstance[] { this.createMinimalAI(new String[] { "ns1" }, "anno1") });
		var objTwo = this.initElement(init,
				new AnnotationInstance[] { this.createMinimalAI(new String[] { "ns2" }, "anno2") });

		this.testSimilarity(objOne, objTwo, AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testAnnotationSize(IAnnotableInitialiser init) {
		var objOne = this.initElement(init,
				new AnnotationInstance[] { this.createMinimalAI(new String[] { "ns1" }, "anno1"),
						this.createMinimalAI(new String[] { "ns2" }, "anno2") });
		var objTwo = this.initElement(init,
				new AnnotationInstance[] { this.createMinimalAI(new String[] { "ns1" }, "anno1") });

		this.testSimilarity(objOne, objTwo, AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testAnnotationNullCheck(IAnnotableInitialiser init) {
		this.testSimilarityNullCheck(
				this.initElement(init,
						new AnnotationInstance[] { this.createMinimalAI(new String[] { "ns1" }, "anno1") }),
				init, true, AnnotationsPackage.Literals.ANNOTABLE__ANNOTATIONS);
	}
}
