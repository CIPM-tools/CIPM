package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;

public interface IPcmUserInteractionObserver {
	public void userInteractionRegistered(AbstractUserInteraction userInteraction);

	public void conflictResolutionStrategyRegistered(ConflictResolutionStrategy crs);

	public void conflictResolutionStrategyApplied(AbstractUserInteraction userInteraction,
			ConflictResolutionStrategy crs);

	public void userInteractionTriggered(AbstractUserInteraction userInteraction);

	public void manualUserInteractionTriggered(AbstractUserInteraction userInteraction);

	public void userInteractionInterceptedByPreviousUserInteraction(AbstractUserInteraction userInteraction,
			List<AbstractUserInteraction> relevantUIs, boolean isInterceptedCompletely);

	public void userInteractionInterceptedByConflictResolutionStrategy(AbstractUserInteraction userInteraction,
			List<ConflictResolutionStrategy> relevantCRSs, boolean isInterceptedCompletely);

	public void userInteractionPreventedByChangePreprocessing(AbstractUserInteraction userInteraction,
			List<ChangePreprocessingRule> preventingRules);

	public void userInteractionsConflicted(AbstractUserInteraction userInteraction,
			List<AbstractUserInteraction> relevantUIs);

	public void userInteractionAndConflictResolutionStrategyConflicted(AbstractUserInteraction userInteraction,
			List<ConflictResolutionStrategy> relevantCRSs);

	public void userInteractionThrewExceptions(AbstractUserInteraction userInteraction, List<Exception> excs);

	public void userInteractionAndConflictResolutionStrategyThrewExceptions(AbstractUserInteraction userInteraction,
			ConflictResolutionStrategy crs, List<Exception> excs);
}
