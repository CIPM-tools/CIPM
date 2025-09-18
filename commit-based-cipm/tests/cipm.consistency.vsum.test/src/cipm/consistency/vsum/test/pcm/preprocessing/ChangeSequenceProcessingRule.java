package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.List;

import tools.vitruv.change.atomic.EChange;

public abstract class ChangeSequenceProcessingRule {
	public abstract List<EChange> apply(List<EChange> changeSequence);
}
