package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;

public class DummyNamespaceConflictResolutionStrategy extends ConflictResolutionStrategy {
	private List<String> nss;
	private EObject triggeringElement;

	public DummyNamespaceConflictResolutionStrategy(EObject triggeringElement, List<String> nss) {
		this.triggeringElement = triggeringElement;
		this.nss = nss;
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		var entry = userInteraction.getDesiredFeatures().iterator().next();
		entry.addValues(this.nss);
		PcmUserInteractionManager.setDesiredFeatureValue(null, entry);
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return userInteraction instanceof NamespaceUserInteraction && (EcoreUtil
				.equals(((NamespaceUserInteraction) userInteraction).getTriggeringElement(), this.triggeringElement));
	}
}
