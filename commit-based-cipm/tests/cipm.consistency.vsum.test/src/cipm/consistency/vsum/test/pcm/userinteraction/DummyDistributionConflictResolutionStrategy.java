package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.palladiosimulator.pcm.core.entity.Entity;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;

public class DummyDistributionConflictResolutionStrategy extends ConflictResolutionStrategy {
	private Entity deletedElement;
	private List<CorrespondenceEntry> correspondences;

	public DummyDistributionConflictResolutionStrategy(Entity deletedElement,
			List<CorrespondenceEntry> correspondences) {
		this.deletedElement = deletedElement;
		this.correspondences = correspondences;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof DistributionUserInteraction && this.deletedElement.getId()
				.equals(((DistributionUserInteraction) userInteraction).getDeletedElement().getId())) {
			for (var cor : this.correspondences) {
				PcmUserInteractionManager.setDesiredCorrespondence(null, cor);
			}
		}
	}
}
