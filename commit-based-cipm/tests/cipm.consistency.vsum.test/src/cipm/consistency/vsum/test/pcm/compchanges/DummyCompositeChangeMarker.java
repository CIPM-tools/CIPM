package cipm.consistency.vsum.test.pcm.compchanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.resolve.EChangeResolverAndApplicator;

public class DummyCompositeChangeMarker {
	private static final Map<String, List<EChange>> changeMarkMap = new HashMap<>();
	private static final List<EChange> handledChanges = new ArrayList<>();

	public static void markChange(EChange change, String mark) {
		if (isChangeMarked(change))
			return;

		var unresolvedChange = EChangeResolverAndApplicator.unresolve(change);

		if (changeMarkMap.containsKey(mark)) {
			changeMarkMap.get(mark).add(unresolvedChange);
		} else {
			var list = new ArrayList<EChange>();
			list.add(unresolvedChange);
			changeMarkMap.put(mark, list);
		}
	}

	public static void changeHandled(EChange change) {
		handledChanges.add(change);
	}

	public static void changeHandled(List<EChange> changes) {
		handledChanges.addAll(changes);
	}

	public static List<EChange> getMarkedChanges(String mark) {
		return changeMarkMap.get(mark);
	}

	public static List<EChange> getMarkedChanges(EChange change) {
		return changeMarkMap.get(getChangeMark(change));
	}

	public static String getChangeMark(EChange change) {
		var unresolvedChange = EChangeResolverAndApplicator.unresolve(change);
		var entry = changeMarkMap.entrySet().stream()
				.filter((e) -> e.getValue().stream().anyMatch((c) -> EcoreUtil.equals(c, unresolvedChange))).findFirst()
				.get();
		return entry.getKey();
	}

	public static boolean isChangeMarked(EChange change) {
		return mapListHasChange(changeMarkMap, change);
	}

	public static boolean isChangeHandled(EChange change) {
		return listHasChange(handledChanges, change);
	}

	public static boolean isUnhandledCompositeChange(EChange change) {
		return isChangeMarked(change) && !isChangeHandled(change);
	}

	public static boolean isAtomicChange(EChange change) {
		return !isChangeMarked(change);
	}

	private static boolean listHasChange(List<EChange> changeList, EChange change) {
		var unresolvedChange = EChangeResolverAndApplicator.unresolve(change);
		return changeList.stream().anyMatch((c) -> EcoreUtil.equals(c, unresolvedChange));
	}

	private static boolean mapListHasChange(Map<?, List<EChange>> map, EChange change) {
		var unresolvedChange = EChangeResolverAndApplicator.unresolve(change);
		return map.values().stream().anyMatch((l) -> l.stream().anyMatch((c) -> EcoreUtil.equals(c, unresolvedChange)));
	}

	public static void reset() {
		changeMarkMap.clear();
		handledChanges.clear();
	}
}
