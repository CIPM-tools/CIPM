package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.IdentifierReference;

/**
 * An interface that contains default methods for tests that use and make
 * assertions about {@link IdentifierReference} instances.
 * 
 * @author Alp Torac Genc
 */
public interface IIdentifierReferenceTest {
	/**
	 * Realises the functionality of
	 * {@code JaMoPPElementUtil.getFirstContainerNotOfGivenType(...)} <br>
	 * <br>
	 * Implementation of that method is copied into this method, since it cannot be
	 * accessed in the current project setup.
	 */
	public default EObject getFirstEligibleContainer(IdentifierReference ref) {
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
	public default boolean isTargetContainerSimilarityCheckReached(IdentifierReference ref1, IdentifierReference ref2) {
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
}
