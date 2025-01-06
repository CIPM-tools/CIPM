package cipm.consistency.cpr.javapcm;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.cpr.javaim.Java2ImChangePropagationSpecification;
import mir.reactions.all.AllChangePropagationSpecification;

import tools.cipm.seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor;
import tools.cipm.seff.finegrained.FineGrainedJava2PcmMethodBodyChangePreprocessor;

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
		if (CommitIntegrationSettingsContainer.getSettingsContainer()
				.getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)) {
			this.addChangeMainprocessor(new FineGrainedJava2PcmMethodBodyChangePreprocessor());
		} else {
			if (CommitIntegrationSettingsContainer.getSettingsContainer()
					.getPropertyAsBoolean(SettingKeys.USE_PCM_IM_CPRS)) {
				this.addChangeMainprocessor(new ExtendedJava2PcmMethodBodyChangePreprocessor());
			} else {
				this.addChangeMainprocessor(new Java2ImChangePropagationSpecification());
			}
		}
	}
}
