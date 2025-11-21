package cipm.consistency.cpr.pcmjava.logger;

import java.util.ArrayList;
import java.util.List;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;

public class PcmUserInteractionAutomaticityStatistics {
	private static PcmUserInteractionAutomaticityStatistics instance;

	/**
	 * All ConflictResolutionStrategies that are used in the experiment, yet do not
	 * serve as oracles for user interactions. This means, they are closer to being
	 * realistic.
	 */
	private final List<String> testIndependentConflictResolutionStrategyIDs = new ArrayList<>();
	/**
	 * All ConflictResolutionStrategies that are used specifically for the
	 * experiment. These ConflictResolutionStrategies are similar to oracles for
	 * user interactions added to automate experiment. They are not realistic
	 * ConflictResolutionStrategies.
	 */
	private final List<String> testSpecificConflictResolutionStrategyIDs = new ArrayList<>();

	/**
	 * All forms of user interaction that are triggered during the experiment, i.e.
	 * the total number of triggered user interactions.
	 */
	private final List<String> triggeredUserInteractionIDs = new ArrayList<>();

	/**
	 * Manual user interactions that are triggered during the experiment, which are
	 * addressed by {@link testSpecificConflictResolutionStrategies}. These user
	 * interactions would be performed manually under realistic settings.
	 */
	private final List<String> triggeredRealisticallySemiAutomaticUserInteractionIDs = new ArrayList<>();

	/**
	 * Manual user interactions that are triggered during the experiment and
	 * partially intercepted by ConflictResolutionStrategies, which are not inside
	 * {@link testSpecificConflictResolutionStrategies}. These are the user
	 * interactions that would actually be partially intercepted under realistic
	 * settings.
	 */
	private final List<String> triggeredRealisticallySemiAutomaticPartiallyInterceptedUserInteractionIDs = new ArrayList<>();

	/**
	 * Manual user interactions that are triggered during the experiment and
	 * intercepted by ConflictResolutionStrategies, which are not inside
	 * {@link testSpecificConflictResolutionStrategies}. These are the user
	 * interactions that would actually be intercepted under realistic settings.
	 */
	private final List<String> triggeredRealisticallyFullyAutomaticUserInteractionIDs = new ArrayList<>();

	/**
	 * Refer to the corresponding ID lists above for more information
	 */
	private int numberOfTriggeredUserInteractions = 0;
	/**
	 * Refer to the corresponding ID lists above for more information
	 */
	private int numberOfTriggeredRealisticallyFullyAutomaticUserInteractions = 0;
	/**
	 * Refer to the corresponding ID lists above for more information
	 */
	private int numberOfTriggeredRealisticallySemiAutomaticPartiallyInterceptedUserInteractions = 0;
	/**
	 * Refer to the corresponding ID lists above for more information
	 */
	private int numberOfTriggeredRealisticallySemiAutomaticNonInterceptedUserInteractions = 0;

	/**
	 * Proportion of realistically fully automatic user interaction count to total
	 * user interaction count
	 */
	private double automaticityDegree = 0;

	private PcmUserInteractionAutomaticityStatistics() {
	}

	public static PcmUserInteractionAutomaticityStatistics getInstance() {
		if (instance == null)
			instance = new PcmUserInteractionAutomaticityStatistics();
		return instance;
	}

	public void addFullyAutomaticUserInteraction(AbstractUserInteraction userInteraction) {
		triggeredUserInteractionIDs.add(userInteraction.getID());
		numberOfTriggeredUserInteractions++;

		if (userInteraction.getAppliedConflictResolutionStrategies().stream()
				.noneMatch((crs) -> testSpecificConflictResolutionStrategyIDs.contains(crs.getID()))) {
			triggeredRealisticallyFullyAutomaticUserInteractionIDs.add(userInteraction.getID());
			numberOfTriggeredRealisticallyFullyAutomaticUserInteractions++;
		} else {
			addTestSpecificCRSInterceptedUserInteraction(userInteraction);
		}
	}

	public void semiAutomaticPartiallyInterceptedUserInteractionTriggered(AbstractUserInteraction userInteraction) {
		triggeredUserInteractionIDs.add(userInteraction.getID());
		numberOfTriggeredUserInteractions++;

		if (userInteraction.getAppliedConflictResolutionStrategies().stream()
				.noneMatch((crs) -> testSpecificConflictResolutionStrategyIDs.contains(crs.getID()))) {
			triggeredRealisticallySemiAutomaticPartiallyInterceptedUserInteractionIDs.add(userInteraction.getID());
			numberOfTriggeredRealisticallySemiAutomaticPartiallyInterceptedUserInteractions++;
		} else {
			addTestSpecificCRSInterceptedUserInteraction(userInteraction);
		}
	}

	private void addTestSpecificCRSInterceptedUserInteraction(AbstractUserInteraction userInteraction) {
		triggeredRealisticallySemiAutomaticUserInteractionIDs.add(userInteraction.getID());
		numberOfTriggeredRealisticallySemiAutomaticNonInterceptedUserInteractions++;
	}

	public void semiAutomaticUserInteractionTriggered(AbstractUserInteraction userInteraction) {
		triggeredUserInteractionIDs.add(userInteraction.getID());
		numberOfTriggeredUserInteractions++;

		triggeredRealisticallySemiAutomaticUserInteractionIDs.add(userInteraction.getID());
		numberOfTriggeredRealisticallySemiAutomaticNonInterceptedUserInteractions++;
	}

	public List<String> getTriggeredUserInteractionIDs() {
		return triggeredUserInteractionIDs;
	}

	public int getNumberOfTriggeredUserInteractions() {
		return numberOfTriggeredUserInteractions;
	}

	public List<String> getTriggeredSemiAutomaticUserInteractionIDs() {
		return triggeredRealisticallySemiAutomaticUserInteractionIDs;
	}

	public int getNumberOfTriggeredSemiAutomaticNonInterceptedUserInteractions() {
		return numberOfTriggeredRealisticallySemiAutomaticNonInterceptedUserInteractions;
	}

	public List<String> getTriggeredSemiAutomaticPartiallyInterceptedUserInteractionIDs() {
		return triggeredRealisticallySemiAutomaticPartiallyInterceptedUserInteractionIDs;
	}

	public int getNumberOfTriggeredSemiAutomaticPartiallyInterceptedUserInteractions() {
		return numberOfTriggeredRealisticallySemiAutomaticPartiallyInterceptedUserInteractions;
	}

	public List<String> getTriggeredFullyAutomaticUserInteractionIDs() {
		return triggeredRealisticallyFullyAutomaticUserInteractionIDs;
	}

	public int getNumberOfTriggeredFullyAutomaticUserInteractions() {
		return numberOfTriggeredRealisticallyFullyAutomaticUserInteractions;
	}

	public List<String> getTestIndependentConflictResolutionStrategyIDs() {
		return testIndependentConflictResolutionStrategyIDs;
	}

	public void addTestIndependentConflictResolutionStrategy(ConflictResolutionStrategy crs) {
		this.testIndependentConflictResolutionStrategyIDs.add(crs.getID());
	}

	public List<String> getTestSpecificConflictResolutionStrategyIDs() {
		return testSpecificConflictResolutionStrategyIDs;
	}

	public void addTestSpecificConflictResolutionStrategy(ConflictResolutionStrategy crs) {
		this.testSpecificConflictResolutionStrategyIDs.add(crs.getID());
	}

	public double computeAutomaticityDegree() {
		automaticityDegree = (double) ((double) numberOfTriggeredRealisticallyFullyAutomaticUserInteractions)
				/ ((double) numberOfTriggeredUserInteractions);
		return automaticityDegree;
	}

	public double getAutomaticityDegree() {
		return automaticityDegree;
	}
}
