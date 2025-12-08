package cipm.consistency.cpr.pcmjava.logger;

/**
 * An enum used as keys in {@link PcmToJavaChangePropagationEntry} to structure
 * the information content
 * 
 * @author Alp Torac Genc
 */
public enum PcmToJavaChangePropagationEntryKey {
	/**
	 * The {@link AbstractUserInteraction}, for which the entry is created
	 */
	USER_INTERACTION,
	/**
	 * The state of the user interaction
	 * 
	 * @see {@link PcmUserInteractionState}
	 */
	USER_INTERACTION_STATE,

	/**
	 * The {@link ConflictResolutionStrategy}, which affected
	 * {@link #USER_INTERACTION} or for which the entry is created.
	 */
	CONFLICT_RESOLUTION_STRATEGY,

	/**
	 * The {@link EAttribute} or {@link EReference} the user interaction asked for
	 * 
	 * @see {@link FeatureEntry}
	 */
	ASKED_FEATURES,
	/**
	 * The correspondence between a PCM and a Java element the user interaction
	 * asked for
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	ASKED_CORRESPONDENCE,
	/**
	 * Whether {@link #ASKED_FEATURES} or {@link #ASKED_CORRESPONDENCE} should be
	 * computed if absent
	 */
	COMPUTE_IF_ABSENT,

	/**
	 * The {@link EAttribute} or {@link EReference} that the user interaction has
	 * received from {@link PcmUserInteractionManager}
	 * 
	 * @see {@link FeatureEntry}
	 */
	RECEIVED_FEATURES,
	/**
	 * The correspondence between a PCM and a Java element that the user interaction
	 * received from {@link PcmUserInteractionManager}
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	RECEIVED_CORRESPONDENCES,

	/**
	 * The {@link EAttribute} or {@link EReference} that the user interaction has
	 * reported to {@link PcmUserInteractionManager}
	 * 
	 * @see {@link FeatureEntry}
	 */
	REPORTED_FEATURES,
	/**
	 * The correspondence between a PCM and a Java element that the user interaction
	 * reported to {@link PcmUserInteractionManager}
	 * 
	 * @see {@link CorrespondenceEntry}
	 */
	REPORTED_CORRESPONDENCES,
}
