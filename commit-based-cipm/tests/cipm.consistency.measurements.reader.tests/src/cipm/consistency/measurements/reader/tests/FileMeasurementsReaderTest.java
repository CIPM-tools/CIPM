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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.ServiceContextRecord;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import cipm.consistency.measurements.reader.file.FileMeasurementsReader;

/**
 * Tests for {@link FileMeasurementsReader}.
 *
 * @author Manar Mazkatli
 */
public class FileMeasurementsReaderTest {

    private FileMeasurementsReader reader;
    private Path tempDir;
    private Path tempJsonFile;

    @BeforeEach
    public void setUp() throws IOException {
        reader = new FileMeasurementsReader();
        tempDir = Files.createTempDirectory("measurements-test");
        tempJsonFile = tempDir.resolve("test-measurements.json");
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up temp files
        if (tempJsonFile != null && Files.exists(tempJsonFile)) {
            Files.delete(tempJsonFile);
        }
        if (tempDir != null && Files.exists(tempDir)) {
            Files.delete(tempDir);
        }
    }

    @Test
    public void testGetId() {
        assertEquals("file-reader", reader.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("File Measurements Reader", reader.getName());
    }

    @Test
    public void testCanHandle_JsonFile() throws IOException {
        Files.writeString(tempJsonFile, "{}");
        assertTrue(reader.canHandle(tempJsonFile.toString()));
    }

    @Test
    public void testCanHandle_MeasurementsFile() throws IOException {
        Path measFile = tempDir.resolve("test.measurements");
        Files.writeString(measFile, "{}");
        assertTrue(reader.canHandle(measFile.toString()));
        Files.delete(measFile);
    }

    @Test
    public void testCanHandle_NonExistentFile() {
        assertFalse(reader.canHandle("/non/existent/file.json"));
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
    public void testCanHandle_Directory() throws IOException {
        assertFalse(reader.canHandle(tempDir.toString()));
    }

    @Test
    public void testCanHandle_WrongExtension() throws IOException {
        Path txtFile = tempDir.resolve("test.txt");
        Files.writeString(txtFile, "{}");
        assertFalse(reader.canHandle(txtFile.toString()));
        Files.delete(txtFile);
    }

    @Test
    public void testReadRecords_InternalActionRecord() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "InternalActionRecord",
                  "internalActionID": "action1",
                  "entryTime": 1000,
                  "exitTime": 1500,
                  "requestedResourceID": "cpu"
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(1, records.size());
        assertTrue(records.get(0) instanceof InternalActionRecord);
        InternalActionRecord record = (InternalActionRecord) records.get(0);
        assertEquals("action1", record.getInternalActionID());
        assertEquals(1000L, record.getEntryTime());
        assertEquals(1500L, record.getExitTime());
        assertEquals("cpu", record.getRequestedResourceID());
    }

    @Test
    public void testReadRecords_LoopActionRecord() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "LoopActionRecord",
                  "loopID": "loop1",
                  "loopIterationCount": 10
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(1, records.size());
        assertTrue(records.get(0) instanceof LoopActionRecord);
        LoopActionRecord record = (LoopActionRecord) records.get(0);
        assertEquals("loop1", record.getLoopID());
        assertEquals(10L, record.getLoopIterationCount());
    }

    @Test
    public void testReadRecords_BranchActionRecord() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "BranchActionRecord",
                  "branchID": "branch1",
                  "executedBranchID": "branch1-case2"
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(1, records.size());
        assertTrue(records.get(0) instanceof BranchActionRecord);
        BranchActionRecord record = (BranchActionRecord) records.get(0);
        assertEquals("branch1", record.getBranchID());
        assertEquals("branch1-case2", record.getExecutedBranchID());
    }

    @Test
    public void testReadRecords_ServiceContextRecord() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "ServiceContextRecord",
                  "serviceID": "service1",
                  "serviceExecutionID": "exec1",
                  "callerExecutionID": "caller1",
                  "sessionID": "session1",
                  "hostID": "host1",
                  "hostName": "localhost",
                  "entryTime": 2000,
                  "exitTime": 3000,
                  "parameters": "param1=value1",
                  "returnValue": "success"
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(1, records.size());
        assertTrue(records.get(0) instanceof ServiceContextRecord);
        ServiceContextRecord record = (ServiceContextRecord) records.get(0);
        assertEquals("service1", record.getServiceID());
        assertEquals("exec1", record.getServiceExecutionID());
        assertEquals("caller1", record.getCallerExecutionID());
        assertEquals("session1", record.getSessionID());
        assertEquals("host1", record.getHostID());
        assertEquals("localhost", record.getHostName());
        assertEquals(2000L, record.getEntryTime());
        assertEquals(3000L, record.getExitTime());
        assertEquals("param1=value1", record.getParameters());
        assertEquals("success", record.getReturnValue());
    }

    @Test
    public void testReadRecords_MultipleRecords() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "InternalActionRecord",
                  "internalActionID": "action1",
                  "entryTime": 1000,
                  "exitTime": 1500
                },
                {
                  "type": "LoopActionRecord",
                  "loopID": "loop1",
                  "loopIterationCount": 5
                },
                {
                  "type": "BranchActionRecord",
                  "branchID": "branch1",
                  "executedBranchID": "branch1-case1"
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(3, records.size());
        assertTrue(records.get(0) instanceof InternalActionRecord);
        assertTrue(records.get(1) instanceof LoopActionRecord);
        assertTrue(records.get(2) instanceof BranchActionRecord);
    }

    @Test
    public void testReadRecords_EmptyRecordsArray() throws Exception {
        String json = """
            {
              "records": []
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(0, records.size());
    }

    @Test
    public void testReadRecords_NoRecordsField() throws Exception {
        String json = """
            {
              "other": "data"
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(0, records.size());
    }

    @Test
    public void testReadRecords_UnknownRecordType() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "UnknownRecordType",
                  "someField": "someValue"
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(0, records.size());
    }

    @Test
    public void testReadRecords_RecordWithoutType() throws Exception {
        String json = """
            {
              "records": [
                {
                  "internalActionID": "action1",
                  "entryTime": 1000
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        List<MeasurementRecord> records = reader.readRecords(tempJsonFile.toString());

        assertEquals(0, records.size());
    }

    @Test
    public void testReadRecords_FileNotFound() {
        assertThrows(MeasurementsReaderException.class, () -> {
            reader.readRecords("/non/existent/file.json");
        });
    }

    @Test
    public void testReadRecords_InvalidJson() throws Exception {
        Files.writeString(tempJsonFile, "{ invalid json }");

        assertThrows(MeasurementsReaderException.class, () -> {
            reader.readRecords(tempJsonFile.toString());
        });
    }

    @Test
    public void testReadRecordsIntoBlock() throws Exception {
        String json = """
            {
              "records": [
                {
                  "type": "InternalActionRecord",
                  "internalActionID": "action1",
                  "entryTime": 1000,
                  "exitTime": 1500
                },
                {
                  "type": "LoopActionRecord",
                  "loopID": "loop1",
                  "loopIterationCount": 5
                }
              ]
            }
            """;
        Files.writeString(tempJsonFile, json);

        MeasurementsBlock block = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
        int count = reader.readRecordsIntoBlock(tempJsonFile.toString(), block);

        assertEquals(2, count);
        assertEquals(2, block.getRecords().size());
        assertTrue(block.getRecords().get(0) instanceof InternalActionRecord);
        assertTrue(block.getRecords().get(1) instanceof LoopActionRecord);
    }

    @Test
    public void testSupportsStreaming() {
        assertFalse(reader.supportsStreaming());
    }
}
