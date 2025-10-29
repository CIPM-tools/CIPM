package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public final class PcmUserInteractionManager {
	private static final List<PcmUserInteractionLogger> loggers = new ArrayList<PcmUserInteractionLogger>();

	private static final List<AbstractUserInteraction> wrappers = new ArrayList<AbstractUserInteraction>();

	private static final List<ConflictResolutionStrategy> resolutionStrats = new ArrayList<ConflictResolutionStrategy>();
	/**
	 * Assumption: All features only have to be changed once at most after change
	 * pre-processing
	 */
	private static final FeatureEntryContainer desiredFeatureValues = new FeatureEntryContainer();
	private static final CorrespondenceEntryContainer desiredCorrespondences = new CorrespondenceEntryContainer();

	public static void addUserInteraction(AbstractUserInteraction userInteraction) {
		if (userInteraction.getDesiredFeatures().stream().anyMatch(
				(df) -> !hasDesiredFeatureValue(df.getTriggeringPCMElement(), df.getAffectedJavaElementFeature()))
				|| userInteraction.getDesiredCorrespondences().stream()
						.anyMatch((dc) -> !hasDesiredCorrespondence(dc.getKnownElement(), dc.getCorrespondenceTag()))) {
			resolutionStrats.forEach((s) -> s.applyFor(userInteraction));
		}

		// Split this part from conflict resolution, since they have to be applied first
		if (userInteraction.getDesiredFeatures().stream().anyMatch(
				(df) -> !hasDesiredFeatureValue(df.getTriggeringPCMElement(), df.getAffectedJavaElementFeature()))) {
			if (!wrappers.contains(userInteraction)) {
				wrappers.add(userInteraction);
			}
			userInteraction.getDesiredFeatures().forEach((feat) -> {
				var setVal = desiredFeatureValues.getAssignedDesiredFeatureEntry(feat.getTriggeringPCMElement(),
						feat.getAffectedJavaElementFeature());
				if (setVal.isPresent()) {
					userInteraction.getDesiredFeatureChangedValue(setVal.get());
				} else if (desiredFeatureValues
						.getDesiredFeatureEntry(feat.getTriggeringPCMElement(), feat.getAffectedJavaElementFeature())
						.isEmpty()) {
					desiredFeatureValues.addDesiredFeature(feat);
				}
			});
		}

		// TODO Account for 1-to-many and many-to-many correspondences
		// Split this part from conflict resolution, since they have to be applied first
		if (userInteraction.getDesiredCorrespondences().stream()
				.anyMatch((dc) -> !hasDesiredCorrespondence(dc.getKnownElement(), dc.getCorrespondenceTag()))) {
			if (!wrappers.contains(userInteraction)) {
				wrappers.add(userInteraction);
			}
			userInteraction.getDesiredCorrespondences().forEach((cor) -> {
				var completeCorOpt = desiredCorrespondences.getCompleteDesiredCorrespondence(cor.getKnownElement(),
						cor.getCorrespondenceTag());
				if (completeCorOpt.hasAnyCompleteCorrespondences()) {
					userInteraction.getDesiredCorrespondenceChange(completeCorOpt);
				} else if (desiredCorrespondences
						.getDesiredCorrespondenceEntry(cor.getKnownElement(), cor.getCorrespondenceTag()).isEmpty()) {
					desiredCorrespondences.addDesiredCorrespondenceEntry(cor);
				}
			});
		}
	}

	public static void removeUserInteraction(AbstractUserInteraction userInteraction) {
		wrappers.remove(userInteraction);
	}

	private static void computeAbsentFeatureValue(FeatureEntry entry) {
		var it = new ArrayList<>(wrappers).iterator();

		// Do not iterate over wrappers as performManualUserInteraction
		// may lead to removal of currentW after it finishes
		while (it.hasNext() && !entry.hasAssignedValue()) {
			var currentW = it.next();
			if (!currentW.hasDesiredFeature(entry.getTriggeringPCMElement(), entry.getAffectedJavaElementFeature())) {
				continue;
			} else {
				currentW.performManualUserInteraction();
				// Manual interaction should update entry
			}
		}
	}

	public static Object getDesiredFeatureValue(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat, boolean computeIfAbsent) {
		var entry = desiredFeatureValues.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat);
		if (entry == null) {
			entry = new FeatureEntry(triggeringPCMElement, affectedJavaElement, feat);
			desiredFeatureValues.addDesiredFeature(entry);
		}

		if (!entry.hasAssignedValue() && computeIfAbsent) {
			computeAbsentFeatureValue(entry);
		}
		return entry.hasAssignedValue() ? entry.getValue() : null;
	}

	public static Object getDesiredFeatureValue(EObject triggeringPCMElement, EStructuralFeature feat,
			boolean computeIfAbsent) {
		return getDesiredFeatureValue(triggeringPCMElement, null, feat, computeIfAbsent);
	}

	public static boolean hasDesiredFeatureValue(EObject triggeringPCMElement, EStructuralFeature feat) {
		return desiredFeatureValues.hasDesiredFeatureValue(triggeringPCMElement, feat);
	}

	public static boolean hasDesiredFeatureValue(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return desiredFeatureValues.hasDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat);
	}

	public static Object removeDesiredFeatureValue(EObject triggeringPCMElement, EStructuralFeature feat,
			Object value) {
		return desiredFeatureValues.removeDesiredFeatureValue(triggeringPCMElement, feat, value);
	}

	public static Object removeDesiredFeatureValue(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat, Object value) {
		return desiredFeatureValues.removeDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat, value);
	}

	public static void setDesiredFeatureValue(AbstractUserInteraction userInteraction, FeatureEntry featEntry) {
		desiredFeatureValues.setDesiredFeatureValue(userInteraction, featEntry);
	}

	public static CorrespondenceEntry getDesiredCorrespondence(EObject knownSide, String correspondenceTag,
			boolean computeIfAbsent) {
		var corEntry = desiredCorrespondences.getCompleteDesiredCorrespondence(knownSide, correspondenceTag);
		if (corEntry.hasAnyCompleteCorrespondences())
			return corEntry;

		if (!computeIfAbsent)
			return null;

		if (desiredCorrespondences.getDesiredCorrespondenceEntry(knownSide, correspondenceTag).isEmpty()) {
			corEntry = new CorrespondenceEntry(knownSide, correspondenceTag);
			desiredCorrespondences.addDesiredCorrespondenceEntry(corEntry);
		}

		computeAbsentCorrespondence(corEntry);

		return corEntry.hasAnyCompleteCorrespondences() ? corEntry : null;
	}

	private static void computeAbsentCorrespondence(CorrespondenceEntry corEntry) {
		var it = new ArrayList<>(wrappers).iterator();
		// Do not iterate over wrappers as performManualUserInteraction
		// may lead to removal of currentW after it finishes
		while (it.hasNext() && !corEntry.hasAnyCompleteCorrespondences()) {
			var currentW = it.next();
			if (!currentW.hasDesiredCorrespondence(corEntry.getKnownElement(), corEntry.getCorrespondenceTag())) {
				continue;
			} else {
				currentW.performManualUserInteraction();
				// Manual interaction is supposed to update the correspondences
				corEntry = desiredCorrespondences.getCompleteDesiredCorrespondence(corEntry.getKnownElement(),
						corEntry.getCorrespondenceTag());
			}
		}
	}

	public static boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return desiredCorrespondences.hasDesiredCorrespondence(knownSide, correspondenceTag);
	}

	public static Object removeDesiredCorrespondence(EObject knownSide, EObject otherSide, String correspondenceTag) {
		return desiredCorrespondences.removeDesiredCorrespondence(knownSide, otherSide, correspondenceTag);
	}

	public static void setDesiredCorrespondence(AbstractUserInteraction userInteraction, CorrespondenceEntry corEntry) {
		desiredCorrespondences.setDesiredCorrespondence(userInteraction, corEntry);
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
		return desiredCorrespondences.getAllCompleteCorrespondences();
	}

	public static Set<FeatureEntry> getAllAssignedFeatures() {
		return desiredFeatureValues.getAllAssignedFeatures();
	}
}
