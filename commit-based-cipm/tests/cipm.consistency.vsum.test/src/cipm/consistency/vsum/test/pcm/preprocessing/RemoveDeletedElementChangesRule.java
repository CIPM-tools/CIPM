package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;

public class RemoveDeletedElementChangesRule extends ChangeSequenceProcessingRule {
	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		var newChangeList = new ArrayList<>(changeSequence);

		/*
		 * Start from the last change and look for changes that delete EObjects. Then
		 * remove all previous changes that become redundant, as their EObject has been
		 * deleted.
		 */
		for (int i = newChangeList.size() - 1; i >= 0; i--) {
			var currentChange = newChangeList.get(i);
			if (!changeSequence.contains(currentChange))
				continue;
			if (ChangeUtil.isEObjectRemovingChange(currentChange)) {
				var creatingChange = this.getMatchingCreationChange(currentChange, changeSequence);
				removeRedundantChangesFor(currentChange, changeSequence);

				// Creating change is within this change sequence and is deleted above
				// Therefore, the deleting change (currentChange) should also be removed
				if (creatingChange != null) {
					changeSequence.remove(currentChange);
				}
			}
		}
		return changeSequence;
	}

	private EChange getMatchingCreationChange(EChange deletingChange, List<EChange> changeSequence) {
		var deletingChangeIdx = changeSequence.indexOf(deletingChange);

		// If the only change is the deleting change (deletingChangeIdx == 0) or
		// deleting change is not in the change sequence (deletingChangeIdx == -1),
		// abort
		if (deletingChangeIdx < 1)
			return null;

		// If there is no removed element in the change, abort
		var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
		if (deletedElement == null)
			return null;

		for (int i = deletingChangeIdx - 1; i >= 0; i--) {
			var currentChange = changeSequence.get(i);
			if (ChangeUtil.isEObjectInvolvedIn(currentChange, deletedElement)
					&& ChangeUtil.isEObjectCreatingChange(currentChange)) {
				return currentChange;
			}
		}

		return null;
	}

	private void removeRedundantChangesFor(EChange deletingChange, List<EChange> changeSequence) {
		var deletingChangeIdx = changeSequence.indexOf(deletingChange);

		// If the only change is the deleting change (deletingChangeIdx == 0) or
		// deleting change is not in the change sequence (deletingChangeIdx == -1),
		// abort
		if (deletingChangeIdx < 1)
			return;

		// If there is no removed element in the change, abort
		var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
		if (deletedElement == null)
			return;

		// Iterate over another list to avoid concurrent modification exceptions
		var newChangeList = new ArrayList<>(changeSequence);
		for (int i = deletingChangeIdx - 1; i >= 0; i--) {
			var currentChange = newChangeList.get(i);
			if (!changeSequence.contains(currentChange))
				continue;
			if (ChangeUtil.isEObjectInvolvedIn(currentChange, deletedElement)) {

				/*
				 * Feature changes that add deletedElement as the new value for a feature of an
				 * EObject != deletedElement should transform to unset changes, as
				 * deletedElement is no longer present. Subtracting changes, on the other hand,
				 * can be discarded
				 */
				if (currentChange instanceof FeatureEChange && currentChange instanceof AdditiveEChange
						&& !ChangeUtil.eObjectsEqual(ChangeUtil.getAffectedEObject(deletingChange), deletedElement)) {
					changeSequence.add(changeSequence.indexOf(currentChange),
							getUnsetChangeFor((FeatureEChange<?, ?>) currentChange));
				}
				changeSequence.remove(currentChange);
			}
		}
	}

	private UnsetFeature<?, ?> getUnsetChangeFor(FeatureEChange<?, ?> change) {
		var unsetChange = FeatureFactory.eINSTANCE.createUnsetFeature();
		unsetChange.setAffectedEObject(change.getAffectedEObject());
		unsetChange.setAffectedFeature(change.getAffectedFeature());
		unsetChange.setAffectedEObjectID(change.getAffectedEObjectID());
		return unsetChange;
	}
}
