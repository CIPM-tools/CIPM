package cipm.consistency.vsum.test.pcm.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

public class ExperimentPcmChangePreprocessor {
	private final static String cachedEObjectURI = "cache:/0";

	public int getMaxDepth(EChange change) {
		var aID = ChangeUtil.getAffectedEObjectID(change);
		var oID = ChangeUtil.getOldValueID(change);
		var nID = ChangeUtil.getNewValueID(change);

		var aDepth = 0;
		var oDepth = 0;
		var nDepth = 0;

		if (aID != null) {
			aDepth = getDepth(URI.createURI(aID));
		}
		if (oID != null) {
			oDepth = getDepth(URI.createURI(oID));
		}
		if (nID != null) {
			nDepth = getDepth(URI.createURI(nID));
		}

		return Math.max(aDepth, Math.max(oDepth, nDepth));
	}

	public int getDepth(URI uri) {
		if (uri == null || !uri.hasFragment())
			return 0;
		var depth = uri.fragment().split("/").length;
		return depth == 0 ? depth : depth - 2;
	}

	public List<EChange> orderPCMchanges(List<EChange> changeSequence) {
		var newChangeList = new ArrayList<EChange>();

		var changes = new HashMap<Integer, ArrayList<EChange>>();
		EChange createChange = null;
		var maxDepth = 0;

		for (int i = 0; i < changeSequence.size(); i++) {
			var currentChange = changeSequence.get(i);
			if (currentChange instanceof CreateEObject) {
				createChange = currentChange;
				continue;
			}
			// Regex used to analyse / verify (remove #):
			// </eobject:CreateEObject>(?!\r\n###(?:<reference:InsertEReference|<attribute:ReplaceSingleValuedEAttribute|<reference:ReplaceSingleValuedEReference))
			//
			// All CreateEObject changes must be preceded by an InsertEReference or
			// ReplaceSingleValuedEReference change that inserts it into the PCM
			var precedsCreate = currentChange instanceof InsertEReference
					|| (currentChange instanceof ReplaceSingleValuedEReference
							&& ChangeUtil.getNewValueID(currentChange) != null
							&& ChangeUtil.getNewValueID(currentChange).equals(cachedEObjectURI));

			/*
			 * FIXME Skip SEFF action changes for now, since accounting for them requires
			 * non-trivial EObject ID dependency tracking. Otherwise their creation /
			 * insertion order may get mixed up, which is a detriment to change resolution
			 * during propagation
			 * 
			 * (As of this version, SEFF reconstruction is disabled in TEAMMATES Java -> PCM
			 * change propagation)
			 */

			var depth = getMaxDepth(currentChange);

			if (maxDepth < depth)
				maxDepth = depth;

			if (!changes.containsKey(depth)) {
				changes.put(depth, new ArrayList<EChange>());
			}

			if (precedsCreate && createChange != null) {
				changes.get(depth).add(createChange);
				createChange = null;
			}
			changes.get(depth).add(currentChange);
		}

		// Make sure that EObjects needed in changes are created and accessible when
		// they are needed
		for (int i = 0; i <= maxDepth; i++) {
			if (changes.containsKey(i)) {
				newChangeList.addAll(changes.get(i));
			}
		}

		return newChangeList;
	}
}
