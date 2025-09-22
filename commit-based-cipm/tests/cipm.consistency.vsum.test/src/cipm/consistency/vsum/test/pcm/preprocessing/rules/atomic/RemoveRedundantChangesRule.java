package cipm.consistency.vsum.test.pcm.preprocessing.rules.atomic;

import java.util.List;

import cipm.consistency.vsum.test.pcm.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;

public class RemoveRedundantChangesRule extends ChangePreprocessingRule {
	private static final ChangePreprocessingRule removeRedundantRootChanges = new RemoveRedundantRootChangesRule();
	private static final ChangePreprocessingRule removeRedundantSingleListEntryChanges = new RemoveRedundantSingleListEntryChangesRule();
	private static final ChangePreprocessingRule removeRedundantUnsetChanges = new RemoveRedundantUnsetChangesRule();
	private static final ChangePreprocessingRule removeRedundantReplaceSingleValuedEAttributeChanges = new RemoveRedundantReplaceSingleValuedEAttributeChangesRule();
	private static final ChangePreprocessingRule removeRedundantExistenceChangesRuleChanges = new RemoveRedundantExistenceChangesRule();
	private static final ChangePreprocessingRule fixReplaceSingleValuedEReferenceChanges = new HandleReplaceSingleValuedEReferenceChangesRule();

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		return
		// Create, InsertRoot, RemoveRoot, Delete changes
		removeRedundantExistenceChangesRuleChanges.apply(
				// Fix replace changes
				fixReplaceSingleValuedEReferenceChanges.apply(
						// Many-valued feature changes (insert/remove from list)
						removeRedundantSingleListEntryChanges.apply(
								// Unset feature changes (unset feature)
								removeRedundantUnsetChanges.apply(
										// Single-valued feature changes (replace EAttribute values)
										removeRedundantReplaceSingleValuedEAttributeChanges.apply(

												changeSequence)))));
	}

}
