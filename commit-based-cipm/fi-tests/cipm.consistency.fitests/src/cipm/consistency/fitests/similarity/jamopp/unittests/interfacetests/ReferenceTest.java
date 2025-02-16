package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.references.Reference;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesArraySelectors;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesReferences;
import cipm.consistency.initialisers.jamopp.references.IReferenceInitialiser;

public class ReferenceTest extends AbstractJaMoPPSimilarityTest implements UsesReferences, UsesArraySelectors {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IReferenceInitialiser.class);
	}

	protected Reference initElement(IReferenceInitialiser init, Reference next, ArraySelector[] arrSels) {
		Reference ref = init.instantiate();
		Assertions.assertTrue(init.initialise(ref));
		Assertions.assertTrue(init.setNext(ref, next));
		Assertions.assertTrue(init.addArraySelectors(ref, arrSels));
		return ref;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNext(IReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createMinimalSR("str1"), null);
		var objTwo = this.initElement(init, this.createMinimalSR("str2"), null);

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.REFERENCE__NEXT);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNextNullCheck(IReferenceInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalSR("str1"), null), init, true,
				ReferencesPackage.Literals.REFERENCE__NEXT);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArraySelector(IReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, null, new ArraySelector[] { this.createMinimalAS(0) });
		var objTwo = this.initElement(init, null, new ArraySelector[] { this.createMinimalAS(1) });

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArraySelectorSize(IReferenceInitialiser init, String displayName) {
		var objOne = this.initElement(init, null,
				new ArraySelector[] { this.createMinimalAS(0), this.createMinimalAS(1) });
		var objTwo = this.initElement(init, null, new ArraySelector[] { this.createMinimalAS(0) });

		this.testSimilarity(objOne, objTwo, ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArraySelectorNullCheck(IReferenceInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, null, new ArraySelector[] { this.createMinimalAS(0) }),
				init, true, ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS);
	}
}
