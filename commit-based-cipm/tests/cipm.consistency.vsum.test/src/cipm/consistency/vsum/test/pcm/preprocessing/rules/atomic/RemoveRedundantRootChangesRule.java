package cipm.consistency.vsum.test.pcm.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.UriUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.rules.ChangePreprocessingRule;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Insert root R -> Remove root R ==> NOP
 */
public class RemoveRedundantRootChangesRule extends ChangePreprocessingRule {
	/*
	 * FIXME Change the implementation to detect language of brackets "()", in order
	 * to find the correct create / delete change pairs:
	 * 
	 * create create remove create remove remove is currently being handled wrong.
	 * Count the amount of left / right brackets you had to iterate
	 */

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		var insertRootChanges = newChangeList.stream().filter((c) -> c instanceof InsertRootEObject)
				.collect(Collectors.toCollection(ArrayList::new));
		var removeRootChanges = Lists.reverse(newChangeList.stream().filter((c) -> c instanceof RemoveRootEObject)
				.collect(Collectors.toCollection(ArrayList::new)));

		for (var removingChange : removeRootChanges) {
			var matchingInsert = insertRootChanges.stream()
					.filter((ic) -> ChangeUtil.areMatchingRootEChanges(ic, removingChange)).findFirst();
			if (matchingInsert.isPresent()) {
//				fixChangeSequence(newChangeList, matchingInsert.get(), removingChange);
				newChangeList.remove(matchingInsert.get());
				newChangeList.remove(removingChange);
			}
		}

		return newChangeList;
	}

	// FIXME Check if you actually have to update the IDs yourself / fixing the
	// issue above solves all problems

//	private void fixChangeSequence(List<EChange> newChangeList, EChange removedInsertChange,
//			EChange removedRemoveChange) {
//		Preconditions.checkArgument(ChangeUtil.newAndOldValuesPresentAndEqual(removedInsertChange, removedRemoveChange),
//				"Given root changes do not match");
//
//		var removedObjID = ChangeUtil.getOldValueID(removedRemoveChange);
//
//		for (var c : newChangeList.subList(newChangeList.indexOf(removedInsertChange) + 1,
//				newChangeList.indexOf(removedRemoveChange))) {
//			var cAffectedObjID = ChangeUtil.getAffectedEObjectID(c);
//			if (cAffectedObjID != null) {
//				ChangeUtil.setAffectedEObjectID(removedRemoveChange, getNewID(removedObjID, cAffectedObjID));
//			}
//
//			var cNewValID = ChangeUtil.getNewValueID(c);
//			if (cNewValID != null) {
//				ChangeUtil.setNewValueID(removedRemoveChange, getNewID(removedObjID, cNewValID));
//			}
//
//			var cOldValID = ChangeUtil.getOldValueID(c);
//			if (cOldValID != null) {
//				ChangeUtil.setOldValueID(removedRemoveChange, getNewID(removedObjID, cOldValID));
//			}
//		}
//	}
//
//	private String[] getIDFragments(String id) {
//		return id.split("/");
//	}
//
//	private int[] getIndicesInIDFragments(String[] fragments) {
//		var indices = new int[fragments.length - 1];
//		for (int i = 1; fragments.length < i; i++) {
//			indices[i - 1] = Integer.parseInt(fragments[i].replaceAll("\\D", ""));
//		}
//		return indices;
//	}
//
//	private int getFragmentMismatchIndex(String[] fragments1, String[] fragments2) {
//		var shortestFragments = fragments1.length < fragments2.length ? fragments1 : fragments2;
//
//		if (!fragments1[0].equals(fragments2[0]))
//			return -1;
//
//		for (int i = 0; i < shortestFragments.length; i++) {
//			if (!fragments1[i].equals(fragments2[i])) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//	private String reconstructID(String[] fragments, int[] newIndices) {
//		var resultingID = fragments[0] + "/";
//
//		for (int i = 1; i < fragments.length; i++) {
//			resultingID += fragments[i].replaceAll("\\d+$", String.valueOf(newIndices[i - 1])) + "/";
//		}
//		return resultingID.substring(0, resultingID.length() - 1);
//	}
//
//	private String getNewID(String removedObjID, String currentObjID) {
//		var removedObjIDFragments = getIDFragments(removedObjID);
//		var removedObjIDFragmentIndices = getIndicesInIDFragments(removedObjIDFragments);
//
//		var currentObjIDfragments = getIDFragments(currentObjID);
//		var mismatchingFragmentIdx = getFragmentMismatchIndex(removedObjIDFragments, currentObjIDfragments);
//
//		if (mismatchingFragmentIdx == -1) {
//			return currentObjID;
//		}
//
//		var mismatchingIdxIdx = mismatchingFragmentIdx - 1;
//
//		var currentObjIDFragmentIdxs = getIndicesInIDFragments(currentObjIDfragments);
//
//		var removedObjIDFragmentsMismatchingIdx = removedObjIDFragmentIndices[mismatchingIdxIdx];
//		var currentObjIDFragmentsMismatchingIdx = currentObjIDFragmentIdxs[mismatchingIdxIdx];
//
//		/*
//		 * For index-based fragments, if /1/2 is removed, /1/3 should shift to /1/2
//		 */
//		if (removedObjIDFragmentsMismatchingIdx < currentObjIDFragmentsMismatchingIdx) {
//			currentObjIDFragmentIdxs[mismatchingIdxIdx] -= 1;
//			return this.reconstructID(currentObjIDfragments, currentObjIDFragmentIdxs);
//		} else {
//			return currentObjID;
//		}
//	}
}
