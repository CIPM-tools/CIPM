package cipm.consistency.vsum.test.pcm.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cipm.consistency.vsum.test.pcm.preprocessing.ChangeUtil;
import cipm.consistency.vsum.test.pcm.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Create R -> InsertRoot R -> RemoveRoot R -> Delete R ==> NOP
 * <p>
 * Create R -> InsertRoot R -> RandomChangesReferencing R -> RemoveRoot R ->
 * Delete R ==> AdaptedRandomChangesReferencing R -> NOP
 */
public class RemoveRedundantExistenceChangesRule extends ChangePreprocessingRule {

	/*
	 * Create and Delete changes are indistinguishable for equal EObjects (wrt.
	 * EcoreUtil). The only difference makers are the insert and remove root
	 * changes.
	 * 
	 * Create changes are always followed by their insert root changes, likewise
	 * remove root changes are always followed by their delete changes.
	 * 
	 * => "Create Insert" and "Remove Delete" pairs should be processed in order and
	 * get removed as soon as a match is made. Only EObject IDs in Insert and Remove
	 * can be used for this purpose.
	 * 
	 * Remove Delete pairs that are removed during iteration should stay in the
	 * change sequence as they are, since it is impossible for them to get negated.
	 */

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		List<EChange> newChangeList = new ArrayList<>(changeSequence);

		var createInsertPairs = new ArrayList<EChange[]>();

		for (int i = 0; i < changeSequence.size() - 1; i++) {
			var currentChange = changeSequence.get(i);
			var nextChange = changeSequence.get(i + 1);

			if (currentChange instanceof CreateEObject && nextChange instanceof InsertRootEObject) {
				createInsertPairs.add(new EChange[] { currentChange, nextChange });
			}

			if (currentChange instanceof RemoveRootEObject && nextChange instanceof DeleteEObject) {
				var matchingPair = Lists
						.reverse(createInsertPairs).stream().filter((pair) -> ChangeUtil
								.areMatchingEObjectExistenceChanges(pair[0], pair[1], currentChange, nextChange))
						.findFirst();

				if (matchingPair.isPresent()) {
					var pair = matchingPair.get();
					newChangeList.remove(pair[0]);
					newChangeList.remove(nextChange);
					fixChangeSequence(newChangeList, pair[1], currentChange);

					newChangeList.remove(pair[1]);
					newChangeList.remove(currentChange);
					createInsertPairs.remove(pair);
				}
			}
		}

		return newChangeList;
	}

	private void fixChangeSequence(List<EChange> newChangeList, EChange removedInsertChange,
			EChange removedRemoveChange) {
		Preconditions.checkArgument(ChangeUtil.newAndOldValuesPresentAndEqual(removedInsertChange, removedRemoveChange),
				"Given root changes do not match");

		var removedObjID = ChangeUtil.getOldValueID(removedRemoveChange);

		/*
		 * Fix EObject IDs and indices (in root changes) of affected changes
		 */
		for (var c : newChangeList.subList(newChangeList.indexOf(removedInsertChange) + 1,
				newChangeList.indexOf(removedRemoveChange))) {
			var cAffectedObjID = ChangeUtil.getAffectedEObjectID(c);
			if (cAffectedObjID != null) {
				ChangeUtil.setAffectedEObjectID(c, getNewID(removedObjID, cAffectedObjID));
			}

			var cNewValID = ChangeUtil.getNewValueID(c);
			if (cNewValID != null) {
				var newID = getNewID(removedObjID, cNewValID);
				ChangeUtil.setNewValueID(c, newID);
				if (c instanceof InsertRootEObject) {
					((InsertRootEObject<?>) c).setIndex(getResourceContentIndex(newID));
				}
			}

			var cOldValID = ChangeUtil.getOldValueID(c);
			if (cOldValID != null) {
				var newID = getNewID(removedObjID, cOldValID);
				ChangeUtil.setOldValueID(c, newID);
				if (c instanceof RemoveRootEObject) {
					((RemoveRootEObject<?>) c).setIndex(getResourceContentIndex(newID));
				}
			}
		}
	}

	private int getResourceContentIndex(String id) {
		var fragments = id.split("/");
		return Integer.parseInt(fragments[fragments.length - 1]);
	}

	private String[] getIDFragments(String id) {
		return id.split("/");
	}

	private int[] getIndicesInIDFragments(String[] fragments) {
		var indices = new int[fragments.length - 1];
		for (int i = 1; i < fragments.length; i++) {
			indices[i - 1] = Integer.parseInt(fragments[i].replaceAll("\\\\D", ""));
		}
		return indices;
	}

	private int getFragmentMismatchIndex(String[] fragments1, String[] fragments2) {
		var shortestFragments = fragments1.length < fragments2.length ? fragments1 : fragments2;

		if (!fragments1[0].equals(fragments2[0]))
			return -1;

		for (int i = 0; i < shortestFragments.length; i++) {
			if (!fragments1[i].equals(fragments2[i])) {
				return i;
			}
		}
		return -1;
	}

	private String reconstructID(String[] fragments, int[] newIndices) {
		var resultingID = fragments[0] + "/";

		for (int i = 1; i < fragments.length; i++) {
			resultingID += fragments[i].replaceAll("\\d+$", String.valueOf(newIndices[i - 1])) + "/";
		}
		return resultingID.substring(0, resultingID.length() - 1);
	}

	private String getNewID(String removedObjID, String currentObjID) {
		var removedObjIDFragments = getIDFragments(removedObjID);
		var removedObjIDFragmentIndices = getIndicesInIDFragments(removedObjIDFragments);

		var currentObjIDfragments = getIDFragments(currentObjID);
		var mismatchingFragmentIdx = getFragmentMismatchIndex(removedObjIDFragments, currentObjIDfragments);

		if (mismatchingFragmentIdx == -1) {
			return currentObjID;
		}

		var mismatchingIdxIdx = mismatchingFragmentIdx - 1;

		var currentObjIDFragmentIdxs = getIndicesInIDFragments(currentObjIDfragments);

		var removedObjIDFragmentsMismatchingIdx = removedObjIDFragmentIndices[mismatchingIdxIdx];
		var currentObjIDFragmentsMismatchingIdx = currentObjIDFragmentIdxs[mismatchingIdxIdx];

		/*
		 * For index-based fragments, if /1/2 is removed, /1/3 should shift to /1/2
		 */
		if (removedObjIDFragmentsMismatchingIdx < currentObjIDFragmentsMismatchingIdx) {
			currentObjIDFragmentIdxs[mismatchingIdxIdx] -= 1;
			return this.reconstructID(currentObjIDfragments, currentObjIDFragmentIdxs);
		} else {
			return currentObjID;
		}
	}
}
