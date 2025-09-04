package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

public class StandardUserInteractionResult extends AbstractModifiableUserInteractionResult {
	public StandardUserInteractionResult(AbstractUserInteractionWrapper... uis) {
		super();
		for (var ui : uis)
			this.addUserInteraction(ui);
	}
}
