package cipm.consistency.fitests.similarity.jamopp.unittests.mocktests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.Reference;
import org.emftext.language.java.references.ReferenceableElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.IIdentifierReferenceTest;

/**
 * Contains mock tests concerning similarity checking of
 * {@link IdentifierReference} instances. Said tests ensure that similarity
 * checking is robust and can handle erroneous {@link IdentifierReference}
 * instances.
 * 
 * @author Alp Torac Genc
 */
public class IdentifierReferenceMockTest extends AbstractJaMoPPSimilarityTest
		implements IMockTest, IIdentifierReferenceTest {

	/**
	 * A variant of {@link #createReferenceableReferenceMock(Reference)} that uses
	 * standard values, except for the previous reference variable.
	 */
	private ReferenceableElement createReferenceableReferenceMock(Reference prevRef) {
		return this.createReferenceableReferenceMock(getAPI().createNewClass(), getAPI().createNewStringReference(),
				prevRef);
	}

	/**
	 * Provides a mock object, which can be used to set the "target" and the "next"
	 * attributes simultaneously. It will delegate methods declared in
	 * {@link Reference} to innerRef and otherwise will delegate all method calls to
	 * spiedInstance.
	 * 
	 * @param spiedInstance The instance that the created mock will spy on
	 * @param innerRef      The instance, to whom calls to {@link Reference} methods
	 *                      will be delegated
	 * @param prevRef       The reference, which will be the preceding reference of
	 *                      the mock wrapping spiedInstance
	 *                      ({@code mockObject.getPrevious() == prevRef})
	 * @return An {@link EObject} mock that implements both
	 *         {@link ReferenceableElement} as well as {@link Reference} interfaces.
	 */
	private ReferenceableElement createReferenceableReferenceMock(ReferenceableElement spiedInstance,
			Reference innerRef, Reference prevRef) {
		var mockElem = mock(spiedInstance.getClass(), withSettings().spiedInstance(spiedInstance)
				.extraInterfaces(Reference.class).defaultAnswer(new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock arg0) throws Throwable {
						if (Reference.class.isAssignableFrom(arg0.getMethod().getDeclaringClass())) {
							return arg0.getMethod().invoke(innerRef, arg0.getArguments());
						} else {
							return arg0.callRealMethod();
						}
					}
				}));
		when(((Reference) mockElem).getPrevious()).thenReturn(prevRef);
		return mockElem;
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
		var objOne = getAPI().createNewIdentifierReference();
		var objTwo = getAPI().createNewIdentifierReference();

		var targetOne = this.createReferenceableReferenceMock(objOne);
		var targetTwo = this.createReferenceableReferenceMock(objTwo);

		objOne.setTarget(targetOne);
		objOne.setNext((Reference) targetOne);

		objTwo.setTarget(targetTwo);
		objTwo.setNext((Reference) targetTwo);

		// Assert that the setup went as intended
		Assertions.assertNull(objOne.eContainer());
		Assertions.assertNull(objTwo.eContainer());
		Assertions.assertEquals(targetOne.eContainer(), objOne);
		Assertions.assertEquals(targetTwo.eContainer(), objTwo);
		Assertions.assertTrue(this.getActualEquality(targetOne, targetTwo));
		Assertions.assertFalse(this.isTargetContainerSimilarityCheckReached(objOne, objTwo));

		// Assert that the desired branches will be reached
		Assertions.assertNotEquals(targetOne.eContainer(), objOne.eContainer());
		Assertions.assertNotEquals(targetTwo.eContainer(), objTwo.eContainer());
		Assertions.assertEquals(targetOne.eContainer(), objOne);
		Assertions.assertEquals(targetTwo.eContainer(), objTwo);

		/*
		 * Swap parameter positions to make sure that the symmetry of similarity
		 * checking is asserted.
		 * 
		 * Do not use this.testSimilarity(...) because DummyClassImplAndReference cannot
		 * be cloned by EcoreUtil.
		 */
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
		var objOne = getAPI().createNewIdentifierReference();
		var objTwo = getAPI().createNewIdentifierReference();

		// A 3rd IdentifierReference is needed as the temporary
		// prev of targetTwo (see below)
		var objThree = getAPI().createNewIdentifierReference();

		var targetOne = this.createReferenceableReferenceMock(objOne);
		var targetTwo = this.createReferenceableReferenceMock(objTwo);

		objOne.setTarget(targetOne);
		objOne.setNext((Reference) targetOne);

		// Only set targetTwo as target in objTwo and not also as next, since that will
		// make objTwo its container
		objTwo.setTarget(targetTwo);
		objThree.setNext((Reference) targetTwo);

		// Assert that the setup went as intended
		Assertions.assertNull(objOne.eContainer());
		Assertions.assertNull(objTwo.eContainer());
		Assertions.assertEquals(targetOne.eContainer(), objOne);
		Assertions.assertNotEquals(targetTwo.eContainer(), objTwo);
		Assertions.assertTrue(this.getActualEquality(targetOne, targetTwo));
		Assertions.assertFalse(this.isTargetContainerSimilarityCheckReached(objOne, objTwo));

		// Assert that the desired branches will be reached
		Assertions.assertNotEquals(targetOne.eContainer(), objOne.eContainer());
		Assertions.assertNotEquals(targetTwo.eContainer(), objTwo.eContainer());
		Assertions.assertEquals(targetOne.eContainer(), objOne);
		Assertions.assertNotEquals(targetTwo.eContainer(), objTwo);

		/*
		 * Swap parameter positions to make sure that similarity checking is
		 * symmetrical.
		 * 
		 * Do not use this.testSimilarity(...) because DummyClassImplAndReference cannot
		 * be cloned by EcoreUtil.
		 */
		Assertions.assertFalse(this.isSimilar(objOne, objTwo));
		Assertions.assertFalse(this.isSimilar(objTwo, objOne));
	}
}
