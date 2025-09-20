package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Insert root R -> Remove root R ==> NOP
 */
public class RemoveRedundantRootChangesRule extends ChangeSequenceProcessingRule {
	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		var insertRootChanges = newChangeList.stream().filter((c) -> c instanceof InsertRootEObject)
				.collect(Collectors.toCollection(ArrayList::new));
		var removeRootChanges = Lists.reverse(newChangeList.stream().filter((c) -> c instanceof RemoveRootEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var rc : removeRootChanges) {
			var matchingInsert = insertRootChanges.stream().filter((ic) -> ChangeUtil.areMatchingRootEChanges(ic, rc))
					.findFirst();
			if (matchingInsert.isPresent()) {
				newChangeList.remove(matchingInsert.get());
				newChangeList.remove(rc);
			}
		}

		return newChangeList;
	}
}
