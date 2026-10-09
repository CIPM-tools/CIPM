package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.instantiations.NewConstructorCall;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;
import cipm.consistency.fluentapi.java.api.ApiFactory;

/**
 * Adds a {@link TypeReference} to the created {@link NewConstructorCall}
 * <i>ncc</i>. The steps it takes are:
 * <ol>
 * <li>Checks whether <i>ncc</i> has a {@link TypeReference}. If yes, stops.
 * Otherwise continues.
 * <li>Creates a {@link Classifier} instance <i>cls</i>
 * <li>Creates a {@link TypeReference} instance <i>tref</i>
 * <li>Sets <i>tref's</i> target to <i>cls</i>
 * <li>Sets <i>ncc's</i> type reference to <i>tref</i>
 * </ol>
 * Does not modify the created {@link NewConstructorCall}, if it already has a
 * {@link TypeReference}.
 * 
 * @author Alp Torac Genc
 *
 */
public class NewConstructorCallInitialiserAdapter implements IEObjectAdaptationStrategy {
	@Override
	public boolean apply(EObject obj) {
		var castedO = (NewConstructorCall) obj;

		if (castedO.getTypeReference() == null) {
			var cls = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewClass();
			var tref = ApiFactory.eINSTANCE.createFluentJavaAPI().newClassifierReference().withTarget(cls)
					.createNow();

			castedO.setTypeReference(tref);

			return castedO.getTypeReference().equals(tref);
		}

		return true;
	}
}
