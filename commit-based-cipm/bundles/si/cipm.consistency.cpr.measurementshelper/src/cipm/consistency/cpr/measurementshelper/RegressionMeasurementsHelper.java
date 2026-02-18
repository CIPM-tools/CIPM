package cipm.consistency.cpr.measurementshelper;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;

/**
 * Exponential Moving Average (EMA) regression-based implementation of measurements helper.
 *
 * <p>This implementation uses exponential smoothing to blend the previous resource
 * demand estimate with the new sliding window average:
 * <pre>
 *   estimate = alpha * windowAvg + (1 - alpha) * previousEstimate
 * </pre>
 * where {@code alpha} (0.0–1.0) controls responsiveness: higher values give more
 * weight to recent data, lower values produce smoother/more stable estimates.
 * </p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Delegates sliding window operations to {@link WindowSlidingMeasurementsHelper}</li>
 *   <li>Exponential smoothing for gradual adaptation to workload changes</li>
 *   <li>Configurable smoothing factor (alpha)</li>
 *   <li>Per-action tracking of smoothed estimates and observation counts</li>
 * </ul>
 * </p>
 */
public class RegressionMeasurementsHelper implements IMeasurementsHelper {

    private static final Logger LOGGER = Logger.getLogger(RegressionMeasurementsHelper.class.getName());

    /** Default smoothing factor (0.3 = moderate responsiveness). */
    public static final double DEFAULT_ALPHA = 0.3;

    // ===============================
    // Components
    // ===============================

    /** Delegate for sliding window operations. */
    private final WindowSlidingMeasurementsHelper windowHelper;

    // ===============================
    // Regression State
    // ===============================

    /** Smoothing factor (0.0–1.0). Higher = more weight on recent data. */
    private final double alpha;

    /** Per-action smoothed estimate (in nanoseconds). */
    private final Map<String, Double> actionEstimates = new HashMap<>();

    /** Per-action observation count. */
    private final Map<String, Integer> actionObservations = new HashMap<>();

    // ===============================
    // Constructors
    // ===============================

    /**
     * Creates a new RegressionMeasurementsHelper with default settings.
     * Default window size: 5000ms, trigger time: 1000ms, alpha: 0.3.
     */
    public RegressionMeasurementsHelper() {
        this.windowHelper = new WindowSlidingMeasurementsHelper();
        this.alpha = DEFAULT_ALPHA;
    }

    /**
     * Creates a new RegressionMeasurementsHelper with custom settings.
     *
     * @param windowSizeMs the sliding window size in milliseconds
     * @param triggerTimeMs the trigger time threshold in milliseconds
     * @param alpha the smoothing factor (0.0–1.0)
     */
    public RegressionMeasurementsHelper(long windowSizeMs, long triggerTimeMs, double alpha) {
        this.windowHelper = new WindowSlidingMeasurementsHelper(windowSizeMs, triggerTimeMs);
        if (alpha < 0.0 || alpha > 1.0) {
            throw new IllegalArgumentException("Alpha must be between 0.0 and 1.0, got: " + alpha);
        }
        this.alpha = alpha;
    }

    // ===============================
    // Configuration
    // ===============================

    /**
     * Gets the smoothing factor.
     *
     * @return the alpha value (0.0–1.0)
     */
    public double getAlpha() {
        return alpha;
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
    // Single Record Analysis with Regression
    // ===============================

    @Override
    public void trackRecord(InternalActionRecord actionRecord) {
        windowHelper.trackRecord(actionRecord);
    }

    /**
     * Analyzes the internal action record using exponential smoothing.
     *
     * <p>This method:
     * <ol>
     *   <li>Adds the response time to the sliding window</li>
     *   <li>Computes the current window average</li>
     *   <li>Blends with previous estimate: {@code estimate = alpha * windowAvg + (1-alpha) * prev}</li>
     *   <li>Returns the smoothed estimate</li>
     * </ol>
     * </p>
     *
     * @param actionRecord the internal action record
     * @return the smoothed resource demand estimate (in nanoseconds)
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

            // Apply exponential smoothing
            double previousEstimate = actionEstimates.getOrDefault(actionId, Double.NaN);
            double newEstimate;

            if (Double.isNaN(previousEstimate)) {
                // First observation: use window average directly
                newEstimate = windowAvg;
            } else {
                // EMA: blend new window average with previous estimate
                newEstimate = alpha * windowAvg + (1.0 - alpha) * previousEstimate;
            }

            // Store updated estimate
            actionEstimates.put(actionId, newEstimate);
            int obsCount = actionObservations.merge(actionId, 1, Integer::sum);

            LOGGER.info(String.format(Locale.US,
                    "Regression updated for action '%s': windowAvg=%.2f ns, prev=%.2f ns, "
                    + "new=%.2f ns (alpha=%.2f, obs=%d)",
                    actionId, windowAvg,
                    Double.isNaN(previousEstimate) ? 0.0 : previousEstimate,
                    newEstimate, alpha, obsCount));

            return Math.round(newEstimate);
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
    // Resource Demand Specification
    // ===============================

    /**
     * Returns the smoothed estimate as a resource demand specification string.
     *
     * @param actionId the action identifier
     * @return the smoothed estimate as a string, or null if no data
     */
    @Override
    public String getResourceDemandSpecification(String actionId) {
        Double estimate = actionEstimates.get(actionId);
        if (estimate == null || Double.isNaN(estimate)) {
            return null;
        }
        return String.valueOf(Math.round(estimate));
    }

    // ===============================
    // Query Methods
    // ===============================

    /**
     * Gets the current smoothed estimate for an action.
     *
     * @param actionId the action identifier
     * @return the smoothed estimate in nanoseconds, or NaN if no data
     */
    public double getEstimate(String actionId) {
        return actionEstimates.getOrDefault(actionId, Double.NaN);
    }

    /**
     * Gets the number of observations (trigger events) for an action.
     *
     * @param actionId the action identifier
     * @return the observation count
     */
    public int getObservationCount(String actionId) {
        return actionObservations.getOrDefault(actionId, 0);
    }

    /**
     * Gets all action IDs that have estimates.
     *
     * @return map of action IDs to their current estimates (in nanoseconds)
     */
    public Map<String, Double> getAllEstimates() {
        return new HashMap<>(actionEstimates);
    }

    /**
     * Clears regression state for a specific action.
     *
     * @param actionId the action identifier
     */
    public void clearEstimate(String actionId) {
        actionEstimates.remove(actionId);
        actionObservations.remove(actionId);
    }

    /**
     * Clears all regression state.
     */
    public void clearAllEstimates() {
        actionEstimates.clear();
        actionObservations.clear();
    }
}
