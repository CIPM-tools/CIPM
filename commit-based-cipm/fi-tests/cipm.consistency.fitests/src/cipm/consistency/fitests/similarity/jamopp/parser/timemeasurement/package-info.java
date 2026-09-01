/**
 * Contains the means to measure, persist and load previous time measurements
 * from tests. <br>
 * <br>
 * {@link ParserTestTimeMeasurer} is the singleton that serves as a facade to
 * time measurement operations and aggregates the time measuring components:
 * <ul>
 * <li>{@link ITimeMeasuringStrategy} encapsulates the means to measure time
 * <li>{@link ITimeMeasurementDataStructure} stores taken time measurements and
 * aggregates in form of {@link TimeMeasurementEntry} instances
 * <li>{@link ITimeMeasurementPersistingStrategy} defines how
 * {@link ITimeMeasurementDataStructure} instances can be saved into files or
 * persisted
 * <li>{@link ITimeMeasurementLoadingStrategy} provides the means to load
 * previously taken time measurements
 * </ul>
 * Time measurements are stored in form of {@link TimeMeasurementEntry}
 * instances, which consist of 2 high-level parts:
 * <ul>
 * <li>{@link ParserTestTimeMeasurementKey} houses all information regarding a
 * time measurement, such as the amount of time elapsed and what the time
 * measurement is taken from. For each piece of information supported by
 * ParserTestTimeMeasurementKey, there is a corresponding
 * {@link ParserTestTimeMeasurerKeyType} constant. ParserTestTimeMeasurementKey
 * instances should be constructed via their builders, such as
 * {@link ParserTestTimeMeasurementKeyBuilder}.
 * <li>{@link ITimeMeasurementTag} that indicates the over-arching purpose of
 * the time measurement, which allows time measurements to be grouped up easier.
 * It is the unifying interface of tag enums (such as
 * {@link GeneralTimeMeasurementTag}), which enables implementing further tags
 * without having to modify existing tag enums.
 * </ul>
 */
package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;