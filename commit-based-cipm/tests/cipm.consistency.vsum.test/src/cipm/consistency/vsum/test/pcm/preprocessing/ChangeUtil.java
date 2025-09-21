package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.AdditiveEChange;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.SubtractiveEChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.list.InsertInListEChange;
import tools.vitruv.change.atomic.feature.list.RemoveFromListEChange;
import tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

public final class ChangeUtil {
	/*
	 * FIXME Unless 2 EObjects are created with features that make them unique,
	 * there is no precise way to determine their equality. Dependencies across
	 * changes have to be analysed to determine what concrete instances are used.
	 */

	public static boolean eObjectsNonNullAndEqual(EObject obj1, EObject obj2) {
		if (obj1 == null || obj2 == null)
			return false;

		if (obj1 == obj2)
			return true;

		var res1 = obj1.eResource();
		var res2 = obj2.eResource();
		if (res1 != null && res2 != null && !res1.getURIFragment(obj1).equals(res2.getURIFragment(obj2)))
			return false;

		return EcoreUtil.equals(obj1, obj2);
	}

	public static boolean idAttributeValuesEqual(EChange change1, EChange change2) {
		if (!(change1 instanceof EObjectExistenceEChange) && !(change2 instanceof EObjectExistenceEChange))
			return true;
		var idVal1 = ((EObjectExistenceEChange<?>) change1).getIdAttributeValue();
		var idVal2 = ((EObjectExistenceEChange<?>) change2).getIdAttributeValue();
		return idVal1 == idVal2 || idVal1.equals(idVal2);
	}

	public static boolean affectedFeatureValueTypeIsEObject(EChange change) {
		var feat = getAffectedFeature(change);
		if (feat == null)
			return false;
		return EObject.class.isAssignableFrom(feat.getEType().getInstanceClass());
	}

	public static boolean affectedFeatureSupportsValueType(EChange change, Class<?> valueType) {
		var feat = getAffectedFeature(change);
		if (feat == null)
			return false;
		return feat.getEType().getInstanceClass().isAssignableFrom(valueType);
	}

	public static boolean affectedFeaturesPresentAndEqual(EChange change1, EChange change2) {
		var feat1 = getAffectedFeature(change1);
		var feat2 = getAffectedFeature(change2);

		if (feat1 == null || feat2 == null)
			return false;

		return feat1 == feat2;
	}

	public static boolean affectedEObjectsPresentAndEqual(EChange change1, EChange change2) {
		var affectedObj1 = getAffectedEObject(change1);
		var affectedObj2 = getAffectedEObject(change2);

		if (affectedObj1 == null || affectedObj2 == null)
			return false;

		if (!idAttributeValuesEqual(change1, change2))
			return false;

		return eObjectsNonNullAndEqual(affectedObj1, affectedObj2);
	}

	public static boolean affectedEObjectFeaturesPresentAndEqual(EChange change1, EChange change2) {
		return affectedFeaturesPresentAndEqual(change1, change2) && affectedEObjectsPresentAndEqual(change1, change2);
	}

	public static boolean newFeatureChangeNegatesOldFeatureChange(EChange oldChange, EChange newChange) {
		// Ensure that the same EObject's same feature is changed
		if (!affectedEObjectFeaturesPresentAndEqual(oldChange, newChange))
			return false;

		// New UnsetFeature negates old FeatureChange
		if (newChange instanceof UnsetFeature)
			return true;

		// New SubtractiveEChange negates old AdditiveEChange
		// Assumption: If there are multiple AdditiveEChanges on the same EObject (and
		// its same feature), there is one SubtractiveEChange between them, which
		// "unsets" the old value
		if (newAndOldValuesPresentAndEqual(oldChange, newChange))
			return true;

		return false;
	}

	public static boolean sameEObjectCreatedAndRemoved(EChange creatingChange, EChange deletingChange) {
		if (creatingChange instanceof CreateEObject && deletingChange instanceof DeleteEObject)
			return affectedEObjectsPresentAndEqual(creatingChange, deletingChange);
		return false;
	}

	public static boolean newAndOldValuesPresentAndEqual(EChange additiveChange, EChange subtractiveChange) {
		if (additiveChange instanceof AdditiveEChange && subtractiveChange instanceof SubtractiveEChange) {
			var newVal = getNewValue(additiveChange);
			var oldVal = getOldValue(subtractiveChange);
			if (newVal == null || oldVal == null)
				return false;
			if (newVal instanceof EObject && oldVal instanceof EObject)
				return eObjectsNonNullAndEqual((EObject) newVal, (EObject) oldVal);
			return oldVal.equals(newVal);
		}
		return false;
	}

	public static boolean newAndOldValuesPresentAndEqual(EChange additiveAndSubractiveChange) {
		return newAndOldValuesPresentAndEqual(additiveAndSubractiveChange, additiveAndSubractiveChange);
	}

	public static EObject getAffectedEObject(EChange change) {
		if (change instanceof EObjectExistenceEChange)
			return ((EObjectExistenceEChange<?>) change).getAffectedEObject();
		if (change instanceof FeatureEChange)
			return ((FeatureEChange<?, ?>) change).getAffectedEObject();
		return null;
	}

	public static String getAffectedEObjectID(EChange change) {
		if (change instanceof EObjectExistenceEChange)
			return ((EObjectExistenceEChange<?>) change).getAffectedEObjectID();
		if (change instanceof FeatureEChange)
			return ((FeatureEChange<?, ?>) change).getAffectedEObjectID();
		return null;
	}

	public static void setAffectedEObjectID(EChange change, String newID) {
		if (change instanceof EObjectExistenceEChange)
			((EObjectExistenceEChange<?>) change).setAffectedEObjectID(newID);
		if (change instanceof FeatureEChange)
			((FeatureEChange<?, ?>) change).setAffectedEObjectID(newID);
	}

	public static EStructuralFeature getAffectedFeature(EChange change) {
		if (change instanceof FeatureEChange)
			return ((FeatureEChange<?, ?>) change).getAffectedFeature();
		return null;
	}

	public static Object getNewValue(EChange change) {
		if (change instanceof AdditiveEChange)
			return ((AdditiveEChange<?>) change).getNewValue();
		return null;
	}

	public static Object getOldValue(EChange change) {
		if (change instanceof SubtractiveEChange)
			return ((SubtractiveEChange<?>) change).getOldValue();
		return null;
	}

	public static int getIndexOfValue(EChange change) {
		if (change instanceof UpdateSingleListEntryEChange)
			return ((UpdateSingleListEntryEChange<?, ?>) change).getIndex();
		return -1;
	}

	public static String getNewValueID(EChange change) {
		if (change instanceof EObjectAddedEChange)
			return ((EObjectAddedEChange<?>) change).getNewValueID();
		return null;
	}

	public static String getOldValueID(EChange change) {
		if (change instanceof EObjectSubtractedEChange)
			return ((EObjectSubtractedEChange<?>) change).getOldValueID();
		return null;
	}

	public static void setNewValueID(EChange change, String newID) {
		if (change instanceof EObjectAddedEChange)
			((EObjectAddedEChange<?>) change).setNewValueID(newID);
	}

	public static void setOldValueID(EChange change, String newID) {
		if (change instanceof EObjectSubtractedEChange)
			((EObjectSubtractedEChange<?>) change).setOldValueID(newID);
	}

	public static EObject getDeletedEObject(EChange change) {
		if (change instanceof DeleteEObject)
			return ((DeleteEObject<?>) change).getAffectedEObject();
		return null;
	}

	public static EObject getRemovedEObject(EChange change) {
		var oldVal = getOldValue(change);
		if (oldVal instanceof EObject)
			return (EObject) oldVal;
		return null;
	}

	public static EObject getInsertedEObject(EChange change) {
		var newVal = getNewValue(change);
		if (newVal instanceof EObject)
			return (EObject) newVal;
		return null;
	}

	public static EObject getCreatedEObject(EChange change) {
		if (change instanceof CreateEObject)
			return ((CreateEObject<?>) change).getAffectedEObject();
		return null;
	}

	public static List<EObject> getInvolvedEObjects(EChange change) {
		var objs = new ArrayList<EObject>();
		if (change instanceof AdditiveEChange && ((AdditiveEChange<?>) change).getNewValue() instanceof EObject)
			objs.add((EObject) ((AdditiveEChange<?>) change).getNewValue());
		if (change instanceof SubtractiveEChange && ((SubtractiveEChange<?>) change).getOldValue() instanceof EObject)
			objs.add((EObject) ((SubtractiveEChange<?>) change).getOldValue());
		if (change instanceof EObjectExistenceEChange)
			objs.add(((EObjectExistenceEChange<?>) change).getAffectedEObject());
		if (change instanceof FeatureEChange)
			objs.add(((FeatureEChange<?, ?>) change).getAffectedEObject());
		return objs;
	}

	public static boolean isEObjectInvolvedIn(EChange change, EObject obj) {
		return containsEObject(getInvolvedEObjects(change), obj);
	}

	public static boolean areMatchingEObjectExistenceChanges(EChange createChange, EChange deleteChange) {
		if (!(createChange instanceof CreateEObject && deleteChange instanceof DeleteEObject))
			return false;
		return sameEObjectCreatedAndRemoved(createChange, deleteChange);
	}

	public static boolean areMatchingRootEChanges(EChange insertChange, EChange removeChange) {
		if (!(insertChange instanceof InsertRootEObject && removeChange instanceof RemoveRootEObject))
			return false;
		return newAndOldValuesPresentAndEqual(insertChange, removeChange);
	}

	public static boolean areMatchingSingleListEntryEChanges(EChange insertChange, EChange removeChange) {
		if (!(insertChange instanceof InsertInListEChange && removeChange instanceof RemoveFromListEChange))
			return false;
		return eObjectsNonNullAndEqual(getAffectedEObject(insertChange), getAffectedEObject(removeChange))
				&& newAndOldValuesPresentAndEqual(insertChange, removeChange)
				&& getIndexOfValue(insertChange) == getIndexOfValue(removeChange)
				&& getAffectedFeature(insertChange) == getAffectedFeature(removeChange);
	}

	public static boolean containsEObject(Collection<EObject> col, EObject objToSeek) {
		if (col == null)
			return false;

		return col.stream().anyMatch((o) -> eObjectsNonNullAndEqual(o, objToSeek));
	}

	public static boolean isRootEObject(EObject obj) {
		if (obj.eResource() == null)
			return false;
		return obj.eResource().getContents().contains(obj);
	}
}
