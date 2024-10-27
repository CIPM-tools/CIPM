package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.ReferenceableElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.initialisers.jamopp.classifiers.ClassInitialiser;
import cipm.consistency.initialisers.jamopp.instantiations.ExplicitConstructorCallInitialiser;
import cipm.consistency.initialisers.jamopp.references.IdentifierReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.references.StringReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.statements.ExpressionStatementInitialiser;

/**
 * Contains tests for similarity checking {@link IdentifierReference} instances.
 * These tests cover cases, which are not addressed in other, simpler tests.
 * 
 * @author Alp Torac Genc
 */
public class IdentifierReferenceContainerTest extends AbstractJaMoPPSimilarityTest implements UsesConcreteClassifiers {
	/**
	 * Realises the functionality of
	 * {@code JaMoPPElementUtil.getFirstContainerNotOfGivenType(...)} <br>
	 * <br>
	 * Implementation of that method is copied into this method, since it cannot be
	 * accessed in the current project setup.
	 */
	protected EObject getFirstEligibleContainer(IdentifierReference ref) {
		var currentContainer = ref.eContainer();

		while (currentContainer != null
				&& (currentContainer instanceof Expression || currentContainer instanceof ArraySelector)) {
			currentContainer = currentContainer.eContainer();
		}

		if (!(currentContainer instanceof Expression) && !(currentContainer instanceof ArraySelector)) {
			return currentContainer;
		}

		return null;
	}

	/**
	 * Conditions here were copied from the
	 * {@code ReferencesSimilaritySwitch.caseIdentifierReference(...)} for the sake
	 * of testing. <br>
	 * <br>
	 * This method is used to determine, whether similarity checking will be
	 * performed on the containers of the target attribute of the given references.
	 * Since reaching the said state requires an advanced setup, it is important to
	 * know if it actually is reached.
	 * 
	 * @return Whether the similarity of {@code refX.getTarget().eContainer()} will
	 *         be computed, where X = {1, 2}.
	 */
	protected boolean isTargetContainerSimilarityCheckReached(IdentifierReference ref1, IdentifierReference ref2) {
		var ref1Container = this.getFirstEligibleContainer(ref1);
		var ref2Container = this.getFirstEligibleContainer(ref2);

		var target1 = ref1.getTarget();
		var target2 = ref2.getTarget();

		EObject target1Container = null;
		if (target1 != null) {
			target1Container = target1.eContainer();
		}

		EObject target2Container = null;
		if (target2 != null) {
			target2Container = target2.eContainer();
		}

		return target1Container != ref1Container && target2Container != ref2Container &&

		// refX cannot be null and there is currently no EObject implementor that can be
		// the target of an IdentifierReference IR and have IR as its container.
		// Currently impossible to break the following conditions with actual EObject
		// implementors
				target1Container != ref1 && target2Container != ref2;
	}

	/**
	 * Nests an {@link ExpressionStatement} es instance within an
	 * {@link ExplicitConstructorCall} ecc instance and sets ref's container to ecc.
	 * <br>
	 * <br>
	 * Can be used to add a container to ref (as in {@code ref.eContainer()}). <br>
	 * <br>
	 * <b>Note: ref's eligible container {@code this.getFirstEligibleContainer(ref)}
	 * will be es.</b> This is ensured by assertions.
	 */
	protected void initialiseIdentifierReference(IdentifierReference ref) {
		var insInit = new ExplicitConstructorCallInitialiser();
		var ecc = insInit.instantiate();
		Assertions.assertTrue(insInit.addArgument(ecc, ref));

		var esInit = new ExpressionStatementInitialiser();
		var es = esInit.instantiate();
		Assertions.assertTrue(esInit.setExpression(es, ecc));

		Assertions.assertEquals(ref.eContainer(), ecc);
		Assertions.assertEquals(this.getFirstEligibleContainer(ref), es);
	}

	/**
	 * Checks whether similarity checking works as intended as far as comparing the
	 * containers of both targets is concerned. Note that the said target containers
	 * will only be compared, if
	 * {@link #isTargetContainerSimilarityCheckReached(IdentifierReference, IdentifierReference)}
	 * returns true for both {@link IdentifierReference} instances. <br>
	 * <br>
	 * The said targets are accessed via
	 * {@code identifierReference.getTarget().eContainer()} <br>
	 * <br>
	 * This test method considers only the currently possible scenarios, where
	 * actual {@link EObject} implementors are used throughout the entire test.
	 */
	@Test
	public void test_AllRealisticCases() {
		var targetName = "cls1";
		var targetWCon1 = this.createMinimalClassifierWithCU(new ClassInitialiser(), targetName, "cu1");
		var targetWCon2 = this.createMinimalClassifierWithCU(new ClassInitialiser(), targetName, "cu2");
		var targetWOCon = this.createMinimalClass(targetName);

		// Ensure that the containers are set correctly
		Assertions.assertNotNull(targetWCon1.eContainer());
		Assertions.assertNotNull(targetWCon2.eContainer());
		Assertions.assertFalse(this.getActualEquality(targetWCon1.eContainer(), targetWCon2.eContainer()));
		Assertions.assertNull(targetWOCon.eContainer());

		var objInit = new IdentifierReferenceInitialiser();

		var objWCon = objInit.instantiate();
		this.initialiseIdentifierReference(objWCon);

		var objWOCon = objInit.instantiate();

		var targetArr = new ReferenceableElement[] { targetWCon1, targetWCon2, targetWOCon };
		var objArr = new IdentifierReference[] { objWCon, objWOCon };

		var targetCloneArr = new ReferenceableElement[targetArr.length];
		var objCloneArr = new IdentifierReference[objArr.length];

		// Make sure that the clones also have containers, if the
		// original object had one.

		for (int i = 0; i < targetArr.length; i++) {
			targetCloneArr[i] = this.cloneEObjWithContainers(targetArr[i]);
			Assertions.assertTrue(this.getActualEquality(targetArr[i], targetCloneArr[i]));
		}

		for (int i = 0; i < objArr.length; i++) {
			objCloneArr[i] = this.cloneEObjWithContainers(objArr[i]);
			Assertions.assertTrue(this.getActualEquality(objArr[i], objCloneArr[i]));
		}

		// Test for all possible realistic container situations of identifier reference
		// instances and that of their target
		for (var targetOne : targetArr) {
			for (var targetTwo : targetCloneArr) {
				for (var objOne : objArr) {
					for (var objTwo : objCloneArr) {
						Assertions.assertTrue(this.getActualEquality(targetOne, targetTwo));
						Assertions.assertTrue(objInit.setTarget(objOne, targetOne));
						Assertions.assertTrue(objInit.setTarget(objTwo, targetTwo));

						var expectedResult = !this.isTargetContainerSimilarityCheckReached(objOne, objTwo)
								|| this.isSimilar(targetOne.eContainer(), targetTwo.eContainer());

						this.testSimilarity(objOne, objTwo, expectedResult);
					}
				}
			}
		}
	}

	/**
	 * Checks whether similarity checking works as intended as far as comparing the
	 * containers of both targets is concerned. Note that the said target containers
	 * will only be compared, if
	 * {@link #isTargetContainerSimilarityCheckReached(IdentifierReference, IdentifierReference)}
	 * returns true for both {@link IdentifierReference} instances. <br>
	 * <br>
	 * The said targets are accessed via
	 * {@code identifierReference.getTarget().eContainer()} <br>
	 * <br>
	 * This test method tackles the case, where two {@link IdentifierReference}
	 * instances IR_1 and IR_2 each have their target as their next:
	 * {@code IR_i.getTarget() == IR_i.getNext()} <br>
	 * <br>
	 * Currently, this case is not realistic.
	 */
	@Test
	public void test_TargetEqualsNext_InBothRefs() {
		var objInit = new IdentifierReferenceInitialiser();
		var objOne = objInit.instantiate();

		var objTwo = objInit.instantiate();

		var targetOne = new DummyClassImplAndReference(objOne, new StringReferenceInitialiser().instantiate());
		var targetTwo = new DummyClassImplAndReference(objTwo, new StringReferenceInitialiser().instantiate());

		Assertions.assertTrue(objInit.setTarget(objOne, targetOne));
		Assertions.assertTrue(objInit.setNext(objOne, (DummyClassImplAndReference) targetOne));

		Assertions.assertTrue(objInit.setTarget(objTwo, targetTwo));
		Assertions.assertTrue(objInit.setNext(objTwo, (DummyClassImplAndReference) targetTwo));

		/*
		 * Make sure that:
		 * 
		 * targetOne.eContainer() != objOne.eContainer() && targetTwo.eContainer() !=
		 * objTwo.eContainer() && targetOne.eContainer() == objOne &&
		 * targetTwo.eContainer() == objTwo
		 */
		Assertions.assertNull(objOne.eContainer());
		Assertions.assertNull(objTwo.eContainer());
		Assertions.assertEquals(targetOne.eContainer(), objOne);
		Assertions.assertEquals(targetTwo.eContainer(), objTwo);
		Assertions.assertTrue(this.getActualEquality(targetOne, targetTwo));
		Assertions.assertFalse(this.isTargetContainerSimilarityCheckReached(objOne, objTwo));

		// Swap parameter positions to make sure that the symmetry of similarity
		// checking is asserted
		Assertions.assertTrue(this.isSimilar(objOne, objTwo));
		Assertions.assertTrue(this.isSimilar(objTwo, objOne));
	}

	/**
	 * Checks whether similarity checking works as intended as far as comparing the
	 * containers of both targets is concerned. Note that the said target containers
	 * will only be compared, if
	 * {@link #isTargetContainerSimilarityCheckReached(IdentifierReference, IdentifierReference)}
	 * returns true for both {@link IdentifierReference} instances. <br>
	 * <br>
	 * The said targets are accessed via
	 * {@code identifierReference.getTarget().eContainer()} <br>
	 * <br>
	 * This test method tackles the case, where one {@link IdentifierReference}
	 * instance IR_1 has its target as its next:
	 * {@code IR_1.getTarget() == IR_1.getNext()}. In this case, IR_2 only has its
	 * target set: {@code IR_2.getTarget() != null && IR_2.getNext() == null} <br>
	 * <br>
	 * Currently, this case is not realistic.
	 */
	@Test
	public void test_TargetEqualsNext_InOneRef() {
		var objInit = new IdentifierReferenceInitialiser();

		var objOne = objInit.instantiate();
		var objTwo = objInit.instantiate();

		var targetOne = new DummyClassImplAndReference(objOne, new StringReferenceInitialiser().instantiate());
		var targetTwo = new DummyClassImplAndReference(objTwo, new StringReferenceInitialiser().instantiate());

		Assertions.assertTrue(objInit.setTarget(objOne, targetOne));
		Assertions.assertTrue(objInit.setNext(objOne, (DummyClassImplAndReference) targetOne));

		// Only set targetTwo as target in objTwo and not also as next, since that will
		// make objTwo its container
		Assertions.assertTrue(objInit.setTarget(objTwo, targetTwo));

		/*
		 * Make sure that:
		 * 
		 * targetOne.eContainer() != objOne.eContainer() && targetTwo.eContainer() !=
		 * objTwo.eContainer() && targetOne.eContainer() == objOne &&
		 * targetTwo.eContainer() != objTwo
		 */
		Assertions.assertNull(objOne.eContainer());
		Assertions.assertNull(objTwo.eContainer());
		Assertions.assertEquals(targetOne.eContainer(), objOne);
		Assertions.assertNotEquals(targetTwo.eContainer(), objTwo);
		Assertions.assertTrue(this.getActualEquality(targetOne, targetTwo));
		Assertions.assertFalse(this.isTargetContainerSimilarityCheckReached(objOne, objTwo));

		// Swap parameter positions to make sure that the symmetry of similarity
		// checking is asserted
		// objX.getNext() matters for similarity checking
		Assertions.assertFalse(this.isSimilar(objOne, objTwo));
		Assertions.assertFalse(this.isSimilar(objTwo, objOne));
	}
}
