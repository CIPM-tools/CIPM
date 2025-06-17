package cipm.consistency.cpr.javapcm;

import mir.reactions.all.AllChangePropagationSpecification;

/**
 * Change propagation specification in order to propagate changes on JaMoPP models to the PCM models.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 */
public class CommitIntegrationJavaPCMChangePropagationSpecification extends AllChangePropagationSpecification {
	@Override
	protected void setup() {
		super.setup();
		// Change propagation specification for changes on method bodies.
		// TODO: Move this to another place.
//		if (CommitIntegrationSettingsContainer.getSettingsContainer()
//				.getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)) {
//			this.addChangeMainprocessor(new FineGrainedJava2PcmMethodBodyChangePreprocessor());
//		} else {
//			this.addChangeMainprocessor(new ExtendedJava2PcmMethodBodyChangePreprocessor());
//		}
	}
}
