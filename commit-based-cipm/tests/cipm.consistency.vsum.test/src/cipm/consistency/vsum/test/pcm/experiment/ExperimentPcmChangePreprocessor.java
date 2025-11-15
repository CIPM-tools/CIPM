package cipm.consistency.vsum.test.pcm.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;

public class ExperimentPcmChangePreprocessor {

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

	public int getCreatedObjectDepth(InsertEReference<?, ?> ir) {
		return URI.createURI(ir.getAffectedEObjectID()).fragment().split("/").length;
	}

	public boolean isContainer(InsertEReference<?, ?> ir, String containerURIFragment) {
		return URI.createURI(ir.getAffectedEObjectID()).fragment().equals(containerURIFragment);
	}

	public boolean isContainerRepository(InsertEReference<?, ?> ir, Repository repo) {
		return isContainer(ir, repo.eResource().getURIFragment(repo));
	}

	private final static String cachedEObjectURI = "cache:/0";

	public List<EChange> orderPCMchanges(List<EChange> changeSequence) {
		var newChangeList = new ArrayList<EChange>();

		var changes = new HashMap<Integer, ArrayList<EChange>>();
		EChange createChange = null;
		var maxDepth = 0;

		// Regex used to analyse / verify (remove #):
		// </eobject:CreateEObject>(?!\r\n###(?:<reference:InsertEReference|<attribute:ReplaceSingleValuedEAttribute|<reference:ReplaceSingleValuedEReference))

		for (int i = 0; i < changeSequence.size(); i++) {
			var currentChange = changeSequence.get(i);
			if (currentChange instanceof CreateEObject) {
				createChange = currentChange;
				continue;
			}
			// All CreateEObject changes must be preceded by an InsertEReference or
			// ReplaceSingleValuedEReference change that inserts it into the PCM
			var precedsCreate = currentChange instanceof InsertEReference
					|| (currentChange instanceof ReplaceSingleValuedEReference
							&& ChangeUtil.getNewValueID(currentChange) != null
							&& ChangeUtil.getNewValueID(currentChange).equals(cachedEObjectURI));

			// Skip SEFF changes
			if (shouldSkipChange(currentChange)) {
				createChange = null;
				continue;
			}

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

		// Add PCM elements in Breadth-First order, as this will ensure that all PCM
		// elements are known
		for (int i = 0; i <= maxDepth; i++) {
			if (changes.containsKey(i)) {
				newChangeList.addAll(orderOperationSignaturesBeforeSEFFCreation(changes.get(i)));
			}
		}

		return newChangeList;
	}

	private static final String seffActionEObjectFragmentPart = "serviceEffectSpecifications";

	private List<EChange> orderOperationSignaturesBeforeSEFFCreation(List<EChange> changes) {
		var newChangeList = new ArrayList<EChange>();

		var seffChanges = new ArrayList<EChange>();
		for (var c : changes) {
			if (isSetSEFFDescribedServiceChange(c) || isSEFFactionChange(c)
					|| (c instanceof CreateEObject && ServiceEffectSpecification.class
							.isAssignableFrom(ChangeUtil.getCreatedEObjectType(c).getInstanceClass()))) {
				seffChanges.add(c);
			} else {
				newChangeList.add(c);
			}
		}

		newChangeList.addAll(seffChanges);
		return newChangeList;
	}

	private boolean shouldSkipChange(EChange change) {
		return isSEFFactionChange(change);
	}

	private boolean isSetSEFFDescribedServiceChange(EChange change) {
		return (ChangeUtil.getAffectedFeature(change) != null
				&& ChangeUtil.getAffectedFeature(change).getName().contains(seffActionEObjectFragmentPart));
	}

	private boolean isSEFFactionChange(EChange change) {
		return (ChangeUtil.getOldValueID(change) != null
				&& ChangeUtil.getOldValueID(change).contains(seffActionEObjectFragmentPart))

				|| (ChangeUtil.getNewValueID(change) != null
						&& ChangeUtil.getNewValueID(change).contains(seffActionEObjectFragmentPart))

				|| (ChangeUtil.getAffectedEObjectID(change) != null
						&& ChangeUtil.getAffectedEObjectID(change).contains(seffActionEObjectFragmentPart));
	}
}
