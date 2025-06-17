package cipm.consistency.cpr.javapcm.teammates;

import cipm.consistency.cpr.javapcm.teammates.seff.TeammatesJava2PcmMethodBodyChangePreprocessor;
import mir.reactions.allTeammatesRules.AllTeammatesRulesChangePropagationSpecification;

/**
 * Change propagation specification in order to propagate changes on JaMoPP models to the PCM models.
 * Specific for TEAMMATES.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 */
public class TeammatesJavaPCMChangePropagationSpecification extends AllTeammatesRulesChangePropagationSpecification {
	@Override
	protected void setup() {
		super.setup();
		// Change propagation specification for changes on method bodies.
		// TODO: Needs to be added somewhere else
		// this.addChangeMainprocessor(new TeammatesJava2PcmMethodBodyChangePreprocessor());
	}
}
