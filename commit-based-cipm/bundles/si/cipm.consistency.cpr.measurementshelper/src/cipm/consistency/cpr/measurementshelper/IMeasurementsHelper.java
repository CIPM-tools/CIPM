package cipm.consistency.cpr.measurementshelper;

import java.util.List;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;

/**
 * Interface for measurements to PCM change propagation helper.
 *
 * <p>Provides trigger logic, filtering, and analysis functions for measurement records.
 * Implementations can use different strategies (e.g., window sliding, batch processing).
 * </p>
 */
public interface IMeasurementsHelper {

    // ===============================
    // Trigger Functions
    // ===============================

    /**
     * Determines whether the CPR should be triggered based on the internal action record.
     *
     * @param actionRecord the internal action record
     * @return true if the trigger condition is met
     */
    boolean trigger(InternalActionRecord actionRecord);

    /**
     * Gets the last update time for a given delta source type.
     *
     * @param deltaSourceType the delta source type identifier
     * @return the last update time, or 0 if not found
     */
    long getLastUpdateTime(String deltaSourceType);

    /**
     * Sets the last update time for a given delta source type.
     *
     * @param deltaSourceType the delta source type identifier
     * @param time the update time
     */
    void setLastUpdateTime(String deltaSourceType, long time);

    /**
     * Sets the trigger time threshold.
     *
     * @param triggerTimeMs the trigger time in milliseconds
     */
    void setTriggerTime(long triggerTimeMs);

    /**
     * Gets the current trigger time threshold.
     *
     * @return the trigger time in milliseconds
     */
    long getTriggerTime();

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
    <T extends MeasurementRecord> List<T> filterElements(
            Measurements sourceModel, Class<T> recordType, long windowMs);

    /**
     * Filters elements from a specific measurements block by type and time window.
     *
     * @param <T> the measurement record type
     * @param block the measurements block
     * @param recordType the class of records to filter
     * @param windowMs the time window in milliseconds
     * @return filtered list of records within the time window
     */
    <T extends MeasurementRecord> List<T> filterElements(
            MeasurementsBlock block, Class<T> recordType, long windowMs);

    // ===============================
    // Analysis Functions
    // ===============================

    /**
     * Analyzes the selected measurements to estimate a parameter.
     *
     * @param selectedMeasurements the filtered measurements
     * @return the estimated parameter value
     */
    double analysis(List<? extends MeasurementRecord> selectedMeasurements);

    /**
     * Estimates a parameter from the selected measurements (e.g., mean response time).
     *
     * @param measurements the list of measurements
     * @return the estimated parameter value
     */
    double estimateParameter(List<? extends MeasurementRecord> measurements);

    // ===============================
    // Single Record Analysis
    // ===============================

    /**
     * Analyzes the internal action record to calculate the resource demand.
     *
     * @param actionRecord the internal action record
     * @return the calculated resource demand (response time in ms)
     */
    long analyse(InternalActionRecord actionRecord);

    /**
     * Analyzes the loop action record to get the iteration count.
     *
     * @param loopRecord the loop action record
     * @return the loop iteration count
     */
    long analyse(LoopActionRecord loopRecord);

    /**
     * Analyzes the branch action record to get the executed branch ID.
     *
     * @param branchRecord the branch action record
     * @return the executed branch ID
     */
    String analyse(BranchActionRecord branchRecord);
}
