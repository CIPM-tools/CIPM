package cipm.consistency.cpr.measurementshelper;

import java.util.List;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;

/**
 * Static facade for measurements to PCM change propagation.
 *
 * <p>This class provides static methods that delegate to a configurable
 * {@link IMeasurementsHelper} implementation. By default, it uses
 * {@link WindowSlidingMeasurementsHelper} for window-based analysis.
 * </p>
 *
 * <p>Usage in reactions files:
 * <pre>
 * cipm.consistency.cpr.measurementshelper.MeasurementsHelper.trigger(newValue)
 * cipm.consistency.cpr.measurementshelper.MeasurementsHelper.analyse(actionRecord)
 * </pre>
 * </p>
 */
public final class MeasurementsHelper {

    /** The delegate implementation. */
    private static IMeasurementsHelper delegate = new WindowSlidingMeasurementsHelper();

    private MeasurementsHelper() {
        // Utility class
    }

    // ===============================
    // Configuration
    // ===============================

    /**
     * Sets the helper implementation to use.
     *
     * @param helper the helper implementation
     */
    public static void setImplementation(IMeasurementsHelper helper) {
        if (helper == null) {
            throw new IllegalArgumentException("Helper implementation cannot be null");
        }
        delegate = helper;
    }

    /**
     * Gets the current helper implementation.
     *
     * @return the helper implementation
     */
    public static IMeasurementsHelper getImplementation() {
        return delegate;
    }

    /**
     * Resets to the default implementation (WindowSlidingMeasurementsHelper).
     */
    public static void resetToDefault() {
        delegate = new WindowSlidingMeasurementsHelper();
    }

    // ===============================
    // Trigger Functions
    // ===============================

    /**
     * Determines whether the CPR should be triggered based on the internal action record.
     *
     * @param actionRecord the internal action record
     * @return true if the trigger condition is met
     */
    public static boolean trigger(InternalActionRecord actionRecord) {
        return delegate.trigger(actionRecord);
    }

    /**
     * Gets the last update time for a given delta source type.
     *
     * @param deltaSourceType the delta source type identifier
     * @return the last update time, or 0 if not found
     */
    public static long getLastUpdateTime(String deltaSourceType) {
        return delegate.getLastUpdateTime(deltaSourceType);
    }

    /**
     * Sets the last update time for a given delta source type.
     *
     * @param deltaSourceType the delta source type identifier
     * @param time the update time
     */
    public static void setLastUpdateTime(String deltaSourceType, long time) {
        delegate.setLastUpdateTime(deltaSourceType, time);
    }

    /**
     * Sets the trigger time threshold.
     *
     * @param triggerTimeMs the trigger time in milliseconds
     */
    public static void setTriggerTime(long triggerTimeMs) {
        delegate.setTriggerTime(triggerTimeMs);
    }

    /**
     * Gets the current trigger time threshold.
     *
     * @return the trigger time in milliseconds
     */
    public static long getTriggerTime() {
        return delegate.getTriggerTime();
    }

    // ===============================
    // Filter Functions
    // ===============================

    /**
     * Filters elements from source model by type and time window.
     *
     * @param <T> the measurement record type
     * @param sourceModel the measurements model
     * @param recordType the class of records to filter
     * @param windowMs the time window in milliseconds
     * @return filtered list of records within the time window
     */
    public static <T extends MeasurementRecord> List<T> filterElements(
            Measurements sourceModel, Class<T> recordType, long windowMs) {
        return delegate.filterElements(sourceModel, recordType, windowMs);
    }

    /**
     * Filters elements from a specific measurements block by type and time window.
     *
     * @param <T> the measurement record type
     * @param block the measurements block
     * @param recordType the class of records to filter
     * @param windowMs the time window in milliseconds
     * @return filtered list of records within the time window
     */
    public static <T extends MeasurementRecord> List<T> filterElements(
            MeasurementsBlock block, Class<T> recordType, long windowMs) {
        return delegate.filterElements(block, recordType, windowMs);
    }

    // ===============================
    // Analysis Functions
    // ===============================

    /**
     * Analyzes the selected measurements to estimate a parameter.
     *
     * @param selectedMeasurements the filtered measurements
     * @return the estimated parameter value
     */
    public static double analysis(List<? extends MeasurementRecord> selectedMeasurements) {
        return delegate.analysis(selectedMeasurements);
    }

    /**
     * Estimates a parameter from the selected measurements (e.g., mean response time).
     *
     * @param measurements the list of measurements
     * @return the estimated parameter value
     */
    public static double estimateParameter(List<? extends MeasurementRecord> measurements) {
        return delegate.estimateParameter(measurements);
    }

    // ===============================
    // Single Record Analysis
    // ===============================

    /**
     * Analyzes the internal action record to calculate the resource demand.
     *
     * @param actionRecord the internal action record
     * @return the calculated resource demand (response time in ms)
     */
    public static long analyse(InternalActionRecord actionRecord) {
        return delegate.analyse(actionRecord);
    }

    /**
     * Analyzes the loop action record to get the iteration count.
     *
     * @param loopRecord the loop action record
     * @return the loop iteration count
     */
    public static long analyse(LoopActionRecord loopRecord) {
        return delegate.analyse(loopRecord);
    }

    /**
     * Analyzes the branch action record to get the executed branch ID.
     *
     * @param branchRecord the branch action record
     * @return the executed branch ID
     */
    public static String analyse(BranchActionRecord branchRecord) {
        return delegate.analyse(branchRecord);
    }
}
