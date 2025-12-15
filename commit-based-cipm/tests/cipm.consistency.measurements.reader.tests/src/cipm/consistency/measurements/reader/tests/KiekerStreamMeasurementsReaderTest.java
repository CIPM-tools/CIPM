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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import cipm.consistency.measurements.reader.kieker.KiekerStreamMeasurementsReader;

/**
 * Tests for {@link KiekerStreamMeasurementsReader}.
 *
 * <p>These tests verify the streaming functionality of the Kieker TCP reader,
 * including server startup/shutdown, source parsing, and record reception.</p>
 *
 * @author Manar Mazkatli
 */
public class KiekerStreamMeasurementsReaderTest {

    private static final int TEST_PORT = 15678;

    private KiekerStreamMeasurementsReader reader;

    @BeforeEach
    public void setUp() {
        reader = new KiekerStreamMeasurementsReader(TEST_PORT);
    }

    @AfterEach
    public void tearDown() {
        if (reader != null) {
            reader.stopStreaming();
        }
    }

    @Test
    public void testGetId() {
        assertEquals("kieker-stream-reader", reader.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Kieker Stream Measurements Reader", reader.getName());
    }

    @Test
    public void testSupportsStreaming() {
        assertTrue(reader.supportsStreaming());
    }

    @Test
    public void testCanHandle_NullSource() {
        assertFalse(reader.canHandle(null));
    }

    @Test
    public void testCanHandle_EmptySource() {
        assertFalse(reader.canHandle(""));
    }

    @Test
    public void testCanHandle_TcpSource() {
        assertTrue(reader.canHandle("tcp://localhost:5678"));
    }

    @Test
    public void testCanHandle_PortOnly() {
        assertTrue(reader.canHandle("5678"));
    }

    @Test
    public void testCanHandle_InvalidSource() {
        assertFalse(reader.canHandle("http://localhost:5678"));
        assertFalse(reader.canHandle("file://test"));
        assertFalse(reader.canHandle("invalid"));
    }

    @Test
    public void testReadRecords_ThrowsException() {
        assertThrows(MeasurementsReaderException.class, () -> {
            reader.readRecords("tcp://localhost:5678");
        });
    }

    @Test
    public void testReadRecordsIntoBlock_ThrowsException() {
        assertThrows(MeasurementsReaderException.class, () -> {
            reader.readRecordsIntoBlock("tcp://localhost:5678", null);
        });
    }

    @Test
    public void testStartStreaming_AlreadyRunning() throws MeasurementsReaderException {
        reader.startStreaming("tcp://localhost:" + TEST_PORT, record -> {});

        // Give server time to start
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Starting again should throw
        assertThrows(MeasurementsReaderException.class, () -> {
            reader.startStreaming("tcp://localhost:" + TEST_PORT, record -> {});
        });
    }

    @Test
    public void testStartAndStopStreaming() throws MeasurementsReaderException, InterruptedException {
        List<MeasurementRecord> receivedRecords = new ArrayList<>();

        reader.startStreaming(String.valueOf(TEST_PORT), record -> {
            receivedRecords.add(record);
        });

        // Give server time to start
        Thread.sleep(300);

        // Stop streaming
        reader.stopStreaming();

        // Should be able to restart
        reader.startStreaming(String.valueOf(TEST_PORT + 1), record -> {
            receivedRecords.add(record);
        });

        Thread.sleep(100);
        reader.stopStreaming();
    }

    @Test
    public void testReaderInstanceNotNull() {
        assertNotNull(reader);
    }

    /**
     * Test that the server starts and can accept a connection.
     */
    @Test
    public void testServerAcceptsConnection() throws Exception {
        CountDownLatch connectionLatch = new CountDownLatch(1);

        reader.startStreaming(String.valueOf(TEST_PORT), record -> {
            // Not expecting records in this test
        });

        // Give server time to start
        Thread.sleep(300);

        // Try to connect
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            assertTrue(socket.isConnected(), "Should be able to connect to server");
            connectionLatch.countDown();
        }

        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), "Connection should succeed");
    }

    /**
     * Test that client connection and disconnection is handled gracefully.
     *
     * <p>Note: Full record transmission testing requires implementing the exact
     * Kieker binary protocol with proper string registry handling. For integration
     * testing with real Kieker data, use {@link KiekerFileMeasurementsReaderIntegrationTest}
     * which reads from actual Kieker log files.</p>
     */
    @Test
    public void testClientConnectionHandling() throws Exception {
        List<MeasurementRecord> receivedRecords = new ArrayList<>();

        reader.startStreaming(String.valueOf(TEST_PORT), record -> {
            System.out.println("Received record: " + record.getClass().getSimpleName());
            receivedRecords.add(record);
        });

        // Give server time to start
        Thread.sleep(300);

        // Connect and disconnect multiple times
        for (int i = 0; i < 3; i++) {
            try (Socket socket = new Socket("localhost", TEST_PORT)) {
                assertTrue(socket.isConnected(), "Connection " + (i + 1) + " should succeed");
                // Brief connection
                Thread.sleep(100);
            }
            Thread.sleep(100);
        }

        System.out.println("****************************************");
        System.out.println("*                                      *");
        System.out.println("*   STREAMING CONNECTION TEST          *");
        System.out.println("*   All 3 connections successful       *");
        System.out.println("*                                      *");
        System.out.println("****************************************");
    }

    /**
     * Print test summary.
     */
    @Test
    public void testPrintStreamingReaderInfo() {
        System.out.println("========================================");
        System.out.println("KIEKER STREAM READER INFO");
        System.out.println("========================================");
        System.out.println("ID: " + reader.getId());
        System.out.println("Name: " + reader.getName());
        System.out.println("Supports Streaming: " + reader.supportsStreaming());
        System.out.println("Default Port: 5678");
        System.out.println("Test Port: " + TEST_PORT);
        System.out.println();
        System.out.println("Protocol:");
        System.out.println("  - TCP socket-based Kieker binary protocol");
        System.out.println("  - Registry entries: clazzId=-1, id, stringLen, string");
        System.out.println("  - Records: clazzId, loggingTimestamp, record data");
        System.out.println("========================================");
    }

}
