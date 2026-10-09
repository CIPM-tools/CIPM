package cipm.consistency.fitests.similarity.jamopp.adaptation;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.MemberContainer;

import cipm.consistency.fitests.similarity.eobject.IEObjectAdaptationStrategy;
import cipm.consistency.fluentapi.java.api.ApiFactory;

/**
 * Adds the created {@link Member} to a {@link MemberContainer}. Does not modify
 * the {@link Member} instance, if it is already in a {@link MemberContainer}.
 * This way, similarity checking 2 {@link Member} instances does not throw an
 * exception, due to them not being contained by a {@link MemberContainer}.
 * 
 * @author Alp Torac Genc
 */
public class MemberInitialiserAdapter implements IEObjectAdaptationStrategy {
	@Override
	public boolean apply(EObject obj) {
		var castedO = (Member) obj;

		if (castedO.eContainer() == null) {
			var mc = ApiFactory.eINSTANCE.createFluentJavaAPI().createNewClass();
			mc.getMembers().add(castedO);
			return castedO.eContainer() == mc;
		}

		return true;
	}
}
