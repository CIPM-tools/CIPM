package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public final class PcmUserInteractionManager {
	private static final List<AbstractUserInteraction> wrappers = new ArrayList<AbstractUserInteraction>();

	private static final List<ConflictResolutionStrategy> resolutionStrats = new ArrayList<ConflictResolutionStrategy>();
	/**
	 * Assumption: All features only have to be changed once at most after change
	 * pre-processing
	 */
	private static final Set<FeatureEntry> desiredFeatureValues = new HashSet<FeatureEntry>();
	private static final Set<CorrespondenceEntry> desiredCorrespondences = new HashSet<CorrespondenceEntry>();

	public static void addUserInteraction(AbstractUserInteraction userInteraction) {
		if (userInteraction.getDesiredFeatures().stream()
				.anyMatch((df) -> !hasDesiredFeatureValue(df.getEObject(), df.getFeature()))
				|| userInteraction.getDesiredCorrespondences().stream()
						.anyMatch((dc) -> !hasDesiredCorrespondence(dc.getKnownElement(), dc.getTag()))) {
			resolutionStrats.forEach((s) -> s.applyFor(userInteraction));
		}

		// Split this part from conflict resolution, since they have to be applied first
		if (userInteraction.getDesiredFeatures().stream()
				.anyMatch((df) -> !hasDesiredFeatureValue(df.getEObject(), df.getFeature()))) {
			if (!wrappers.contains(userInteraction)) {
				wrappers.add(userInteraction);
			}
			userInteraction.getDesiredFeatures().forEach((feat) -> {
				var setVal = getAssignedDesiredFeatureEntry(feat.getEObject(), feat.getFeature());
				if (setVal.isPresent()) {
					userInteraction.getDesiredFeatureChangedValue(setVal.get());
				} else if (getDesiredFeatureEntry(feat.getEObject(), feat.getFeature()).isEmpty()) {
					desiredFeatureValues.add(feat);
				}
			});
		}

		// TODO Account for 1-to-many and many-to-many correspondences
		// Split this part from conflict resolution, since they have to be applied first
		if (userInteraction.getDesiredCorrespondences().stream()
				.anyMatch((dc) -> !hasDesiredCorrespondence(dc.getKnownElement(), dc.getTag()))) {
			if (!wrappers.contains(userInteraction)) {
				wrappers.add(userInteraction);
			}
			userInteraction.getDesiredCorrespondences().forEach((cor) -> {
				var completeCorOpt = getCompleteDesiredCorrespondence(cor.getKnownElement(), cor.getTag());
				if (completeCorOpt.hasAnyCompleteCorrespondences()) {
					userInteraction.getDesiredCorrespondenceChange(completeCorOpt);
				} else if (getDesiredCorrespondenceEntry(cor.getKnownElement(), cor.getTag()).isEmpty()) {
					desiredCorrespondences.add(cor);
				}
			});
		}
	}

	public static void removeUserInteraction(AbstractUserInteraction userInteraction) {
		wrappers.remove(userInteraction);
	}

	public static Object getDesiredFeatureValue(EObject obj, EStructuralFeature feat, boolean computeIfAbsent) {
		FeatureEntry entry = null;
		var valOpt = getAssignedDesiredFeatureEntry(obj, feat);
		if (valOpt.isPresent()) {
			entry = valOpt.get();
		} else if ((valOpt = getDesiredFeatureEntry(obj, feat)).isPresent()) {
			entry = valOpt.get();
		} else {
			entry = new FeatureEntry(obj, feat);
			desiredFeatureValues.add(entry);
		}

		if (!entry.hasAssignedValue() && computeIfAbsent) {
			var it = new ArrayList<>(wrappers).iterator();

			// Do not iterate over wrappers as performManualUserInteraction
			// may lead to removal of currentW after it finishes
			while (it.hasNext() && !entry.hasAssignedValue()) {
				var currentW = it.next();
				if (!currentW.hasDesiredFeature(obj, feat)) {
					continue;
				} else {
					currentW.performManualUserInteraction();
					// Manual interaction should update entry
				}
			}
		}
		return entry.hasAssignedValue() ? entry.getValue() : null;
	}

	public static boolean hasDesiredFeatureValue(EObject obj, EStructuralFeature feat) {
		return getAssignedDesiredFeatureEntry(obj, feat).isPresent();
	}

	public static Object removeDesiredFeatureValue(EObject obj, EStructuralFeature feat, Object value) {
		var valOpt = getAssignedDesiredFeatureEntry(obj, feat);
		if (valOpt.isEmpty())
			return null;

		var val = valOpt.get();

		if (val.hasValue(value)) {
			val.unsetOrRemoveValue(value);
			return value;
		}

		return null;
	}

	public static void setDesiredFeatureValue(AbstractUserInteraction userInteraction, FeatureEntry featEntry) {
		var valOpt = getAssignedDesiredFeatureEntry(featEntry.getEObject(), featEntry.getFeature());
		if (valOpt.isPresent()) {
			valOpt.get().setValuesFrom(featEntry);
		} else if ((valOpt = getDesiredFeatureEntry(featEntry.getEObject(), featEntry.getFeature())).isPresent()) {
			valOpt.get().setValuesFrom(featEntry);
		} else {
			desiredFeatureValues.add(featEntry);
		}
	}

	private static Optional<FeatureEntry> getDesiredFeatureEntry(EObject obj, EStructuralFeature feat) {
		return desiredFeatureValues.stream().filter((e) -> e.isFeatureEntryFor(obj, feat)).findFirst();
	}

	private static Optional<FeatureEntry> getAssignedDesiredFeatureEntry(EObject obj, EStructuralFeature feat) {
		return desiredFeatureValues.stream().filter((e) -> e.isFeatureEntryFor(obj, feat) && e.hasAssignedValue())
				.findFirst();
	}

	private static Optional<CorrespondenceEntry> getDesiredCorrespondenceEntry(EObject obj, String correspondenceTag) {
		return desiredCorrespondences.stream().filter((t) -> t.hasElement(obj) && t.isTagEqual(correspondenceTag))
				.findFirst();
	}

	private static CorrespondenceEntry getCompleteDesiredCorrespondence(EObject obj, String correspondenceTag) {
		var result = new CorrespondenceEntry(obj, correspondenceTag);
		desiredCorrespondences.stream().filter((t) -> t.hasAnyCompleteCorrespondencesWith(obj, correspondenceTag))
				.forEach((ce) -> result.addCorrespondences(ce));
		return result;
	}

	private static Optional<CorrespondenceEntry> getCompleteDesiredCorrespondence(EObject knownSide, EObject otherSide,
			String correspondenceTag) {
		return desiredCorrespondences.stream()
				.filter((t) -> t.hasCorrespondence(knownSide, otherSide) && t.isTagEqual(correspondenceTag))
				.findFirst();
	}

	public static CorrespondenceEntry getDesiredCorrespondence(EObject knownSide, String correspondenceTag,
			boolean computeIfAbsent) {
		var corEntry = getCompleteDesiredCorrespondence(knownSide, correspondenceTag);
		if (corEntry.hasAnyCompleteCorrespondences())
			return corEntry;

		if (!computeIfAbsent)
			return null;

		if (getDesiredCorrespondenceEntry(knownSide, correspondenceTag).isEmpty()) {
			desiredCorrespondences.add(new CorrespondenceEntry(knownSide, correspondenceTag));
		}

		var it = new ArrayList<>(wrappers).iterator();

		// Do not iterate over wrappers as performManualUserInteraction
		// may lead to removal of currentW after it finishes
		while (it.hasNext() && !corEntry.hasAnyCompleteCorrespondences()) {
			var currentW = it.next();
			if (!currentW.hasDesiredCorrespondence(knownSide, correspondenceTag)) {
				continue;
			} else {
				currentW.performManualUserInteraction();
				// Manual interaction is supposed to update the correspondences
				corEntry = getCompleteDesiredCorrespondence(knownSide, correspondenceTag);
			}
		}

		return corEntry.hasAnyCompleteCorrespondences() ? corEntry : null;
	}

	public static boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return getCompleteDesiredCorrespondence(knownSide, correspondenceTag).hasAnyCompleteCorrespondences();
	}

	public static Object removeDesiredCorrespondence(EObject knownSide, EObject otherSide, String correspondenceTag) {
		var optCor = getCompleteDesiredCorrespondence(knownSide, otherSide, correspondenceTag);
		if (optCor.isEmpty())
			return null;

		var cor = optCor.get();
		cor.removeCorrespondent(otherSide);

		if (!cor.hasAnyCompleteCorrespondences()) {
			desiredCorrespondences.remove(cor);
		}

		return cor;
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

	public static Set<CorrespondenceEntry> getAllCompleteCorrespondences() {
		return desiredCorrespondences.stream().filter((c) -> c.hasAnyCompleteCorrespondences())
				.collect(Collectors.toCollection(Set::of));
	}

	public static Set<FeatureEntry> getAllAssignedFeatures() {
		return desiredFeatureValues.stream().filter((v) -> v.hasAssignedValue())
				.collect(Collectors.toCollection(Set::of));
	}
}
