package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.List;

import tools.vitruv.change.atomic.EChange;

public class RemoveRedundantChangesRule extends ChangeSequenceProcessingRule {
	private static final ChangeSequenceProcessingRule removeRedundantRootChanges = new RemoveRedundantRootChangesRule();
	private static final ChangeSequenceProcessingRule removeRedundantFeatValListChanges = new RemoveRedundantFeatValListChangesRule();
	private static final ChangeSequenceProcessingRule removeRedundantDeletedElementFeatureChanges = new RemoveDeletedElementFeatureChangesRule();
	private static final ChangeSequenceProcessingRule removeRedundantExistenceChangesRuleChanges = new RemoveRedundantExistenceChangesRule();

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		return
		// Create & Remove changes
		removeRedundantExistenceChangesRuleChanges.apply(
				// Single-valued feature changes (replace value)
				removeRedundantDeletedElementFeatureChanges.apply(
						// Many-valued feature changes (insert/remove from list)
						removeRedundantFeatValListChanges.apply(
								// Root element insertion and removal changes
								removeRedundantRootChanges.apply(changeSequence))));
	}

}
