package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class ConflictResolutionStrategy {
	public abstract void applyFor(AbstractUserInteraction userInteraction);

	public boolean isApplicableFor(AbstractUserInteraction userInteraction, List<EObject> triggeringPCMelement,
			List<EObject> javaContext, List<EStructuralFeature> featList) {
		return isApplicableFor(userInteraction, triggeringPCMelement, false, javaContext, false, featList, false);
	}

	public boolean isApplicableFor(AbstractUserInteraction userInteraction, List<EObject> triggeringPCMelement,
			boolean considerTriggeringPCMelementSubset, List<EObject> javaContext, boolean considerJavaContextSubset,
			List<EStructuralFeature> featList, boolean considerFeatListSubset) {
		return (isApplicableForTriggeringPCMElements(userInteraction, triggeringPCMelement,
				considerTriggeringPCMelementSubset)
				|| isApplicableForJavaContext(userInteraction, javaContext, considerJavaContextSubset))
				&& isApplicableForFeatList(userInteraction, featList, considerFeatListSubset);
	}

	public boolean isApplicableForTriggeringPCMElements(AbstractUserInteraction userInteraction,
			List<EObject> triggeringPCMelement, boolean considerJavaContextSubset) {
		var uiTriggeringPCMElems = userInteraction.getTriggeringPCMelements();
		if (uiTriggeringPCMElems.isEmpty())
			return true;

		if (triggeringPCMelement == null || triggeringPCMelement.isEmpty())
			return false;

		return (considerJavaContextSubset && uiTriggeringPCMElems.stream()
				.anyMatch((uipcm) -> triggeringPCMelement.stream().anyMatch((pcm) -> EcoreUtil.equals(uipcm, pcm))))

				||

				(!considerJavaContextSubset && uiTriggeringPCMElems.size() == triggeringPCMelement.size()
						&& uiTriggeringPCMElems.stream().allMatch((uipcm) -> triggeringPCMelement.stream()
								.anyMatch((pcm) -> EcoreUtil.equals(uipcm, pcm))));
	}

	public boolean isApplicableForJavaContext(AbstractUserInteraction userInteraction, List<EObject> javaContext,
			boolean considerJavaContextSubset) {
		var uiJavaContext = userInteraction.getAffectedJavaElements();
		if (uiJavaContext.isEmpty())
			return true;

		if (javaContext == null || javaContext.isEmpty())
			return false;

		return (considerJavaContextSubset && uiJavaContext.stream()
				.anyMatch((uijc) -> javaContext.stream().anyMatch((jc) -> EcoreUtil.equals(uijc, jc))))

				||

				(!considerJavaContextSubset && uiJavaContext.size() == javaContext.size() && uiJavaContext.stream()
						.allMatch((uijc) -> javaContext.stream().anyMatch((jc) -> EcoreUtil.equals(uijc, jc))));
	}

	public boolean isApplicableForFeatList(AbstractUserInteraction userInteraction, List<EStructuralFeature> featList,
			boolean considerFeatListSubset) {
		var uiFeatList = userInteraction.getDesiredFeatures();
		if (uiFeatList.isEmpty())
			return true;

		if (featList == null || featList.isEmpty())
			return false;

		return (considerFeatListSubset
				&& uiFeatList.stream().anyMatch((fe) -> featList.stream().anyMatch((fle) -> fe.featureEquals(fle))))

				||

				(!considerFeatListSubset && uiFeatList.stream()
						.allMatch((fe) -> featList.stream().anyMatch((fle) -> fe.featureEquals(fle))));
	}
}
