package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class AbstractUserInteraction {
	public abstract void performManualUserInteraction();

	public abstract void getDesiredFeatureChangedValue(FeatureEntry featEntry);

	public abstract void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry);

	public abstract boolean hasDesiredFeature(EObject obj, EStructuralFeature feat);

	public abstract boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag);

	public abstract Set<FeatureEntry> getDesiredFeatures();

	public abstract Set<CorrespondenceEntry> getDesiredCorrespondences();

	protected boolean isDesiredFeatureValuePresent(EObject obj, EStructuralFeature feat) {
		return PcmUserInteractionManager.hasDesiredFeatureValue(obj, feat);
	}

	protected void reportDesiredFeatureValue(FeatureEntry featEntry) {
		PcmUserInteractionManager.setDesiredFeatureValue(this, featEntry);
	}

	protected Object retrieveDesiredFeatureValueIfPresent(EObject obj, EStructuralFeature feat) {
		return PcmUserInteractionManager.getDesiredFeatureValue(obj, feat, false);
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

	public Object resolveForFeature(EObject obj, EStructuralFeature feat) {
		return PcmUserInteractionManager.getDesiredFeatureValue(obj, feat, true);
	}

	public Object resolveForCorrespondence(EObject knownSide, String correspondenceTag) {
		return PcmUserInteractionManager.getDesiredCorrespondence(knownSide, correspondenceTag, true);
	}
}
