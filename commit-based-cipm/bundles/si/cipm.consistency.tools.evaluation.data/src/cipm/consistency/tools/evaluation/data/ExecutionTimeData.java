package cipm.consistency.tools.evaluation.data;

import java.util.HashMap;
import java.util.Map;

/**
 * A data structure for execution times.
 *
 * @author Martin Armbruster
 */
public class ExecutionTimeData {
    private long changePropagationTime;
    private long instrumentationTime;
    private long overallTime;

    /**
     * Fine-grained timings for measurement points identified by a fixed, descriptive label
     * rather than a per-call type (e.g. "javaModelComparison"), each possibly reached more
     * than once per run (e.g. once per file compared). See {@link #recordFineGrainedTiming}.
     */
    private Map<String, AggregatedTiming> fineGrainedTimings = new HashMap<>();

    /**
     * Timings for change propagation, aggregated per concrete EChange subtype rather than
     * per call - propagateChange runs once per individual change, and a single commit-history
     * run can produce tens of thousands of them. See {@link #recordChangePropagationTimeForChangeType}.
     */
    private Map<String, AggregatedTiming> changePropagationTimePerChangeType = new HashMap<>();

    /**
     * Timings for the method-body-specific propagation step, aggregated per concrete EChange
     * subtype for the same reason as {@link #changePropagationTimePerChangeType} - kept
     * separate from it because this step only runs conditionally (when the change is a
     * method-body change), unlike the unconditional base propagation.
     * See {@link #recordBodyTransformationTimeForChangeType}.
     */
    private Map<String, AggregatedTiming> bodyTransformationTimePerChangeType = new HashMap<>();

    public long getChangePropagationTime() {
        return changePropagationTime;
    }

    public void setChangePropagationTime(long changePropagationTime) {
        this.changePropagationTime = changePropagationTime;
    }

    public long getInstrumentationTime() {
        return instrumentationTime;
    }

    public void setInstrumentationTime(long instrumentationTime) {
        this.instrumentationTime = instrumentationTime;
    }

    public long getOverallTime() {
        return overallTime;
    }

    public void setOverallTime(long overallTime) {
        this.overallTime = overallTime;
    }

    public Map<String, AggregatedTiming> getFineGrainedTimings() {
        return fineGrainedTimings;
    }

    public void setFineGrainedTimings(Map<String, AggregatedTiming> fineGrainedTimings) {
        this.fineGrainedTimings = fineGrainedTimings;
    }

    /**
     * Folds one more measurement into the aggregate for the given label, creating a fresh
     * aggregate the first time a label is seen. A label can be recorded against more than
     * once per run (e.g. once per file compared).
     *
     * @param label a descriptive name for the measurement point (e.g. "javaModelComparison").
     * @param duration the duration of the call just measured.
     */
    public void recordFineGrainedTiming(String label, long duration) {
        fineGrainedTimings.computeIfAbsent(label, key -> new AggregatedTiming()).record(duration);
    }

    public Map<String, AggregatedTiming> getChangePropagationTimePerChangeType() {
        return changePropagationTimePerChangeType;
    }

    public void setChangePropagationTimePerChangeType(
            Map<String, AggregatedTiming> changePropagationTimePerChangeType) {
        this.changePropagationTimePerChangeType = changePropagationTimePerChangeType;
    }

    /**
     * Folds one more change-propagation measurement into the aggregate for the given change
     * type, creating a fresh aggregate the first time a type is seen.
     *
     * @param changeType a label identifying the concrete EChange subtype propagated (e.g.
     *            {@code change.getClass().getSimpleName()}).
     * @param duration the duration of the call just measured.
     */
    public void recordChangePropagationTimeForChangeType(String changeType, long duration) {
        changePropagationTimePerChangeType.computeIfAbsent(changeType, key -> new AggregatedTiming())
            .record(duration);
    }

    public Map<String, AggregatedTiming> getBodyTransformationTimePerChangeType() {
        return bodyTransformationTimePerChangeType;
    }

    public void setBodyTransformationTimePerChangeType(
            Map<String, AggregatedTiming> bodyTransformationTimePerChangeType) {
        this.bodyTransformationTimePerChangeType = bodyTransformationTimePerChangeType;
    }

    /**
     * Folds one more measurement into the aggregate for the given change type, for the
     * method-body-specific propagation step - see {@link #bodyTransformationTimePerChangeType}.
     *
     * @param changeType a label identifying the concrete EChange subtype propagated.
     * @param duration the duration of the call just measured.
     */
    public void recordBodyTransformationTimeForChangeType(String changeType, long duration) {
        bodyTransformationTimePerChangeType.computeIfAbsent(changeType, key -> new AggregatedTiming())
            .record(duration);
    }
}
