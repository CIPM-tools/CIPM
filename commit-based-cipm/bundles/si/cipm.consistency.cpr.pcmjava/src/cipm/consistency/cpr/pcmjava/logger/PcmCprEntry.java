package cipm.consistency.cpr.pcmjava.logger;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;
import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;

public class PcmCprEntry {
	// General
	private AbstractUserInteraction userInteraction;
	private List<Exception> userInteractionExceptions;
	private PcmUserInteractionState userInteractionState;
	private List<EObject> affectedPCMElements;
	private List<EObject> affectedJavaElements;

	// INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY
	// PARTIALLY_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY
	// CONFLICT_BETWEEN_CONFLICT_RESOLUTION_STRATEGIES
	private List<ConflictResolutionStrategy> relevantConflictResolutionStrats;
	private List<Exception> conflictResolutionStrategyExceptions;

	// INTERCEPTED_BY_PREVIOUS_USER_INTERACTION_ANSWER
	// PARTIALLY_INTERCEPTED_BY_PREVIOUS_USER_INTERACTION_ANSWER
	// CONFLICT_BETWEEN_PREVIOUS_USER_INTERACTION_ANSWERS
	private List<AbstractUserInteraction> relevantPreviousUserInteractions;

	// PREVENTED_BY_CHANGE_PREPROCESSING
	private List<ChangePreprocessingRule> relevantChangePreprocesingRules;

	public void setUserInteraction(AbstractUserInteraction userInteraction) {
		this.userInteraction = userInteraction;
	}
}
