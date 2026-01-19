/*******************************************************************************
 * Copyright (c) 2025
 * Karlsruhe Institute of Technology (KIT)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Manar Mazkatli - initial API and implementation
 ******************************************************************************/
package cipm.consistency.measurements.reader;

import java.util.List;

import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsBlock;

/**
 * Interface for reading measurement records from various sources.
 *
 * <p>This is the main abstraction for measurement input in CIPM. Implementations
 * can read measurements from different sources such as files, TCP streams,
 * monitoring tools like Kieker, or custom formats.</p>
 *
 * <h2>Reader Capabilities</h2>
 * <ul>
 *   <li><b>Batch reading</b>: Read all records at once using {@link #readRecords(String)}</li>
 *   <li><b>Block reading</b>: Read directly into a measurements block using
 *       {@link #readRecordsIntoBlock(String, MeasurementsBlock)}</li>
 *   <li><b>Streaming</b>: Continuous reading with callbacks (optional, check
 *       {@link #supportsStreaming()})</li>
 * </ul>
 *
 * <h2>Implementation Guidelines</h2>
 * <ol>
 *   <li>Provide unique {@link #getId()} and human-readable {@link #getName()}</li>
 *   <li>Implement {@link #canHandle(String)} to detect supported sources</li>
 *   <li>If streaming is supported, override {@link #supportsStreaming()},
 *       {@link #startStreaming(String, MeasurementRecordCallback)}, and
 *       {@link #stopStreaming()}</li>
 * </ol>
 *
 * <h2>Available Implementations</h2>
 * <ul>
 *   <li>{@link cipm.consistency.measurements.reader.file.FileMeasurementsReader} -
 *       Reads from JSON files</li>
 *   <li>{@link cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader} -
 *       Reads from Kieker log directories</li>
 *   <li>{@link cipm.consistency.measurements.reader.kieker.KiekerStreamMeasurementsReader} -
 *       Streams from Kieker TCP</li>
 * </ul>
 *
 * @author Manar Mazkatli
 * @see MeasurementsReaderRegistry
 * @see MeasurementRecord
 */
public interface MeasurementsReader {

    /**
     * Returns the unique identifier of this reader.
     *
     * @return the reader ID
     */
    String getId();

    /**
     * Returns a human-readable name for this reader.
     *
     * @return the reader name
     */
    String getName();

    /**
     * Checks if this reader can handle the given source.
     *
     * @param source the source identifier (e.g., file path, URL)
     * @return true if this reader can handle the source
     */
    boolean canHandle(String source);

    /**
     * Reads all measurement records from the source.
     *
     * @param source the source identifier (e.g., file path, URL)
     * @return list of measurement records
     * @throws MeasurementsReaderException if reading fails
     */
    List<MeasurementRecord> readRecords(String source) throws MeasurementsReaderException;

    /**
     * Reads measurement records and adds them to the given measurements block.
     *
     * @param source the source identifier
     * @param block the measurements block to add records to
     * @return number of records added
     * @throws MeasurementsReaderException if reading fails
     */
    int readRecordsIntoBlock(String source, MeasurementsBlock block) throws MeasurementsReaderException;

    /**
     * Checks if this reader supports streaming (continuous reading).
     *
     * @return true if streaming is supported
     */
    default boolean supportsStreaming() {
        return false;
    }

    /**
     * Starts streaming measurements from the source.
     * Records will be passed to the callback as they arrive.
     *
     * @param source the source identifier
     * @param callback callback to receive records
     * @throws MeasurementsReaderException if streaming fails
     * @throws UnsupportedOperationException if streaming is not supported
     */
    default void startStreaming(String source, MeasurementRecordCallback callback)
            throws MeasurementsReaderException {
        throw new UnsupportedOperationException("Streaming not supported by this reader");
    }

    /**
     * Stops streaming measurements.
     */
    default void stopStreaming() {
        // Default implementation does nothing
    }

    /**
     * Callback interface for receiving measurement records during streaming.
     */
    @FunctionalInterface
    interface MeasurementRecordCallback {
        /**
         * Called when a new measurement record is received.
         *
         * @param record the received record
         */
        void onRecord(MeasurementRecord record);
    }
}
