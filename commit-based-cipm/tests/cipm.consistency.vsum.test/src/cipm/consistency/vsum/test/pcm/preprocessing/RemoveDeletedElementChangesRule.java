package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
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
			var matchingInsert = insertRootChanges.stream().filter((ic) -> ChangeUtil.areMatchingRootEChange(ic, rc))
					.findFirst();
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

		for (var dc : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(dc);
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
				if (ChangeUtil.isEObjectInvolvedIn(currentChange, deletedElement)) {

					/*
					 * Feature changes that add deletedElement as the new value for a feature of an
					 * EObject != deletedElement should transform to unset changes, as
					 * deletedElement is no longer present. Subtracting changes, on the other hand,
					 * can be discarded
					 */
					if (currentChange instanceof FeatureEChange && currentChange instanceof AdditiveEChange
							&& !ChangeUtil.eObjectsEqual(ChangeUtil.getAffectedEObject(currentChange),
									deletedElement)) {
						newChangeList.add(newChangeList.indexOf(currentChange),
								getUnsetChangeFor((FeatureEChange<?, ?>) currentChange));
					}
					newChangeList.remove(currentChange);
				}
			}

			var matchingCreate = creatingChanges.stream()
					.filter((cc) -> ChangeUtil.areMatchingEObjectExistenceChanges(cc, dc)).findFirst();
			if (matchingCreate.isPresent()) {
				newChangeList.remove(matchingCreate.get());
				newChangeList.remove(dc);
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
