package cipm.consistency.cpr.pcmjava.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

public class HandleReplaceSingleValuedEReferenceChangesRule extends ChangePreprocessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangesList = new ArrayList<>(changeSequence);

		var deletingChanges = Lists.reverse(changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var deletingChange : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
			List<ReplaceSingleValuedEReference<?, ?>> affectedSingleValuedFeatureChanges = newChangesList
					// Only changes prior to deletingChange are relevant
					.subList(0, newChangesList.indexOf(deletingChange)).stream()
					// Only feature changes are relevant
					.filter((c) -> c instanceof ReplaceSingleValuedEReference)
					.map((c) -> (ReplaceSingleValuedEReference<?, ?>) c)
					// Only changes involving deletedElement are relevant
					.filter((c) -> ChangeUtil.isEObjectInvolvedIn(c, deletedElement))
					.collect(Collectors.toCollection(ArrayList::new));

			for (var affectedChange : affectedSingleValuedFeatureChanges) {

				/*
				 * ReplaceSingleValuedEReferences must be handled carefully, since EObject ID
				 * fields are mandatory and change propagation will not be able to resolve
				 * deletedElement once it is deleted. There are 4 cases:
				 * 
				 * 1) deletedElement is affectedEObject: Change must be replaced with an unset
				 * change, in order to update the EOpposite feature's value in its holder, which
				 * signals that deletedElement should be removed from EOpposite feature's
				 * holder.
				 * 
				 * 2) deletedElement is new value: Change must be replaced with an unset change
				 * 
				 * 3) deletedElement is old value: Change's old value (and old value ID) must be
				 * set to null, if change's new value is not null. Otherwise handle it same as
				 * 2).
				 * 
				 * 4) None of the above: Remove the change
				 */
				if (ChangeUtil.eObjectsNonNullAndEqual(affectedChange.getAffectedEObject(), deletedElement)
						|| ChangeUtil.eObjectsNonNullAndEqual(affectedChange.getNewValue(), deletedElement)
						|| (ChangeUtil.eObjectsNonNullAndEqual(affectedChange.getOldValue(), deletedElement)
								&& affectedChange.getNewValue() == null)) {
					// Case 1) or 2)
					handleValueObjectDeleted(newChangesList, affectedChange, deletingChanges);
				} else if (ChangeUtil.eObjectsNonNullAndEqual(affectedChange.getOldValue(), deletedElement)
						&& affectedChange.getNewValue() != null) {
					// Case 3)
					unsetOldValueOfReplaceChange(affectedChange);
				} else {
					// Case 4)
					ChangeUtil.removeChange(affectedChange, newChangesList);
				}

				// TODO Fix if an EObject can be involved in a change in indirect ways (i.e.
				// without being present directly as an attribute)
			}
		}
		return newChangesList;
	}

	private void handleValueObjectDeleted(List<EChange> newChangesList,
			ReplaceSingleValuedEReference<?, ?> castedChange, List<EChange> deletingChanges) {
		// Either deletedElement is new value OR deletedElement is old value and new
		// value is null
		ChangeUtil.replaceChange(castedChange, this.getUnsetChangeFor(castedChange), newChangesList);
	}

	private UnsetFeature<?, ?> getUnsetChangeFor(ReplaceSingleValuedEReference<?, ?> change) {
		var unsetChange = FeatureFactory.eINSTANCE.createUnsetFeature();
		unsetChange.setAffectedEObject(change.getAffectedEObject());
		unsetChange.setAffectedFeature(change.getAffectedFeature());
		unsetChange.setAffectedEObjectID(change.getAffectedEObjectID());
		return unsetChange;
	}

	private void unsetOldValueOfReplaceChange(ReplaceSingleValuedEReference<?, ?> change) {
		change.setOldValue(null);
		change.setOldValueID(null);
	}
}
