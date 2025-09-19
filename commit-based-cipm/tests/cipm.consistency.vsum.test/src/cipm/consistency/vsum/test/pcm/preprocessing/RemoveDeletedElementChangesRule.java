package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

public class RemoveDeletedElementChangesRule extends ChangeSequenceProcessingRule {
	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		/*
		 * Start from the last change and look for changes that delete EObjects. Then
		 * remove all previous changes that become redundant, as their EObject has been
		 * deleted.
		 */
		for (int i = changeSequence.size() - 1; i >= 0; i--) {
			var currentChange = changeSequence.get(i);
			if (!newChangeList.contains(currentChange))
				continue;
			if (currentChange instanceof DeleteEObject) {
				var creatingChange = this.getMatchingCreationChange(currentChange, changeSequence);
				newChangeList = this.removeRedundantChangesFor(currentChange, changeSequence);

				// Creating change is within this change sequence and is deleted above
				// Therefore, the deleting change (currentChange) should also be removed
				if (creatingChange != null) {
					var rootInsertingChange = this.getMatchingRootInsertChange(currentChange, changeSequence);
					var rootRemovingChange = this.getMatchingRootRemoveChange(currentChange, changeSequence);

					// If currentChange is deleting a root EObject, remove the root insertion and
					// removal changes too
					if (rootInsertingChange != null && rootRemovingChange != null) {
						newChangeList.remove(rootInsertingChange);
						newChangeList.remove(rootRemovingChange);
					}

					// Remove the negated deleting change (currentChange)
					newChangeList.remove(currentChange);
				}
			}
		}
		return newChangeList;
	}

	private EChange getMatchingRootInsertChange(EChange deletingChange, List<EChange> changeSequence) {
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
					&& currentChange instanceof InsertRootEObject) {
				return currentChange;
			}
		}

		return null;
	}

	private EChange getMatchingRootRemoveChange(EChange deletingChange, List<EChange> changeSequence) {
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
					&& currentChange instanceof RemoveRootEObject) {
				return currentChange;
			}
		}

		return null;
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
					&& currentChange instanceof CreateEObject) {
				return currentChange;
			}
		}

		return null;
	}

	private List<EChange> removeRedundantChangesFor(EChange deletingChange, List<EChange> changeSequence) {
		var newChangeList = new ArrayList<>(changeSequence);
		var deletingChangeIdx = changeSequence.indexOf(deletingChange);

		// If the only change is the deleting change (deletingChangeIdx == 0) or
		// deleting change is not in the change sequence (deletingChangeIdx == -1),
		// abort
		if (deletingChangeIdx < 1)
			return newChangeList;

		// If there is no removed element in the change, abort
		var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
		if (deletedElement == null)
			return newChangeList;

		// Iterate over another list to avoid concurrent modification exceptions
		for (int i = deletingChangeIdx - 1; i >= 0; i--) {
			var currentChange = changeSequence.get(i);
			if (!newChangeList.contains(currentChange))
				continue;
			// Do not remove RemoveRootEObject instances, unless there is a preceding
			// CreateEObject
			if (currentChange instanceof RemoveRootEObject)
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
					newChangeList.add(newChangeList.indexOf(currentChange),
							getUnsetChangeFor((FeatureEChange<?, ?>) currentChange));
				}
				newChangeList.remove(currentChange);
			}
		}

		return newChangeList;
	}

	private UnsetFeature<?, ?> getUnsetChangeFor(FeatureEChange<?, ?> change) {
		var unsetChange = FeatureFactory.eINSTANCE.createUnsetFeature();
		unsetChange.setAffectedEObject(change.getAffectedEObject());
		unsetChange.setAffectedFeature(change.getAffectedFeature());
		unsetChange.setAffectedEObjectID(change.getAffectedEObjectID());
		return unsetChange;
	}
}
