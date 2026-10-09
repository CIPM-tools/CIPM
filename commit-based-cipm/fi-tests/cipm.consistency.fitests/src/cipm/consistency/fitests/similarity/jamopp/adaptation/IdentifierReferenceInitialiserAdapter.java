package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.IdentifierReference;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;
import cipm.consistency.fluentapi.java.api.ApiFactory;

/**
 * Let <b>IR</b> be an {@link IdentifierReference} instance.
 * {@link IdentifierReferenceInitialiserAdapter} then nests an
 * {@link ExpressionStatement} <b>es</b> instance within an
 * {@link ExplicitConstructorCall} <b>ecc</b> instance and sets <b>IR</b>'s
 * container to <b>ecc</b>. This way, <b>IR</b> will have a container, which is
 * neither of type {@link Expression} nor {@link ArraySelector} (i.e. an
 * eligible container). <br>
 * <br>
 * <b>Note: <b>IR</b>'s eligible container will be es.</b>
 * 
 * @author Alp Torac Genc
 */
public class IdentifierReferenceInitialiserAdapter implements IEObjectAdaptationStrategy {
	/**
	 * Realises the functionality of
	 * {@code JaMoPPElementUtil.getFirstContainerNotOfGivenType(...)}
	 */
	private EObject getFirstEligibleContainer(EObject obj) {
		var firstEligibleContainer = obj.eContainer();

		while (firstEligibleContainer != null) {
			if (Expression.class.isAssignableFrom(firstEligibleContainer.getClass())
					|| ArraySelector.class.isAssignableFrom(firstEligibleContainer.getClass())) {
				firstEligibleContainer = firstEligibleContainer.eContainer();
			} else {
				break;
			}
		}

		return firstEligibleContainer;
	}

	@Override
	public boolean apply(EObject obj) {
		var castedO = (IdentifierReference) obj;

		var firstEligibleContainer = this.getFirstEligibleContainer(castedO);

		if (firstEligibleContainer == null) {
			var ecc = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewExplicitConstructorCall();
			ecc.getArguments().add(castedO);

			var es = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewExpressionStatement();
			es.setExpression(ecc);

			return this.getFirstEligibleContainer(castedO) == es;
		}

		return this.getFirstEligibleContainer(castedO) != null;
	}
}
