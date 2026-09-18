package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A class that can store various information on time measurements taken from
 * tests. The redundant attributes of this class are only declared for
 * persisting technologies to serialise and save them in data files.
 * 
 * @author Alp Torac Genc
 */
public class DefaultTimeMeasurementDataStructure implements ITimeMeasurementDataStructure {
	/**
	 * @see {@link #getEndTime()}
	 */
	private LocalDateTime startTime;
	/**
	 * @see {@link #getStartTime()}
	 */
	private LocalDateTime endTime;
	/**
	 * The description of the tool that is used for taking time measurements. Only
	 * declared in order to include it to the time measurement file.
	 */
	private String timeMeasurerDescription;
	/**
	 * The time unit in time measurements. Only declared in order to include it to
	 * the time measurement file.
	 */
	private TimeUnit timeUnit;
	/**
	 * The sum of all taken time measurements.
	 */
	private Long overallRunTime;

	/**
	 * Contains the sum of time measurements for individual tags in
	 * {@link #overallRunTime}. Only declared in order to include it to the time
	 * measurement file. Should be reset after saving all time measurements, so that
	 * the values here are not duplicated.
	 */
	private final Map<ITimeMeasurementTag, Long> measurementTagSummary = new HashMap<ITimeMeasurementTag, Long>();

	/**
	 * Contains the proportion of time measurements with certain tags in
	 * {@link #overallRunTime} (in percentage). Only declared in order to include it
	 * to the time measurement file. Should be reset after saving all time
	 * measurements, so that the values here are not duplicated.
	 */
	private final Map<ITimeMeasurementTag, String> measurementTagPercentageSummary = new HashMap<ITimeMeasurementTag, String>();

	/**
	 * Contains all time measurements taken.
	 */
	private final Collection<TimeMeasurementEntry> measurements = new ArrayList<TimeMeasurementEntry>();

	@Override
	public void timeMeasuringStarted(LocalDateTime startTime) {
		if (this.getStartTime() == null) {
			this.startTime = startTime;
		}
	}

	@Override
	public void timeMeasuringFinished(LocalDateTime endTime) {
		if (this.getStartTime() != null && this.getEndTime() == null) {
			this.endTime = endTime;
			this.summariseTimeMeasurements();
		}
	}

	@Override
	public void reset() {
		this.startTime = null;

		this.endTime = null;

		this.overallRunTime = null;
		this.timeMeasurerDescription = null;
		this.timeUnit = null;

		this.clearSummaryMaps();
		this.measurements.clear();
	}

	/**
	 * Reset the summary maps after having saved, as the values of their entries
	 * will contain duplicated measurements otherwise.
	 */
	public void dataStructureSaved() {
		this.clearSummaryMaps();
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
			var measurement = measurementEntry.getTimeElapsed();

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

	@Override
	public void addTimeMeasurement(TimeMeasurementEntry entry) {
		this.measurements.add(entry);
	}

	@Override
	public LocalDateTime getStartTime() {
		return this.startTime;
	}

	@Override
	public LocalDateTime getEndTime() {
		return this.endTime;
	}

	@Override
	public String getTimeMeasurerDescription() {
		return this.timeMeasurerDescription;
	}

	@Override
	public void setTimeMeasurerDescription(String description) {
		this.timeMeasurerDescription = description;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	@Override
	public void setTimeUnit(TimeUnit unit) {
		this.timeUnit = unit;
	}

	@Override
	public Collection<TimeMeasurementEntry> getTimeMeasurementEntries() {
		return new ArrayList<TimeMeasurementEntry>(this.measurements);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultTimeMeasurementDataStructure)) {
			return false;
		}

		var castedO = (DefaultTimeMeasurementDataStructure) obj;

		return this.getStartTime().isEqual(castedO.getStartTime()) && this.getEndTime().isEqual(castedO.getEndTime())
				&& this.getTimeUnit().equals(castedO.getTimeUnit())
				&& this.getTimeMeasurerDescription().equals(castedO.getTimeMeasurerDescription())
				&& this.getTimeMeasurementEntries().size() == castedO.getTimeMeasurementEntries().size()
				&& this.getTimeMeasurementEntries().containsAll(castedO.getTimeMeasurementEntries());
	}
}
