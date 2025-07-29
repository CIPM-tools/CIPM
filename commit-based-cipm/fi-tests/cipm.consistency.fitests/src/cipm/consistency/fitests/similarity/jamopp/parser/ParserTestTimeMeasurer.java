package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

import org.apache.commons.lang.time.StopWatch;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * A class for taking time measurements using
 * {@link org.apache.commons.lang.time.StopWatch}, and saving them. <br>
 * <br>
 * The time measurements taken here are contain no duplications; i.e. if another
 * time measurement is taken while a previous time measurement continues (for
 * instance, while a method's run time is measured, a new time measurement
 * starts for one of its inner method calls), they will be separate. <br>
 * <br>
 * Also contains the means to save the time measurements to JSON files using the
 * GSON library. While saving the time measurements, the (non-static) attributes
 * of this class annotated with {@link com.google.gson.annotations.Expose} will
 * be translated to JSON objects and then written to a JSON file. This way, only
 * the desired attributes of this class are saved, as opposed to all of them.
 * 
 * @author Alp Torac Genc
 */
public class ParserTestTimeMeasurer {
	/**
	 * The pattern that will be used to transform a date to a string, which will be
	 * used in the name of the saved measurements file.
	 */
	private final static DateTimeFormatter filenameTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
	/**
	 * The pattern that will be used to transform a date to a string, which will be
	 * written into the saved measurements file.
	 */
	private final static DateTimeFormatter fileContentTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
	/**
	 * The only instance of this class.
	 */
	private static ParserTestTimeMeasurer instance;

	/**
	 * The time, when the first time measurement starts via
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}.
	 */
	private LocalDateTime startTime;

	/**
	 * The time, when taking time measurements ends via
	 * {@link #finishTimeMeasuring()}.
	 */
	private LocalDateTime endTime;

	/**
	 * The string representation of {@link #startTime}, which will be written into
	 * the saved measurements file.
	 */
	@Expose
	private String startTimeString;

	/**
	 * The string representation of {@link #endTime}, which will be written into the
	 * saved measurements file.
	 */
	@Expose
	private String endTimeString;

	/**
	 * The name of the tool that is used for taking time measurements. Only declared
	 * in order to include it to the time measurement file.
	 */
	@Expose
	private final String timeMeasurer = StopWatch.class.getName();
	/**
	 * The time unit in time measurements. Only declared in order to include it to
	 * the time measurement file.
	 */
	@Expose
	private final String timeUnit = "Milliseconds (ms)";

	/**
	 * The sum of all taken time measurements.
	 */
	@Expose
	private Long overallRunTime;

	/**
	 * Contains the sum of time measurements for individual tags in
	 * {@link #overallRunTime}. Only declared in order to include it to the time
	 * measurement file. Should be reset after saving all time measurements, so that
	 * the values here are not duplicated.
	 */
	@Expose
	private final Map<ITimeMeasurementTag, Long> measurementTagSummary = new HashMap<ITimeMeasurementTag, Long>();

	/**
	 * Contains the proportion of time measurements with certain tags in
	 * {@link #overallRunTime} (in percentage). Only declared in order to include it
	 * to the time measurement file. Should be reset after saving all time
	 * measurements, so that the values here are not duplicated.
	 */
	@Expose
	private final Map<ITimeMeasurementTag, String> measurementTagPercentageSummary = new HashMap<ITimeMeasurementTag, String>();

	/**
	 * Contains all time measurements taken.
	 */
	@Expose
	private final Collection<TimeMeasurementEntry> measurements = new ArrayList<TimeMeasurementEntry>();

	/**
	 * A stack that contains all StopWatch instances that are used during
	 * performance measurement. The reason to use a stack here is, there are cases,
	 * where methods make calls to other methods and their run times overlap. By
	 * suspending the outer method's StopWatch and pushing a new StopWatch onto the
	 * stack, the inner methods' run times can be measured accurately. Then the new
	 * StopWatch can be popped and stopped to get the run time of the inner method.
	 * Finally, the outer method's StopWatch can be resumed to resume the time
	 * measurement.
	 */
	private final Stack<StopWatch> watches = new Stack<StopWatch>();

	private ParserTestTimeMeasurer() {
	}

	/**
	 * @return The only instance of this class.
	 */
	public static ParserTestTimeMeasurer getInstance() {
		if (instance == null)
			instance = new ParserTestTimeMeasurer();
		return instance;
	}

	/**
	 * Starts measuring the time for a certain purpose given via the parameters. If
	 * another time measurement is ongoing (i.e. if this method is called multiple
	 * times without {@link #stopTimeMeasurement()} calls in between), the previous
	 * time measurement is paused until the new time measurement is stopped via
	 * {@link #stopTimeMeasurement()}. <br>
	 * <br>
	 * This method is to be seen as the opening bracket for the closing bracket
	 * {@link #stopTimeMeasurement()} such that the time elapsed while executing the
	 * lines between this method call and that method call is the time measurement.
	 * Not using them similar to brackets will result in problems. <br>
	 * <br>
	 * 
	 * @param key The key of the taken time measurement, which describes what the
	 *            time measurement is taken from
	 * @param tag The tag of the time measurement, which is used to group time
	 *            measurements
	 */
	public void startTimeMeasurement(String key, ITimeMeasurementTag tag) {
		if (this.startTime == null) {
			this.startTime = LocalDateTime.now();
			this.startTimeString = fileContentTimeFormatter.format(this.startTime);
		}

		/*
		 * Suspends the potential outer method's Stopwatch, so that time measurements do
		 * not overlap
		 */
		if (!watches.isEmpty()) {
			var outerMethodWatch = watches.peek();
			outerMethodWatch.suspend();
		}

		var currentMethodWatch = new StopWatch();

		this.measurements.add(new TimeMeasurementEntry(currentMethodWatch, key, tag));

		watches.push(currentMethodWatch);
		currentMethodWatch.start();
	}

	/**
	 * Stops the most recently started time measurement (via
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}). If the most
	 * recent time measurement paused a previous time measurement, it is resumed.
	 * <br>
	 * <br>
	 * This method is to be seen as the closing bracket for the opening bracket
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}, such that the
	 * time elapsed while executing the lines between that method call and this
	 * method call is the time measurement. Not using them similar to brackets will
	 * result in inaccurate measurements. <br>
	 * <br>
	 * If taking time measurements should end altogether, use
	 * {@link #finishTimeMeasuring()} instead.
	 */
	public void stopTimeMeasurement() {
		var currentMethodWatch = watches.pop();
		currentMethodWatch.stop();

		/*
		 * Resumes the potential outer method's Stopwatch, which was previously
		 * suspended
		 */
		if (!watches.isEmpty()) {
			watches.peek().resume();
		}
	}

	/**
	 * Signals that taking time measurements is over and the taken time measurements
	 * should be processed. <br>
	 * <br>
	 * If a singular time measurement should be stopped, use
	 * {@link #stopTimeMeasurement()} instead.
	 */
	public void finishTimeMeasuring() {
		if (this.endTime == null) {
			this.endTime = LocalDateTime.now();
			this.endTimeString = fileContentTimeFormatter.format(LocalDateTime.now());
		}

		this.measurements.forEach((m) -> m.computeTime());
	}

	/**
	 * Ends taking time measurements, if not already done, then processes all taken
	 * time measurements. Finally, saves all taken time measurements, as well as
	 * their summaries represented by certain attributes of this instance, at the
	 * given path, in a JSON file.
	 * 
	 * @param measurementsSavePath The absolute path, at which all taken time
	 *                             measurements should be saved.
	 */
	public void save(Path measurementsSavePath) {
		this.finishTimeMeasuring();
		this.summariseTimeMeasurements();

		var filePath = measurementsSavePath.resolve(this.getFullFileName());

		// Ensure that all necessary parent directories exist prior to saving
		measurementsSavePath.toFile().mkdirs();

		var gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		try (BufferedWriter writer = Files.newBufferedWriter(filePath); var gsonWriter = gson.newJsonWriter(writer)) {
			gson.toJson(this, this.getClass(), gsonWriter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					String.format("Could not save the expected similarity results at %s", filePath), e);
		}

		/*
		 * Reset the summary maps after having saved, as the values of their entries
		 * will contain duplicated measurements otherwise.
		 */
		this.clearSummaryMaps();
	}

	/**
	 * @return The name (with file extension) of the measurements file that will be
	 *         saved.
	 */
	private String getFullFileName() {
		var fileExtension = ".json";
		return String.format("%s___%s%s", filenameTimeFormatter.format(this.startTime),
				filenameTimeFormatter.format(this.endTime), fileExtension);
	}

	/**
	 * Cleans all values derived from the taken time measurements, so that no time
	 * measurement is duplicated while computing them.
	 */
	private void clearSummaryMaps() {
		measurementTagSummary.clear();
		measurementTagPercentageSummary.clear();
	}

	/**
	 * Summarises all taken time measurements by grouping them based on the given
	 * key, and then by summing all entries in each group.
	 * 
	 * @param <K>        The type of the key, based on which taken time entries are
	 *                   to be grouped
	 * @param summaryMap A map, which will contain the summary of all taken time
	 *                   measurements based on the foreseen key
	 * @param keyAccess  A function for deriving the key, which will be used to
	 *                   split taken time measurements, from their entries.
	 */
	private <K> void summariseTimeMeasurements(Map<K, Long> summaryMap, Function<TimeMeasurementEntry, K> keyAccess) {
		for (var measurementEntry : this.measurements) {
			var key = keyAccess.apply(measurementEntry);
			var measurement = measurementEntry.getMillis();

			if (summaryMap.containsKey(key)) {
				var summaryEntry = summaryMap.get(key);
				summaryMap.replace(key, summaryEntry + measurement);
			} else {
				summaryMap.put(key, measurement);
			}
		}
	}

	/**
	 * Summarises all taken time measurements and puts the derived values into the
	 * foreseen Map-based attributes of this class.
	 */
	private void summariseTimeMeasurements() {
		this.summariseTimeMeasurements(this.measurementTagSummary, TimeMeasurementEntry::getTag);

		this.overallRunTime = this.measurementTagSummary.values().stream().reduce(Long.valueOf(0), (t1, t2) -> t1 + t2);

		this.measurementTagSummary.entrySet().forEach((e) -> this.measurementTagPercentageSummary.put(e.getKey(),
				String.format("%.2f", (e.getValue().doubleValue() / overallRunTime.doubleValue()) * 100)));
	}

	/**
	 * A class that encapsulates singular time measurements.
	 * 
	 * @author Alp Torac Genc
	 */
	private class TimeMeasurementEntry {
		private StopWatch watch;

		@Expose
		private Long millis;

		@Expose
		private final String key;
		@Expose
		private final ITimeMeasurementTag tag;

		/**
		 * @param watch An object that keeps track of the start and end time of the time
		 *              measurement. The start and end times of this time measurement
		 *              are provided indirectly through this parameter, as it may be
		 *              necessary to pause and resume this time measurement.
		 * @param key   The key of the time measurement, which describes its purpose
		 *              further
		 * @param tag   The tag of the time measurement, which can be used for a
		 *              high-level grouping of time measurements based on what they are
		 *              taken from
		 */
		private TimeMeasurementEntry(StopWatch watch, String key, ITimeMeasurementTag tag) {
			this.watch = watch;
			this.tag = tag;
			this.key = key;
		}

		public Long getMillis() {
			return millis;
		}

		public String getKey() {
			return key;
		}

		public ITimeMeasurementTag getTag() {
			return tag;
		}

		public StopWatch getWatch() {
			return watch;
		}

		/**
		 * Computes the actual time value of this time measurement using
		 * {@link #getWatch()}
		 */
		public void computeTime() {
			if (this.watch != null) {
				this.millis = Long.valueOf(this.watch.getTime());
				this.watch = null;
			}
		}
	}
}
