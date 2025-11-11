package cipm.consistency.cpr.pcmjava.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;

/**
 * Set obj.feat to R -> Delete R ==> (Unset R or NOP) -> Delete R
 */
public class RemoveRedundantReplaceSingleValuedEAttributeChangesRule extends ChangePreprocessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangesList = new ArrayList<>(changeSequence);

		var deletingChanges = Lists.reverse(changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var deletingChange : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
			List<ReplaceSingleValuedEAttribute<?, ?>> affectedChanges = newChangesList
					// Only changes prior to deletingChange are relevant
					.subList(0, newChangesList.indexOf(deletingChange)).stream()
					.filter((c) -> c instanceof ReplaceSingleValuedEAttribute)
					.map((c) -> (ReplaceSingleValuedEAttribute<?, ?>) c)
					// Only changes involving deletedElement are relevant
					.filter((c) -> ChangeUtil.eObjectsNonNullAndEqual(c.getAffectedEObject(), deletedElement))
					.collect(Collectors.toCollection(ArrayList::new));

			for (var affectedChange : affectedChanges) {
				ChangeUtil.removeChange(affectedChange, newChangesList);
			}
		}
		return newChangesList;
	}
}
