package cipm.consistency.cpr.pcmjava.preprocessing.rules.atomic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.net4j.util.collection.Pair;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;
import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.UnsetFeature;

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
						new Pair<>(URI.createURI(affectedID).toString(), URI.createURI(affectedID).toString()));
//				eobjsInChanges.add(ChangeUtil.getAffectedEObject(change));
			}
			if (oldID != null) {
				uriConverter.put((EObject) ChangeUtil.getOldValue(change),
						new Pair<>(URI.createURI(oldID).toString(), URI.createURI(oldID).toString()));
//				eobjsInChanges.add((EObject) ChangeUtil.getOldValue(change));
			}
			if (newID != null) {
				uriConverter.put((EObject) ChangeUtil.getNewValue(change),
						new Pair<>(URI.createURI(newID).toString(), URI.createURI(newID).toString()));
//				eobjsInChanges.add((EObject) ChangeUtil.getNewValue(change));
			}
		}

		var adapter = new EContentAdapter() {
			@Override
			public void notifyChanged(Notification notification) {
				super.notifyChanged(notification);

				var allObjs = new ArrayList<Object>();

				allObjs.add(notification.getNewValue());
				allObjs.add(notification.getOldValue());
				allObjs.add(notification.getNotifier());
				allObjs.add(notification.getFeature());

				var eobjs = new ArrayList<EObject>();
				for (var obj : allObjs) {
					filterEObjects(obj, eobjs);
				}

				for (var obj : eobjs) {
					if (uriConverter.containsKey(obj)) {
						uriConverter.get(obj).setElement2(obj.eResource().getURI()
								.appendFragment(obj.eResource().getURIFragment(obj)).toString());
					}
				}
			}

			private void filterEObjects(Object obj, List<EObject> eobjList) {
				if (obj == null || obj instanceof EChange || obj instanceof EStructuralFeature)
					return;
				if (obj instanceof Collection)
					((Collection<?>) obj).stream().forEach((o) -> filterEObjects(o, eobjList));
				if (obj instanceof EObject)
					eobjList.add((EObject) obj);
			}
		};

		// TODO Use full URIs instead of fragments, since it can cause issues with the
		// case "/0"
		// TODO Add all generated EObjects to the same resource as the original ones
		// TODO Check for prefixes in URIs and replace them using uriConverter

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

		returnValue.stream().filter((c) -> c instanceof UnsetFeature<?, ?>).map((c) -> (UnsetFeature<?, ?>) c)
				.filter((c) -> ChangeUtil.isCacheID(c.getAffectedEObjectID())).collect(Collectors.toList())
				.forEach(returnValue::remove);

		for (var processedChange : returnValue) {
			var affectedObj = ChangeUtil.getAffectedEObject(processedChange);
			var oldObj = ChangeUtil.getOldValue(processedChange);
			var newObj = ChangeUtil.getNewValue(processedChange);

			if (affectedObj != null && uriConverter.containsKey(affectedObj)) {
				ChangeUtil.setAffectedEObjectID(processedChange, uriConverter.get(affectedObj).getElement2());
			}
			if (oldObj != null && uriConverter.containsKey(oldObj)) {
				ChangeUtil.setOldValueID(processedChange, uriConverter.get(oldObj).getElement2());
			}
			if (newObj != null && uriConverter.containsKey(newObj)) {
				ChangeUtil.setNewValueID(processedChange, uriConverter.get(newObj).getElement2());
			}
		}

		adapter.unsetTarget(adaptedResource);
		return returnValue;
	}

}
