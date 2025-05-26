package cipm.consistency.cpr.pcmjava;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import mir.reactions.all.AllChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor;

/**
 * Change propagation specification in order to propagate changes on PCM
 * models to Java.
 * 
 * @author Alp Torac Genc
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster (advisor)
 */
public class CommitIntegrationPCMJavaChangePropagationSpecification extends AllChangePropagationSpecification {
	@Override
	protected void setup() {
		super.setup();
		
		// Use the PCM -> IM rules, since the change propagation is PCM -> Java
		this.addChangeMainprocessor(new ExtendedJava2PcmMethodBodyChangePreprocessor());
	}
}
