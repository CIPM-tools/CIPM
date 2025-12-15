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
package cipm.consistency.measurements.reader.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.measurements.reader.MeasurementsReader;
import cipm.consistency.measurements.reader.MeasurementsReaderRegistry;
import cipm.consistency.measurements.reader.file.FileMeasurementsReader;
import cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader;
import cipm.consistency.measurements.reader.kieker.KiekerStreamMeasurementsReader;

/**
 * Tests for {@link MeasurementsReaderRegistry}.
 *
 * @author Manar Mazkatli
 */
public class MeasurementsReaderRegistryTest {

    private MeasurementsReaderRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = MeasurementsReaderRegistry.getInstance();
        registry.reset(); // Reset to default state
    }

    @Test
    public void testGetInstance() {
        MeasurementsReaderRegistry instance1 = MeasurementsReaderRegistry.getInstance();
        MeasurementsReaderRegistry instance2 = MeasurementsReaderRegistry.getInstance();
        assertNotNull(instance1);
        assertEquals(instance1, instance2, "Should return same singleton instance");
    }

    @Test
    public void testDefaultReadersRegistered() {
        List<MeasurementsReader> readers = registry.getReaders();

        // Should have 3 default readers
        assertEquals(3, readers.size());

        // Check for each reader type
        boolean hasFileReader = readers.stream()
                .anyMatch(r -> r instanceof FileMeasurementsReader);
        boolean hasKiekerFileReader = readers.stream()
                .anyMatch(r -> r instanceof KiekerFileMeasurementsReader);
        boolean hasKiekerStreamReader = readers.stream()
                .anyMatch(r -> r instanceof KiekerStreamMeasurementsReader);

        assertTrue(hasFileReader, "Should have FileMeasurementsReader");
        assertTrue(hasKiekerFileReader, "Should have KiekerFileMeasurementsReader");
        assertTrue(hasKiekerStreamReader, "Should have KiekerStreamMeasurementsReader");
    }

    @Test
    public void testGetReaderById() {
        Optional<MeasurementsReader> fileReader = registry.getReader("file-reader");
        assertTrue(fileReader.isPresent());
        assertTrue(fileReader.get() instanceof FileMeasurementsReader);

        Optional<MeasurementsReader> kiekerFileReader = registry.getReader("kieker-file-reader");
        assertTrue(kiekerFileReader.isPresent());
        assertTrue(kiekerFileReader.get() instanceof KiekerFileMeasurementsReader);

        Optional<MeasurementsReader> kiekerStreamReader = registry.getReader("kieker-stream-reader");
        assertTrue(kiekerStreamReader.isPresent());
        assertTrue(kiekerStreamReader.get() instanceof KiekerStreamMeasurementsReader);
    }

    @Test
    public void testGetReaderById_NotFound() {
        Optional<MeasurementsReader> reader = registry.getReader("non-existent-reader");
        assertFalse(reader.isPresent());
    }

    @Test
    public void testFindReaderFor_JsonFile() {
        // Create a temp json file path (doesn't need to exist for canHandle check in some cases)
        Optional<MeasurementsReader> reader = registry.getReaders().stream()
                .filter(r -> r.getId().equals("file-reader"))
                .findFirst();
        assertTrue(reader.isPresent());
    }

    @Test
    public void testFindReaderFor_TcpSource() {
        Optional<MeasurementsReader> reader = registry.findReaderFor("tcp://localhost:5678");
        assertTrue(reader.isPresent());
        assertTrue(reader.get() instanceof KiekerStreamMeasurementsReader);
    }

    @Test
    public void testFindReaderFor_PortOnly() {
        Optional<MeasurementsReader> reader = registry.findReaderFor("5678");
        assertTrue(reader.isPresent());
        assertTrue(reader.get() instanceof KiekerStreamMeasurementsReader);
    }

    @Test
    public void testFindReaderFor_NoMatch() {
        Optional<MeasurementsReader> reader = registry.findReaderFor("unknown://source");
        assertFalse(reader.isPresent());
    }

    @Test
    public void testUnregister() {
        assertTrue(registry.getReader("file-reader").isPresent());

        boolean removed = registry.unregister("file-reader");
        assertTrue(removed);
        assertFalse(registry.getReader("file-reader").isPresent());
    }

    @Test
    public void testUnregister_NotFound() {
        boolean removed = registry.unregister("non-existent-reader");
        assertFalse(removed);
    }

    @Test
    public void testReset() {
        // Unregister a reader
        registry.unregister("file-reader");
        assertEquals(2, registry.getReaders().size());

        // Reset should restore defaults
        registry.reset();
        assertEquals(3, registry.getReaders().size());
        assertTrue(registry.getReader("file-reader").isPresent());
    }

    @Test
    public void testGetReadersReturnsUnmodifiableList() {
        List<MeasurementsReader> readers = registry.getReaders();

        try {
            readers.clear();
            // If we get here without exception, fail the test
            assertTrue(false, "Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }
}
