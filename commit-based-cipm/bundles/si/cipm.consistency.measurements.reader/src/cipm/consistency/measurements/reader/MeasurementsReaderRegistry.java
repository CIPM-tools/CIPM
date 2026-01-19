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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import cipm.consistency.measurements.reader.file.FileMeasurementsReader;
import cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader;
import cipm.consistency.measurements.reader.kieker.KiekerStreamMeasurementsReader;

/**
 * Registry for {@link MeasurementsReader} implementations.
 *
 * <p>This registry provides a central point for registering and retrieving
 * measurement readers. It follows the singleton pattern and automatically
 * registers default readers on initialization.</p>
 *
 * <h2>Default Readers</h2>
 * <p>The following readers are registered by default:</p>
 * <ul>
 *   <li>{@link FileMeasurementsReader} - JSON file reader</li>
 *   <li>{@link KiekerFileMeasurementsReader} - Kieker log directory reader</li>
 *   <li>{@link KiekerStreamMeasurementsReader} - Kieker TCP stream reader</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * MeasurementsReaderRegistry registry = MeasurementsReaderRegistry.getInstance();
 *
 * // Find a reader for a source
 * Optional&lt;MeasurementsReader&gt; reader = registry.findReaderFor("/path/to/kieker-logs");
 * if (reader.isPresent()) {
 *     List&lt;MeasurementRecord&gt; records = reader.get().readRecords("/path/to/kieker-logs");
 * }
 *
 * // Register a custom reader
 * registry.register(new MyCustomReader());
 * </pre>
 *
 * @author Manar Mazkatli
 * @see MeasurementsReader
 */
public class MeasurementsReaderRegistry {

    private static final Logger LOGGER = Logger.getLogger(MeasurementsReaderRegistry.class.getName());

    private static MeasurementsReaderRegistry instance;

    private final List<MeasurementsReader> readers;

    private MeasurementsReaderRegistry() {
        this.readers = new ArrayList<>();
        registerDefaultReaders();
    }

    /**
     * Returns the singleton instance of the registry.
     *
     * @return the registry instance
     */
    public static synchronized MeasurementsReaderRegistry getInstance() {
        if (instance == null) {
            instance = new MeasurementsReaderRegistry();
        }
        return instance;
    }

    /**
     * Registers the default readers.
     */
    private void registerDefaultReaders() {
        register(new FileMeasurementsReader());
        register(new KiekerFileMeasurementsReader());
        register(new KiekerStreamMeasurementsReader());
    }

    /**
     * Registers a new measurements reader.
     *
     * @param reader the reader to register
     */
    public void register(MeasurementsReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }

        // Check for duplicate IDs
        Optional<MeasurementsReader> existing = readers.stream()
                .filter(r -> r.getId().equals(reader.getId()))
                .findFirst();

        if (existing.isPresent()) {
            LOGGER.warn("Reader with ID '" + reader.getId() + "' already registered, replacing");
            readers.remove(existing.get());
        }

        readers.add(reader);
        LOGGER.info("Registered measurements reader: " + reader.getName() + " (" + reader.getId() + ")");
    }

    /**
     * Unregisters a measurements reader by ID.
     *
     * @param readerId the ID of the reader to unregister
     * @return true if the reader was found and unregistered
     */
    public boolean unregister(String readerId) {
        return readers.removeIf(r -> r.getId().equals(readerId));
    }

    /**
     * Returns all registered readers.
     *
     * @return unmodifiable list of readers
     */
    public List<MeasurementsReader> getReaders() {
        return Collections.unmodifiableList(readers);
    }

    /**
     * Returns a reader by ID.
     *
     * @param readerId the reader ID
     * @return the reader, or empty if not found
     */
    public Optional<MeasurementsReader> getReader(String readerId) {
        return readers.stream()
                .filter(r -> r.getId().equals(readerId))
                .findFirst();
    }

    /**
     * Finds a reader that can handle the given source.
     *
     * @param source the source identifier
     * @return the first reader that can handle the source, or empty if none found
     */
    public Optional<MeasurementsReader> findReaderFor(String source) {
        return readers.stream()
                .filter(r -> r.canHandle(source))
                .findFirst();
    }

    /**
     * Finds all readers that can handle the given source.
     *
     * @param source the source identifier
     * @return list of readers that can handle the source
     */
    public List<MeasurementsReader> findAllReadersFor(String source) {
        List<MeasurementsReader> result = new ArrayList<>();
        for (MeasurementsReader reader : readers) {
            if (reader.canHandle(source)) {
                result.add(reader);
            }
        }
        return result;
    }

    /**
     * Clears all registered readers and re-registers the default ones.
     */
    public void reset() {
        readers.clear();
        registerDefaultReaders();
    }
}
