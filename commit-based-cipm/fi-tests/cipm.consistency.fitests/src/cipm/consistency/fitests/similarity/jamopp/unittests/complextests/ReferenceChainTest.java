package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.emftext.language.java.references.Reference;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.references.IReferenceInitialiser;

/**
 * Contains tests for cases, where {@link Reference} instances build reference
 * chains (with {@code .setNext(...)}). <br>
 * <br>
 * There are also tests for cycles of length 1, 2 and 3 individually. Even
 * though they serve the same purpose, it is useful to have all of them run,
 * since one can oversee such cases and fail to address them properly in the
 * implementation of similarity checking. <br>
 * <br>
 * Tests for reference chains, where the cycle does not include the first
 * reference are also included, in order to cover all forms of possible
 * reference cycles for references that have up to one next reference (->
 * corresponds to next reference):
 * <ul>
 * <li>Circular reference chains:
 * {@code ref1 -> ref2 -> ... -> refN -> ref1 -> ...}
 * <li>Sub-chain with a cycle:
 * {@code ref1 -> ref2 -> ... -> refN1 -> refN2 -> ... -> refNM -> refN1 -> ...}
 * </ul>
 * The said tests are parameterized over all concrete
 * {@link IReferenceInitialiser} sub-types as well as their combinations, in
 * order to ensure that neither specific {@link Reference} sub-type(s) nor
 * combinations thereof cause issues. <br>
 * <br>
 * <b>Note: Running the tests within this class can take a long time, as they
 * parameterize over all possible combinations of {@link Reference} sub-type(s).
 * </b> <br>
 * <br>
 * 
 * @author Alp Torac Genc
 */
public class ReferenceChainTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * @return Non-adapted initialisers for each {@link Reference} sub-types.
	 */
	private static Stream<Arguments> genTestParams_ForOne() {
		return AbstractJaMoPPSimilarityTest.getNonAdaptedInitialiserArgumentsFor(IReferenceInitialiser.class);
	}

	/**
	 * @return Each possible pair of non-adapted initialisers for each
	 *         {@link Reference} sub-types.
	 */
	private static Stream<Arguments> genTestParams_ForTwo() {
		var params = new ArrayList<Arguments>();
		for (var init1 : getNonAdaptedInitialisersFor(IReferenceInitialiser.class)) {
			for (var init2 : getNonAdaptedInitialisersFor(IReferenceInitialiser.class)) {
				params.add(Arguments.of(init1, init2,
						String.format("%s, %s", init1.getInstanceClassOfInitialiser().getSimpleName(),
								init2.getInstanceClassOfInitialiser().getSimpleName())));
			}
		}
		return params.stream();
	}

	/**
	 * @return Each possible triplet of non-adapted initialisers for each
	 *         {@link Reference} sub-types.
	 */
	private static Stream<Arguments> genTestParams_ForThree() {
		var params = new ArrayList<Arguments>();
		for (var init1 : getNonAdaptedInitialisersFor(IReferenceInitialiser.class)) {
			for (var init2 : getNonAdaptedInitialisersFor(IReferenceInitialiser.class)) {
				for (var init3 : getNonAdaptedInitialisersFor(IReferenceInitialiser.class)) {
					params.add(Arguments.of(init1, init2, init3,
							String.format("%s, %s, %s", init1.getInstanceClassOfInitialiser().getSimpleName(),
									init2.getInstanceClassOfInitialiser().getSimpleName(),
									init3.getInstanceClassOfInitialiser().getSimpleName())));
				}
			}
		}
		return params.stream();
	}

	/**
	 * Attempts to run similarity checking in form of
	 * {@code isSimilar(ref1, ref2) ; isSimilar(ref2, ref1)}, compares the results,
	 * ensures that no exceptions are thrown. Note that the similarity checking not
	 * terminating will most likely result in exceptions. <br>
	 * <br>
	 * Directly uses isSimilar to avoid cloning refs, so that the underlying cloning
	 * mechanisms are not involved. This is important; since ref1 and ref2 could be
	 * forming a cycle, which the cloning mechanisms may not support. <br>
	 * <br>
	 * Does not make any assertions on the similarity checking result. Asserts only
	 * that it terminates without exceptions and that the similarity checking is
	 * symmetric (i.e. isSimilar(lhs, rhs) == isSimilar(rhs, lhs)).
	 */
	private void assertTerminates(Reference ref1, Reference ref2) {
		Assertions.assertDoesNotThrow(() -> {
			Assertions.assertEquals(this.isSimilar(ref1, ref2), this.isSimilar(ref2, ref1));
		});
	}

	/**
	 * Attempts to run similarity checking for any combination of the given
	 * {@link Reference} instances (not just pairwise) and checks whether any
	 * exceptions are thrown. If there are exceptions, it could be an indicator of
	 * endless recursions and/or loops.
	 */
	private void cycleAssertionsFor(Reference[] refs1, Reference[] refs2) {
		/*
		 * All iRef variables are similar, since all of them are in a cycle, where all
		 * elements in the cycle are similar.
		 */
		for (int i1 = 0; i1 < refs1.length; i1++) {
			var ref11 = refs1[i1];
			for (int i2 = 0; i2 < refs1.length; i2++) {
				var ref12 = refs1[i2];
				this.assertTerminates(ref11, ref12);
				for (int j1 = 0; j1 < refs2.length; j1++) {
					var ref21 = refs2[j1];
					this.assertTerminates(ref11, ref21);
					this.assertTerminates(ref12, ref21);
					for (int j2 = 0; j2 < refs2.length; j2++) {
						var ref22 = refs2[j2];
						this.assertTerminates(ref11, ref22);
						this.assertTerminates(ref12, ref22);
						this.assertTerminates(ref21, ref22);
					}
				}
			}
		}
	}

	/**
	 * Ensures that a {@link Reference} instance ref1 referencing nextRef1 is
	 * similar to a reference instance ref2 referencing nextRef2, if types of ref1
	 * and ref2 are equal and types of nextRef1 and nextRef2 are equal: <br>
	 * <br>
	 * {@code ref1 -> nextRef1} <br>
	 * ----------VS----------<br>
	 * {@code ref2 -> nextRef2}
	 * 
	 * @param refInit     Initialiser of ref1 and ref2
	 * @param nextRefInit Initialiser of nextRef1 and nextRef2
	 */
	@ParameterizedTest(name = "{2}")
	@MethodSource("genTestParams_ForTwo")
	public void test_ReferenceCombinations_SimilarNext(IReferenceInitialiser refInit, IReferenceInitialiser nextRefInit,
			String displayName) {
		var ref1 = refInit.instantiate();
		var nextRef1 = nextRefInit.instantiate();
		refInit.setNext(ref1, nextRef1);

		var ref2 = refInit.instantiate();
		var nextRef2 = nextRefInit.instantiate();
		refInit.setNext(ref2, nextRef2);

		Assertions.assertTrue(this.isSimilar(ref1, ref2));
	}

	/**
	 * Tests cases, where 2 {@link Reference} instances ref1 and ref2 each reference
	 * nextRef1 and nextRef2 respectively, with types of nextRef1 and nextRef2 being
	 * potentially different:<br>
	 * <br>
	 * {@code ref1 -> nextRef1} <br>
	 * -----------VS-----------<br>
	 * {@code ref2 -> nextRef2} <br>
	 * <br>
	 * Note: While
	 * {@link #test_ReferenceCombinations_SimilarNext(IReferenceInitialiser, IReferenceInitialiser)}
	 * asserts that both references should be similar, the assertion here is more
	 * advanced.
	 * 
	 * @param refXInit     Initialiser of ref1 and ref2
	 * @param nextRef1Init Initialiser of nextRef1
	 * @param nextRef2Init Initialiser of nextRef2
	 */
	@ParameterizedTest(name = "{3}")
	@MethodSource("genTestParams_ForThree")
	public void test_ReferenceCombinations_DifferentNext(IReferenceInitialiser refXInit,
			IReferenceInitialiser nextRef1Init, IReferenceInitialiser nextRef2Init, String displayName) {
		var ref1 = refXInit.instantiate();
		var nextRef1 = nextRef1Init.instantiate();
		refXInit.setNext(ref1, nextRef1);

		var ref2 = refXInit.instantiate();
		var nextRef2 = nextRef2Init.instantiate();
		refXInit.setNext(ref2, nextRef2);

		var nextClssSimilar = nextRef1.getClass().equals(nextRef2.getClass());
		var breaksSimilarity = this.getExpectedSimilarityResult(ref1.getClass(),
				ReferencesPackage.Literals.REFERENCE__NEXT);
		Assertions.assertEquals(this.isSimilar(ref1, ref2), nextClssSimilar || breaksSimilarity);
		Assertions.assertEquals(this.isSimilar(ref2, ref1), nextClssSimilar || breaksSimilarity);
	}

	/**
	 * Tests whether similarity checking can detect and handle cycles of
	 * {@link Reference} instances with a length of 1, i.e. references that
	 * reference themselves:<br>
	 * <br>
	 * {@code ref -> ref} <br>
	 * <br>
	 * Performs this check for each sub-type of {@link Reference}.
	 * 
	 * @param refInit Initialiser of ref
	 */
	@Disabled("Until cycle checking mechanisms are implemented")
	@ParameterizedTest(name = "{1}")
	@MethodSource("genTestParams_ForOne")
	public void test_ReferenceCycles_OneReferenceCycle(IReferenceInitialiser refInit, String displayName) {
		var ref = refInit.instantiate();
		refInit.setNext(ref, ref);
		this.cycleAssertionsFor(new Reference[] { ref }, new Reference[] { ref });
	}

	/**
	 * Tests whether similarity checking can detect and handle cycles of
	 * {@link Reference} instances with a length of 2:<br>
	 * <br>
	 * {@code ref11 -> ref12 -> ref11} <br>
	 * ----------------VS----------------<br>
	 * {@code ref21 -> ref22 -> ref21} <br>
	 * <br>
	 * Performs this check for each combination of sub-type of {@link Reference}.
	 * 
	 * @param refX1Init Initialiser of ref11 and ref21
	 * @param refX2Init Initialiser of ref12 and ref22
	 */
	@Disabled("Until cycle checking mechanisms are implemented")
	@ParameterizedTest(name = "{2}")
	@MethodSource("genTestParams_ForTwo")
	public void test_ReferenceCycles_TwoReferencesCycle(IReferenceInitialiser refX1Init,
			IReferenceInitialiser refX2Init, String displayName) {
		var ref11 = refX1Init.instantiate();
		var ref12 = refX2Init.instantiate();

		refX1Init.setNext(ref11, ref12);
		refX2Init.setNext(ref12, ref11);

		var ref21 = refX1Init.instantiate();
		var ref22 = refX2Init.instantiate();

		refX1Init.setNext(ref21, ref22);
		refX2Init.setNext(ref22, ref21);

		this.cycleAssertionsFor(new Reference[] { ref11, ref12 }, new Reference[] { ref21, ref22 });
	}

	/**
	 * Tests whether similarity checking can detect and handle cycles of
	 * {@link Reference} instances, which has a length of 3: <br>
	 * <br>
	 * {@code ref11 -> ref12 -> ref13 -> ref11} <br>
	 * ----------------------VS----------------------<br>
	 * {@code ref21 -> ref22 -> ref23 -> ref21} <br>
	 * <br>
	 * Note: Only testing for cycles with a length smaller than 3 is not enough,
	 * since they can be easily accounted for, due to both {@link Reference}
	 * instances (or the same reference instance, if it references itself) being
	 * directly accessible from on another (by simply using the corresponding
	 * getters). This is, however, not the case once cycles contain more than 2
	 * instances. <br>
	 * <br>
	 * Performs this check for each combination of sub-type of {@link Reference}.
	 * 
	 * @param refX1Init Initialiser of ref11 and ref21
	 * @param refX2Init Initialiser of ref12 and ref22
	 * @param refX3Init Initialiser of ref13 and ref23
	 */
	@Disabled("Until cycle checking mechanisms are implemented")
	@ParameterizedTest(name = "{3}")
	@MethodSource("genTestParams_ForThree")
	public void test_ReferenceCycles_ThreeReferencesCycle(IReferenceInitialiser refX1Init,
			IReferenceInitialiser refX2Init, IReferenceInitialiser refX3Init, String displayName) {
		var ref11 = refX1Init.instantiate();
		var ref12 = refX2Init.instantiate();
		var ref13 = refX3Init.instantiate();

		refX1Init.setNext(ref11, ref12);
		refX2Init.setNext(ref12, ref13);
		refX3Init.setNext(ref13, ref11);

		var ref21 = refX1Init.instantiate();
		var ref22 = refX2Init.instantiate();
		var ref23 = refX3Init.instantiate();

		refX1Init.setNext(ref21, ref22);
		refX2Init.setNext(ref22, ref23);
		refX3Init.setNext(ref23, ref21);

		this.cycleAssertionsFor(new Reference[] { ref11, ref12, ref13 }, new Reference[] { ref21, ref22, ref23 });
	}

	/**
	 * Tests whether similarity checking can detect and handle cycles of
	 * {@link Reference} instances, where the cycle (of length 2) does not include
	 * the first reference: <br>
	 * <br>
	 * {@code ref11 -> ref12 -> ref13 -> ref12 -> ref13} <br>
	 * -----------------------------VS-----------------------------<br>
	 * {@code ref21 -> ref22 -> ref23 -> ref22 -> ref23} <br>
	 * <br>
	 * Note: Only testing for cycles, which consist of the entire reference chain is
	 * not enough, since such cases do not necessarily cover the scenario presented
	 * here. Especially if cycle checking expects the first reference (ref1) to
	 * repeat and not the other ones. <br>
	 * <br>
	 * Performs this check for each combination of sub-type of {@link Reference}.
	 * 
	 * @param refX1Init Initialiser of ref11 and ref21
	 * @param refX2Init Initialiser of ref12 and ref22
	 * @param refX3Init Initialiser of ref13 and ref23
	 */
	@Disabled("Until cycle checking mechanisms are implemented")
	@ParameterizedTest(name = "{3}")
	@MethodSource("genTestParams_ForThree")
	public void test_ReferenceCycles_OneRefLeadingToTwoRefCycle(IReferenceInitialiser refX1Init,
			IReferenceInitialiser refX2Init, IReferenceInitialiser refX3Init, String displayName) {
		var ref11 = refX1Init.instantiate();
		var ref12 = refX2Init.instantiate();
		var ref13 = refX3Init.instantiate();

		refX1Init.setNext(ref11, ref12);
		refX2Init.setNext(ref12, ref13);
		refX3Init.setNext(ref13, ref12);

		var ref21 = refX1Init.instantiate();
		var ref22 = refX2Init.instantiate();
		var ref23 = refX3Init.instantiate();

		refX1Init.setNext(ref21, ref22);
		refX2Init.setNext(ref22, ref23);
		refX3Init.setNext(ref23, ref22);

		/*
		 * Directly use isSimilar to avoid cloning ref, so that the underlying cloning
		 * mechanisms are not involved and the cycle directly lands into similarity
		 * checking.
		 * 
		 * All ref variables are similar, since all of them are pairwise similar.
		 */
		this.cycleAssertionsFor(new Reference[] { ref11, ref12, ref13 }, new Reference[] { ref21, ref22, ref23 });
	}
}
