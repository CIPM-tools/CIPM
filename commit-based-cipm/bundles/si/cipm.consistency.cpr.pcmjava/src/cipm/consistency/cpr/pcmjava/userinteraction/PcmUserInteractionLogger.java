package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;

import cipm.consistency.cpr.pcmjava.preprocessing.rules.ChangePreprocessingRule;

public class PcmUserInteractionLogger implements IPcmUserInteractionObserver {
	// TODO Implement

	private final List<PcmUserInteractionLoggerEntry> entries = new ArrayList<>();

	private int getTotalUserInteractionCount() {
		return 0;
	}

	private int getUserInteractionCountByState(PcmUserInteractionState state) {
		return 0;
	}

	@Override
	public void manualUserInteractionTriggered(AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionInterceptedByPreviousUserInteraction(AbstractUserInteraction userInteraction,
			List<AbstractUserInteraction> relevantUIs, boolean isInterceptedCompletely) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionInterceptedByConflictResolutionStrategy(AbstractUserInteraction userInteraction,
			List<ConflictResolutionStrategy> relevantCRSs, boolean isInterceptedCompletely) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionPreventedByChangePreprocessing(AbstractUserInteraction userInteraction,
			List<ChangePreprocessingRule> preventingRules) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionsConflicted(AbstractUserInteraction userInteraction,
			List<AbstractUserInteraction> relevantUIs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionAndConflictResolutionStrategyConflicted(AbstractUserInteraction userInteraction,
			List<ConflictResolutionStrategy> relevantCRSs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionThrewExceptions(AbstractUserInteraction userInteraction, List<Exception> excs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionAndConflictResolutionStrategyThrewExceptions(AbstractUserInteraction userInteraction,
			ConflictResolutionStrategy crs, List<Exception> excs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userInteractionRegistered(AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conflictResolutionStrategyRegistered(ConflictResolutionStrategy crs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conflictResolutionStrategyApplied(AbstractUserInteraction userInteraction,
			ConflictResolutionStrategy crs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userInteractionTriggered(AbstractUserInteraction userInteraction) {
		// TODO Auto-generated method stub
		
	}
}
