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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.ResourceUtilizationRecord;
import cipm.consistency.measurements.ServiceContextRecord;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader;

/**
 * Tests for {@link KiekerFileMeasurementsReader}.
 *
 * <p>These tests verify the functionality of the Kieker file reader
 * including directory detection, error handling, and actual file reading.</p>
 *
 * @author Manar Mazkatli
 */
public class KiekerFileMeasurementsReaderIntegrationTest {

    private static final String KIEKER_TEST_DATA_PATH = "testdata/kieker";

    private KiekerFileMeasurementsReader reader;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
        reader = new KiekerFileMeasurementsReader();
    }

    @Test
    public void testGetId() {
        assertEquals("kieker-file-reader", reader.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Kieker File Measurements Reader", reader.getName());
    }

    @Test
    public void testSupportsStreaming() {
        assertFalse(reader.supportsStreaming());
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
    public void testCanHandle_NonExistentDirectory() {
        assertFalse(reader.canHandle("/non/existent/directory"));
    }

    @Test
    public void testCanHandle_DirectoryWithoutKiekerMap() {
        assertFalse(reader.canHandle(tempDir.getAbsolutePath()));
    }

    @Test
    public void testCanHandle_DirectoryWithKiekerMap() throws Exception {
        File kiekerMap = new File(tempDir, "kieker.map");
        assertTrue(kiekerMap.createNewFile(), "Failed to create kieker.map");
        assertTrue(reader.canHandle(tempDir.getAbsolutePath()));
    }

    @Test
    public void testCanHandle_FileInsteadOfDirectory() throws Exception {
        File testFile = new File(tempDir, "testfile.txt");
        assertTrue(testFile.createNewFile(), "Failed to create test file");
        assertFalse(reader.canHandle(testFile.getAbsolutePath()));
    }

    @Test
    public void testReaderInstanceNotNull() {
        assertNotNull(reader);
    }

    /**
     * Test reading Kieker files and printing EMF records categorized by type.
     */
    @Test
    public void testReadKiekerFileAndPrintByCategory() throws MeasurementsReaderException {
        String kiekerDataDir = findKiekerDataDirectory();

        if (!isKiekerDataAvailable(kiekerDataDir)) {
            System.out.println("Skipping test: Kieker test data not available at: " + kiekerDataDir);
            return;
        }

        System.out.println("========================================");
        System.out.println("KIEKER FILE READING TEST");
        System.out.println("========================================");
        System.out.println("Reading from: " + kiekerDataDir);
        System.out.println();

        // Read records
        List<MeasurementRecord> records = reader.readRecords(kiekerDataDir);

        assertNotNull(records, "Records list should not be null");

        // Print prominent record count
        System.out.println("****************************************");
        System.out.println("*                                      *");
        System.out.println("*   TOTAL EMF RECORDS: " + String.format("%-15d", records.size()) + "*");
        System.out.println("*                                      *");
        System.out.println("****************************************");
        System.out.println();

        // If no records, print diagnostic info
        if (records.isEmpty()) {
            System.out.println("WARNING: No records were converted!");
            System.out.println("Check that:");
            System.out.println("  1. The Kieker data files are present");
            System.out.println("  2. The record types in kieker.map are supported");
            System.out.println("  3. The Calibration record classes are available");
            System.out.println();
        }

        // Categorize records
        List<InternalActionRecord> internalActionRecords = new ArrayList<>();
        List<ServiceContextRecord> serviceContextRecords = new ArrayList<>();
        List<LoopActionRecord> loopActionRecords = new ArrayList<>();
        List<BranchActionRecord> branchActionRecords = new ArrayList<>();
        List<ResourceUtilizationRecord> resourceUtilizationRecords = new ArrayList<>();
        List<MeasurementRecord> unknownRecords = new ArrayList<>();

        for (MeasurementRecord record : records) {
            if (record instanceof InternalActionRecord) {
                internalActionRecords.add((InternalActionRecord) record);
            } else if (record instanceof ServiceContextRecord) {
                serviceContextRecords.add((ServiceContextRecord) record);
            } else if (record instanceof LoopActionRecord) {
                loopActionRecords.add((LoopActionRecord) record);
            } else if (record instanceof BranchActionRecord) {
                branchActionRecords.add((BranchActionRecord) record);
            } else if (record instanceof ResourceUtilizationRecord) {
                resourceUtilizationRecords.add((ResourceUtilizationRecord) record);
            } else {
                unknownRecords.add(record);
            }
        }

        // Determine max records to print (more if small total)
        int maxPrint = records.size() < 50 ? 20 : 5;

        // Print InternalActionRecords (from ResponseTimeRecord)
        System.out.println("========================================");
        System.out.println("INTERNAL ACTION RECORDS (ResponseTimeRecord)");
        System.out.println("Count: " + internalActionRecords.size());
        System.out.println("========================================");
        int count = 0;
        for (InternalActionRecord r : internalActionRecords) {
            if (count < maxPrint) {
                System.out.println("  [" + count + "] InternalActionID: " + r.getInternalActionID());
                System.out.println("      ResourceID: " + r.getRequestedResourceID());
                System.out.println("      EntryTime: " + r.getEntryTime());
                System.out.println("      ExitTime: " + r.getExitTime());
                System.out.println("      Duration: " + (r.getExitTime() - r.getEntryTime()) + " ns");
                System.out.println("      ---");
            }
            count++;
        }
        if (internalActionRecords.size() > maxPrint) {
            System.out.println("  ... and " + (internalActionRecords.size() - maxPrint) + " more");
        }
        System.out.println();

        // Print ServiceContextRecords (from ServiceCallRecord)
        System.out.println("========================================");
        System.out.println("SERVICE CONTEXT RECORDS (ServiceCallRecord)");
        System.out.println("Count: " + serviceContextRecords.size());
        System.out.println("========================================");
        count = 0;
        for (ServiceContextRecord r : serviceContextRecords) {
            if (count < maxPrint) {
                System.out.println("  [" + count + "] ServiceID: " + r.getServiceID());
                System.out.println("      ServiceExecutionID: " + r.getServiceExecutionID());
                System.out.println("      CallerExecutionID: " + r.getCallerExecutionID());
                System.out.println("      SessionID: " + r.getSessionID());
                System.out.println("      HostID: " + r.getHostID());
                System.out.println("      HostName: " + r.getHostName());
                System.out.println("      EntryTime: " + r.getEntryTime());
                System.out.println("      ExitTime: " + r.getExitTime());
                System.out.println("      Parameters: " + r.getParameters());
                System.out.println("      ReturnValue: " + r.getReturnValue());
                System.out.println("      ---");
            }
            count++;
        }
        if (serviceContextRecords.size() > maxPrint) {
            System.out.println("  ... and " + (serviceContextRecords.size() - maxPrint) + " more");
        }
        System.out.println();

        // Print LoopActionRecords (from LoopRecord)
        System.out.println("========================================");
        System.out.println("LOOP ACTION RECORDS (LoopRecord)");
        System.out.println("Count: " + loopActionRecords.size());
        System.out.println("========================================");
        count = 0;
        for (LoopActionRecord r : loopActionRecords) {
            if (count < maxPrint) {
                System.out.println("  [" + count + "] LoopID: " + r.getLoopID());
                System.out.println("      LoopIterationCount: " + r.getLoopIterationCount());
                System.out.println("      ---");
            }
            count++;
        }
        if (loopActionRecords.size() > maxPrint) {
            System.out.println("  ... and " + (loopActionRecords.size() - maxPrint) + " more");
        }
        System.out.println();

        // Print BranchActionRecords (from BranchRecord)
        System.out.println("========================================");
        System.out.println("BRANCH ACTION RECORDS (BranchRecord)");
        System.out.println("Count: " + branchActionRecords.size());
        System.out.println("========================================");
        count = 0;
        for (BranchActionRecord r : branchActionRecords) {
            if (count < maxPrint) {
                System.out.println("  [" + count + "] BranchID: " + r.getBranchID());
                System.out.println("      ExecutedBranchID: " + r.getExecutedBranchID());
                System.out.println("      ---");
            }
            count++;
        }
        if (branchActionRecords.size() > maxPrint) {
            System.out.println("  ... and " + (branchActionRecords.size() - maxPrint) + " more");
        }
        System.out.println();

        // Print ResourceUtilizationRecords
        System.out.println("========================================");
        System.out.println("RESOURCE UTILIZATION RECORDS");
        System.out.println("Count: " + resourceUtilizationRecords.size());
        System.out.println("========================================");
        count = 0;
        for (ResourceUtilizationRecord r : resourceUtilizationRecords) {
            if (count < maxPrint) {
                System.out.println("  [" + count + "] ResourceID: " + r.getResourceID());
                System.out.println("      Utilization: " + r.getUtilization());
                System.out.println("      Timestamp: " + r.getTimestamp());
                System.out.println("      ---");
            }
            count++;
        }
        if (resourceUtilizationRecords.size() > maxPrint) {
            System.out.println("  ... and " + (resourceUtilizationRecords.size() - maxPrint) + " more");
        }
        System.out.println();

        // Print unknown records
        if (!unknownRecords.isEmpty()) {
            System.out.println("========================================");
            System.out.println("UNKNOWN/UNCONVERTED RECORDS");
            System.out.println("Count: " + unknownRecords.size());
            System.out.println("========================================");
            for (MeasurementRecord r : unknownRecords) {
                System.out.println("  Type: " + r.getClass().getSimpleName());
            }
            System.out.println();
        }

        // Summary
        System.out.println("========================================");
        System.out.println("SUMMARY");
        System.out.println("========================================");
        System.out.println("  InternalActionRecords:      " + internalActionRecords.size());
        System.out.println("  ServiceContextRecords:      " + serviceContextRecords.size());
        System.out.println("  LoopActionRecords:          " + loopActionRecords.size());
        System.out.println("  BranchActionRecords:        " + branchActionRecords.size());
        System.out.println("  ResourceUtilizationRecords: " + resourceUtilizationRecords.size());
        System.out.println("  Unknown/Unconverted:        " + unknownRecords.size());
        System.out.println("  -----------------------------------");
        System.out.println("  TOTAL:                      " + records.size());
        System.out.println("========================================");

        // Also test readRecordsIntoBlock
        System.out.println();
        System.out.println("Testing readRecordsIntoBlock...");
        MeasurementsBlock block = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
        int blockCount = reader.readRecordsIntoBlock(kiekerDataDir, block);
        System.out.println("Records added to block: " + blockCount);
        assertEquals(records.size(), blockCount, "Block count should match list count");
        assertEquals(blockCount, block.getRecords().size(), "Block records size should match count");
        System.out.println("readRecordsIntoBlock: SUCCESS");
    }

    private String findKiekerDataDirectory() {
        String[] basePaths = {
            System.getProperty("user.dir"),
            System.getProperty("user.dir") + "/..",
            ".",
            ".."
        };

        for (String basePath : basePaths) {
            File dir = new File(basePath, KIEKER_TEST_DATA_PATH);
            if (dir.exists() && dir.isDirectory()) {
                File kiekerMap = new File(dir, "kieker.map");
                if (kiekerMap.exists()) {
                    return dir.getAbsolutePath();
                }
            }
        }
        return KIEKER_TEST_DATA_PATH;
    }

    private boolean isKiekerDataAvailable(String kiekerDataDir) {
        File dir = new File(kiekerDataDir);
        File kiekerMap = new File(dir, "kieker.map");
        return dir.exists() && dir.isDirectory() && kiekerMap.exists();
    }
}
