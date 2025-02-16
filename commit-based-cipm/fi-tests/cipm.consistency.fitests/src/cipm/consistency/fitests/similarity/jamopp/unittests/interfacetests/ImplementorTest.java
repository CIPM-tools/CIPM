package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.classifiers.Implementor;
import org.emftext.language.java.types.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.classifiers.IImplementorInitialiser;

public class ImplementorTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IImplementorInitialiser.class);
	}

	protected Implementor initElement(IImplementorInitialiser init, TypeReference[] impls) {
		Implementor result = init.instantiate();
		Assertions.assertTrue(init.addImplements(result, impls));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImplements(IImplementorInitialiser init) {
		var objOne = this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls1") });
		var objTwo = this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls2") });

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImplementsSize(IImplementorInitialiser init) {
		var objOne = this.initElement(init,
				new TypeReference[] { this.createMinimalClsRef("cls1"), this.createMinimalClsRef("cls2") });
		var objTwo = this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls1") });

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testImplementsNullCheck(IImplementorInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, new TypeReference[] { this.createMinimalClsRef("cls1") }),
				init, false, ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS);
	}
}
