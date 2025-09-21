package cipm.consistency.vsum.test.pcm.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;

/**
 * Insert Obj in (many-valued feature) feat -> Remove Obj from feat ==> NOP
 */
public class RemoveRedundantSingleListEntryChangesRule extends ChangePreprocessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		var insertChanges = newChangeList.stream().filter((c) -> c instanceof InsertInListEChange)
				.collect(Collectors.toCollection(ArrayList::new));
		var removeChanges = newChangeList.stream().filter((c) -> c instanceof RemoveFromListEChange)
				.collect(Collectors.toCollection(ArrayList::new));

		for (var rc : removeChanges) {
			var matchingInsert = insertChanges.stream()
					.filter((ic) -> ChangeUtil.areMatchingSingleListEntryEChanges(ic, rc)).findFirst();
			if (matchingInsert.isPresent()) {
				newChangeList.remove(matchingInsert.get());
				newChangeList.remove(rc);
			}
		}

		return newChangeList;
	}

}
