package cipm.consistency.cpr.pcmjava.logger;

import java.util.LinkedHashMap;
import java.util.Map;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;

/**
 * A class that can contain various information on events throughout change
 * propagation.
 * 
 * @see {@link PcmToJavaChangePropagationEntryKey} for more information
 * 
 * @author Alp Torac Genc
 */
public class PcmToJavaChangePropagationEntry {
	private final Map<PcmToJavaChangePropagationEntryKey, Object> entries = new LinkedHashMap<>();

	private void set(PcmToJavaChangePropagationEntryKey key, Object val) {
		entries.put(key, val);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(PcmToJavaChangePropagationEntryKey key) {
		return entries.containsKey(key) ? (T) entries.get(key) : null;
	}

	public void setUserInteraction(AbstractUserInteraction userInteraction) {
		set(PcmToJavaChangePropagationEntryKey.USER_INTERACTION, userInteraction);
	}

	public AbstractUserInteraction getUserInteraction() {
		return get(PcmToJavaChangePropagationEntryKey.USER_INTERACTION);
	}

	public void setUserInteractionState(PcmUserInteractionState userInteractionState) {
		set(PcmToJavaChangePropagationEntryKey.USER_INTERACTION_STATE, userInteractionState);
	}

	public PcmUserInteractionState getUserInteractionState() {
		return get(PcmToJavaChangePropagationEntryKey.USER_INTERACTION_STATE);
	}

	public void setAskedFeature(FeatureEntry askedFeats) {
		set(PcmToJavaChangePropagationEntryKey.ASKED_FEATURES, askedFeats);
	}

	public FeatureEntry getAskedFeature() {
		return get(PcmToJavaChangePropagationEntryKey.ASKED_FEATURES);
	}

	public void setAskedCorrespondence(CorrespondenceEntry askedCor) {
		set(PcmToJavaChangePropagationEntryKey.ASKED_CORRESPONDENCE, askedCor);
	}

	public CorrespondenceEntry getAskedCorrespondence() {
		return get(PcmToJavaChangePropagationEntryKey.ASKED_CORRESPONDENCE);
	}

	public void setReceivedFeature(Object receivedFeats) {
		set(PcmToJavaChangePropagationEntryKey.RECEIVED_FEATURES, receivedFeats);
	}

	public Object getReceivedFeature() {
		return get(PcmToJavaChangePropagationEntryKey.RECEIVED_FEATURES);
	}

	public void setReceivedCorrespondence(CorrespondenceEntry receivedCors) {
		set(PcmToJavaChangePropagationEntryKey.RECEIVED_CORRESPONDENCES, receivedCors);
	}

	public CorrespondenceEntry getReceivedCorrespondence() {
		return get(PcmToJavaChangePropagationEntryKey.RECEIVED_CORRESPONDENCES);
	}

	public void setReportedFeature(FeatureEntry reportedFeats) {
		set(PcmToJavaChangePropagationEntryKey.REPORTED_FEATURES, reportedFeats);
	}

	public FeatureEntry getReportedFeature() {
		return get(PcmToJavaChangePropagationEntryKey.REPORTED_FEATURES);
	}

	public void setReportedCorrespondence(CorrespondenceEntry reportedCors) {
		set(PcmToJavaChangePropagationEntryKey.REPORTED_CORRESPONDENCES, reportedCors);
	}

	public CorrespondenceEntry getReportedCorrespondence() {
		return get(PcmToJavaChangePropagationEntryKey.REPORTED_CORRESPONDENCES);
	}

	public void setConflictResolutionStrategy(ConflictResolutionStrategy crs) {
		set(PcmToJavaChangePropagationEntryKey.CONFLICT_RESOLUTION_STRATEGY, crs);
	}

	public ConflictResolutionStrategy getConflictResolutionStrategy() {
		return get(PcmToJavaChangePropagationEntryKey.CONFLICT_RESOLUTION_STRATEGY);
	}

	public void setComputeIfAbsent(boolean computeIfAbsent) {
		set(PcmToJavaChangePropagationEntryKey.COMPUTE_IF_ABSENT, computeIfAbsent);
	}

	public boolean getComputeIfAbsent() {
		return get(PcmToJavaChangePropagationEntryKey.COMPUTE_IF_ABSENT);
	}

	@Override
	public String toString() {
		return this.entries.toString();
	}
}
