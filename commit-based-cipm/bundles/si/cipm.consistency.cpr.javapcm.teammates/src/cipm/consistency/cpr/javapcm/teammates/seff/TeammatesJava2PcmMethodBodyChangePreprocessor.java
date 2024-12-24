package cipm.consistency.cpr.javapcm.teammates.seff;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor;

public class TeammatesJava2PcmMethodBodyChangePreprocessor extends ExtendedJava2PcmMethodBodyChangePreprocessor {
	public TeammatesJava2PcmMethodBodyChangePreprocessor() {
		super(new TeammatesCodeToSeffFactory(), false);
	}
}
