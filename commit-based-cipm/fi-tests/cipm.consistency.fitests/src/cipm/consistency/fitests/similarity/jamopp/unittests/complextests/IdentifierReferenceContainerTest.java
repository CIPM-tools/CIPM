package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.ReferenceableElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.IIdentifierReferenceTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesConcreteClassifiers;
import cipm.consistency.initialisers.jamopp.classifiers.ClassInitialiser;
import cipm.consistency.initialisers.jamopp.references.IdentifierReferenceInitialiser;

/**
 * Contains tests for similarity checking {@link IdentifierReference} instances.
 * These tests cover cases, which are not addressed in other, simpler tests.
 * 
 * @author Alp Torac Genc
 */
public class IdentifierReferenceContainerTest extends AbstractJaMoPPSimilarityTest
		implements UsesConcreteClassifiers, IIdentifierReferenceTest {
	/**
	 * Checks whether similarity checking works as intended as far as comparing the
	 * containers of both targets is concerned. Note that the said target containers
	 * will only be compared, if
	 * {@link #isTargetContainerSimilarityCheckReached(IdentifierReference, IdentifierReference)}
	 * returns true for both {@link IdentifierReference} instances. <br>
	 * <br>
	 * The said targets are accessed via
	 * {@code identifierReference.getTarget().eContainer()}
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
}
