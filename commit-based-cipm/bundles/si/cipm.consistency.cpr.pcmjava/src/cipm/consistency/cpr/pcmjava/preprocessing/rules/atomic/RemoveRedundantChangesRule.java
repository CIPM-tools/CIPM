package cipm.consistency.cpr.pcmjava.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.net4j.util.collection.Pair;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;

public class RemoveRedundantChangesRule extends ChangePreprocessingRule {
	private static final ChangePreprocessingRule removeRedundantRootChanges = new RemoveRedundantRootChangesRule();
	private static final ChangePreprocessingRule removeRedundantSingleListEntryChanges = new RemoveRedundantSingleListEntryChangesRule();
	private static final ChangePreprocessingRule removeRedundantUnsetChanges = new RemoveRedundantUnsetChangesRule();
	private static final ChangePreprocessingRule removeRedundantReplaceSingleValuedEAttributeChanges = new RemoveRedundantReplaceSingleValuedEAttributeChangesRule();
	private static final ChangePreprocessingRule removeRedundantExistenceChangesRuleChanges = new RemoveRedundantExistenceChangesRule();
	private static final ChangePreprocessingRule fixReplaceSingleValuedEReferenceChanges = new HandleReplaceSingleValuedEReferenceChangesRule();

	private Map<EObject, Pair<String, String>> uriConverter;

	@Override
	public List<EChange> apply(List<EChange> changeSequence) {
		if (changeSequence == null || changeSequence.isEmpty()) {
			return new ArrayList<>();
		}

		uriConverter = new HashMap<>();

		for (var change : changeSequence) {
			var affectedID = ChangeUtil.getAffectedEObjectID(change);
			var oldID = ChangeUtil.getOldValueID(change);
			var newID = ChangeUtil.getNewValueID(change);

			if (affectedID != null) {
				uriConverter.put(ChangeUtil.getAffectedEObject(change),
						new Pair<>(URI.createURI(affectedID).fragment(), URI.createURI(affectedID).fragment()));
//				eobjsInChanges.add(ChangeUtil.getAffectedEObject(change));
			}
			if (oldID != null) {
				uriConverter.put((EObject) ChangeUtil.getOldValue(change),
						new Pair<>(URI.createURI(oldID).fragment(), URI.createURI(oldID).fragment()));
//				eobjsInChanges.add((EObject) ChangeUtil.getOldValue(change));
			}
			if (newID != null) {
				uriConverter.put((EObject) ChangeUtil.getNewValue(change),
						new Pair<>(URI.createURI(newID).fragment(), URI.createURI(newID).fragment()));
//				eobjsInChanges.add((EObject) ChangeUtil.getNewValue(change));
			}
		}

		var adapter = new EContentAdapter() {
			@Override
			public void notifyChanged(Notification notification) {
				super.notifyChanged(notification);

				var eobjs = new ArrayList<EObject>();

				switch (notification.getEventType()) {
				case Notification.ADD:
					eobjs.add((EObject) notification.getNewValue());
				case Notification.REMOVE:
					eobjs.add((EObject) notification.getOldValue());
				case Notification.MOVE:
					eobjs.add((EObject) notification.getNewValue());
					eobjs.add((EObject) notification.getOldValue());
				case Notification.ADD_MANY:
					eobjs.addAll((EList<EObject>) notification.getNewValue());
				case Notification.REMOVE_MANY:
					eobjs.addAll((EList<EObject>) notification.getOldValue());
				default:
				}

				for (var obj : eobjs) {
					if (uriConverter.containsKey(obj)) {
						uriConverter.get(obj).setElement2(obj.eResource().getURIFragment(obj));
					}
				}
			}
		};

		var adaptedResource = changeSequence.get(0).eResource();
		adaptedResource.eAdapters().add(adapter);
		var returnValue = // Create, InsertRoot, RemoveRoot, Delete changes
				removeRedundantExistenceChangesRuleChanges.apply(
						// Fix replace changes
						fixReplaceSingleValuedEReferenceChanges.apply(
								// Many-valued feature changes (insert/remove from list)
								removeRedundantSingleListEntryChanges.apply(
										// Unset feature changes (unset feature)
										removeRedundantUnsetChanges.apply(
												// Single-valued feature changes (replace EAttribute values)
												removeRedundantReplaceSingleValuedEAttributeChanges.apply(

														changeSequence)))));

		for (var processedChange : returnValue) {
			var affectedObj = ChangeUtil.getAffectedEObject(processedChange);
			var oldObj = ChangeUtil.getOldValue(processedChange);
			var newObj = ChangeUtil.getNewValue(processedChange);

			if (affectedObj != null) {
				ChangeUtil.setAffectedEObjectID(processedChange, uriConverter.get(affectedObj).getElement2());
			}
			if (oldObj != null) {
				ChangeUtil.setAffectedEObjectID(processedChange, uriConverter.get(oldObj).getElement2());
			}
			if (newObj != null) {
				ChangeUtil.setAffectedEObjectID(processedChange, uriConverter.get(newObj).getElement2());
			}
		}

		adapter.unsetTarget(adaptedResource);
		return returnValue;
	}

}
