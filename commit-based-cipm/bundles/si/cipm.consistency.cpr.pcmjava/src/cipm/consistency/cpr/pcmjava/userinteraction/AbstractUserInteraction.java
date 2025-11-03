package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class AbstractUserInteraction {
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
	}

	protected Object retrieveDesiredFeatureValueIfPresent(EObject triggeringPCMElement, EStructuralFeature feat) {
		return retrieveDesiredFeatureValueIfPresent(triggeringPCMElement, null, feat);
	}

	protected Object retrieveDesiredFeatureValueIfPresent(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat, false);
	}

	protected boolean isDesiredCorrespondencePresent(EObject knownSide, String correspondenceTag) {
		return PcmUserInteractionManager.hasDesiredCorrespondence(knownSide, correspondenceTag);
	}

	protected void reportDesiredCorrespondence(CorrespondenceEntry corEntry) {
		PcmUserInteractionManager.setDesiredCorrespondence(this, corEntry);
	}

	protected void finaliseUserInteraction() {
		PcmUserInteractionManager.removeUserInteraction(this);
	}

	protected CorrespondenceEntry retrieveDesiredCorrespondenceIfPresent(EObject knownSide, String correspondenceTag) {
		return PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, false);
	}

	public abstract boolean isResolved();

	public abstract void resolveAll();

	public Object resolveForFeature(EObject triggeringPCMElement, EStructuralFeature feat) {
		return resolveForFeature(triggeringPCMElement, null, feat);
	}

	public Object resolveForFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat, true);
	}

	public CorrespondenceEntry resolveForCorrespondence(EObject knownSide, String correspondenceTag) {
		return PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, true);
	}
}
