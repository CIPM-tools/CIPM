package cipm.consistency.cpr.pcmjava.preprocessing;

import tools.vitruv.change.atomic.EChange;

public class PcmChangeWrapper {
	private PcmDependencyContainer depCon;
	private EChange wrapee;

	public PcmChangeWrapper(EChange wrapee, PcmDependencyContainer depCon) {
		this.wrapee = wrapee;
		this.depCon = depCon;
	}
}
