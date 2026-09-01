package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.time.LocalDateTime;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.StopWatch;

/**
 * A class for taking time measurements using
 * {@link org.apache.commons.lang.time.StopWatch}, and saving them. <br>
 * <br>
 * The time measurements taken here are contain no duplications; i.e. if another
 * time measurement is taken while a previous time measurement continues (for
 * instance, while a method's run time is measured, a new time measurement
 * starts for one of its inner method calls), they will be separate.
 * 
 * @author Alp Torac Genc
 */
public class StopwatchStrategy implements ITimeMeasuringStrategy {

	/**
	 * {@link #getStartTime()}
	 */
	private LocalDateTime startTime;
	/**
	 * {@link #getEndTime()}
	 */
	private LocalDateTime endTime;

	/**
	 * A stack that contains all StopWatch instances that are used during
	 * performance measurement. The reason to use a stack here is, there are cases,
	 * where methods make calls to other methods and their run times overlap. By
	 * suspending the outer method's StopWatch and pushing a new StopWatch onto the
	 * stack, the inner methods' run times can be measured accurately. Then the new
	 * StopWatch can be popped and stopped to get the run time of the inner method.
	 * Finally, the outer method's StopWatch can be resumed to resume the time
	 * measurement.<br>
	 * <br>
	 * All StopWatch are accompanied by an entry, along which they were created.
	 * This way, the StopWatch instances and their corresponding entries are easier
	 * to access.
	 */
	private final Stack<StopWatchEntryPair> watchEntryPairs = new Stack<StopWatchEntryPair>();

	/**
	 * @implSpec If another time measurement is ongoing (i.e. if this method is
	 *           called multiple times without {@link #stopTimeMeasurement()} calls
	 *           in between), the previous time measurement is paused until the new
	 *           time measurement is stopped via {@link #stopTimeMeasurement()}.
	 *           <br>
	 *           <br>
	 *           This method is to be seen as the opening bracket for the closing
	 *           bracket {@link #stopTimeMeasurement()} such that the time elapsed
	 *           while executing the lines between this method call and that method
	 *           call is the time measurement. Not using them similar to brackets
	 *           will result in problems.
	 */
	@Override
	public void startTimeMeasurement(ParserTestTimeMeasurementKey key, ITimeMeasurementTag tag) {
		/*
		 * Suspends the potential outer method's Stopwatch, so that time measurements do
		 * not overlap
		 */
		if (!watchEntryPairs.isEmpty()) {
			var outerMethodPair = watchEntryPairs.peek();
			outerMethodPair.getWatch().suspend();
		}

		var currentMethodWatch = new StopWatch();

		var entry = new TimeMeasurementEntry(key, tag);

		watchEntryPairs.push(new StopWatchEntryPair(currentMethodWatch, entry));
		currentMethodWatch.start();
	}

	/**
	 * @implSpec If the most recent time measurement paused a previous time
	 *           measurement, it is resumed. <br>
	 *           <br>
	 *           This method is to be seen as the closing bracket for the opening
	 *           bracket {@link #startTimeMeasurement(String, ITimeMeasurementTag)},
	 *           such that the time elapsed while executing the lines between that
	 *           method call and this method call is the time measurement. Not using
	 *           them similar to brackets will result in inaccurate measurements.
	 */
	@Override
	public TimeMeasurementEntry stopTimeMeasurement() {
		var currentMethodPair = watchEntryPairs.pop();
		var watch = currentMethodPair.getWatch();
		var entry = currentMethodPair.getEntry();

		watch.stop();
		entry.setTimeElapsed(watch.getTime());

		/*
		 * Resumes the potential outer method's Stopwatch, which was previously
		 * suspended
		 */
		if (!watchEntryPairs.isEmpty()) {
			watchEntryPairs.peek().getWatch().resume();
		}
		return entry;
	}

	@Override
	public void timeMeasuringFinished() {
		if (this.hasTimeMeasurementStarted() && !this.hasTimeMeasurementFinished()) {
			this.endTime = LocalDateTime.now();
		}
	}

	@Override
	public void timeMeasuringStarted() {
		if (!this.hasTimeMeasurementStarted()) {
			this.startTime = LocalDateTime.now();

			// Reset the end time, since time measuring just started
			this.endTime = null;
		}
	}

	/**
	 * Time measurement is assumed to have started, if
	 * {@link #timeMeasuringStarted()} has been called but
	 * {@link #timeMeasuringFinished()} is not called yet.
	 */
	@Override
	public boolean hasTimeMeasurementStarted() {
		return this.getStartTime() != null && this.getEndTime() == null;
	}

	/**
	 * Time measurement is assumed to have finished, if both
	 * {@link #timeMeasuringStarted()} and {@link #timeMeasuringFinished()} have
	 * been called.
	 */
	@Override
	public boolean hasTimeMeasurementFinished() {
		return this.getStartTime() != null && this.getEndTime() != null;
	}

	/**
	 * Since {@link StopWatch} is used, the used time unit is milliseconds (ms).
	 * 
	 * @return {@link TimeUnit#MILLISECONDS}
	 */
	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}

	@Override
	public String getTimeMeasurerDescription() {
		return StopWatch.class.getName();
	}

	@Override
	public LocalDateTime getStartTime() {
		return this.startTime;
	}

	@Override
	public LocalDateTime getEndTime() {
		return this.endTime;
	}

	private class StopWatchEntryPair {
		private final StopWatch watch;
		private final TimeMeasurementEntry entry;

		private StopWatchEntryPair(StopWatch watch, TimeMeasurementEntry entry) {
			this.watch = watch;
			this.entry = entry;
		}

		private StopWatch getWatch() {
			return watch;
		}

		private TimeMeasurementEntry getEntry() {
			return entry;
		}
	}
}
