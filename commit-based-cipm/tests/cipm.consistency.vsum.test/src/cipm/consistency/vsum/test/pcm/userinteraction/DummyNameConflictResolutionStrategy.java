package cipm.consistency.vsum.test.pcm.userinteraction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;

public class DummyNameConflictResolutionStrategy extends ConflictResolutionStrategy {
	private String predefinedName;
	private EObject triggeringElement;

	public DummyNameConflictResolutionStrategy(EObject triggeringElement, String predefinedName) {
		this.triggeringElement = triggeringElement;
		this.predefinedName = predefinedName;
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		var entry = userInteraction.getDesiredFeatures().iterator().next();
		entry.setOrAddValue(predefinedName);
		PcmUserInteractionManager.setDesiredFeatureValue(null, entry);
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return userInteraction instanceof NameUserInteraction && (EcoreUtil
				.equals(((NameUserInteraction) userInteraction).getTriggeringElement(), this.triggeringElement));
	}
}
