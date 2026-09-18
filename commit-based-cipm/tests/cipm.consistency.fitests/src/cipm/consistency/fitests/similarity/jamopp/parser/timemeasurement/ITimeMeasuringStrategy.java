package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * An interface for classes that encapsulate the means to measure time.
 * 
 * @author Alp Torac Genc
 */
public interface ITimeMeasuringStrategy {
	/**
	 * Starts measuring the time using the underlying time measuring strategy for a
	 * certain purpose denoted in the parameters. <br>
	 * <br>
	 * Time measuring should have been started via {@link #timeMeasuringStarted()}
	 * prior to calling this method.
	 * 
	 * @param key The key of the taken time measurement, which describes what the
	 *            time measurement is taken from
	 * @param tag The tag of the time measurement, which is used to group time
	 *            measurements
	 */
	public void startTimeMeasurement(ParserTestTimeMeasurementKey key, ITimeMeasurementTag tag);

	/**
	 * Stops the most recently started time measurement (via
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}). Refer to the
	 * concrete implementor for more information. <br>
	 * <br>
	 * If taking time measurements should end altogether, use
	 * {@link #timeMeasuringFinished()} instead.
	 * 
	 * @return The time measurement entry that is generated for the stopped time
	 *         measurement.
	 */
	public TimeMeasurementEntry stopTimeMeasurement();

	/**
	 * Signals to the concrete implementor that time measuring has started. Calling
	 * this multiple times before calling {@link #timeMeasuringFinished()} should
	 * have no effect past the first call. <br>
	 * <br>
	 * Use {@link #timeMeasuringFinished()} for ending time measuring. Re-call this
	 * method to start anew.
	 */
	public void timeMeasuringStarted();

	/**
	 * Signals to the concrete implementor that time measuring has ended. Calling
	 * this before calling {@link #timeMeasuringStarted()} should have no
	 * effect.<br>
	 * <br>
	 * If taking time measurements is to start anew, call
	 * {@link #timeMeasuringStarted()} before
	 * {@link #startTimeMeasurement(ParserTestTimeMeasurementKey, ITimeMeasurementTag)}.
	 */
	public void timeMeasuringFinished();

	/**
	 * @return Whether time measurements are currently being taken.
	 */
	public boolean hasTimeMeasurementStarted();

	/**
	 * @return Whether time measurement taking is currently over.
	 */
	public boolean hasTimeMeasurementFinished();

	/**
	 * Can be used to acquire information on how time is measured. Check the
	 * concrete implementor for more information on the returned value.
	 * 
	 * @return The description of the underlying tool that is used for taking time
	 *         measurements.
	 */
	public String getTimeMeasurerDescription();

	/**
	 * @return The time unit used while taking measurements.
	 */
	public TimeUnit getTimeUnit();

	/**
	 * @return The time when time measuring has begun via
	 *         {@link #timeMeasuringStarted()}
	 */
	public LocalDateTime getStartTime();

	/**
	 * @return The time when time measuring has ended via
	 *         {@link #timeMeasuringFinished()}
	 */
	public LocalDateTime getEndTime();
}
