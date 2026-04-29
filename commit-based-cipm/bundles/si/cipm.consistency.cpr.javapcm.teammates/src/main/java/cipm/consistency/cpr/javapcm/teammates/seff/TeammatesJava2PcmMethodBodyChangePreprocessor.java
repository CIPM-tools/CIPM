package cipm.consistency.cpr.javapcm.teammates.seff;

import tools.cipm.seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor;

public class TeammatesJava2PcmMethodBodyChangePreprocessor extends ExtendedJava2PcmMethodBodyChangePreprocessor {
	public TeammatesJava2PcmMethodBodyChangePreprocessor() {
		super(new TeammatesCodeToSeffFactory(), false);
	}
}
