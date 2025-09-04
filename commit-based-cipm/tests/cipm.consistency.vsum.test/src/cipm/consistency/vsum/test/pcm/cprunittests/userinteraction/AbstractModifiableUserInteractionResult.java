package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class AbstractModifiableUserInteractionResult extends AbstractUserInteractionResult {
	public void addUserInteraction(IUserInteractionWrapper wrapper) {
		this.getModifiableRelevantUserInteractions().add(wrapper);
	}

	public void removeUserInteraction(IUserInteractionWrapper wrapper) {
		this.getModifiableRelevantUserInteractions().remove(wrapper);
	}

	public void addResult(EStructuralFeature feature, Object result) {
		this.getModifiableResults().put(feature, result);
	}

	public void removeResult(EStructuralFeature feature) {
		this.getModifiableResults().remove(feature);
	}

	public void replaceResult(EStructuralFeature feature, Object newResult) {
		this.getModifiableResults().replace(feature, newResult);
	}
}
