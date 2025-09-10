package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class DummyDistributionConflictResolutionStrategy extends ConflictResolutionStrategy {
	private EObject deletedElement;
	private List<CorrespondenceEntry> correspondences;

	public DummyDistributionConflictResolutionStrategy(EObject deletedElement,
			List<CorrespondenceEntry> correspondences) {
		this.deletedElement = deletedElement;
		this.correspondences = correspondences;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof DistributionUserInteraction
		// FIXME Find a way to pinpoint the related DistributionUserInteraction
		// FIXME EcoreUtil is too strict here, since some derivable attributes change
//				&& EcoreUtil
//				.equals(((DistributionUserInteraction) userInteraction).getDeletedElement(), this.deletedElement)
		) {
			for (var cor : this.correspondences) {
				PcmUserInteractionManager.setDesiredCorrespondence(null, cor);
			}
		}
	}
}
