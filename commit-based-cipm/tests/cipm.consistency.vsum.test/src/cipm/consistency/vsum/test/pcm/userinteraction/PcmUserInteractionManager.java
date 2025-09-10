package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
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
	private static final Set<CorrespondenceEntry> desiredCorrespondences = new HashSet<CorrespondenceEntry>();

	public static void addUserInteraction(AbstractUserInteraction userInteraction) {
		if (!desiredFeatureValues.keySet().containsAll(userInteraction.getDesiredFeatures())
				|| !desiredCorrespondences.containsAll(userInteraction.getDesiredCorrespondences())) {
			resolutionStrats.forEach((s) -> s.applyFor(userInteraction));
		}

		// Split this part from conflict resolution, since they have to be applied first
		if (!desiredFeatureValues.keySet().containsAll(userInteraction.getDesiredFeatures())) {
			if (!wrappers.contains(userInteraction)) {
				wrappers.add(userInteraction);
			}
			userInteraction.getDesiredFeatures().forEach((f) -> {
				if (!desiredFeatureValues.containsKey(f)) {
					desiredFeatureValues.put(f, unsetKey);
				} else if (desiredFeatureValues.get(f) != unsetKey) {
					userInteraction.getDesiredFeatureChangedValue(f, desiredFeatureValues.get(f));
				}
			});
		}

		// TODO Account for 1-to-many and many-to-many correspondences
		// Split this part from conflict resolution, since they have to be applied first
		if (!desiredCorrespondences.containsAll(userInteraction.getDesiredCorrespondences())) {
			if (!wrappers.contains(userInteraction)) {
				wrappers.add(userInteraction);
			}
			userInteraction.getDesiredCorrespondences().forEach((cor) -> {
				var completeCorOpt = getCompleteDesiredCorrespondence(cor.getKnownElement(), cor.getTag());
				if (completeCorOpt.isPresent()) {
					var completeCor = completeCorOpt.get();
					userInteraction.getDesiredCorrespondenceChange(completeCor);
				} else if (getDesiredCorrespondenceEntry(cor.getKnownElement(), cor.getTag()).isEmpty()) {
					desiredCorrespondences.add(cor);
				}
			});
		}
	}

	public static void removeUserInteraction(AbstractUserInteraction userInteraction) {
		wrappers.remove(userInteraction);
	}

	public static Object getDesiredFeatureValue(EStructuralFeature feat, boolean computeIfAbsent) {
		var val = desiredFeatureValues.containsKey(feat) ? desiredFeatureValues.get(feat) : unsetKey;
		if (computeIfAbsent && val == unsetKey) {
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

	private static Optional<CorrespondenceEntry> getDesiredCorrespondenceEntry(EObject knownSide,
			String correspondenceTag) {
		return desiredCorrespondences.stream()
				.filter((t) -> t.hasCorrespondent(knownSide) && t.isTagEqual(correspondenceTag)).findFirst();
	}

	private static Optional<CorrespondenceEntry> getCompleteDesiredCorrespondence(EObject knownSide,
			String correspondenceTag) {
		return desiredCorrespondences.stream()
				.filter((t) -> t.hasAnyCompleteCorrespondencesWith(knownSide, correspondenceTag)).findFirst();
	}

	public static CorrespondenceEntry getDesiredCorrespondence(EObject knownSide, String correspondenceTag,
			boolean computeIfAbsent) {
		var optCompleteCor = getCompleteDesiredCorrespondence(knownSide, correspondenceTag);
		if (optCompleteCor.isPresent())
			return optCompleteCor.get();

		if (!computeIfAbsent)
			return null;

		if (getDesiredCorrespondenceEntry(knownSide, correspondenceTag).isEmpty()) {
			desiredCorrespondences.add(new CorrespondenceEntry(knownSide, correspondenceTag));
		}

		var it = new ArrayList<>(wrappers).iterator();

		while (it.hasNext() && optCompleteCor.isEmpty()) {
			var currentW = it.next();
			if (!currentW.hasDesiredCorrespondence(knownSide, correspondenceTag)) {
				continue;
			} else {
				currentW.performManualUserInteraction();
				// Manual interaction is supposed to update the correspondence
				optCompleteCor = getCompleteDesiredCorrespondence(knownSide, correspondenceTag);
			}
		}

		return optCompleteCor.isPresent() ? optCompleteCor.get() : null;
	}

	public static boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return getCompleteDesiredCorrespondence(knownSide, correspondenceTag).isPresent();
	}

	public static Object removeDesiredCorrespondence(EObject knownSide, EObject otherSide, String correspondenceTag) {
		// TODO Fix
		var cor = getDesiredCorrespondence(knownSide, correspondenceTag, true);
		return cor != null ? desiredCorrespondences.remove(cor) : null;
	}

	public static void setDesiredCorrespondence(AbstractUserInteraction userInteraction, CorrespondenceEntry corEntry) {
		var corEntryOpt = getDesiredCorrespondenceEntry(corEntry.getKnownElement(), corEntry.getTag());
		var ce = corEntryOpt.orElseGet(() -> null);
		if (ce != null) {
			ce.addCorrespondences(corEntry);
		} else {
			desiredCorrespondences.add(corEntry);
		}
	}

	public static void addConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		resolutionStrats.add(strat);
	}

	public static void reset() {
		wrappers.clear();
		desiredFeatureValues.clear();
		resolutionStrats.clear();
		desiredCorrespondences.clear();
	}
}
