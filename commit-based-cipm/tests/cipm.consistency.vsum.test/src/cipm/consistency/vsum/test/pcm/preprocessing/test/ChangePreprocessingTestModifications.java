package cipm.consistency.vsum.test.pcm.preprocessing.test;

import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
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
	 * <li>Insert obj (into resource)
	 * <li>Replace obj.ID
	 * </ol>
	 */
	public static Consumer<Resource> addObjToResourceAction(EObject obj) {
		return (r) -> r.getContents().add(obj);
	}

	/**
	 * Removes an EObject from the given resource. Resulting changes:
	 * <ol>
	 * <li>Remove obj (from resource)
	 * <li>Delete obj (from cache)
	 * </ol>
	 */
	public static Consumer<Resource> removeObjFromResourceAction(EObject obj) {
		return (r) -> r.getContents().remove(obj);
	}

	/**
	 * Sets the name (entityName) of the obj to newName.
	 * <p>
	 * Delegates to
	 * {@link #setObjSingleValuedFeatAction(EObject, EStructuralFeature, Object)}
	 */
	public static Consumer<Resource> setObjEntityNameAction(EObject obj, String newName) {
		return setObjSingleValuedFeatAction(obj, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME, newName);
	}

	/**
	 * Sets the value of a feature of obj. Resulting changes:
	 * <ol>
	 * <li>Replace obj.feat's value
	 * </ol>
	 */
	public static Consumer<Resource> setObjSingleValuedFeatAction(EObject obj, EStructuralFeature feat, Object val) {
		preFeatSetActionArgumentCheck(obj, feat, val);

		return (r) -> {
			EObject objInRes = null;
			var it = r.getAllContents();
			while (it.hasNext()) {
				var currentObj = it.next();
				if (ChangeUtil.eObjectsEqual(currentObj, obj)) {
					objInRes = currentObj;
					break;
				}
			}
			objInRes.eSet(feat, val);
		};
	}

	private static void preFeatSetActionArgumentCheck(EObject obj, EStructuralFeature feat, Object val) {
		Preconditions.checkArgument(feat.isChangeable(), "Given feat must be changeable");
		Preconditions.checkArgument(val != null || feat.isUnsettable(),
				"Given feat must be unsettable for val = null to be allowed");
		Preconditions.checkArgument(!feat.isMany(), "Given feat must be single-valued (!feat.isMany())");
		Preconditions.checkArgument(obj.eClass().getEAllStructuralFeatures().contains(feat),
				"Given obj must support feat");
	}
}
