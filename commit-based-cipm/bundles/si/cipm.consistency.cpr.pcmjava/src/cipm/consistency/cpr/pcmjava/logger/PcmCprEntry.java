package cipm.consistency.cpr.pcmjava.logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;

public class PcmCprEntry {
	private final Map<PcmCprEntryKey, Object> entries = new LinkedHashMap<>();

	private void set(PcmCprEntryKey key, Object val) {
		entries.put(key, val);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(PcmCprEntryKey key) {
		return entries.containsKey(key) ? (T) entries.get(key) : null;
	}

	public void setUserInteraction(AbstractUserInteraction userInteraction) {
		set(PcmCprEntryKey.USER_INTERACTION, userInteraction);
	}

	public AbstractUserInteraction getUserInteraction() {
		return get(PcmCprEntryKey.USER_INTERACTION);
	}

	public void setUserInteractionState(PcmUserInteractionState userInteractionState) {
		set(PcmCprEntryKey.USER_INTERACTION_STATE, userInteractionState);
	}

	public PcmUserInteractionState getUserInteractionState() {
		return get(PcmCprEntryKey.USER_INTERACTION_STATE);
	}

	public void setAffectedPcmElements(List<EObject> affectedPcmElements) {
		set(PcmCprEntryKey.AFFECTED_PCM_ELEMENTS, affectedPcmElements);
	}

	public List<EObject> getAffectedPcmElements() {
		return get(PcmCprEntryKey.AFFECTED_PCM_ELEMENTS);
	}

	public void setAffectedJavaElements(List<EObject> affectedJavaElements) {
		set(PcmCprEntryKey.AFFECTED_JAVA_ELEMENTS, affectedJavaElements);
	}

	public List<EObject> getAffectedJavaElements() {
		return get(PcmCprEntryKey.AFFECTED_JAVA_ELEMENTS);
	}

	public void setAskedFeature(FeatureEntry askedFeats) {
		set(PcmCprEntryKey.ASKED_FEATURES, askedFeats);
	}

	public FeatureEntry getAskedFeature() {
		return get(PcmCprEntryKey.ASKED_FEATURES);
	}

	public void setAskedCorrespondence(CorrespondenceEntry askedCor) {
		set(PcmCprEntryKey.ASKED_CORRESPONDENCE, askedCor);
	}

	public CorrespondenceEntry getAskedCorrespondence() {
		return get(PcmCprEntryKey.ASKED_CORRESPONDENCE);
	}

	public void setReceivedFeature(Object receivedFeats) {
		set(PcmCprEntryKey.RECEIVED_FEATURES, receivedFeats);
	}

	public Object getReceivedFeature() {
		return get(PcmCprEntryKey.RECEIVED_FEATURES);
	}

	public void setReceivedCorrespondence(CorrespondenceEntry receivedCors) {
		set(PcmCprEntryKey.RECEIVED_CORRESPONDENCES, receivedCors);
	}

	public CorrespondenceEntry getReceivedCorrespondence() {
		return get(PcmCprEntryKey.RECEIVED_CORRESPONDENCES);
	}

	public void setReportedFeature(FeatureEntry reportedFeats) {
		set(PcmCprEntryKey.REPORTED_FEATURES, reportedFeats);
	}

	public FeatureEntry getReportedFeature() {
		return get(PcmCprEntryKey.REPORTED_FEATURES);
	}

	public void setReportedCorrespondence(CorrespondenceEntry reportedCors) {
		set(PcmCprEntryKey.REPORTED_CORRESPONDENCES, reportedCors);
	}

	public CorrespondenceEntry getReportedCorrespondence() {
		return get(PcmCprEntryKey.REPORTED_CORRESPONDENCES);
	}

	public void setConflictResolutionStrategy(ConflictResolutionStrategy crs) {
		set(PcmCprEntryKey.CONFLICT_RESOLUTION_STRATEGY, crs);
	}

	public ConflictResolutionStrategy getConflictResolutionStrategy() {
		return get(PcmCprEntryKey.CONFLICT_RESOLUTION_STRATEGY);
	}

	public void setComputeIfAbsent(boolean computeIfAbsent) {
		set(PcmCprEntryKey.COMPUTE_IF_ABSENT, computeIfAbsent);
	}

	public boolean getComputeIfAbsent() {
		return get(PcmCprEntryKey.COMPUTE_IF_ABSENT);
	}

	@Override
	public String toString() {
		return this.entries.toString();
	}
}
