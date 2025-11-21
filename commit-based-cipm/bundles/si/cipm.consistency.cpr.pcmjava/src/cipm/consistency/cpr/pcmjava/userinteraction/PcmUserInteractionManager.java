package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.cpr.pcmjava.logger.PcmCprLogger;

public final class PcmUserInteractionManager {
	private static final Map<Class<? extends CanModifyEntries>, Integer> addedInstanceCount = new HashMap<>();
	private static final List<AbstractUserInteraction> wrappers = new ArrayList<AbstractUserInteraction>();

	private static final List<ConflictResolutionStrategy> resolutionStrats = new ArrayList<ConflictResolutionStrategy>();
	/**
	 * Assumption: All features only have to be changed once at most after change
	 * pre-processing
	 */
	private static final FeatureEntryContainer desiredFeatureValues = new FeatureEntryContainer();
	private static final CorrespondenceEntryContainer desiredCorrespondences = new CorrespondenceEntryContainer();

	public static void addUserInteraction(AbstractUserInteraction userInteraction) {
		incrementAddedInstanceCountAndSetID(userInteraction);

		// Only apply ConflictResolutionStrategies, if userInteraction is not resolved
		var stratIt = resolutionStrats.iterator();
		while (!userInteraction.isResolved() && stratIt.hasNext()) {
			stratIt.next().applyIfPossible(userInteraction);
		}

		// Add user interaction, if it needs a feature value that is currently not
		// present
		if (userInteraction.getDesiredFeatures().stream().anyMatch(
				(df) -> !hasDesiredFeatureValue(df.getTriggeringPCMElement(), df.getAffectedJavaElementFeature()))) {
			addUserInteractionStrategy(userInteraction);
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

		// Add user interaction, if it needs a correspondence entry that is currently
		// not present
		if (userInteraction.getDesiredCorrespondences().stream()
				.anyMatch((dc) -> !hasDesiredCorrespondence(dc.getKnownElement(), dc.getCorrespondenceTag()))) {
			addUserInteractionStrategy(userInteraction);
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
		if (wrappers.contains(userInteraction)) {
			wrappers.remove(userInteraction);
			PcmCprLogger.getInstance().userInteractionRemoved(userInteraction);
		}
	}

	public static void removeConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		if (resolutionStrats.contains(strat)) {
			resolutionStrats.remove(strat);
			PcmCprLogger.getInstance().conflictResolutionStrategyRemoved(strat);
		}
	}

	private static FeatureEntry computeAbsentFeatureValue(FeatureEntry entry) {
		var it = new ArrayList<>(wrappers).iterator();

		// Do not iterate over wrappers as performManualUserInteraction
		// may lead to removal of currentW after it finishes
		while (it.hasNext() && !entry.hasAssignedValue()) {
			var currentW = it.next();
			if (!currentW.hasDesiredFeature(entry.getTriggeringPCMElement(), entry.getAffectedJavaElementFeature())) {
				continue;
			} else {
				PcmCprLogger.getInstance().manualUserInteractionTriggered(currentW);
				currentW.performManualUserInteraction();
				PcmCprLogger.getInstance().manualUserInteractionPerformed(currentW);
				// Manual interaction should update entry
				return desiredFeatureValues.getAssignedDesiredFeatureEntry(entry.getTriggeringPCMElement(),
						entry.getAffectedJavaElementFeature()).orElseGet(() -> null);
			}
		}
		return null;
	}

	public static Object getDesiredFeatureValue(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat, boolean computeIfAbsent) {
		var entry = desiredFeatureValues.getDesiredFeatureValue(triggeringPCMElement, affectedJavaElement, feat);
		if (entry == null) {
			entry = new FeatureEntry(triggeringPCMElement, affectedJavaElement, feat);
			desiredFeatureValues.addDesiredFeature(entry);
		}

		if (!entry.hasAssignedValue() && computeIfAbsent) {
			entry = computeAbsentFeatureValue(entry);
		}
		return entry != null && entry.hasAssignedValue() ? entry.getValue() : null;
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

	public static void setDesiredFeatureValue(CanModifyEntries modifierOfFeatEntry, FeatureEntry featEntry) {
		desiredFeatureValues.setDesiredFeatureValue(featEntry);
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

		corEntry = computeAbsentCorrespondence(corEntry);

		return corEntry.hasAnyCompleteCorrespondences() ? corEntry : null;
	}

	private static CorrespondenceEntry computeAbsentCorrespondence(CorrespondenceEntry corEntry) {
		var it = new ArrayList<>(wrappers).iterator();
		// Do not iterate over wrappers as performManualUserInteraction
		// may lead to removal of currentW after it finishes
		while (it.hasNext() && !corEntry.hasAnyCompleteCorrespondences()) {
			var currentW = it.next();
			if (!currentW.hasDesiredCorrespondence(corEntry.getKnownElement(), corEntry.getCorrespondenceTag())) {
				continue;
			} else {
				PcmCprLogger.getInstance().manualUserInteractionTriggered(currentW);
				currentW.performManualUserInteraction();
				PcmCprLogger.getInstance().manualUserInteractionPerformed(currentW);
				// Manual interaction is supposed to update the correspondences
				return desiredCorrespondences.getCompleteDesiredCorrespondence(corEntry.getKnownElement(),
						corEntry.getCorrespondenceTag());
			}
		}
		return null;
	}

	public static boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return desiredCorrespondences.hasDesiredCorrespondence(knownSide, correspondenceTag);
	}

	public static Object removeDesiredCorrespondence(EObject knownSide, EObject otherSide, String correspondenceTag) {
		return desiredCorrespondences.removeDesiredCorrespondence(knownSide, otherSide, correspondenceTag);
	}

	public static void setDesiredCorrespondence(CanModifyEntries modifierOfCorEntry, CorrespondenceEntry corEntry) {
		desiredCorrespondences.setDesiredCorrespondence(corEntry);
	}

	private static void addUserInteractionStrategy(AbstractUserInteraction userInteraction) {
		if (!wrappers.contains(userInteraction)) {
			wrappers.add(userInteraction);
			PcmCprLogger.getInstance().userInteractionRegistered(userInteraction);
		}
	}

	public static void addConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		if (!resolutionStrats.contains(strat)) {
			incrementAddedInstanceCountAndSetID(strat);
			resolutionStrats.add(strat);
			PcmCprLogger.getInstance().conflictResolutionStrategyRegistered(strat);
		}
	}

	private static void incrementAddedInstanceCountAndSetID(CanModifyEntries cme) {
		if (cme == null)
			return;
		var cls = cme.getClass();

		if (!addedInstanceCount.containsKey(cls)) {
			addedInstanceCount.put(cls, 0);
		}
		var newCount = addedInstanceCount.get(cls) + 1;
		addedInstanceCount.put(cls, newCount);
		cme.setID(cls.getSimpleName() + "-" + newCount, false);
	}

	public static void reset() {
		List.copyOf(wrappers).forEach((w) -> removeUserInteraction(w));
		desiredFeatureValues.clear();
		List.copyOf(resolutionStrats).forEach((s) -> removeConflictResolutionStrategy(s));
		resolutionStrats.clear();
		desiredCorrespondences.clear();
		addedInstanceCount.clear();
	}

	public static Set<CorrespondenceEntry> getAllCompleteCorrespondences() {
		return desiredCorrespondences.getAllCompleteCorrespondences();
	}

	public static Set<FeatureEntry> getAllAssignedFeatures() {
		return desiredFeatureValues.getAllAssignedFeatures();
	}
}
