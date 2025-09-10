package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class AbstractUserInteraction {
	public abstract void performManualUserInteraction();

	public abstract void getDesiredFeatureChangedValue(EStructuralFeature feat, Object newValues);

	public abstract void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry);

	public abstract boolean hasDesiredFeature(EStructuralFeature feat);

	public abstract boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag);

	public abstract List<EStructuralFeature> getDesiredFeatures();

	public abstract Set<CorrespondenceEntry> getDesiredCorrespondences();

	protected boolean isDesiredFeatureValuePresent(EStructuralFeature feat) {
		return PcmUserInteractionManager.hasDesiredFeatureValue(feat);
	}

	protected void reportDesiredFeatureValue(EStructuralFeature feat, Object newValues) {
		PcmUserInteractionManager.setDesiredFeatureValue(this, feat, newValues);
	}

	protected Object retrieveDesiredFeatureValueIfPresent(EStructuralFeature feat) {
		return PcmUserInteractionManager.getDesiredFeatureValue(feat, false);
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
}
