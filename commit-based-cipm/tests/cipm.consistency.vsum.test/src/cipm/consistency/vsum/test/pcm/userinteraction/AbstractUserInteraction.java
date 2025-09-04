package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class AbstractUserInteraction {
	private final List<EStructuralFeature> desiredFeatures;

	public AbstractUserInteraction() {
		this(new ArrayList<EStructuralFeature>());
	}

	public AbstractUserInteraction(List<EStructuralFeature> desiredFeatures) {
		this.desiredFeatures = desiredFeatures;
	}

	public abstract void performManualUserInteraction();

	public abstract void getDesiredFeatureChangedValue(EStructuralFeature feat, Object newValues);

	public boolean hasDesiredFeature(EStructuralFeature feat) {
		return this.desiredFeatures.contains(feat);
	}

	public List<EStructuralFeature> getDesiredFeatures() {
		return new ArrayList<>(this.desiredFeatures);
	}

	protected List<EStructuralFeature> getModifiableDesiredFeaturesList() {
		return this.desiredFeatures;
	}

	protected boolean isDesiredFeatureValuePresent(EStructuralFeature feat) {
		return PcmUserInteractionManager.hasDesiredFeatureValue(feat);
	}

	protected void reportDesiredFeatureValue(EStructuralFeature feat, Object newValues) {
		PcmUserInteractionManager.setDesiredFeatureValue(this, feat, newValues);
	}

	protected void finaliseUserInteraction() {
		PcmUserInteractionManager.removeUserInteraction(this);
	}
}
