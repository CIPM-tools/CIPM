package cipm.consistency.cpr.measurementshelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;

/**
 * PMF-based implementation of measurements helper.
 *
 * <p>This implementation builds a discrete random variable using a Probability
 * Mass Function (PMF). Each PMF value represents the average response time
 * observed during a sliding window period. When a trigger fires, the current
 * window average is discretized and added to the PMF.
 * </p>
 *
 * <p>The PMF is maintained per internal action ID and can be retrieved as a
 * PCM Stochastic Expression (StoEx) string using
 * {@link #getResourceDemandSpecification(String)}.
 * </p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Delegates sliding window operations to {@link WindowSlidingMeasurementsHelper}</li>
 *   <li>Builds a PMF from window averages</li>
 *   <li>Configurable discretization granularity</li>
 *   <li>Returns PCM StoEx DoublePMF specification for resource demands</li>
 * </ul>
 * </p>
 */
public class PMFMeasurementsHelper implements IMeasurementsHelper {

    private static final Logger LOGGER = Logger.getLogger(PMFMeasurementsHelper.class.getName());

    /** Default granularity for discretization: 1 millisecond in nanoseconds. */
    public static final long DEFAULT_GRANULARITY_NS = 1_000_000L;

    // ===============================
    // Components
    // ===============================

    /** Delegate for sliding window operations. */
    private final WindowSlidingMeasurementsHelper windowHelper;

    // ===============================
    // PMF State
    // ===============================

    /** Per-action PMF: actionId -> (discretizedValueNs -> observationCount). */
    private final Map<String, Map<Long, Integer>> actionPMFs = new HashMap<>();

    /** Per-action total observation count. */
    private final Map<String, Integer> actionTotals = new HashMap<>();

    /** Granularity for discretizing window averages (in nanoseconds). */
    private long granularityNs;

    // ===============================
    // Constructors
    // ===============================

    /**
     * Creates a new PMFMeasurementsHelper with default settings.
     * Default window size: 5000ms, trigger time: 1000ms, granularity: 1ms.
     */
    public PMFMeasurementsHelper() {
        this.windowHelper = new WindowSlidingMeasurementsHelper();
        this.granularityNs = DEFAULT_GRANULARITY_NS;
    }

    /**
     * Creates a new PMFMeasurementsHelper with custom window and trigger settings.
     *
     * @param windowSizeMs the sliding window size in milliseconds
     * @param triggerTimeMs the trigger time threshold in milliseconds
     */
    public PMFMeasurementsHelper(long windowSizeMs, long triggerTimeMs) {
        this.windowHelper = new WindowSlidingMeasurementsHelper(windowSizeMs, triggerTimeMs);
        this.granularityNs = DEFAULT_GRANULARITY_NS;
    }

    /**
     * Creates a new PMFMeasurementsHelper with custom settings.
     *
     * @param windowSizeMs the sliding window size in milliseconds
     * @param triggerTimeMs the trigger time threshold in milliseconds
     * @param granularityMs the discretization granularity in milliseconds
     */
    public PMFMeasurementsHelper(long windowSizeMs, long triggerTimeMs, long granularityMs) {
        this.windowHelper = new WindowSlidingMeasurementsHelper(windowSizeMs, triggerTimeMs);
        this.granularityNs = granularityMs * 1_000_000L;
    }

    // ===============================
    // Configuration
    // ===============================

    /**
     * Sets the discretization granularity.
     *
     * @param granularityMs the granularity in milliseconds
     */
    public void setGranularity(long granularityMs) {
        this.granularityNs = granularityMs * 1_000_000L;
    }

    /**
     * Gets the discretization granularity.
     *
     * @return the granularity in milliseconds
     */
    public long getGranularity() {
        return granularityNs / 1_000_000L;
    }

    /**
     * Gets the underlying window helper.
     *
     * @return the window sliding measurements helper
     */
    public WindowSlidingMeasurementsHelper getWindowHelper() {
        return windowHelper;
    }

    // ===============================
    // Trigger Functions (delegated)
    // ===============================

    @Override
    public boolean trigger(InternalActionRecord actionRecord) {
        return windowHelper.trigger(actionRecord);
    }

    @Override
    public long getLastUpdateTime(String deltaSourceType) {
        return windowHelper.getLastUpdateTime(deltaSourceType);
    }

    @Override
    public void setLastUpdateTime(String deltaSourceType, long time) {
        windowHelper.setLastUpdateTime(deltaSourceType, time);
    }

    @Override
    public void setTriggerTime(long triggerTimeMs) {
        windowHelper.setTriggerTime(triggerTimeMs);
    }

    @Override
    public long getTriggerTime() {
        return windowHelper.getTriggerTime();
    }

    // ===============================
    // Filter Functions (delegated)
    // ===============================

    @Override
    public <T extends MeasurementRecord> List<T> filterElements(
            Measurements sourceModel, Class<T> recordType, long windowMs) {
        return windowHelper.filterElements(sourceModel, recordType, windowMs);
    }

    @Override
    public <T extends MeasurementRecord> List<T> filterElements(
            MeasurementsBlock block, Class<T> recordType, long windowMs) {
        return windowHelper.filterElements(block, recordType, windowMs);
    }

    // ===============================
    // Analysis Functions (delegated)
    // ===============================

    @Override
    public double analysis(List<? extends MeasurementRecord> selectedMeasurements) {
        return windowHelper.analysis(selectedMeasurements);
    }

    @Override
    public double estimateParameter(List<? extends MeasurementRecord> measurements) {
        return windowHelper.estimateParameter(measurements);
    }

    // ===============================
    // Single Record Analysis with PMF
    // ===============================

    @Override
    public void trackRecord(InternalActionRecord actionRecord) {
        windowHelper.trackRecord(actionRecord);
    }

    /**
     * Analyzes the internal action record, updates the PMF, and returns the
     * expected value of the PMF.
     *
     * <p>This method:
     * <ol>
     *   <li>Adds the response time to the sliding window</li>
     *   <li>Computes the current window average</li>
     *   <li>Discretizes the average and adds it to the PMF</li>
     *   <li>Returns the expected value of the PMF</li>
     * </ol>
     * </p>
     *
     * @param actionRecord the internal action record
     * @return the expected value of the PMF (in nanoseconds)
     */
    @Override
    public long analyse(InternalActionRecord actionRecord) {
        long responseTime = actionRecord.getExitTime() - actionRecord.getEntryTime();
        String actionId = actionRecord.getInternalActionID();

        if (actionId != null) {
            // Add to sliding window
            windowHelper.addToSlidingWindow(actionId, responseTime, actionRecord.getExitTime());

            // Get current window average
            double windowAvg = windowHelper.getWindowAverage(actionId);

            // Discretize and add to PMF
            long discretized = discretize(windowAvg);
            updatePMF(actionId, discretized);

            LOGGER.info(String.format(Locale.US,
                    "PMF updated for action '%s': windowAvg=%.2f ns, discretized=%d ns, PMF entries=%d, total obs=%d",
                    actionId, windowAvg, discretized, getPMFSize(actionId), getPMFTotalObservations(actionId)));

            // Return expected value of PMF
            return Math.round(getExpectedValue(actionId));
        }

        return responseTime;
    }

    @Override
    public long analyse(LoopActionRecord loopRecord) {
        return windowHelper.analyse(loopRecord);
    }

    @Override
    public String analyse(BranchActionRecord branchRecord) {
        return windowHelper.analyse(branchRecord);
    }

    // ===============================
    // PMF Operations
    // ===============================

    /**
     * Discretizes a value according to the configured granularity.
     *
     * @param value the value to discretize (in nanoseconds)
     * @return the discretized value (in nanoseconds)
     */
    private long discretize(double value) {
        if (granularityNs <= 0) {
            return Math.round(value);
        }
        return Math.round(value / granularityNs) * granularityNs;
    }

    /**
     * Updates the PMF for a specific action with a new discretized value.
     *
     * @param actionId the action identifier
     * @param discretizedValue the discretized value to add
     */
    private void updatePMF(String actionId, long discretizedValue) {
        Map<Long, Integer> pmf = actionPMFs.computeIfAbsent(actionId, k -> new LinkedHashMap<>());
        pmf.merge(discretizedValue, 1, Integer::sum);
        actionTotals.merge(actionId, 1, Integer::sum);
    }

    /**
     * Gets the expected value (weighted average) of the PMF for an action.
     *
     * @param actionId the action identifier
     * @return the expected value in nanoseconds, or 0 if no data
     */
    public double getExpectedValue(String actionId) {
        Map<Long, Integer> pmf = actionPMFs.get(actionId);
        if (pmf == null || pmf.isEmpty()) {
            return 0.0;
        }

        int total = actionTotals.getOrDefault(actionId, 0);
        if (total == 0) {
            return 0.0;
        }

        return pmf.entrySet().stream()
                .mapToDouble(e -> (double) e.getKey() * e.getValue() / total)
                .sum();
    }

    /**
     * Gets the number of distinct values in the PMF for an action.
     *
     * @param actionId the action identifier
     * @return the number of distinct PMF values
     */
    public int getPMFSize(String actionId) {
        Map<Long, Integer> pmf = actionPMFs.get(actionId);
        return pmf != null ? pmf.size() : 0;
    }

    /**
     * Gets the total number of observations in the PMF for an action.
     *
     * @param actionId the action identifier
     * @return the total observation count
     */
    public int getPMFTotalObservations(String actionId) {
        return actionTotals.getOrDefault(actionId, 0);
    }

    /**
     * Gets the PMF as a map from discretized values to probabilities for an action.
     *
     * @param actionId the action identifier
     * @return map of value (ns) to probability, or empty map if no data
     */
    public Map<Long, Double> getPMF(String actionId) {
        Map<Long, Integer> pmf = actionPMFs.get(actionId);
        if (pmf == null || pmf.isEmpty()) {
            return new LinkedHashMap<>();
        }

        int total = actionTotals.getOrDefault(actionId, 0);
        if (total == 0) {
            return new LinkedHashMap<>();
        }

        return pmf.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (double) e.getValue() / total,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    /**
     * Returns the PCM Stochastic Expression (StoEx) representation of the PMF
     * for a specific action.
     *
     * <p>Format: {@code DoublePMF[(value1;prob1)(value2;prob2)...]}
     * where values are in nanoseconds (same unit as response times).</p>
     *
     * @param actionId the action identifier
     * @return the PMF as a PCM StoEx string, or null if no data
     */
    @Override
    public String getResourceDemandSpecification(String actionId) {
        Map<Long, Integer> pmf = actionPMFs.get(actionId);
        if (pmf == null || pmf.isEmpty()) {
            return null;
        }

        int total = actionTotals.getOrDefault(actionId, 0);
        if (total == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder("DoublePMF[");
        for (Map.Entry<Long, Integer> entry : pmf.entrySet()) {
            double value = entry.getKey().doubleValue();
            double probability = (double) entry.getValue() / total;
            sb.append(String.format(Locale.US, "(%.1f;%.6f)", value, probability));
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Clears the PMF data for a specific action.
     *
     * @param actionId the action identifier
     */
    public void clearPMF(String actionId) {
        actionPMFs.remove(actionId);
        actionTotals.remove(actionId);
    }

    /**
     * Clears all PMF data.
     */
    public void clearAllPMFs() {
        actionPMFs.clear();
        actionTotals.clear();
    }
}
