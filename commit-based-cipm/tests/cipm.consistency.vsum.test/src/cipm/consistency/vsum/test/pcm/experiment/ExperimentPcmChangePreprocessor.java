package cipm.consistency.vsum.test.pcm.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.seff.SeffPackage;
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

			/*
			 * FIXME Skip SEFF action changes for now, since accounting for them requires
			 * non-trivial EObject ID dependency tracking. Otherwise their creation /
			 * insertion order may get mixed up, which is a detriment to change resolution
			 * during propagation
			 */
//			if (shouldSkipChange(currentChange)) {
//				createChange = null;
//				continue;
//			}

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
//				newChangeList.addAll(orderOperationSignaturesBeforeSEFFCreation(changes.get(i)));
				newChangeList.addAll(changes.get(i));
			}
		}

		return newChangeList;
	}

	private List<EChange> orderOperationSignaturesBeforeSEFFCreation(List<EChange> changes) {
		var newChangeList = new ArrayList<EChange>();

		var seffChanges = new ArrayList<EChange>();
		for (var c : changes) {
			if (isSetSEFFDescribedServiceChange(c)
					|| ChangeUtil.createsEObjectOfType(c, SeffPackage.Literals.SERVICE_EFFECT_SPECIFICATION)) {
				seffChanges.add(c);
			} else {
				newChangeList.add(c);
			}
		}

		newChangeList.addAll(seffChanges);
		return newChangeList;
	}

	private List<EChange> orderRolesAfterSEFFCreation(List<EChange> changes) {
		var newChangeList = new ArrayList<EChange>();

		var roleChanges = new ArrayList<EChange>();
		for (var c : changes) {
			if (ChangeUtil.involvesFeature(c,
					EntityPackage.Literals.INTERFACE_PROVIDING_ENTITY__PROVIDED_ROLES_INTERFACE_PROVIDING_ENTITY)
					|| ChangeUtil.involvesFeature(c,
							EntityPackage.Literals.INTERFACE_REQUIRING_ENTITY__REQUIRED_ROLES_INTERFACE_REQUIRING_ENTITY)
					|| ChangeUtil.createsEObjectOfType(c, RepositoryPackage.Literals.ROLE)) {
				roleChanges.add(c);
			} else {
				newChangeList.add(c);
			}
		}

		newChangeList.addAll(roleChanges);
		return newChangeList;
	}

	private boolean shouldSkipChange(EChange change) {
		return isSEFFactionChange(change);
	}

	private boolean isSetSEFFDescribedServiceChange(EChange change) {
		return RepositoryPackage.Literals.BASIC_COMPONENT__SERVICE_EFFECT_SPECIFICATIONS_BASIC_COMPONENT
				.equals(ChangeUtil.getAffectedFeature(change));
	}

	private boolean isSEFFactionChange(EChange change) {
		return ChangeUtil.involvesFeature(change, SeffPackage.Literals.BRANCH_ACTION__BRANCHES_BRANCH)
				|| ChangeUtil.involvesFeature(change, SeffPackage.Literals.ABSTRACT_LOOP_ACTION__BODY_BEHAVIOUR_LOOP)
				|| ChangeUtil.involvesFeature(change,
						SeffPackage.Literals.EXTERNAL_CALL_ACTION__CALLED_SERVICE_EXTERNAL_SERVICE)
				|| ChangeUtil.involvesFeature(change, SeffPackage.Literals.EXTERNAL_CALL_ACTION__ROLE_EXTERNAL_SERVICE);
	}
}
