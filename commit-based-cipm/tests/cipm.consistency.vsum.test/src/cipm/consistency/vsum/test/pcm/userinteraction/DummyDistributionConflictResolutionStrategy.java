package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.collection.Triplet;

public class DummyDistributionConflictResolutionStrategy extends ConflictResolutionStrategy {
	private EObject deletedElement;
	private List<Triplet<EObject, EObject, String>> correspondences;

	public DummyDistributionConflictResolutionStrategy(EObject deletedElement,
			List<Triplet<EObject, EObject, String>> correspondences) {
		this.deletedElement = deletedElement;
		this.correspondences = correspondences;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof DistributionUserInteraction
//				&& EcoreUtil
//				.equals(((DistributionUserInteraction) userInteraction).getDeletedElement(), this.deletedElement)
		) {
			for (var cor : this.correspondences) {
				PcmUserInteractionManager.setDesiredCorrespondence(null, cor.getElement1(), cor.getElement2(),
						cor.getElement3());
			}
		}
	}
}
