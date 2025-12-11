package cipm.consistency.cpr.pcmjava.logger;

/**
 * An enum, whose values indicate various states of a
 * {@link AbstractUserInteraction} or {@link ConflictResolutionStrategy} in PCM
 * to Java change propagation.
 * 
 * @author Alp Torac Genc
 */
public enum PcmUserInteractionState {
	/**
	 * User interaction is registered to {@link PcmUserInteractionManager}
	 */
	USER_INTERACTION_REGISTERED,
	/**
	 * User interaction is removed from {@link PcmUserInteractionManager}
	 */
	USER_INTERACTION_REMOVED,

	/**
	 * Manual user interaction is triggered
	 */
	MANUAL_INTERVENTION_TRIGGERED,

	/**
	 * User interaction needs certain feature values ({@link EAttribute} or
	 * {@link EReference}) to be input.
	 * 
	 * @see {@link FeatureEntry}
	 */
	USER_INTERACTION_ASKED_FOR_FEATURES,
	/**
	 * User interaction needs certain correspondences between PCM and Java model
	 * elements to be input.
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	USER_INTERACTION_ASKED_FOR_CORRESPONDENCES,

	/**
	 * User interaction received certain feature values ({@link EAttribute} or
	 * {@link EReference}) from {@link PcmUserInteractionManager}
	 * 
	 * @see {@link FeatureEntry}
	 */
	USER_INTERACTION_RECEIVED_FEATURES,
	/**
	 * User interaction received certain correspondences between PCM and Java model
	 * elements from {@link PcmUserInteractionManager}
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	USER_INTERACTION_RECEIVED_CORRESPONDENCES,

	/**
	 * User interaction reported certain feature values ({@link EAttribute} or
	 * {@link EReference}) to {@link PcmUserInteractionManager}
	 * 
	 * @see {@link FeatureEntry}
	 */
	USER_INTERACTION_REPORTED_FEATURES,
	/**
	 * User interaction reported certain correspondences between PCM and Java model
	 * elements to {@link PcmUserInteractionManager}
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	USER_INTERACTION_REPORTED_CORRESPONDENCES,

	/**
	 * User interaction is complete, but not yet removed from
	 * {@link PcmUserInteractionManager}
	 */
	USER_INTERACTION_FINALISED,

	/**
	 * Manual user interaction is over
	 */
	MANUAL_INTERVENTION_OVER,

	/**
	 * Conflict resolution strategy has affected a user interaction
	 */
	CONFLICT_RESOLUTION_STRATEGY_APPLIED_TO_USER_INTERACTION,
	/**
	 * Conflict resolution strategy is registered to
	 * {@link PcmUserInteractionManager}
	 */
	CONFLICT_RESOLUTION_STRATEGY_REGISTERED,
	/**
	 * Conflict resolution strategy is removed from
	 * {@link PcmUserInteractionManager}
	 */
	CONFLICT_RESOLUTION_STRATEGY_REMOVED,

	/**
	 * Conflict resolution strategy reported certain feature values
	 * ({@link EAttribute} or {@link EReference}) to
	 * {@link PcmUserInteractionManager}
	 * 
	 * @see {@link FeatureEntry}
	 */
	CONFLICT_RESOLUTION_STRATEGY_REPORTED_FEATURES,
	/**
	 * Conflict resolution strategy reported certain correspondences between PCM and
	 * Java model elements to {@link PcmUserInteractionManager}
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	CONFLICT_RESOLUTION_STRATEGY_REPORTED_CORRESPONDENCES,

	/**
	 * Conflict resolution strategy provided the user interaction with what it
	 * needs, manual user interaction is no longer necessary
	 */
	USER_INTERACTION_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY,
	/**
	 * Conflict resolution strategy partially provided the user interaction with
	 * what it needs, manual user interaction is still necessary
	 */
	USER_INTERACTION_PARTIALLY_INTERCEPTED_BY_CONFLICT_RESOLUTION_STRATEGY,
}
