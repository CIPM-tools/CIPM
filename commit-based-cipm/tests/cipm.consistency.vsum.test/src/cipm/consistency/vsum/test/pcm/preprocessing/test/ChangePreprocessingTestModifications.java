package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.pcm.core.entity.EntityPackage;

import com.google.common.base.Preconditions;

import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;

public class ChangePreprocessingTestModifications {
	/**
	 * Adds an EObject to the given resource. Resulting changes:
	 * <ol>
	 * <li>Create obj (in cache)
	 * <li>InsertRoot obj (into resource)
	 * <li>Replace obj.ID
	 * </ol>
	 */
	public static Consumer<Resource> addRootToResourceAction(EObject obj) {
		return (r) -> r.getContents().add(obj);
	}

	/**
	 * Removes an EObject from the given resource. Resulting changes:
	 * <ol>
	 * <li>RemoveRoot obj (from resource)
	 * <li>Delete obj (from cache)
	 * </ol>
	 */
	public static Consumer<Resource> removeRootFromResourceAction(EObject obj) {
		return (r) -> r.getContents().remove(obj);
	}

	/**
	 * Sets the name (entityName) of the obj to newName.
	 * <p>
	 * Delegates to
	 * {@link #setSingleValuedFeatAction(EObject, EStructuralFeature, Object)}
	 */
	public static Consumer<Resource> setEntityNameAction(EObject obj, String newName) {
		return setSingleValuedFeatAction(obj, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME, newName);
	}

	/**
	 * Sets the value of a feature of obj. Resulting changes:
	 * <ol>
	 * <li>Replace obj.feat's value
	 * </ol>
	 */
	public static Consumer<Resource> setSingleValuedFeatAction(EObject obj, EStructuralFeature feat, Object val) {
		preSingleValuedFeatSetActionArgumentCheck(obj, feat, val);

		return (r) -> findEObjInRes(r, obj).eSet(feat, val);
	}

	private static EObject findEObjInRes(Resource r, EObject obj) {
		var it = r.getAllContents();
		while (it.hasNext()) {
			var currentObj = it.next();
			if (ChangeUtil.eObjectsEqual(currentObj, obj)) {
				return currentObj;
			}
		}
		return null;
	}

	public static Consumer<Resource> addToManyValuedFeatAction(EObject obj, EStructuralFeature feat, Object val) {
		preManyValuedFeatActionArgumentCheck(obj, feat, val);

		return (r) -> addToManyValuedFeatVal(findEObjInRes(r, obj), feat, val);
	}

	public static Consumer<Resource> removeFromManyValuedFeatAction(EObject obj, EStructuralFeature feat, Object val) {
		preManyValuedFeatActionArgumentCheck(obj, feat, val);

		return (r) -> removeFromManyValuedFeatVal(findEObjInRes(r, obj), feat, val);
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> getManyValuedFeatVal(EObject obj, EStructuralFeature feat) {
		Preconditions.checkArgument(feat.isMany(), "Given feat must be many-valued (feat.isMany())");
		Preconditions.checkArgument(obj.eClass().getEAllStructuralFeatures().contains(feat),
				"Given obj must support feat");

		var currentVal = obj.eGet(feat);
		if (currentVal == null)
			return new BasicEList<T>();
		return new ArrayList<>((List<T>) currentVal);
	}

	private static void addToManyValuedFeatVal(EObject obj, EStructuralFeature feat, Object val) {
		preFeatSetArgumentCheck(obj, feat, val);
		var currentVal = getManyValuedFeatVal(obj, feat);
		currentVal.add(val);
		obj.eSet(feat, currentVal);

		// Set bidirectional reference, if any
		if (feat instanceof EReference) {
			var ref = (EReference) feat;
			var oppositeRef = ref.getEOpposite();
			if (oppositeRef != null) {
				((EObject) val).eSet(oppositeRef, obj);
			}
		}
	}

	private static void removeFromManyValuedFeatVal(EObject obj, EStructuralFeature feat, Object val) {
		preFeatSetArgumentCheck(obj, feat, val);
		var currentVal = getManyValuedFeatVal(obj, feat);
		currentVal.remove(val);
		obj.eSet(feat, currentVal);

		// Unset bidirectional reference, if any
		if (feat instanceof EReference) {
			var ref = (EReference) feat;
			var oppositeRef = ref.getEOpposite();
			if (oppositeRef != null) {
				((EObject) val).eUnset(oppositeRef);
			}
		}
	}

	private static void preFeatSetArgumentCheck(EObject obj, EStructuralFeature feat, Object val) {
		Preconditions.checkArgument(feat.isChangeable(), "Given feat must be changeable");
		Preconditions.checkArgument(val != null || feat.isUnsettable(),
				"Given feat must be unsettable for val = null to be allowed");
		Preconditions.checkArgument(obj.eClass().getEAllStructuralFeatures().contains(feat),
				"Given obj must support feat");
		var featValCls = feat.getEType().getInstanceClass();
		Preconditions.checkArgument(feat.getEType().getInstanceClass().isAssignableFrom(val.getClass()),
				"Type of val must be: " + featValCls.getSimpleName());
	}

	private static void preManyValuedFeatActionArgumentCheck(EObject obj, EStructuralFeature feat, Object val) {
		preFeatSetArgumentCheck(obj, feat, val);
		Preconditions.checkArgument(feat.isMany(), "Given feat must be many-valued (feat.isMany())");
	}

	private static void preSingleValuedFeatSetActionArgumentCheck(EObject obj, EStructuralFeature feat, Object val) {
		preFeatSetArgumentCheck(obj, feat, val);
		Preconditions.checkArgument(!feat.isMany(), "Given feat must be single-valued (!feat.isMany())");
	}
}
