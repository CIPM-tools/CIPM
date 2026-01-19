package cipm.consistency.cpr.measurementshelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;

/**
 * Window sliding implementation of measurements helper.
 *
 * <p>This implementation maintains a sliding window of measurements and calculates
 * the average of all measurements within the window. The window slides as new
 * measurements arrive, ensuring that only recent measurements within the window
 * are used for analysis.
 * </p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Configurable window size in milliseconds (default: 5000ms)</li>
 *   <li>Configurable trigger time threshold (default: 1000ms)</li>
 *   <li>Average-based analysis over all measurements in the window</li>
 *   <li>Per-action tracking of measurements for fine-grained analysis</li>
 * </ul>
 * </p>
 *
 * <p>Properties:
 * <ul>
 *   <li>{@code windowSize} - The size of the sliding window in milliseconds</li>
 *   <li>{@code triggerTime} - The time threshold for triggering CPR updates</li>
 * </ul>
 * </p>
 */
public class WindowSlidingMeasurementsHelper implements IMeasurementsHelper {

    /** Default window size in milliseconds (5 seconds). */
    public static final long DEFAULT_WINDOW_SIZE = 5000L;

    /** Default trigger time in milliseconds (1 second). */
    public static final long DEFAULT_TRIGGER_TIME = 1000L;

    // ===============================
    // Properties
    // ===============================

    /** The size of the sliding window in milliseconds. */
    private long windowSize = DEFAULT_WINDOW_SIZE;

    /** The time threshold for triggering CPR updates in milliseconds. */
    private long triggerTime = DEFAULT_TRIGGER_TIME;

    // ===============================
    // State
    // ===============================

    private final Map<String, Long> lastUpdateTimes = new HashMap<>();
    private final Map<String, LinkedList<TimestampedValue>> slidingWindows = new HashMap<>();

    // ===============================
    // Constructors
    // ===============================

    /**
     * Creates a new WindowSlidingMeasurementsHelper with default settings.
     * Default window size: 5000ms, default trigger time: 1000ms.
     */
    public WindowSlidingMeasurementsHelper() {
        // Use defaults
    }

    /**
     * Creates a new WindowSlidingMeasurementsHelper with custom settings.
     *
     * @param windowSizeMs the window size in milliseconds
     * @param triggerTimeMs the trigger time threshold in milliseconds
     */
    public WindowSlidingMeasurementsHelper(long windowSizeMs, long triggerTimeMs) {
        this.windowSize = windowSizeMs;
        this.triggerTime = triggerTimeMs;
    }

    // ===============================
    // Properties Configuration
    // ===============================

    /**
     * Sets the sliding window size.
     *
     * @param windowSizeMs the window size in milliseconds
     */
    public void setWindowSize(long windowSizeMs) {
        this.windowSize = windowSizeMs;
    }

    /**
     * Gets the sliding window size.
     *
     * @return the window size in milliseconds
     */
    public long getWindowSize() {
        return windowSize;
    }

    @Override
    public void setTriggerTime(long triggerTimeMs) {
        this.triggerTime = triggerTimeMs;
    }

    @Override
    public long getTriggerTime() {
        return triggerTime;
    }

    // ===============================
    // Trigger Functions
    // ===============================

    @Override
    public boolean trigger(InternalActionRecord actionRecord) {
        long currentTime = System.currentTimeMillis();
        long recordExitTime = actionRecord.getExitTime();
        return (currentTime - recordExitTime) >= triggerTime;
    }

    @Override
    public long getLastUpdateTime(String deltaSourceType) {
        return lastUpdateTimes.getOrDefault(deltaSourceType, 0L);
    }

    @Override
    public void setLastUpdateTime(String deltaSourceType, long time) {
        lastUpdateTimes.put(deltaSourceType, time);
    }

    // ===============================
    // Filter Functions
    // ===============================

    @Override
    public <T extends MeasurementRecord> List<T> filterElements(
            Measurements sourceModel, Class<T> recordType, long windowMs) {
        long currentTime = System.currentTimeMillis();

        return sourceModel.getRepositories().stream()
                .flatMap(repo -> repo.getBlocks().stream())
                .flatMap(block -> block.getRecords().stream())
                .filter(recordType::isInstance)
                .map(recordType::cast)
                .filter(record -> (currentTime - getTimestamp(record)) <= windowMs)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends MeasurementRecord> List<T> filterElements(
            MeasurementsBlock block, Class<T> recordType, long windowMs) {
        long currentTime = System.currentTimeMillis();

        return block.getRecords().stream()
                .filter(recordType::isInstance)
                .map(recordType::cast)
                .filter(record -> (currentTime - getTimestamp(record)) <= windowMs)
                .collect(Collectors.toList());
    }

    /**
     * Gets the timestamp from a measurement record.
     *
     * @param record the measurement record
     * @return the timestamp (exit time for InternalActionRecord, 0 otherwise)
     */
    private long getTimestamp(MeasurementRecord record) {
        if (record instanceof InternalActionRecord) {
            return ((InternalActionRecord) record).getExitTime();
        }
        return 0L;
    }

    // ===============================
    // Analysis Functions
    // ===============================

    @Override
    public double analysis(List<? extends MeasurementRecord> selectedMeasurements) {
        return estimateParameter(selectedMeasurements);
    }

    /**
     * Estimates a parameter by calculating the average of all measurements
     * in the provided list (which should be filtered by the window size).
     *
     * <p>This method calculates the arithmetic mean of response times
     * (exitTime - entryTime) for all InternalActionRecords in the list.</p>
     *
     * @param measurements the list of measurements (should be within the window)
     * @return the average response time, or 0.0 if no measurements
     */
    @Override
    public double estimateParameter(List<? extends MeasurementRecord> measurements) {
        if (measurements.isEmpty()) {
            return 0.0;
        }

        // Calculate average of all response times in the window
        return measurements.stream()
                .filter(InternalActionRecord.class::isInstance)
                .map(InternalActionRecord.class::cast)
                .mapToLong(r -> r.getExitTime() - r.getEntryTime())
                .average()
                .orElse(0.0);
    }

    // ===============================
    // Sliding Window Operations
    // ===============================

    /**
     * Adds a value to the sliding window for a specific action.
     *
     * @param actionId the action identifier
     * @param value the value to add
     * @param timestamp the timestamp of the measurement
     */
    public void addToSlidingWindow(String actionId, double value, long timestamp) {
        LinkedList<TimestampedValue> window = slidingWindows.computeIfAbsent(
                actionId, k -> new LinkedList<>());

        // Remove expired entries outside the window
        long cutoff = timestamp - windowSize;
        while (!window.isEmpty() && window.peekFirst().timestamp < cutoff) {
            window.pollFirst();
        }

        // Add new entry
        window.addLast(new TimestampedValue(timestamp, value));
    }

    /**
     * Gets the average of all measurements in the sliding window for an action.
     *
     * <p>This is the main analysis method that returns the average of all
     * measurements within the previous window size.</p>
     *
     * @param actionId the action identifier
     * @return the average of all measurements in the window, or 0 if no data
     */
    public double getWindowAverage(String actionId) {
        LinkedList<TimestampedValue> window = slidingWindows.get(actionId);
        if (window == null || window.isEmpty()) {
            return 0.0;
        }

        // Clean up expired entries
        long cutoff = System.currentTimeMillis() - windowSize;
        while (!window.isEmpty() && window.peekFirst().timestamp < cutoff) {
            window.pollFirst();
        }

        if (window.isEmpty()) {
            return 0.0;
        }

        // Calculate average of all values in the window
        return window.stream()
                .mapToDouble(tv -> tv.value)
                .average()
                .orElse(0.0);
    }

    /**
     * Gets the count of measurements in the sliding window for an action.
     *
     * @param actionId the action identifier
     * @return the number of measurements in the window
     */
    public int getWindowCount(String actionId) {
        LinkedList<TimestampedValue> window = slidingWindows.get(actionId);
        if (window == null) {
            return 0;
        }

        // Clean up expired entries
        long cutoff = System.currentTimeMillis() - windowSize;
        while (!window.isEmpty() && window.peekFirst().timestamp < cutoff) {
            window.pollFirst();
        }

        return window.size();
    }

    /**
     * Gets all values in the sliding window for an action.
     *
     * @param actionId the action identifier
     * @return list of all values currently in the window
     */
    public List<Double> getWindowValues(String actionId) {
        LinkedList<TimestampedValue> window = slidingWindows.get(actionId);
        if (window == null) {
            return new ArrayList<>();
        }

        // Clean up expired entries
        long cutoff = System.currentTimeMillis() - windowSize;
        while (!window.isEmpty() && window.peekFirst().timestamp < cutoff) {
            window.pollFirst();
        }

        return window.stream()
                .map(tv -> tv.value)
                .collect(Collectors.toList());
    }

    /**
     * Clears all data from the sliding window for a specific action.
     *
     * @param actionId the action identifier
     */
    public void clearWindow(String actionId) {
        slidingWindows.remove(actionId);
    }

    /**
     * Clears all sliding window data.
     */
    public void clearAllWindows() {
        slidingWindows.clear();
    }

    // ===============================
    // Single Record Analysis
    // ===============================

    /**
     * Analyzes the internal action record and returns the average response time
     * of all measurements in the sliding window for this action.
     *
     * <p>This method:
     * <ol>
     *   <li>Calculates the response time for the current record</li>
     *   <li>Adds it to the sliding window for this action</li>
     *   <li>Returns the average of all measurements in the window</li>
     * </ol>
     * </p>
     *
     * @param actionRecord the internal action record
     * @return the average response time of all measurements in the window (in ms)
     */
    @Override
    public long analyse(InternalActionRecord actionRecord) {
        long responseTime = actionRecord.getExitTime() - actionRecord.getEntryTime();

        // Add to sliding window for this action
        String actionId = actionRecord.getInternalActionID();
        if (actionId != null) {
            addToSlidingWindow(actionId, responseTime, actionRecord.getExitTime());
            // Return the average of all measurements in the window
            return Math.round(getWindowAverage(actionId));
        }

        return responseTime;
    }

    @Override
    public long analyse(LoopActionRecord loopRecord) {
        return loopRecord.getLoopIterationCount();
    }

    @Override
    public String analyse(BranchActionRecord branchRecord) {
        return branchRecord.getExecutedBranchID();
    }

    // ===============================
    // Helper Classes
    // ===============================

    /**
     * A timestamped value for the sliding window.
     */
    private static class TimestampedValue {
        final long timestamp;
        final double value;

        TimestampedValue(long timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }
}
