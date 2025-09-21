package cipm.consistency.vsum.test.pcm.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;

/**
 * Create R -> Delete R ==> NOP
 */
public class RemoveRedundantExistenceChangesRule extends ChangePreprocessingRule {

	/*
	 * FIXME Change the implementation to detect language of brackets "()", in order
	 * to find the correct create / delete change pairs:
	 * 
	 * "create create remove create remove remove" is currently being handled wrong.
	 * Count the amount of left / right brackets you had to iterate:
	 * 
	 * Increment when you iterate over a bracket of same type, decrement when you
	 * iterate over a bracket of opposing type. When you get to 0, close the bracket
	 */

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
