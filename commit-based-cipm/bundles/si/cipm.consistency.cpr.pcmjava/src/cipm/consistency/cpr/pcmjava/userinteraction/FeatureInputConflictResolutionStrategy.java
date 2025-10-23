package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class FeatureInputConflictResolutionStrategy extends ConflictResolutionStrategy {
	private final List<EObject> triggeringPCMelement;
	private final List<EObject> javaContext;
	private final List<EStructuralFeature> featList;
	private final List<Object> featValList;

	public FeatureInputConflictResolutionStrategy(List<EObject> triggeringPCMelement, List<EObject> javaContext,
			List<EStructuralFeature> featList, List<Object> featValList) {
		this.triggeringPCMelement = triggeringPCMelement;
		this.javaContext = javaContext;
		this.featList = featList;
		this.featValList = featValList;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (featValList != null && isApplicableFor(userInteraction, triggeringPCMelement, javaContext, featList)) {
			for (var entry : userInteraction.getDesiredFeatures()) {
				var feat = entry.getAffectedJavaElementFeature();
				int featIdx = featList.indexOf(feat);
				Object featVal = null;

				if (featIdx != -1) {
					featVal = featValList.get(featIdx);
				} else {
					continue;
				}

				if (!feat.isMany()) {
					entry.setOrAddValue(featVal);
				} else {
					entry.setMultipleValues((List<?>) featVal);
				}

				PcmUserInteractionManager.setDesiredFeatureValue(null, entry);
			}
		}
	}
}
