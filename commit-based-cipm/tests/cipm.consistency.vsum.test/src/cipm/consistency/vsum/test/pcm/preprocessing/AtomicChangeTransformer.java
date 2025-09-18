package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.ReferenceFactory;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

public class AtomicChangeTransformer {
	public List<EChange> transform(Collection<EChange> changes) {
		var result = new ArrayList<EChange>();
		for (var c : changes) {
			result.addAll(this.transform(c));
		}
		return result;
	}

	public List<EChange> transform(EChange change) {
		if (change instanceof ReplaceSingleValuedEAttribute)
			return this.transformReplaceSingleValuedEAttribute((ReplaceSingleValuedEAttribute<?, ?>) change);

		if (change instanceof ReplaceSingleValuedEReference)
			return this.transformReplaceSingleValuedEReference((ReplaceSingleValuedEReference<?, ?>) change);

		return List.of(change);
	}

	private List<EChange> transformReplaceSingleValuedEAttribute(ReplaceSingleValuedEAttribute<?, ?> change) {
		var affectedObj = change.getAffectedEObject();
		var affectedObjIdx = ChangeUtil.getIndexOfObj(change, affectedObj);
		if (affectedObjIdx == -1) {
			affectedObjIdx = 0;
		}

		var insertChange = AttributeFactory.eINSTANCE.createInsertEAttributeValue();
		insertChange.setAffectedEObject(affectedObj);
		insertChange.setAffectedEObjectID(change.getAffectedEObjectID());
		insertChange.setAffectedFeature((EAttribute) change.getAffectedFeature());
		insertChange.setIndex(affectedObjIdx);
		insertChange.setNewValue(change.getNewValue());
		insertChange.setWasUnset(change.isWasUnset());

		// FIXME Fix transformed replacement changes

		/*
		 * If the replace change was attempting to set a feature, which was unset or
		 * null, only insert change should occur.
		 */
		if (change.isWasUnset() || change.getOldValue() == null) {
			return List.of(insertChange);
		}

		var removeChange = AttributeFactory.eINSTANCE.createRemoveEAttributeValue();
		removeChange.setAffectedEObject(affectedObj);
		removeChange.setAffectedEObjectID(change.getAffectedEObjectID());
		removeChange.setAffectedFeature((EAttribute) change.getAffectedFeature());
		removeChange.setIndex(affectedObjIdx);
		removeChange.setOldValue(change.getOldValue());

		return List.of(removeChange, insertChange);
	}

	private List<EChange> transformReplaceSingleValuedEReference(ReplaceSingleValuedEReference<?, ?> change) {
		var affectedObj = change.getAffectedEObject();
		var affectedObjIdx = ChangeUtil.getIndexOfObj(change, affectedObj);
		if (affectedObjIdx == -1) {
			affectedObjIdx = 0;
		}

		var insertChange = ReferenceFactory.eINSTANCE.createInsertEReference();
		insertChange.setAffectedEObject(affectedObj);
		insertChange.setAffectedEObjectID(change.getAffectedEObjectID());
		insertChange.setAffectedFeature((EReference) change.getAffectedFeature());
		insertChange.setIndex(affectedObjIdx);
		insertChange.setNewValue(change.getNewValue());
		insertChange.setNewValueID(change.getNewValueID());
		insertChange.setWasUnset(change.isWasUnset());

		// FIXME Fix transformed replacement changes

		/*
		 * If the replace change was attempting to set a feature, which was unset or
		 * null, only insert change should occur.
		 */
		if (change.isWasUnset() || change.getOldValue() == null) {
			return List.of(insertChange);
		}

		var removeChange = ReferenceFactory.eINSTANCE.createRemoveEReference();
		removeChange.setAffectedEObject(affectedObj);
		removeChange.setAffectedEObjectID(change.getAffectedEObjectID());
		removeChange.setAffectedFeature((EReference) change.getAffectedFeature());
		removeChange.setIndex(affectedObjIdx);
		removeChange.setOldValue(change.getOldValue());
		removeChange.setOldValueID(change.getOldValueID());

		return List.of(removeChange, insertChange);
	}
}
