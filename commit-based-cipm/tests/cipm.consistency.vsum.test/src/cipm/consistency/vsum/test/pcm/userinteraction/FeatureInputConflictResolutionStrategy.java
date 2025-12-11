package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;

public class FeatureInputConflictResolutionStrategy extends ConflictResolutionStrategy {
	private final List<EObject> triggeringPCMelement;
	private final List<EObject> javaContext;
	private final List<EStructuralFeature> featList;
	private final List<?> featValList;

	public FeatureInputConflictResolutionStrategy(List<EObject> triggeringPCMelement, List<EObject> javaContext,
			List<EStructuralFeature> featList, List<?> featValList) {
		this.triggeringPCMelement = triggeringPCMelement;
		this.javaContext = javaContext;
		this.featList = featList;
		this.featValList = featValList;
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
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

			reportDesiredFeatureValue(userInteraction, entry);
		}
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return featValList != null && isRelevantFor(userInteraction, triggeringPCMelement, javaContext, featList);
	}
}
