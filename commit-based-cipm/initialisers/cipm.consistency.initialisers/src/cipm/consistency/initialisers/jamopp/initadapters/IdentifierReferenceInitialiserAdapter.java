package cipm.consistency.initialisers.jamopp.initadapters;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.IdentifierReference;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserAdapterStrategy;
import cipm.consistency.initialisers.jamopp.instantiations.ExplicitConstructorCallInitialiser;
import cipm.consistency.initialisers.jamopp.statements.ExpressionStatementInitialiser;

/**
 * An {@link IInitialiserAdapterStrategy} implementation that can be used with
 * {@link IInitialiserBase} implementors that instantiate
 * {@link IdentifierReference}. <br>
 * <br>
 * Let <b>IR</b> be an {@link IdentifierReference} instance.
 * {@link IdentifierReferenceInitialiserAdapter} then nests an uninitialised
 * {@link ExpressionStatement} <b>es</b> instance within an uninitialised
 * {@link ExplicitConstructorCall} <b>ecc</b> instance and sets <b>IR</b>'s
 * container to <b>ecc</b>. This way, <b>IR</b> will have a container, which is
 * neither of type {@link Expression} nor {@link ArraySelector} (i.e. an
 * eligible container). <br>
 * <br>
 * <b>Note: <b>IR</b>'s eligible container will be es.</b>
 * 
 * @author Alp Torac Genc
 */
public class IdentifierReferenceInitialiserAdapter implements IInitialiserAdapterStrategy {

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
	public boolean apply(IInitialiser init, Object obj) {
		var castedO = (IdentifierReference) obj;

		var firstEligibleContainer = this.getFirstEligibleContainer(castedO);

		if (firstEligibleContainer == null) {
			var insInit = new ExplicitConstructorCallInitialiser();
			var ecc = insInit.instantiate();
			insInit.addArgument(ecc, castedO);

			var esInit = new ExpressionStatementInitialiser();
			var es = esInit.instantiate();
			esInit.setExpression(es, ecc);

			return this.getFirstEligibleContainer(castedO) == es;
		}

		return this.getFirstEligibleContainer(castedO) != null;
	}

	@Override
	public IInitialiserAdapterStrategy newStrategy() {
		return new IdentifierReferenceInitialiserAdapter();
	}
}
