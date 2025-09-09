package cipm.consistency.vsum.test.pcm.userinteraction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class DummyNameConflictResolutionStrategy extends ConflictResolutionStrategy {
	private String predefinedName;
	private EObject triggeringElement;

	public DummyNameConflictResolutionStrategy(EObject triggeringElement, String predefinedName) {
		this.triggeringElement = triggeringElement;
		this.predefinedName = predefinedName;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof NameUserInteraction && (EcoreUtil
				.equals(((NameUserInteraction) userInteraction).getTriggeringElement(), this.triggeringElement))) {
			PcmUserInteractionManager.setDesiredFeatureValue(null, userInteraction.getDesiredFeatures().get(0),
					this.predefinedName);
		}
	}
}
