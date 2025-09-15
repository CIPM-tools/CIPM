package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class DummyNamespaceConflictResolutionStrategy extends ConflictResolutionStrategy {
	private List<String> nss;
	private EObject triggeringElement;

	public DummyNamespaceConflictResolutionStrategy(EObject triggeringElement, List<String> nss) {
		this.triggeringElement = triggeringElement;
		this.nss = nss;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof NamespaceUserInteraction && (EcoreUtil
				.equals(((NamespaceUserInteraction) userInteraction).getTriggeringElement(), this.triggeringElement))) {
			var entry = userInteraction.getDesiredFeatures().iterator().next();
			entry.addValues(this.nss);
			PcmUserInteractionManager.setDesiredFeatureValue(null, entry);
		}
	}
}
