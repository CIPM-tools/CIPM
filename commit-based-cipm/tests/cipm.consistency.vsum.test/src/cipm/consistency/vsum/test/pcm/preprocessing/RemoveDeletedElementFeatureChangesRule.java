package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.UpdateMultiValuedFeatureEChange;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

/**
 * <p>
 * Set containment obj.feat to R -> Delete R ==> NOP -> Delete R
 * <p>
 * Set containing obj.feat to R -> Delete R ==> Unset obj.feat -> Delete R
 */
public class RemoveDeletedElementFeatureChangesRule extends ChangeSequenceProcessingRule {

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		/*
		 * Reverse the list for easier iteration
		 */
		List<EChange> newChangesList = new ArrayList<>(changeSequence);

		var deletingChanges = Lists.reverse(changeSequence.stream().filter((c) -> c instanceof DeleteEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var deletingChange : deletingChanges) {
			var deletedElement = ChangeUtil.getDeletedEObject(deletingChange);
			List<FeatureEChange<?, ?>> affectedSingleValuedFeatureChanges = newChangesList
					// Only changes prior to deletingChange are relevant
					.subList(0, newChangesList.indexOf(deletingChange)).stream()
					// Only feature changes are relevant
					.filter((c) -> c instanceof FeatureEChange).map((c) -> (FeatureEChange<?, ?>) c)
					// Many-valued feature changes have to be handled elsewhere, since they are
					// needed by change propagation to place affected EObjects to staged area.
					// Including these changes will result in change resolution errors, due to
					// EObject IDs being invalid
					.filter((c) -> !(c instanceof UpdateMultiValuedFeatureEChange))
					// Only changes involving deletedElement are relevant
					.filter((c) -> ChangeUtil.isEObjectInvolvedIn(c, deletedElement))
					.collect(Collectors.toCollection(ArrayList::new));

			for (var affectedChange : affectedSingleValuedFeatureChanges) {

				/*
				 * ReplaceSingleValuedEReferences must be handled carefully, since EObject ID
				 * fields are mandatory and change propagation will not be able to resolve
				 * deletedElement once it is deleted. There are 3 cases:
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
				 */
				if (affectedChange instanceof ReplaceSingleValuedEReference) {
					var castedChange = (ReplaceSingleValuedEReference<?, ?>) affectedChange;
					if (ChangeUtil.eObjectsNonNullAndEqual(castedChange.getAffectedEObject(), deletedElement)
							|| ChangeUtil.eObjectsNonNullAndEqual(castedChange.getNewValue(), deletedElement)
							|| (ChangeUtil.eObjectsNonNullAndEqual(castedChange.getOldValue(), deletedElement)
									&& castedChange.getNewValue() == null)) {
						handleValueObjectDeleted(newChangesList, castedChange, deletingChanges, deletedElement);
					} else if (ChangeUtil.eObjectsNonNullAndEqual(castedChange.getOldValue(), deletedElement)
							&& castedChange.getNewValue() != null) {
						// deletedElement is old value and its new value is not null
						unsetOldValueOfReplaceChange(castedChange);
					}

					// TODO Fix if an EObject can be involved in a change in indirect ways (i.e.
					// without being present directly as an attribute)

				} else {
					/*
					 * All other feature changes that involve deletedElement should be removed
					 */
					newChangesList.remove(affectedChange);
				}
			}
		}
		return newChangesList;
	}

//	private void handleAffectedObjectDeleted(List<EChange> newChangesList,
//			ReplaceSingleValuedEReference<?, ?> castedChange, List<EChange> deletingChanges, EObject deletedElement) {
//		// deletedElement is affectedEObject
//		var oppositeFeat = castedChange.getAffectedFeature().getEOpposite();
//		var oppositeObj = (EObject) castedChange.getAffectedEObject().eGet(castedChange.getAffectedFeature());
//		var isOppositeObjDeleted = deletingChanges.stream()
//				.anyMatch((c) -> ChangeUtil.eObjectsNonNullAndEqual(ChangeUtil.getDeletedEObject(c), oppositeObj));
//		if (!isOppositeObjDeleted) {
//			var idx = newChangesList.indexOf(castedChange);
//			if (!oppositeFeat.isMany()) {
//				newChangesList.add(idx, this.getUnsetChangeForEOpposite(castedChange));
//			} else {
//				newChangesList.add(idx, this.getListRemovalChangeForEOpposite(castedChange));
//			}
//		}
//		newChangesList.remove(castedChange);
//	}

	private void handleValueObjectDeleted(List<EChange> newChangesList,
			ReplaceSingleValuedEReference<?, ?> castedChange, List<EChange> deletingChanges, EObject deletedElement) {
		// Either deletedElement is new value OR deletedElement is old value and new
		// value is null
		var idx = newChangesList.indexOf(castedChange);
		newChangesList.add(idx, this.getUnsetChangeFor(castedChange));
		newChangesList.remove(castedChange);
	}

//	private RemoveFromListEChange<?, ?, ?> getListRemovalChangeForEOpposite(
//			ReplaceSingleValuedEReference<?, ?> change) {
//		var oppositeFeat = change.getAffectedFeature().getEOpposite();
//		var oppositeObj = change.getOldValue();
//		var oppositeObjID = change.getOldValueID();
//
//		var changeAffectedObj = change.getAffectedEObject();
//		var changeAffectedObjID = change.getAffectedEObjectID();
//
//		var removeChange = ReferenceFactory.eINSTANCE.createRemoveEReference();
//		removeChange.setAffectedEObject(oppositeObj);
//		removeChange.setAffectedFeature(oppositeFeat);
//		removeChange.setAffectedEObjectID(oppositeObjID);
//		removeChange.setOldValue(changeAffectedObj);
//		removeChange.setOldValueID(changeAffectedObjID);
//		var list = (List<?>) oppositeObj.eGet(oppositeFeat);
//		removeChange.setIndex((list.indexOf(changeAffectedObj)));
//		return removeChange;
//	}

	private UnsetFeature<?, ?> getUnsetChangeFor(ReplaceSingleValuedEReference<?, ?> change) {
		var unsetChange = FeatureFactory.eINSTANCE.createUnsetFeature();
		unsetChange.setAffectedEObject(change.getAffectedEObject());
		unsetChange.setAffectedFeature(change.getAffectedFeature());
		unsetChange.setAffectedEObjectID(change.getAffectedEObjectID());
		return unsetChange;
	}

//	private UnsetFeature<?, ?> getUnsetChangeForEOpposite(ReplaceSingleValuedEReference<?, ?> change) {
//		var oppositeFeat = change.getAffectedFeature().getEOpposite();
//		var oppositeObj = (EObject) change.getAffectedEObject().eGet(change.getAffectedFeature());
//		var oppositeObjID = change.getOldValueID();
//
//		var unsetChange = FeatureFactory.eINSTANCE.createUnsetFeature();
//		unsetChange.setAffectedEObject(oppositeObj);
//		unsetChange.setAffectedFeature(oppositeFeat);
//		unsetChange.setAffectedEObjectID(oppositeObjID);
//		return unsetChange;
//	}

	private void unsetOldValueOfReplaceChange(ReplaceSingleValuedEReference<?, ?> change) {
		change.setOldValue(null);
		change.setOldValueID(null);
	}
}
