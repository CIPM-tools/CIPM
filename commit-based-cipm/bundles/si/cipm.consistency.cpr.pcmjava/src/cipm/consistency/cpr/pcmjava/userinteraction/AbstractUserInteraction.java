package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.cpr.pcmjava.logger.PcmCprLogger;

public abstract class AbstractUserInteraction implements CanModifyEntries {
	private final List<ConflictResolutionStrategy> appliedCRSs = new ArrayList<>();
	private String id;

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setID(String id, boolean forceSet) {
		if (forceSet || this.id == null) {
			this.id = id;
		}
	}

	@Override
	public String toString() {
		if (getID() != null)
			return getID();
		return super.toString();
	}

	public void conflictResolutionStrategyApplied(ConflictResolutionStrategy crs) {
		this.appliedCRSs.add(crs);
	}

	public List<ConflictResolutionStrategy> getAppliedConflictResolutionStrategies() {
		return List.copyOf(appliedCRSs);
	}

	public abstract List<EObject> getTriggeringPCMelements();

	public abstract List<EObject> getAffectedPCMElements();

	public abstract List<EObject> getAffectedJavaElements();

	public abstract void performManualUserInteraction();

	public abstract void getDesiredFeatureChangedValue(FeatureEntry featEntry);

	public abstract void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry);

	public boolean hasDesiredFeature(EObject triggeringPCMElement, EStructuralFeature feat) {
		return this.hasDesiredFeature(triggeringPCMElement, null, feat);
	}

	public abstract boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat);

	public abstract boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag);

	public abstract Set<FeatureEntry> getDesiredFeatures();

	public abstract Set<CorrespondenceEntry> getDesiredCorrespondences();

	protected boolean isDesiredFeatureValuePresent(EObject triggeringPCMElement, EStructuralFeature feat) {
		return isDesiredFeatureValuePresent(triggeringPCMElement, null, feat);
	}

	protected boolean isDesiredFeatureValuePresent(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat);
	}

	protected void reportDesiredFeatureValue(FeatureEntry featEntry) {
		PcmUserInteractionManager.setDesiredFeatureValue(this, featEntry);
		PcmCprLogger.getInstance().userInteractionReportedFeature(this, featEntry);
	}

	protected Object retrieveDesiredFeatureValueIfPresent(EObject triggeringPCMElement, EStructuralFeature feat) {
		return retrieveDesiredFeatureValueIfPresent(triggeringPCMElement, null, feat);
	}

	protected Object retrieveDesiredFeatureValueIfPresent(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		PcmCprLogger.getInstance().userInteractionAskedForFeature(this, triggeringPCMElement, affectedJavaElement, feat,
				false);
		var result = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat,
				false);
		PcmCprLogger.getInstance().userInteractionGotFeatureFor(this, result, triggeringPCMElement, affectedJavaElement,
				feat, false);
		return result;
	}

	protected boolean isDesiredCorrespondencePresent(EObject knownSide, String correspondenceTag) {
		return PcmUserInteractionManager.hasDesiredCorrespondence(knownSide, correspondenceTag);
	}

	protected void reportDesiredCorrespondence(CorrespondenceEntry corEntry) {
		PcmUserInteractionManager.setDesiredCorrespondence(this, corEntry);
		PcmCprLogger.getInstance().userInteractionReportedCorrespondence(this, corEntry);
	}

	protected void finaliseUserInteraction() {
		PcmUserInteractionManager.removeUserInteraction(this);
		PcmCprLogger.getInstance().userInteractionFinalised(this);
	}

	protected CorrespondenceEntry retrieveDesiredCorrespondenceIfPresent(EObject knownSide, String correspondenceTag) {
		PcmCprLogger.getInstance().userInteractionAskedForCorrespondence(this, knownSide, correspondenceTag, false);
		var result = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, false);
		PcmCprLogger.getInstance().userInteractionGotCorrespondenceFor(this, result, knownSide, correspondenceTag,
				true);
		return result;
	}

	public abstract boolean isResolved();

	public abstract void resolveAll();

	public Object resolveForFeature(EObject triggeringPCMElement, EStructuralFeature feat) {
		return resolveForFeature(triggeringPCMElement, null, feat);
	}

	public Object resolveForFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		PcmCprLogger.getInstance().userInteractionAskedForFeature(this, triggeringPCMElement, affectedJavaElement, feat,
				true);
		var result = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat,
				true);
		PcmCprLogger.getInstance().userInteractionGotFeatureFor(this, result, triggeringPCMElement, affectedJavaElement,
				feat, true);
		return result;
	}

	public CorrespondenceEntry resolveForCorrespondence(EObject knownSide, String correspondenceTag) {
		PcmCprLogger.getInstance().userInteractionAskedForCorrespondence(this, knownSide, correspondenceTag, true);
		var result = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, true);
		PcmCprLogger.getInstance().userInteractionGotCorrespondenceFor(this, result, knownSide, correspondenceTag,
				true);
		return result;
	}
}
