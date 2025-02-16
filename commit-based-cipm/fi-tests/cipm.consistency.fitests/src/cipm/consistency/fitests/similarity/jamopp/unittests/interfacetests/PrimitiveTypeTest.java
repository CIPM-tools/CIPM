package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.initialisers.jamopp.types.IPrimitiveTypeInitialiser;

public class PrimitiveTypeTest extends AbstractJaMoPPSimilarityTest implements UsesConcreteClassifiers {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IPrimitiveTypeInitialiser.class);
	}

	protected PrimitiveType initElement(IPrimitiveTypeInitialiser init, Classifier target) {
		var res = init.instantiate();
		Assertions.assertTrue(init.initialise(res));
		Assertions.assertEquals(init.canSetTargetTo(res, target), init.setTarget(res, target));
		return res;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTarget(IPrimitiveTypeInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createMinimalClass("cls"));
		var objTwo = init.instantiate();

		this.testSimilarity(objOne, objTwo, PrimitiveType.class, TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testTargetNullCheck(IPrimitiveTypeInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalClass("cls1")), init, true,
				PrimitiveType.class, TypesPackage.Literals.CLASSIFIER_REFERENCE__TARGET);
	}
}