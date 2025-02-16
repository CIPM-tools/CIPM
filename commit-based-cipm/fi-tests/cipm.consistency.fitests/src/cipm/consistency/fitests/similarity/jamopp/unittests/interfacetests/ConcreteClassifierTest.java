package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import org.emftext.language.java.containers.Package;

import java.util.stream.Stream;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesPackages;
import cipm.consistency.initialisers.jamopp.classifiers.IConcreteClassifierInitialiser;

public class ConcreteClassifierTest extends AbstractJaMoPPSimilarityTest implements UsesPackages {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IConcreteClassifierInitialiser.class);
	}

	protected ConcreteClassifier initElement(IConcreteClassifierInitialiser init, Package pac) {

		ConcreteClassifier result = init.instantiate();
		Assertions.assertTrue(init.setPackage(result, pac));

		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testPackage(IConcreteClassifierInitialiser init) {
		var objOne = this.initElement(init, this.createMinimalPackage("pOneNS", 2));
		var objTwo = this.initElement(init, this.createMinimalPackage("pTwoNS", 2));

		this.testSimilarity(objOne, objTwo, ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testPackageNullCheck(IConcreteClassifierInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalPackage("pOneNS", 2)), init, false,
				ClassifiersPackage.Literals.CONCRETE_CLASSIFIER__PACKAGE);
	}
}
