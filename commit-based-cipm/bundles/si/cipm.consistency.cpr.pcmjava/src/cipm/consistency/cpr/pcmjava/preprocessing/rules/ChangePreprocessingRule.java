package cipm.consistency.cpr.pcmjava.preprocessing.rules;

import java.util.List;

import tools.vitruv.change.atomic.EChange;

public abstract class ChangePreprocessingRule {
	public abstract List<EChange> apply(List<EChange> changeSequence);
}
