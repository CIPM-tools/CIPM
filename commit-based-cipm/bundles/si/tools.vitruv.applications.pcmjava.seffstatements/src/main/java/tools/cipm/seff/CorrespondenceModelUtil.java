package tools.cipm.seff;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public final class CorrespondenceModelUtil {
	private CorrespondenceModelUtil() {}
	
	public static <TCorrespondingType> List<TCorrespondingType> getCorrespondingEObjects(EditableCorrespondenceModelView<Correspondence> cm,
			EObject object, Class<TCorrespondingType> type) {
		return getCorrespondingEObjects(cm, List.of(object), type);
	}
	
	public static <TCorrespondingType> List<TCorrespondingType> getCorrespondingEObjects(EditableCorrespondenceModelView<Correspondence> cm,
			List<EObject> objects, Class<TCorrespondingType> type) {
		return cm.getCorrespondingEObjects(objects).parallelStream().flatMap(list -> list.parallelStream())
			.filter(obj -> type.isInstance(obj)).map(obj -> type.cast(obj)).collect(Collectors.toList());
	}
	
	public static void removeCorrespondencesFor(EditableCorrespondenceModelView<Correspondence> cm,
			EObject object) {
		removeCorrespondencesFor(cm, object, null);
	}
	
	public static void removeCorrespondencesFor(EditableCorrespondenceModelView<Correspondence> cm,
			EObject object, String tag) {
		removeCorrespondencesFor(cm, List.of(object), tag);
	}
	
	public static void removeCorrespondencesFor(EditableCorrespondenceModelView<Correspondence> cm,
			List<EObject> objects) {
		removeCorrespondencesFor(cm, objects, null);
	}
	
	public static void removeCorrespondencesFor(EditableCorrespondenceModelView<Correspondence> cm,
			List<EObject> objects, String tag) {
		var allCorrespondingElements = cm.getCorrespondingEObjects(objects, tag);
		for (var corresponding : allCorrespondingElements) {
			cm.removeCorrespondencesBetween(objects, corresponding, tag);
		}
	}
}
