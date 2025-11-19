package cipm.consistency.cpr.pcmjava.preprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
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
import tools.vitruv.change.atomic.feature.list.UpdateSingleListEntryEChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootEChange;

public final class ChangeUtil {
	private static final String cacheIDPrefix = "cache:/";

	public static void replaceInAllIDs(EChange change, String regexInOldID, String replacement) {
		var affectedID = getAffectedEObjectID(change);
		if (affectedID != null) {
			setAffectedEObjectID(change, affectedID.replaceAll(regexInOldID, replacement));
		}
		var oldID = getOldValueID(change);
		if (oldID != null) {
			setOldValueID(change, oldID.replaceAll(regexInOldID, replacement));
		}
		var newID = getNewValueID(change);
		if (newID != null) {
			setNewValueID(change, newID.replaceAll(regexInOldID, replacement));
		}
	}
	
	public static boolean isCacheURI(URI uri) {
		return isCacheURI(uri.toString());
	}

	public static boolean isCacheURI(String uri) {
		return uri.startsWith(cacheIDPrefix);
	}

	public static void adaptChangeURIs(Resource changeResource, Resource targetModelResource) {
		for (var change : changeResource.getContents()) {
			if (change instanceof EChange)
				adaptChangeURIs((EChange) change, targetModelResource);
		}
	}

	public static void adaptChangeURIs(EChange change, Resource targetModelResource) {
		var affectedID = getAffectedEObjectID(change);
		if (affectedID != null) {
			setAffectedEObjectID(change, adaptURI(affectedID, targetModelResource));
		}
		var oldID = getOldValueID(change);
		if (oldID != null) {
			setOldValueID(change, adaptURI(oldID, targetModelResource));
		}
		var newID = getNewValueID(change);
		if (newID != null) {
			setNewValueID(change, adaptURI(newID, targetModelResource));
		}
		var uri = getRootChangeURI(change);
		if (uri != null) {
			setRootChangeURI(change, targetModelResource.getURI().toString());
		}
	}

	public static void replaceChangeIDs(EChange change, String idToReplace, String replacementID) {
		var affectedID = getAffectedEObjectID(change);
		if (affectedID != null && affectedID.equals(idToReplace)) {
			setAffectedEObjectID(change, replacementID);
		}
		var oldID = getOldValueID(change);
		if (oldID != null && oldID.equals(idToReplace)) {
			setOldValueID(change, replacementID);
		}
		var newID = getNewValueID(change);
		if (newID != null && newID.equals(idToReplace)) {
			setNewValueID(change, replacementID);
		}
	}

	private static String adaptURI(String uri, Resource res) {
		if (isCacheURI(uri))
			return uri;

		var fragment = URI.createURI(uri).fragment();
		return res.getURI().appendFragment(fragment).toString();
	}

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

	public static String getRootChangeURI(EChange change) {
		if (change instanceof RootEChange) {
			return ((RootEChange) change).getUri();
		}
		return null;
	}

	public static void setRootChangeURI(EChange change, String newURI) {
		if (change instanceof RootEChange) {
			((RootEChange) change).setUri(newURI);
		}
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

	public static EClass getCreatedEObjectType(EChange change) {
		if (change instanceof CreateEObject)
			return ((CreateEObject<?>) change).getAffectedEObjectType();
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

	public static boolean areMatchingEObjectExistenceChanges(EChange createChange, EChange insertRootChange,
			EChange removeRootChange, EChange deleteChange) {
		if (!(createChange instanceof CreateEObject && deleteChange instanceof DeleteEObject
				&& insertRootChange instanceof InsertRootEObject && removeRootChange instanceof RemoveRootEObject))
			return false;

		var idVal1 = ((EObjectExistenceEChange<?>) createChange).getIdAttributeValue();
		var idVal2 = ((EObjectExistenceEChange<?>) deleteChange).getIdAttributeValue();
		if (idVal1 != idVal2 && ((idVal1 == null ^ idVal2 == null) || !idVal1.equals(idVal2)))
			return false;

		if (!affectedEObjectsPresentAndEqual(createChange, deleteChange))
			return false;

		return areMatchingRootEChanges(insertRootChange, removeRootChange);
	}

	public static boolean areMatchingRootEChanges(EChange insertChange, EChange removeChange) {
		if (!(insertChange instanceof InsertRootEObject && removeChange instanceof RemoveRootEObject))
			return false;

		if (!newAndOldValuesPresentAndEqual(insertChange, removeChange))
			return false;

		var castedIC = (InsertRootEObject<?>) insertChange;
		var castedRC = (RemoveRootEObject<?>) removeChange;
		var icNewVal = (EObject) castedIC.getNewValue();
		var rcOldVal = (EObject) castedRC.getOldValue();

		var icURIWithIdx = URI.createURI(castedIC.getUri()).appendFragment("/" + String.valueOf(castedIC.getIndex()))
				.toString();

		if (icURIWithIdx.equals(castedRC.getOldValueID()))
			return true;

		if (icNewVal instanceof Identifier && rcOldVal instanceof Identifier) {
			return URI.createURI(castedIC.getUri()).appendFragment("/" + ((Identifier) icNewVal).getId()).toString()
					.equals(castedRC.getOldValueID());
		}

		return false;
	}

	public static boolean areMatchingSingleListEntryEChanges(EChange insertChange, EChange removeChange) {
		if (!(insertChange instanceof InsertInListEChange && removeChange instanceof RemoveFromListEChange))
			return false;
		return eObjectsNonNullAndEqual(getAffectedEObject(insertChange), getAffectedEObject(removeChange))
				&& newAndOldValuesPresentAndEqual(insertChange, removeChange)
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
