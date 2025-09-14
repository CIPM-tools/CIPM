package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.nio.file.Path;

/**
 * A utility class for taking time measurements during tests.
 * 
 * @author Alp Torac Genc
 */
public class ParserTestTimeMeasurer {
	private ITimeMeasurementDataStructure dataStructure;
	private ITimeMeasurementPersistingStrategy persistingStrat;
	private ITimeMeasuringStrategy measuringStrat;

	/**
	 * The only instance of this class.
	 */
	private static ParserTestTimeMeasurer instance;

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
	 * Starts measuring the time using the underlying time measuring strategy for a
	 * certain purpose denoted in the parameters. <br>
	 * <br>
	 * Time measuring should have been started via {@link #startTimeMeasuring()}
	 * prior to calling this method.
	 * 
	 * @param key The key of the taken time measurement, which describes what the
	 *            time measurement is taken from
	 * @param tag The tag of the time measurement, which is used to group time
	 *            measurements
	 * 
	 * @see {@link #getMeasuringStrat()}
	 */
	public void startTimeMeasurement(ParserTestTimeMeasurementKey key, ITimeMeasurementTag tag) {
		this.measuringStrat.startTimeMeasurement(key, tag);
	}

	/**
	 * Stops the most recently started time measurement (via
	 * {@link #startTimeMeasurement(String, ITimeMeasurementTag)}) and saves it in
	 * the underlying data structure. <br>
	 * <br>
	 * If taking time measurements should end altogether, use
	 * {@link #finishTimeMeasuring()} instead.
	 * 
	 * @see {@link #getMeasuringStrat()}
	 * @see {@link #getDataStructure()}
	 */
	public void stopTimeMeasurement() {
		this.dataStructure.addTimeMeasurement(this.measuringStrat.stopTimeMeasurement());
	}

	/**
	 * Signals that taking time measurements should start and prepares the
	 * underlying mechanisms for time measuring. <br>
	 * <br>
	 * Use {@link #finishTimeMeasuring()} for ending time measuring. Re-call this
	 * method to start anew.
	 * 
	 * @see {@link #getDataStructure()}
	 * @see {@link #getMeasuringStrat()}
	 */
	public void startTimeMeasuring() {
		this.measuringStrat.timeMeasuringStarted();

		this.dataStructure.setTimeMeasurerDescription(this.measuringStrat.getTimeMeasurerDescription());
		this.dataStructure.setTimeUnit(this.measuringStrat.getTimeUnit());
		this.dataStructure.timeMeasuringStarted(this.measuringStrat.getStartTime());
	}

	/**
	 * Signals that taking time measurements is over and the taken time measurements
	 * should be processed.<br>
	 * <br>
	 * If taking time measurements is to start anew, call
	 * {@link #startTimeMeasuring()} before
	 * {@link #startTimeMeasurement(ParserTestTimeMeasurementKey, ITimeMeasurementTag)}.
	 * 
	 * @see {@link #getDataStructure()}
	 * @see {@link #getMeasuringStrat()}
	 */
	public void finishTimeMeasuring() {
		this.measuringStrat.timeMeasuringFinished();
		var time = this.measuringStrat.getEndTime();
		this.dataStructure.timeMeasuringFinished(time);
	}

	/**
	 * Ends taking time measurements (if not already done), then processes all taken
	 * time measurements. Finally, saves the underlying data structure that was
	 * collecting all time measurements according to the underlying persisting
	 * strategy.
	 * 
	 * @param measurementsSavePath The absolute path, at which all taken time
	 *                             measurements should be saved.
	 * 
	 * @see {@link #getDataStructure()}
	 * @see {@link #getPersistingStrat()}
	 */
	public void save(Path measurementsSavePath) {
		this.finishTimeMeasuring();

		this.persistingStrat.save(dataStructure, measurementsSavePath);

		this.dataStructure.dataStructureSaved();
	}

	/**
	 * Resets all collected time measurements.
	 */
	public void reset() {
		this.dataStructure.reset();
	}

	/**
	 * @return The data structure that will collect all time measurements taken
	 */
	public ITimeMeasurementDataStructure getDataStructure() {
		return dataStructure;
	}

	/**
	 * @param dataStructure {@link #getDataStructure()}
	 */
	public void setDataStructure(ITimeMeasurementDataStructure dataStructure) {
		this.dataStructure = dataStructure;
	}

	/**
	 * @return The strategy for persisting all time measurements
	 */
	public ITimeMeasurementPersistingStrategy getPersistingStrat() {
		return persistingStrat;
	}

	/**
	 * @param persistingStrat {@link #getPersistingStrat()}
	 */
	public void setPersistingStrat(ITimeMeasurementPersistingStrategy persistingStrat) {
		this.persistingStrat = persistingStrat;
	}

	/**
	 * @return The mechanism that will be used to take time measurements
	 */
	public ITimeMeasuringStrategy getMeasuringStrat() {
		return measuringStrat;
	}

	/**
	 * @param measuringStrat {@link #getMeasuringStrat()}
	 */
	public void setMeasuringStrat(ITimeMeasuringStrategy measuringStrat) {
		this.measuringStrat = measuringStrat;
	}
}
