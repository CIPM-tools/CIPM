package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;

public final class PcmUserInteractionManager {
	private static final Object unsetKey = new Object();

	private static final List<AbstractUserInteraction> wrappers = new ArrayList<AbstractUserInteraction>();

	private static final List<ConflictResolutionStrategy> resolutionStrats = new ArrayList<ConflictResolutionStrategy>();
	/**
	 * Assumption: All features only have to be changed once at most after change
	 * pre-processing
	 */
	private static final Map<EStructuralFeature, Object> desiredFeatureValues = new HashMap<EStructuralFeature, Object>();

	public static void addUserInteraction(AbstractUserInteraction userInteraction) {
		if (!desiredFeatureValues.keySet().containsAll(userInteraction.getDesiredFeatures())) {
			resolutionStrats.forEach((s) -> s.applyFor(userInteraction));
		}

		if (!desiredFeatureValues.keySet().containsAll(userInteraction.getDesiredFeatures())) {
			userInteraction.getDesiredFeatures().forEach((f) -> {
				if (!desiredFeatureValues.containsKey(f)) {
					desiredFeatureValues.put(f, unsetKey);
				} else if (desiredFeatureValues.get(f) != unsetKey) {
					userInteraction.getDesiredFeatureChangedValue(f, desiredFeatureValues.get(f));
				}
			});
			wrappers.add(userInteraction);
		}
	}

	public static void removeUserInteraction(AbstractUserInteraction userInteraction) {
		wrappers.remove(userInteraction);
	}

	public static Object getDesiredFeatureValue(EStructuralFeature feat) {
		var val = desiredFeatureValues.containsKey(feat) ? desiredFeatureValues.get(feat) : unsetKey;
		if (val == unsetKey) {
			var it = new ArrayList<>(wrappers).iterator();

			while (it.hasNext() && val == unsetKey) {
				var currentW = it.next();
				if (!currentW.hasDesiredFeature(feat)) {
					continue;
				} else {
					currentW.performManualUserInteraction();
					val = desiredFeatureValues.get(feat);
				}
			}
		}
		return val != unsetKey ? val : null;
	}

	public static boolean hasDesiredFeatureValue(EStructuralFeature feat) {
		if (desiredFeatureValues.containsKey(feat)) {
			return desiredFeatureValues.get(feat) != unsetKey;
		}
		return false;
	}

	public static Object removeDesiredFeatureValue(EStructuralFeature feat) {
		return desiredFeatureValues.containsKey(feat) ? desiredFeatureValues.remove(feat) : unsetKey;
	}

	public static void setDesiredFeatureValue(AbstractUserInteraction userInteraction, EStructuralFeature feat,
			Object value) {
		desiredFeatureValues.put(feat, value);
		if (value != unsetKey) {
			wrappers.forEach((w) -> {
				if (w != userInteraction) {
					w.getDesiredFeatureChangedValue(feat, value);
				}
			});
		}
	}

	public static void addConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		resolutionStrats.add(strat);
	}

	public static void reset() {
		wrappers.clear();
		desiredFeatureValues.clear();
		resolutionStrats.clear();
	}
}
