package cipm.consistency.vsum.test.pcm.userinteraction;

public class DummyNameConflictResolutionStrategy extends ConflictResolutionStrategy {
	private String predefinedName;

	public DummyNameConflictResolutionStrategy(String predefinedName) {
		this.predefinedName = predefinedName;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof NameUserInteraction) {
			PcmUserInteractionManager.setDesiredFeatureValue(null, userInteraction.getDesiredFeatures().get(0),
					this.predefinedName);
		}
	}
}
