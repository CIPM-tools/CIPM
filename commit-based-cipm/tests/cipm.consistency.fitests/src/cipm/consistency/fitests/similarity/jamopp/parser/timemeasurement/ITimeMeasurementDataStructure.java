package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * An interface for classes that encapsulate the means to store time
 * measurements within {@link TimeMeasurementEntry} instances, which were taken
 * via {@link ITimeMeasuringStrategy}.
 * 
 * @author Alp Torac Genc
 */
public interface ITimeMeasurementDataStructure {
	/**
	 * @return {@link ITimeMeasuringStrategy#getTimeMeasurerDescription()}
	 */
	public String getTimeMeasurerDescription();

	/**
	 * @param description {@link ITimeMeasuringStrategy#getTimeMeasurerDescription()}
	 */
	public void setTimeMeasurerDescription(String description);

	/**
	 * @return {@link ITimeMeasuringStrategy#getTimeUnit()}
	 */
	public TimeUnit getTimeUnit();

	/**
	 * @param unit {@link ITimeMeasuringStrategy#getTimeUnit()}
	 */
	public void setTimeUnit(TimeUnit unit);

	/**
	 * Use {@link #timeMeasuringStarted(LocalDateTime)} to set the start time, reset
	 * it via {@link #reset()}.
	 * 
	 * @return {@link ITimeMeasuringStrategy#getStartTime()}
	 */
	public LocalDateTime getStartTime();

	/**
	 * Use {@link #timeMeasuringFinished(LocalDateTime)} to set the end time, reset
	 * it via {@link #reset()}.
	 * 
	 * @return {@link ITimeMeasuringStrategy#getEndTime()}
	 */
	public LocalDateTime getEndTime();

	/**
	 * Signals the data structure that time measuring started. Calling this method
	 * multiple times before calling {@link #timeMeasuringFinished(LocalDateTime)}
	 * or {@link #reset()} should have no effect.
	 */
	public void timeMeasuringStarted(LocalDateTime startTime);

	/**
	 * Signals the data structure that time measuring is over. This method should be
	 * called after {@link #timeMeasuringStarted(LocalDateTime)} but before
	 * {@link #reset()} for it to have any effect. In any other case, this method
	 * should do nothing.
	 */
	public void timeMeasuringFinished(LocalDateTime endTime);

	/**
	 * Resets all current information within this instance.
	 */
	public void reset();

	/**
	 * Signals the data structure that it has been saved.<br>
	 * <br>
	 * If time measuring is to start anew and the current time measurements should
	 * be reset, an additional call to {@link #reset()} is necessary.
	 */
	public void dataStructureSaved();

	/**
	 * Adds the given time measurement entry (as {@link TimeMeasurementEntry}
	 * instance) to this data structure.
	 */
	public void addTimeMeasurement(TimeMeasurementEntry entry);

	/**
	 * The underlying collection, which stores all entries, should not be returned
	 * as is, since that may result in unforeseen modifications from outside.
	 * However, the entries within are allowed to be the original ones, so that no
	 * unnecessary copies are made.
	 * 
	 * @return All time measurement entries added to this instance.
	 */
	public Collection<TimeMeasurementEntry> getTimeMeasurementEntries();
}
