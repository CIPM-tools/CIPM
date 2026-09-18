package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

/**
 * A class that encapsulates singular time measurements.
 * 
 * @author Alp Torac Genc
 */
public class TimeMeasurementEntry {
	private long timeElapsed;
	private final ParserTestTimeMeasurementKey key;
	private final ITimeMeasurementTag tag;

	/**
	 * @param key {@link #getKey()}
	 * @param tag {@link #getTag()}
	 */
	public TimeMeasurementEntry(ParserTestTimeMeasurementKey key, ITimeMeasurementTag tag) {
		this.tag = tag;
		this.key = key;
	}

	/**
	 * The concrete time unit should be specified within the
	 * {@link ITimeMeasurementDataStructure} that stores this instance
	 * 
	 * @return The amount of time units associated with the time measurement
	 */
	public long getTimeElapsed() {
		return timeElapsed;
	}

	/**
	 * @param timeElapsed {@link #getTimeElapsed()}
	 */
	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	/**
	 * @return Information associated with the time measurement, which describe what
	 *         was measured
	 */
	public ParserTestTimeMeasurementKey getKey() {
		return key;
	}

	/**
	 * @return The tag of the time measurement, which can be used for a high-level
	 *         grouping of time measurements based on what they are taken from
	 */
	public ITimeMeasurementTag getTag() {
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TimeMeasurementEntry)) {
			return false;
		}
		var castedO = (TimeMeasurementEntry) obj;

		return this.getTag().equals(castedO.getTag()) && this.getKey().equals(castedO.getKey())
				&& this.getTimeElapsed() == castedO.getTimeElapsed();
	}
}