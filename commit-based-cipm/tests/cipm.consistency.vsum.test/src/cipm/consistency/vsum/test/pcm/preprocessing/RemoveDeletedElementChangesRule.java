package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange;
import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootEChange;

/**
 * <p>
 * Set containment obj.feat to R -> Delete R ==> NOP -> Delete R
 * <p>
 * Set containing obj.feat to R -> Delete R ==> Unset obj.feat -> Delete R
 * <p>
 * Create R -> Delete R ==> NOP
 * <p>
 * Create R -> changeSubSeq -> Delete R ==> NOP -> changeSubSeq without changes
 * on R -> NOP
 */
public class RemoveDeletedElementChangesRule extends ChangeSequenceProcessingRule {
	/**
	 * Assumption: Even if an EObject is re-created, its ID will be different, so
	 * that no 2 EObjects will be equal
	 */
	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		/*
		 * Handle RootEChanges before, since they move EObjects in and out of staged
		 * area. DeleteEObject alone can only delete EObjects inside the staged area and
		 * CreateEObject can only create an EObject inside the staged area, thus they
		 * cannot operate as expected without RootEChanges.
		 */
		removeRedundantRootChanges(newChangeList);
		/*
		 * Handle UpdateMultiValuedFeatureEChange here, as unsetting them is not
		 * possible and they have to remain, if their counterpart is missing.
		 */
		removeRedundantFeatValListChanges(newChangeList);
		removeRedundantChanges(newChangeList, changeSequence);

		return newChangeList;
	}

	/**
	 * Removes redundant insert and remove root changes from newChangeList
	 * <p>
	 * Modifies newChangeList
	 */
	private void removeRedundantRootChanges(List<EChange> newChangeList) {
		var insertRootChanges = newChangeList.stream().filter((c) -> c instanceof InsertRootEObject)
				.collect(Collectors.toCollection(ArrayList::new));
		var removeRootChanges = newChangeList.stream().filter((c) -> c instanceof RemoveRootEObject)
				.collect(Collectors.toCollection(ArrayList::new));

		for (var rc : removeRootChanges) {
			var matchingInsert = insertRootChanges.stream().filter((ic) -> ChangeUtil.areMatchingRootEChanges(ic, rc))
					.findFirst();
			if (matchingInsert.isPresent()) {
				newChangeList.remove(matchingInsert.get());
				newChangeList.remove(rc);
			}
		}
	}

	private void removeRedundantFeatValListChanges(List<EChange> newChangeList) {
		var insertChanges = newChangeList.stream().filter((c) -> c instanceof InsertInListEChange)
				.collect(Collectors.toCollection(ArrayList::new));
		var removeChanges = newChangeList.stream().filter((c) -> c instanceof RemoveFromListEChange)
				.collect(Collectors.toCollection(ArrayList::new));

		for (var rc : removeChanges) {
			var matchingInsert = insertChanges.stream()
					.filter((ic) -> ChangeUtil.areMatchingFeatValListEChanges(ic, rc)).findFirst();
			if (matchingInsert.isPresent()) {
				newChangeList.remove(matchingInsert.get());
				newChangeList.remove(rc);
			}
		}
	}

	/**
	 * Removes redundant insert and remove root changes from newChangeList. Uses
	 * changeSequence as context.
	 * <p>
	 * Modifies newChangeList, does not modify changeSequence
	 */
	private void removeRedundantChanges(List<EChange> newChangeList, List<EChange> changeSequence) {
		var creatingChanges = changeSequence.stream().filter((c) -> c instanceof CreateEObject)
				.collect(Collectors.toCollection(ArrayList::new));
		var deletingChanges = changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new));

		// TODO Refactor once what changes can be removed is fully clear

		for (var deletingChange : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
			var matchingCreateChangeOfDeletingChange = creatingChanges.stream()
					.filter((cc) -> ChangeUtil.areMatchingEObjectExistenceChanges(cc, deletingChange)).findFirst();
			for (var currentChange : List.copyOf(newChangeList)) {
				/*
				 * Exclude EObjectExistenceEChanges, especially dc. Since they each are
				 * designated for creating / removing one EObject, they should not interfere
				 * with one another. If dc becomes redundant (i.e. its matching create change is
				 * present), it will be removed below.
				 */
				if (currentChange instanceof EObjectExistenceEChange)
					continue;
				/*
				 * Exclude RootEChanges, since they move EObjects in and out of staged area.
				 * DeleteEObject alone can only delete EObjects inside the staged area, thus
				 * they cannot operate as expected without RootEChanges.
				 */
				if (currentChange instanceof RootEChange)
					continue;
				/*
				 * Exclude UpdateMultiValuedFeatureEChange. As it is not possible to simply
				 * unset their corresponding feature, they have to be handled elsewhere.
				 */
				if (currentChange instanceof UpdateMultiValuedFeatureEChange)
					continue;
				if (ChangeUtil.isEObjectInvolvedIn(currentChange, deletedElement)) {
					var currentChangeAffectedObj = ChangeUtil.getAffectedEObject(currentChange);

					/*
					 * Use unset changes instead of ReplaceSingleValuedEReferences that involve
					 * deletedElement, in order to avoid change propagation issues. Since
					 * deletedElement will no longer be present, an unset change should signal that
					 * deletedElement is no longer used as a value.
					 */
					// FIXME Use ReplaceSingleValuedFeatureEChanges instead of
					// ReplaceSingleValuedEReference for the first conditional, if tests fail
					if (currentChange instanceof ReplaceSingleValuedEReference
							// deletedElement's EReferences require no unset changes, if deletedElement is
							// confirmed to have been created within the given
							// changeSequence, prior to deletingChange.
							&& (matchingCreateChangeOfDeletingChange.isEmpty()
									|| !ChangeUtil.eObjectsEqual(currentChangeAffectedObj, deletedElement))) {
						newChangeList.add(newChangeList.indexOf(currentChange),
								getUnsetChangeFor((FeatureEChange<?, ?>) currentChange));
					}
					newChangeList.remove(currentChange);
				}
			}

			if (matchingCreateChangeOfDeletingChange.isPresent()) {
				newChangeList.remove(matchingCreateChangeOfDeletingChange.get());
				newChangeList.remove(deletingChange);
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
