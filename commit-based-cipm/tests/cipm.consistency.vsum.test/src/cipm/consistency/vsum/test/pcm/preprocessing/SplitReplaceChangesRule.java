package cipm.consistency.vsum.test.pcm.preprocessing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.feature.single.ReplaceSingleValuedFeatureEChange;

public class SplitReplaceChangesRule extends ChangeSequenceProcessingRule {
	/*
	 * FIXME Clarify why the exception is thrown for the created insertion changes
	 * 
	 * "java.lang.IllegalStateException: cannot execute command generated for
	 * EChange:
	 * tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl@
	 * 2220780d (affectedEObjectID: res.repository#/0) (index: -1) (newValue:
	 * _t1y8EJVHEfCUgrRDGAOPaw, wasUnset: false) "
	 * 
	 * For indices -1, 0 and 1 the same exception occurs
	 * 
	 * Without an ID, an exception is thrown due to a precondition that keeps
	 * affectedObjectID mandatory. Setting it to the ID of the replacement change's
	 * affectedObjectID results in the exception above
	 * 
	 * replace -> remove, insert transformation taken from TGG repository
	 * 
	 * 
	 * 
	 * TODO Clarify whether it is even possible to set a single-valued feature via
	 * insert / remove changes. The only way might be replace changes
	 */

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		var result = new ArrayList<EChange>();
		for (var c : changeSequence) {
			result.addAll(this.splitIfReplaceChange(c));
		}
		return result;
	}

	public List<EChange> splitIfReplaceChange(EChange change) {
		if (change instanceof ReplaceSingleValuedEAttribute)
			return this.transformReplaceSingleValuedEAttribute((ReplaceSingleValuedEAttribute<?, ?>) change);

		if (change instanceof ReplaceSingleValuedEReference)
			return this.transformReplaceSingleValuedEReference((ReplaceSingleValuedEReference<?, ?>) change);

		return List.of(change);
	}

	private List<EChange> transformReplaceSingleValuedEAttribute(ReplaceSingleValuedEAttribute<?, ?> change) {
		var affectedObj = change.getAffectedEObject();
		var affectedObjIdx = ChangeUtil.getIndexOfObj(change, affectedObj);

		var insertChange = TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(affectedObj,
				(EAttribute) change.getAffectedFeature(), affectedObjIdx, change.getNewValue());
		setInsertChangeEObjectIDIfNull(change, insertChange);

		// FIXME Fix transformed replacement changes

		/*
		 * If the replace change was attempting to set a feature, which was null, only
		 * insert change should occur. Additionally, if the old and new value of the
		 * feature are equal, only insert change should occur.
		 */
		if (shouldOnlyCreateInsertChange(change)) {
			return List.of(insertChange);
		}

		var removeChange = TypeInferringAtomicEChangeFactory.getInstance().createRemoveAttributeChange(affectedObj,
				(EAttribute) change.getAffectedFeature(), affectedObjIdx, change.getOldValue());
		setInsertChangeEObjectIDIfNull(change, removeChange);

		return List.of(removeChange, insertChange);
	}

	private List<EChange> transformReplaceSingleValuedEReference(ReplaceSingleValuedEReference<?, ?> change) {
		var affectedObj = change.getAffectedEObject();
		var affectedObjIdx = ChangeUtil.getIndexOfObj(change, affectedObj);

		var insertChange = TypeInferringAtomicEChangeFactory.getInstance().createInsertReferenceChange(affectedObj,
				(EReference) change.getAffectedFeature(), change.getNewValue(), affectedObjIdx);
		setInsertChangeEObjectIDIfNull(change, insertChange);

		// FIXME Fix transformed replacement changes

		/*
		 * If the replace change was attempting to set a feature, which was null, only
		 * insert change should occur. Additionally, if the old and new value of the
		 * feature are equal, only insert change should occur.
		 */
		if (shouldOnlyCreateInsertChange(change)) {
			return List.of(insertChange);
		}

		var removeChange = TypeInferringAtomicEChangeFactory.getInstance().createRemoveReferenceChange(affectedObj,
				(EReference) change.getAffectedFeature(), change.getOldValue(), affectedObjIdx);
		setInsertChangeEObjectIDIfNull(change, removeChange);

		return List.of(removeChange, insertChange);
	}

	private boolean shouldOnlyCreateInsertChange(ReplaceSingleValuedFeatureEChange<?, ?, ?> change) {
		return ChangeUtil.newAndOldValuesPresentAndEqual(change) || change.getOldValue() == null;
	}

	private void setInsertChangeEObjectIDIfNull(ReplaceSingleValuedFeatureEChange<?, ?, ?> change,
			FeatureEChange<?, ?> createdChange) {
		if (createdChange.getAffectedEObjectID() == null) {
//			var affectedObj = createdChange.getAffectedEObject();
//			var affectedObjRes = affectedObj.eResource();
//			var affectedObjFrag = affectedObjRes.getURIFragment(affectedObj);
//			createdChange.setAffectedEObjectID(
//					affectedObjRes.getURI().appendFragment(affectedObjFrag).toString());
//			createdChange.setAffectedEObjectID(affectedObjFrag);
			createdChange.setAffectedEObjectID(change.getAffectedEObjectID());
		}
	}
}
