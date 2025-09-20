package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;

/**
 * Create R -> Delete R ==> NOP
 */
public class RemoveRedundantExistenceChangesRule extends ChangeSequenceProcessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		var creatingChanges = changeSequence.stream().filter((c) -> c instanceof CreateEObject)
				.collect(Collectors.toCollection(ArrayList::new));
		var deletingChanges = Lists.reverse(changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var deletingChange : deletingChanges) {
			var matchingCreateChangeOfDeletingChange = creatingChanges.stream()
					.filter((cc) -> ChangeUtil.areMatchingEObjectExistenceChanges(cc, deletingChange)).findFirst();

			if (matchingCreateChangeOfDeletingChange.isPresent()) {
				newChangeList.remove(matchingCreateChangeOfDeletingChange.get());
				newChangeList.remove(deletingChange);
			}
		}

		return newChangeList;
	}

}
