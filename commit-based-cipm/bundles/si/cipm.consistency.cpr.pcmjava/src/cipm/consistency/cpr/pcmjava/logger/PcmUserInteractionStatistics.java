package cipm.consistency.cpr.pcmjava.logger;

import java.util.List;

import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;

public class PcmUserInteractionStatistics {
	public static PcmUserInteractionStatistics instance;

	/**
	 * All ConflictResolutionStrategies that are used during the experiment.
	 */
	private List<ConflictResolutionStrategy> usedConflictResolutionStrategies;
	/**
	 * All ConflictResolutionStrategies that are used specifically for the
	 * experiment. These ConflictResolutionStrategies are similar to test oracles
	 * added to automate experiment. They are not realistic
	 * ConflictResolutionStrategies.
	 */
	private List<ConflictResolutionStrategy> testSpecificConflictResolutionStrategies;

	/**
	 * All forms of user interaction that are triggered during the experiment, i.e.
	 * the total number of triggered user interactions.
	 */
	private int numberOfTriggeredUserInteractions = 0;
	/**
	 * Manual user interactions that are triggered during the experiment, which are
	 * addressed by {@link testSpecificConflictResolutionStrategies}. These user
	 * interactions would be performed manually under realistic settings.
	 */
	private int numberOfTriggeredManualUserInteractions = 0;
	/**
	 * Manual user interactions that are triggered during the experiment and
	 * partially intercepted by ConflictResolutionStrategies, which are not inside
	 * {@link testSpecificConflictResolutionStrategies}. These are the user
	 * interactions that would actually be partially intercepted under realistic
	 * settings.
	 */
	private int numberOfTriggeredSemiAutomaticUserInteractions = 0;
	/**
	 * Manual user interactions that are triggered during the experiment and
	 * intercepted by ConflictResolutionStrategies, which are not inside
	 * {@link testSpecificConflictResolutionStrategies}. These are the user
	 * interactions that would actually be intercepted under realistic settings.
	 */
	private int numberOfTriggeredFullyAutomaticUserInteractions = 0;

	private PcmUserInteractionStatistics() {
	}

	public static PcmUserInteractionStatistics getInstance() {
		if (instance == null)
			instance = new PcmUserInteractionStatistics();
		return instance;
	}

}
