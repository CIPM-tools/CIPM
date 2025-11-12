package cipm.consistency.cpr.pcmjava.preprocessing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.UnsetFeature;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;

/**
 * Assumptions:
 * 
 * 1) If the tracked EObject is deleted / removed and then re-created, the new
 * EObject is considered independent of its deleted / removed version
 */
public class EObjectWrapper {
	private static final String cachedEObjectURI = "cache:/0";
	private final EChange firstChangeWithID;
	private final EClass eobjectType;
	private final String initialEObjectID;

	private List<EChange> changeSequence;
	private final Map<EChange, String> changeToIDMap = new LinkedHashMap<>();

	public EObjectWrapper(EChange firstChangeWithID, EClass eobjectType, String initialEObjectID) {
		this.firstChangeWithID = firstChangeWithID;
		this.eobjectType = eobjectType;
		this.initialEObjectID = initialEObjectID;

		putChangeToIDPair(firstChangeWithID, initialEObjectID);
	}
	
	public void setOriginalChangeSequence(List<EChange> changeSequence) {
		if (this.changeSequence == null) {
			this.changeSequence = new ArrayList<>(changeSequence);
			setIDsForChangeSequence();
		}
	}
	
	private void setIDsForChangeSequence() {
		var startIdx = this.changeSequence.indexOf(firstChangeWithID);
		this.changeSequence.stream().skip(startIdx + 1).forEach((c) -> adaptIDForChange(c));
	}

	private void adaptIDForChange(EChange change) {
		if (change instanceof CreateEObject)
			adaptIDForChange((CreateEObject<?>) change);
		else if (change instanceof DeleteEObject)
			adaptIDForChange((DeleteEObject<?>) change);
		else if (change instanceof InsertRootEObject)
			adaptIDForChange((InsertRootEObject<?>) change);
		else if (change instanceof RemoveRootEObject)
			adaptIDForChange((RemoveRootEObject<?>) change);
		else if (change instanceof UnsetFeature)
			adaptIDForChange((UnsetFeature<?, ?>) change);
		else if (change instanceof InsertEAttributeValue)
			adaptIDForChange((InsertEAttributeValue<?, ?>) change);
		else if (change instanceof RemoveEAttributeValue)
			adaptIDForChange((RemoveEAttributeValue<?, ?>) change);
		else if (change instanceof InsertEReference)
			adaptIDForChange((InsertEReference<?, ?>) change);
		else if (change instanceof RemoveEReference)
			adaptIDForChange((RemoveEReference<?, ?>) change);
		else if (change instanceof ReplaceSingleValuedEAttribute)
			adaptIDForChange((ReplaceSingleValuedEAttribute<?, ?>) change);
		else if (change instanceof ReplaceSingleValuedEReference)
			adaptIDForChange((ReplaceSingleValuedEReference<?, ?>) change);
		else {
			throw new IllegalArgumentException("Unknown change type: " + change.getClass().getName());
		}
	}

	/**
	 * Assumption: A change (possibly this change) previously introduced the tracked
	 * EObject (via setTrackedEObject)
	 * 
	 * Assumption: Inserting/Moving
	 */
	private void adaptIDForChange(CreateEObject<?> change) {
		if (!change.getAffectedEObjectType().equals(eobjectType)
				|| !change.getAffectedEObjectID().startsWith(initialEObjectID) || !changeToIDMap.isEmpty())
			return;

		putChangeToIDPair(change, change.getAffectedEObjectID());
	}

	/**
	 * Assumption: A change previously introduced the tracked EObject
	 * 
	 * Assumption: DeleteEObject is the last occurrence of the tracked EObject
	 */
	private void adaptIDForChange(DeleteEObject<?> change) {
		if (!change.getAffectedEObjectType().equals(eobjectType)
				|| !change.getAffectedEObjectID().startsWith(this.getIDForChange(change)))
			return;

		putChangeToIDPair(change, change.getAffectedEObjectID());
	}

	/**
	 * Assumption: A change previously introduced the tracked EObject, since
	 * InsertRootEObject is only supposed to apply after a CreateEObject change
	 */
	private void adaptIDForChange(InsertRootEObject<?> change) {
		if (change.getNewValueID().startsWith(this.getIDForChange(change)))
			putChangeToIDPair(change, change.getNewValueID());
	}

	/**
	 * Assumption: A change previously introduced the tracked EObject, since
	 * RemoveRootEObject is only supposed to apply after a RemoveEReference change
	 */
	private void adaptIDForChange(RemoveRootEObject<?> change) {
		if (change.getOldValueID().startsWith(this.getIDForChange(change)))
			// RemoveRootEObject does NOT explicitly name the cache ID => Must track it
			// manually. Assume here that only cachedEObjectURI is used and the cache index
			// is never incremented, i.e. RemoveRootEObject always directly precedes a
			// DeleteEObject change
			putChangeToIDPair(change, cachedEObjectURI);
	}

	/**
	 * Assumption: UnsetFeature changes will not affect an EObject's ID, as that
	 * would go against change metamodel logic
	 */
	private void adaptIDForChange(UnsetFeature<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change)))
			putChangeToIDPair(change, change.getAffectedEObjectID());
	}

	/**
	 * Assumption: InsertEAttributeValue changes will not affect an EObject's ID, as
	 * these changes only affect EAttributes, which cannot affect EObject hierarchy
	 */
	private void adaptIDForChange(InsertEAttributeValue<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change)))
			putChangeToIDPair(change, change.getAffectedEObjectID());
	}

	/**
	 * Assumption: RemoveEAttributeValue changes will not affect an EObject's ID, as
	 * these changes only affect EAttributes, which cannot affect EObject hierarchy
	 */
	private void adaptIDForChange(RemoveEAttributeValue<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change)))
			putChangeToIDPair(change, change.getAffectedEObjectID());
	}

	private void adaptIDForChange(InsertEReference<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getAffectedEObjectID());
		} else if (change.getNewValueID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getNewValueID());
		}
	}

	private void adaptIDForChange(RemoveEReference<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getAffectedEObjectID());
		} else if (change.getOldValueID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getOldValueID());
		}
	}

	private void adaptIDForChange(ReplaceSingleValuedEAttribute<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change))
				&& change.getAffectedFeature().equals(EcorePackage.Literals.EATTRIBUTE__ID))
			putChangeToIDPair(change, (String) change.getNewValue());
	}

	private void adaptIDForChange(ReplaceSingleValuedEReference<?, ?> change) {
		if (change.getAffectedEObjectID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getAffectedEObjectID());
		} else if (change.getOldValueID() != null && change.getOldValueID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getOldValueID());
		} else if (change.getNewValueID() != null && change.getNewValueID().startsWith(this.getIDForChange(change))) {
			putChangeToIDPair(change, change.getNewValueID());
		}
	}

	private void putChangeToIDPair(EChange change, String id) {
		putChangeToIDPair(change, id, false);
	}

	private void putChangeToIDPair(EChange change, String id, boolean forceReplacement) {
		var prevID = changeToIDMap.put(change, id);

		if (forceReplacement && prevID == null) {
			prevID = id;
		}

		if (prevID != null) {
			final var finalPrevID = prevID;
			changeToIDMap.entrySet().stream().dropWhile((e) -> e.getKey() != change).skip(1).forEach((e) -> {
				// Replace the ID prefix to account for nested elements
				e.setValue(e.getValue().replace(finalPrevID, id));
				ChangeUtil.replaceChangeIDs(change, finalPrevID, e.getValue());
			});
		}
	}

	private String getIDForChange(EChange change) {
		if (!changeSequence.contains(change))
			return null;

		if (changeToIDMap.containsKey(change))
			return changeToIDMap.get(change);

		var mostRecentIDAffectingChange = getPreviousMostRecentIDAffectingChange(change);
		if (mostRecentIDAffectingChange != null)
			return changeToIDMap.get(mostRecentIDAffectingChange);

		return null;
	}

	private EChange getPreviousMostRecentIDAffectingChange(EChange change) {
		var changeIdx = changeSequence.indexOf(change);
		for (int i = changeIdx - 1; i > -1; i--) {
			var currentChange = changeSequence.get(i);
			if (changeToIDMap.containsKey(currentChange))
				return currentChange;
		}
		return null;
	}
}
