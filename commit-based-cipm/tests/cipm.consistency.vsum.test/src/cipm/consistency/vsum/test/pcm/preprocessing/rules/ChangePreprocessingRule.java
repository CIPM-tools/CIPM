package cipm.consistency.vsum.test.pcm.preprocessing.rules;

import java.util.List;

import tools.vitruv.change.atomic.EChange;

public abstract class ChangePreprocessingRule {
	public abstract List<EChange> apply(List<EChange> changeSequence);
}
