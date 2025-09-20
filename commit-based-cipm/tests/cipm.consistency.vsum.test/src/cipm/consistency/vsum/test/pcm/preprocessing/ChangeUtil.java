package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uka.ipd.sdq.identifier.Identifier;
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
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

public final class ChangeUtil {
	public static boolean eObjectsNonNullAndEqual(EObject obj1, EObject obj2) {
		if (obj1 == null || obj2 == null)
			return false;
		if (!EcoreUtil.equals(obj1, obj2))
			return false;

		if (obj1 instanceof Identifier && obj2 instanceof Identifier) {
			var id1 = ((Identifier) obj1).getId();
			var id2 = ((Identifier) obj2).getId();
			return id1 == id2 || id1.equals(id2);
		}

		return true;
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

	public static boolean sameEObjectCreatedAndRemoved(EChange oldChange, EChange newChange) {
		if (oldChange instanceof InsertRootEObject && newChange instanceof RemoveRootEObject) {
			var castedOC = (InsertRootEObject<?>) oldChange;
			var castedNC = (RemoveRootEObject<?>) newChange;
			return eObjectsNonNullAndEqual(castedOC.getNewValue(), castedNC.getOldValue());
		}

		if (oldChange instanceof CreateEObject && newChange instanceof DeleteEObject) {
			return affectedEObjectsPresentAndEqual(oldChange, newChange);
		}

		return false;
	}

	public static boolean newAndOldValuesPresentAndEqual(EChange oldChange, EChange newChange) {
		if (oldChange instanceof AdditiveEChange && newChange instanceof SubtractiveEChange) {
			var newVal = getNewValue(oldChange);
			var oldVal = getOldValue(newChange);
			if (newVal == null || oldVal == null)
				return false;
			if (newVal instanceof EObject && oldVal instanceof EObject)
				return eObjectsNonNullAndEqual((EObject) newVal, (EObject) oldVal);
			return oldVal.equals(newVal);
		}
		return false;
	}

	public static boolean newAndOldValuesPresentAndEqual(EChange change) {
		return newAndOldValuesPresentAndEqual(change, change);
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
		return eObjectsNonNullAndEqual(getAffectedEObject(createChange), getAffectedEObject(deleteChange));
	}

	public static boolean areMatchingRootEChanges(EChange insertChange, EChange removeChange) {
		if (!(insertChange instanceof InsertRootEObject && removeChange instanceof RemoveRootEObject))
			return false;
		return eObjectsNonNullAndEqual((EObject) getNewValue(insertChange), (EObject) getOldValue(removeChange));
	}

	public static boolean areMatchingFeatValListEChanges(EChange insertChange, EChange removeChange) {
		if (!(insertChange instanceof InsertInListEChange && removeChange instanceof RemoveFromListEChange))
			return false;
		return eObjectsNonNullAndEqual(getAffectedEObject(insertChange), getAffectedEObject(removeChange))
				&& getAffectedFeature(insertChange) == getAffectedFeature(removeChange);
	}

	public static boolean containsEObject(Collection<EObject> col, EObject objToSeek) {
		if (col == null)
			return false;

		return col.stream().anyMatch((o) -> eObjectsNonNullAndEqual(o, objToSeek));
	}

	public static int getIndexOfObj(EChange change, EObject obj) {
		var affectedObj = getAffectedEObject(change);
		if (affectedObj == null)
			return -1;

		var affectedObjCon = affectedObj.eContainer();
		if (affectedObjCon == null)
			return -1;

		var affectedObjIdxInCon = affectedObjCon.eContents().indexOf(affectedObj);
		if (affectedObjIdxInCon > -1)
			return affectedObjIdxInCon;

		var affectedObjInRes = affectedObj.eResource();
		if (affectedObjInRes == null)
			return -1;

		var affectedObjIdxInRes = affectedObjInRes.getContents().indexOf(affectedObj);
		return affectedObjIdxInRes;
	}

	public static boolean isRootEObject(EObject obj) {
		if (obj.eResource() == null)
			return false;
		return obj.eResource().getContents().contains(obj);
	}
}
