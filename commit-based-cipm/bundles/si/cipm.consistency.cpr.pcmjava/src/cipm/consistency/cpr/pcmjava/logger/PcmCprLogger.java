package cipm.consistency.cpr.pcmjava.logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;

public class PcmCprLogger {
	private static PcmCprLogger instance;
	/**
	 * Contains log entries about triggered user interactions and used conflict
	 * resolution strategies
	 */
	private static final List<PcmCprEntry> entries = new ArrayList<>();
	/**
	 * Tracks status information for individual user interactions, used for deriving
	 * automaticity statistics.
	 * 
	 * @see {@link PcmUserInteractionStatistics}
	 */
	private static final Map<AbstractUserInteraction, PcmUserInteractionState> stateMap = new LinkedHashMap<>();

	private final Map<String, String> serialisedEntries = new LinkedHashMap<>();

	private PcmCprLogger() {
	}

	public static PcmCprLogger getInstance() {
		if (instance == null) {
			instance = new PcmCprLogger();
		}
		return instance;
	}

	public void manualUserInteractionTriggered(AbstractUserInteraction abstractUserInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.MANUAL_INTERVENTION_TRIGGERED);
		entries.add(entry);
	}

	public void manualUserInteractionPerformed(AbstractUserInteraction abstractUserInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		setStatus(abstractUserInteraction, PcmUserInteractionState.MANUAL_INTERVENTION_OVER);
		entry.setUserInteractionState(PcmUserInteractionState.MANUAL_INTERVENTION_OVER);
		entries.add(entry);
	}

	public void userInteractionAskedForCorrespondence(AbstractUserInteraction abstractUserInteraction,
			EObject knownSide, String correspondenceTag, boolean computeIfAbsent) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_ASKED_FOR_CORRESPONDENCES);
		entry.setAskedCorrespondence(new CorrespondenceEntry(knownSide, correspondenceTag));
		entry.setComputeIfAbsent(computeIfAbsent);
		entries.add(entry);
	}

	public void userInteractionGotCorrespondenceFor(AbstractUserInteraction abstractUserInteraction,
			CorrespondenceEntry result, EObject knownSide, String correspondenceTag, boolean computeIfAbsent) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_RECEIVED_CORRESPONDENCES);
		entry.setAskedCorrespondence(new CorrespondenceEntry(knownSide, correspondenceTag));
		entry.setReceivedCorrespondence(result);
		entry.setComputeIfAbsent(computeIfAbsent);
		entries.add(entry);
	}

	public void userInteractionAskedForFeature(AbstractUserInteraction abstractUserInteraction,
			EObject triggeringPCMElement, EObject affectedJavaElement, EStructuralFeature feat,
			boolean computeIfAbsent) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_ASKED_FOR_FEATURES);
		entry.setAskedFeature(new FeatureEntry(triggeringPCMElement, affectedJavaElement, feat));
		entry.setComputeIfAbsent(computeIfAbsent);
		entries.add(entry);
	}

	public void userInteractionGotFeatureFor(AbstractUserInteraction abstractUserInteraction, Object result,
			EObject triggeringPCMElement, EObject affectedJavaElement, EStructuralFeature feat,
			boolean computeIfAbsent) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_RECEIVED_FEATURES);
		entry.setAskedFeature(new FeatureEntry(triggeringPCMElement, affectedJavaElement, feat));
		entry.setReceivedFeature(result);
		entry.setComputeIfAbsent(computeIfAbsent);
		entries.add(entry);
	}

	public void userInteractionFinalised(AbstractUserInteraction abstractUserInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		setStatus(abstractUserInteraction, PcmUserInteractionState.USER_INTERACTION_FINALISED);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_FINALISED);
		entries.add(entry);
	}

	public void userInteractionReportedCorrespondence(AbstractUserInteraction abstractUserInteraction,
			CorrespondenceEntry corEntry) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_REPORTED_CORRESPONDENCES);
		entry.setReportedCorrespondence(corEntry);
		entries.add(entry);
	}

	public void userInteractionReportedFeature(AbstractUserInteraction abstractUserInteraction,
			FeatureEntry featEntry) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_REPORTED_FEATURES);
		entry.setReportedFeature(featEntry);
		entries.add(entry);
	}

	public void conflictResolutionStrategyPartiallyInterceptedUserInteraction(
			ConflictResolutionStrategy conflictResolutionStrategy, AbstractUserInteraction userInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(userInteraction);
		setStatus(userInteraction,
				PcmUserInteractionState.USER_INTERACTION_PARTIALLY_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY);
		entry.setUserInteractionState(
				PcmUserInteractionState.USER_INTERACTION_PARTIALLY_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY);
		entry.setConflictResolutionStrategy(conflictResolutionStrategy);
		entries.add(entry);
	}

	public void conflictResolutionStrategyInterceptedUserInteraction(
			ConflictResolutionStrategy conflictResolutionStrategy, AbstractUserInteraction userInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(userInteraction);
		setStatus(userInteraction,
				PcmUserInteractionState.USER_INTERACTION_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY);
		entry.setUserInteractionState(
				PcmUserInteractionState.USER_INTERACTION_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY);
		entry.setConflictResolutionStrategy(conflictResolutionStrategy);
		PcmUserInteractionStatistics.getInstance().addFullyAutomaticUserInteraction(userInteraction);
		entries.add(entry);
	}

	public void conflictResolutionStrategyAppliedFor(ConflictResolutionStrategy conflictResolutionStrategy,
			AbstractUserInteraction userInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(userInteraction);
		entry.setUserInteractionState(PcmUserInteractionState.CONFLICT_RESOLUTION_STRATEGY_APPLIED_TO_USER_INTERACTION);
		entry.setConflictResolutionStrategy(conflictResolutionStrategy);
		entries.add(entry);
	}

	public void conflictResolutionStrategyRegistered(ConflictResolutionStrategy strat) {
		var entry = new PcmCprEntry();
		entry.setConflictResolutionStrategy(strat);
		entry.setUserInteractionState(PcmUserInteractionState.CONFLICT_RESOLUTION_STRATEGY_REGISTERED);
		entries.add(entry);
	}

	public void conflictResolutionStrategyRemoved(ConflictResolutionStrategy strat) {
		var entry = new PcmCprEntry();
		entry.setConflictResolutionStrategy(strat);
		entry.setUserInteractionState(PcmUserInteractionState.CONFLICT_RESOLUTION_STRATEGY_REMOVED);
		entries.add(entry);
	}

	public void conflictResolutionStrategyReportedCorrespondence(AbstractUserInteraction abstractUserInteraction,
			ConflictResolutionStrategy strat, CorrespondenceEntry corEntry) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setConflictResolutionStrategy(strat);
		entry.setUserInteractionState(PcmUserInteractionState.CONFLICT_RESOLUTION_STRATEGY_REPORTED_CORRESPONDENCES);
		entry.setReportedCorrespondence(corEntry);
		entries.add(entry);
	}

	public void conflictResolutionStrategyReportedFeature(AbstractUserInteraction abstractUserInteraction,
			ConflictResolutionStrategy strat, FeatureEntry featEntry) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(abstractUserInteraction);
		entry.setConflictResolutionStrategy(strat);
		entry.setUserInteractionState(PcmUserInteractionState.CONFLICT_RESOLUTION_STRATEGY_REPORTED_FEATURES);
		entry.setReportedFeature(featEntry);
		entries.add(entry);
	}

	public void userInteractionRegistered(AbstractUserInteraction userInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(userInteraction);
		setStatus(userInteraction, PcmUserInteractionState.USER_INTERACTION_REGISTERED);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_REGISTERED);
		entries.add(entry);
	}

	public void userInteractionRemoved(AbstractUserInteraction userInteraction) {
		var entry = new PcmCprEntry();
		entry.setUserInteraction(userInteraction);
		setStatus(userInteraction, PcmUserInteractionState.USER_INTERACTION_REMOVED);
		entry.setUserInteractionState(PcmUserInteractionState.USER_INTERACTION_REMOVED);
		entries.add(entry);
	}

	private void setStatus(AbstractUserInteraction userInteraction, PcmUserInteractionState status) {
		var prevStatus = stateMap.getOrDefault(userInteraction, null);

		if (status == PcmUserInteractionState.MANUAL_INTERVENTION_OVER) {
			if (prevStatus == PcmUserInteractionState.USER_INTERACTION_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY) {
				PcmUserInteractionStatistics.getInstance().addFullyAutomaticUserInteraction(userInteraction);
			} else if (prevStatus == PcmUserInteractionState.USER_INTERACTION_PARTIALLY_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY) {
				PcmUserInteractionStatistics.getInstance()
						.semiAutomaticPartiallyInterceptedUserInteractionTriggered(userInteraction);
			} else {
				PcmUserInteractionStatistics.getInstance().semiAutomaticUserInteractionTriggered(userInteraction);
			}
		}

		stateMap.put(userInteraction, status);
	}

	public void clearEntries() {
		entries.clear();
		stateMap.clear();
		serialisedEntries.clear();
	}

	public void prepareForSerialisation() {
		for (int i = 0; i < entries.size(); i++) {
			var e = entries.get(i);
			var entryName = e.getClass().getSimpleName() + "-" + i;

			serialisedEntries.put(entryName, e.toString());
		}
	}
}
