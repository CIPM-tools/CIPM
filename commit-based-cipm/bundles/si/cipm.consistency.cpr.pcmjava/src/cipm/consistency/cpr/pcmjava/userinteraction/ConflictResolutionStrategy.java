package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class ConflictResolutionStrategy {
	public abstract void applyFor(AbstractUserInteraction userInteraction);

	public boolean isApplicableFor(AbstractUserInteraction userInteraction, List<EObject> triggeringPCMelement,
			List<EObject> javaContext, List<EStructuralFeature> featList) {
		var uiTriggeringPCMElements = userInteraction.getTriggeringPCMelements();
		if (triggeringPCMelement != null && (triggeringPCMelement.size() != uiTriggeringPCMElements.size()
				|| !(EcoreUtil.equals(uiTriggeringPCMElements, triggeringPCMelement))))
			return false;

		var uiJavaContext = userInteraction.getAffectedJavaElements();
		if (javaContext != null
				&& (uiJavaContext.size() != javaContext.size() || !(EcoreUtil.equals(uiJavaContext, javaContext))))
			return false;

		var uiFeatList = userInteraction.getDesiredFeatures();
		if (featList != null
				&& uiFeatList.stream().noneMatch((fe) -> featList.stream().anyMatch((fle) -> fe.featureEquals(fle))))
			return false;

		return true;
	}
}
