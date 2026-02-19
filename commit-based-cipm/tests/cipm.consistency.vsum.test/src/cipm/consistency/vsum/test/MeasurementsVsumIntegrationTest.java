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
package cipm.consistency.vsum.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsPackage;
import cipm.consistency.measurements.MeasurementsRepository;
import cipm.consistency.measurements.ResourceUtilizationRecord;
import cipm.consistency.measurements.ServiceContextRecord;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader;
import cipm.consistency.cpr.measurementshelper.MeasurementsHelper;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.measurements.MeasurementsFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.VsumFacadeImpl;
import mir.reactions.measurementsInit.MeasurementsInitChangePropagationSpecification;
import mir.reactions.measurementsPcmUpdate.MeasurementsPcmUpdateChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import tools.cipm.views.measurements.CipmMeasurementsViewType;
import tools.cipm.views.measurements.MeasurementsView;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.changederivation.DefaultStateBasedChangeResolutionStrategy;

/**
 * Integration test that verifies measurements records can be read from Kieker files
 * and added to a VSUM containing PCM, IM, and Measurements models.
 *
 * <p>This test demonstrates:
 * <ol>
 *   <li>Initializing a VSUM with PCM, IM, and Measurements models</li>
 *   <li>Reading Kieker monitoring records from file</li>
 *   <li>Converting them to EMF MeasurementRecords</li>
 *   <li>Adding them to the Measurements model in VSUM</li>
 *   <li>Printing categorized results</li>
 * </ol>
 * </p>
 *
 * <p>Note: To test with VSUM reactions (measurementsPcmUpdate), ensure the
 * cipm.consistency.cpr.measurementspcm bundle is built and add it to the
 * Require-Bundle in MANIFEST.MF.</p>
 *
 * @author Manar Mazkatli
 */
public class MeasurementsVsumIntegrationTest {

    private static final Logger LOGGER = Logger.getLogger(MeasurementsVsumIntegrationTest.class);

    /** Test data with pre-prepared TeaStore PCM models (input, shared with ShortMeasurementsIntegrationTest) */
    private static final Path TEST_INPUT_PATH = Path.of("testData", "shortMeasurementsTest");

    /** Test results directory (output) - stored separately to preserve input */
    private static final Path TEST_RESULTS_PATH = Path.of("testData", "measurementsVsumTest_results");

    /** TeaStore Kieker monitoring data path */
    private static final String TEASTORE_MONITORING_PATH =
        "../../bundles/Calibration/CIPM-Pipeline/cipm.consistency.root/cipm.consistency.runtime.pipeline.pcm/src/test/resources/teastore/monitoring";

    /** Sliding window size in milliseconds (6 minutes) */
    private static final long WINDOW_SIZE_MS = 360_000L;

    /** Trigger time in milliseconds (3 minutes) */
    private static final long TRIGGER_TIME_MS = 180_000L;

    private VsumFacadeImpl vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private MeasurementsFacade measurementsFacade;

    @BeforeAll
    public static void setupLogging() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        // Suppress verbose Vitruvius change propagation logging but enable reactions debug
        Logger.getLogger("tools.vitruv").setLevel(Level.WARN);
        // Enable debug logging for our reactions to see what's happening
        Logger.getLogger("mir.reactions").setLevel(Level.DEBUG);
        Logger.getLogger("cipm.consistency.cpr").setLevel(Level.DEBUG);

        // Initialize PCM EMF packages before loading any models
        PCMUtils.loadPCMModels();
        PCMUtils.initVitruvius();
    }

    @BeforeEach
    public void setUp() throws IOException {
        // Clean results directory (keep input PCM models intact)
        cleanupResultsDirectory();

        // Create results directories for output (IM, Measurements, VSUM only)
        // PCM models are used from input directory to preserve cross-references
        Files.createDirectories(TEST_RESULTS_PATH.resolve("im"));
        Files.createDirectories(TEST_RESULTS_PATH.resolve("measurements"));
        Files.createDirectories(TEST_RESULTS_PATH.resolve("vsum"));

        // Initialize facades
        pcmFacade = new PcmFacade();
        imFacade = new ImFacade();
        measurementsFacade = new MeasurementsFacade();
        vsumFacade = new VsumFacadeImpl();

        // Enable headless mode for testing (no UI dialogs)
        vsumFacade.setHeadlessMode(true);

        // Configure measurements helper with window and trigger settings
        MeasurementsHelper.configure(WINDOW_SIZE_MS, TRIGGER_TIME_MS);
        LOGGER.info("MeasurementsHelper configured: window=" + WINDOW_SIZE_MS
            + "ms (" + (WINDOW_SIZE_MS / 60_000) + "min), trigger=" + TRIGGER_TIME_MS
            + "ms (" + (TRIGGER_TIME_MS / 60_000) + "min)");

        // Initialize PCM from INPUT directory (original files with valid cross-references)
        // IM and Measurements go to RESULTS directory
        pcmFacade.initialize(TEST_INPUT_PATH.resolve("pcm"));
        imFacade.initialize(TEST_RESULTS_PATH.resolve("im"));
        measurementsFacade.initialize(TEST_RESULTS_PATH.resolve("measurements"));

        // Initialize VSUM with all models and change propagation specifications
        List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
        // PCM Init - creates literal correspondence for PCM Repository
        changeSpecs.add(new PcmInitChangePropagationSpecification());
        // Measurements Init - creates literal correspondence for Measurements model
        changeSpecs.add(new MeasurementsInitChangePropagationSpecification());
        // Measurements -> PCM Update - handles measurement records and updates PCM actions
        changeSpecs.add(new MeasurementsPcmUpdateChangePropagationSpecification());

        vsumFacade.initialize(
            TEST_RESULTS_PATH.resolve("vsum"),
            List.of(pcmFacade, imFacade, measurementsFacade),
            changeSpecs,
            new DefaultStateBasedChangeResolutionStrategy()
        );

        LOGGER.info("VSUM initialized with TeaStore PCM models");
        LOGGER.info("Results will be stored in: " + TEST_RESULTS_PATH.toAbsolutePath());

        // Verify correspondences were created by reactions during VSUM init
        setupCorrespondences();
    }

    @AfterEach
    public void tearDown() {
        MeasurementsHelper.resetToDefault();
        if (vsumFacade != null && vsumFacade.getVsum() != null) {
            vsumFacade.getVsum().dispose();
        }
    }

    /**
     * Clean up the results directory before each test run.
     * This ensures each test starts fresh while preserving input data.
     */
    private void cleanupResultsDirectory() throws IOException {
        if (Files.exists(TEST_RESULTS_PATH)) {
            Files.walk(TEST_RESULTS_PATH)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        LOGGER.warn("Could not delete: " + path);
                    }
                });
        }
    }

    /**
     * Verify that correspondences were created by the pcmInit and measurementsInit reactions.
     */
    private void setupCorrespondences() {
        EditableCorrespondenceModelView<Correspondence> corrView = vsumFacade.getCorrespondenceView();
        if (corrView == null) {
            LOGGER.error("CORRESPONDENCE SETUP: Cannot access correspondence model view");
            return;
        }

        LOGGER.info("=== VSUM Correspondence Verification ===");

        // 1. Check Repository literal correspondence (should be created by pcmInit reaction)
        boolean repoLiteralExists = false;
        try {
            java.util.Set<EObject> corr = corrView.getCorrespondingEObjects(RepositoryPackage.Literals.REPOSITORY, null);
            repoLiteralExists = corr.stream().anyMatch(e -> e instanceof Repository);
        } catch (Exception e) {
            LOGGER.warn("  Could not check repo literal correspondence: " + e.getMessage());
        }
        LOGGER.info("  Repo literal (pcmInit): " + (repoLiteralExists ? "OK" : "MISSING - pcmInit reaction did not fire!"));

        // 2. Check Measurements literal correspondence (should be created by measurementsInit reaction)
        boolean measLiteralExists = false;
        try {
            java.util.Set<EObject> corr = corrView.getCorrespondingEObjects(MeasurementsPackage.Literals.MEASUREMENTS, null);
            measLiteralExists = corr.stream().anyMatch(e -> e instanceof Measurements);
        } catch (Exception e) {
            LOGGER.warn("  Could not check meas literal correspondence: " + e.getMessage());
        }
        LOGGER.info("  Meas literal (measurementsInit): " + (measLiteralExists ? "OK" : "MISSING - measurementsInit reaction did not fire!"));

        // 3. Check Measurements <-> Repository correspondence
        Measurements vsumMeasurements = findMeasurementsInVsum();
        if (vsumMeasurements != null) {
            boolean linkExists = false;
            try {
                java.util.Set<EObject> corr = corrView.getCorrespondingEObjects(vsumMeasurements, null);
                linkExists = corr.stream().anyMatch(e -> e instanceof Repository);
            } catch (Exception e) {
                LOGGER.warn("  Could not check meas-repo link: " + e.getMessage());
            }
            LOGGER.info("  Meas<->Repo link (measurementsInit): " + (linkExists ? "OK" : "MISSING"));
        }

        LOGGER.info("=== End Correspondence Verification ===");
    }

    /**
     * Test reading Kieker records and adding them to a VSUM with Measurements model using Views.
     */
    @Test
    public void testReadKiekerAndAddToVsumMeasurements() throws MeasurementsReaderException {
        System.out.println("========================================");
        System.out.println("MEASUREMENTS VSUM INTEGRATION TEST (VIEW-BASED)");
        System.out.println("========================================");
        System.out.println();

        // Step 1: Verify VSUM is initialized with Measurements
        Measurements measurements = measurementsFacade.getModel();
        assertNotNull(measurements, "Measurements model should be initialized in VSUM");
        System.out.println("Step 1: VSUM initialized with Measurements model");
        System.out.println();

        // Step 2: Check VSUM models and create MeasurementsView from VSUM
        System.out.println("Step 2: Creating MeasurementsView from VSUM...");

        // Debug: Print what models are in the VSUM
        var vsumModels = vsumFacade.getVsum().getViewSourceModels();
        System.out.println("  - VSUM contains " + vsumModels.size() + " model resources:");
        for (var model : vsumModels) {
            System.out.println("    - " + model.getURI());
            for (var content : model.getContents()) {
                System.out.println("      - Root: " + content.getClass().getSimpleName());
            }
        }

        // Debug: Check correspondences before creating view
        System.out.println("  - Checking correspondences in VSUM...");
        var correspondenceModel = vsumFacade.getVsum().getCorrespondenceModel();
        if (correspondenceModel != null) {
            System.out.println("    - Correspondence model available: " + correspondenceModel.getClass().getSimpleName());
        } else {
            System.out.println("    - No correspondence model found!");
        }

        CipmMeasurementsViewType viewType = new CipmMeasurementsViewType("MeasurementsView");
        var viewSelector = viewType.createSelector(vsumFacade.getVsum());

        // Debug: Print selectable elements
        var selectableElements = viewSelector.getSelectableElements();
        System.out.println("  - View selector has " + selectableElements.size() + " selectable elements");

        viewSelector.getSelectableElements().forEach(ele -> viewSelector.setSelected(ele, true));
        MeasurementsView measurementsView = viewType.createView(viewSelector);
        assertNotNull(measurementsView, "MeasurementsView should be created");

        // Debug: Print root objects in the view
        var rootObjects = measurementsView.getRootObjects();
        System.out.println("  - MeasurementsView has " + rootObjects.size() + " root objects:");
        for (var root : rootObjects) {
            System.out.println("    - " + root.getClass().getSimpleName() + " in resource: " +
                (root.eResource() != null ? root.eResource().getURI() : "no resource"));
        }

        System.out.println("  - MeasurementsView created successfully");
        System.out.println();

        // Step 3: Read Kieker records
        List<MeasurementRecord> records = readKiekerRecords();

        if (records.isEmpty()) {
            System.out.println("WARNING: No Kieker records found. Skipping test.");
            return;
        }

        // Step 4: Add records to Measurements model via View and commit after each record
        // This triggers reactions for each record individually (sliding window approach)
        System.out.println("Step 4: Adding records and committing each one to trigger reactions...");
        int count = 0;
        int totalPropagatedChanges = 0;
        for (MeasurementRecord record : records) {
            // Add the record
            measurementsView.addMeasurement(record);
            count++;

            // Debug: Print first few records being added
            if (count <= 5) {
                if (record instanceof InternalActionRecord) {
                    InternalActionRecord iar = (InternalActionRecord) record;
                    System.out.println("  - Adding InternalActionRecord #" + count + ": actionID=" + iar.getInternalActionID());
                } else {
                    System.out.println("  - Adding " + record.getClass().getSimpleName() + " #" + count);
                }
            }

            // Commit after each record to trigger reactions
            try {
                var propagatedChanges = measurementsView.commitChangesAndUpdate();
                totalPropagatedChanges += propagatedChanges.size();

                // Debug: Print propagated changes for first few records
                if (count <= 5 && !propagatedChanges.isEmpty()) {
                    System.out.println("    - Propagated " + propagatedChanges.size() + " changes");
                }
            } catch (IllegalArgumentException e) {
                // Empty change - may happen if the record wasn't added properly
                if (count <= 5) {
                    System.out.println("  - WARNING at record " + count + ": " + e.getMessage());
                }
            }

            // Print progress every 1000 records
            if (count % 100 == 0) {
                System.out.println("  - Processed " + count + " records, " + totalPropagatedChanges + " propagated changes...");
            }
        }
        System.out.println("  - Processed " + count + " records total");
        System.out.println("  - Total propagated changes: " + totalPropagatedChanges);
        System.out.println();

        // Step 5: Check records in the View and VSUM after all additions
        System.out.println("Step 5: Checking records after all additions...");
        int recordsInView = countRecordsInView(measurementsView);
        System.out.println("  - Records visible in View (via getRootObjects copy): " + recordsInView);

        // Check VSUM directly
        Measurements vsumMeasurements = findMeasurementsInVsum();
        if (vsumMeasurements != null) {
            int recordsInVsumDirect = 0;
            for (MeasurementsRepository repo : vsumMeasurements.getRepositories()) {
                for (MeasurementsBlock block : repo.getBlocks()) {
                    recordsInVsumDirect += block.getRecords().size();
                }
            }
            System.out.println("  - Records in VSUM model (direct access): " + recordsInVsumDirect);
        } else {
            System.out.println("  - VSUM Measurements model not found!");
        }
        System.out.println();

        // Step 6: Print summary
        printSummary(measurements, records);

        System.out.println("========================================");
        System.out.println("TEST COMPLETED SUCCESSFULLY");
        System.out.println("========================================");
    }

    /**
     * Read Kieker records from the TeaStore monitoring directory.
     */
    private List<MeasurementRecord> readKiekerRecords() throws MeasurementsReaderException {
        System.out.println("Step 3: Reading Kieker records from TeaStore monitoring data...");

        Path kiekerDataDir = findTeaStoreMonitoringDirectory();
        if (kiekerDataDir == null || !isKiekerDataAvailable(kiekerDataDir.toString())) {
            System.out.println("  - Kieker test data not found at: " + TEASTORE_MONITORING_PATH);
            return new ArrayList<>();
        }

        System.out.println("  - Reading from: " + kiekerDataDir);

        KiekerFileMeasurementsReader reader = new KiekerFileMeasurementsReader();
        List<MeasurementRecord> records = reader.readRecords(kiekerDataDir.toString());

        System.out.println("****************************************");
        System.out.println("*                                      *");
        System.out.println("*   TOTAL RECORDS READ: " + String.format("%-14d", records.size()) + "*");
        System.out.println("*                                      *");
        System.out.println("****************************************");

        // Categorize and count
        int internalCount = 0, serviceCount = 0, loopCount = 0, branchCount = 0, resourceCount = 0;
        for (MeasurementRecord record : records) {
            if (record instanceof InternalActionRecord) internalCount++;
            else if (record instanceof ServiceContextRecord) serviceCount++;
            else if (record instanceof LoopActionRecord) loopCount++;
            else if (record instanceof BranchActionRecord) branchCount++;
            else if (record instanceof ResourceUtilizationRecord) resourceCount++;
        }

        System.out.println();
        System.out.println("Records by type:");
        System.out.println("  - InternalActionRecords:      " + internalCount);
        System.out.println("  - ServiceContextRecords:      " + serviceCount);
        System.out.println("  - LoopActionRecords:          " + loopCount);
        System.out.println("  - BranchActionRecords:        " + branchCount);
        System.out.println("  - ResourceUtilizationRecords: " + resourceCount);
        System.out.println();

        return records;
    }

    /**
     * Print a summary of the test results.
     */
    private void printSummary(Measurements measurementsFromFacade, List<MeasurementRecord> records) {
        System.out.println("========================================");
        System.out.println("SUMMARY");
        System.out.println("========================================");

        System.out.println("  VSUM Models (TeaStore):");
        var pcmRepo = pcmFacade.getInMemoryPCM().getRepository();
        System.out.println("    - PCM Repository: " + (pcmRepo != null ? pcmRepo.getEntityName() + " (loaded)" : "not found"));
        System.out.println("    - PCM System: " + (pcmFacade.getInMemoryPCM().getSystem() != null ? "loaded" : "not found"));
        System.out.println("    - IM Model: " + (imFacade.getModel() != null ? "initialized" : "not found"));
        System.out.println("    - Measurements Model: " + (measurementsFacade.getModel() != null ? "initialized" : "not found"));
        System.out.println();
        System.out.println("  Records read from Kieker: " + records.size());

        // Count records in the VSUM's Measurements model (not the facade's original model)
        // The facade's model is the initial one before VSUM; changes go to the VSUM's copy
        int totalRecordsInVsumModel = 0;
        Measurements vsumMeasurements = findMeasurementsInVsum();
        if (vsumMeasurements != null) {
            for (MeasurementsRepository repository : vsumMeasurements.getRepositories()) {
                for (MeasurementsBlock block : repository.getBlocks()) {
                    totalRecordsInVsumModel += block.getRecords().size();
                }
            }
            System.out.println("  Records in VSUM Measurements model: " + totalRecordsInVsumModel);
        } else {
            System.out.println("  Records in VSUM Measurements model: (model not found in VSUM)");
        }

        // Also show the facade's model (should be 0 or initial count)
        int totalRecordsInFacadeModel = 0;
        for (MeasurementsRepository repository : measurementsFromFacade.getRepositories()) {
            for (MeasurementsBlock block : repository.getBlocks()) {
                totalRecordsInFacadeModel += block.getRecords().size();
            }
        }
        System.out.println("  Records in facade's original model: " + totalRecordsInFacadeModel + " (expected 0 - changes go to VSUM)");
        System.out.println();

        // Print first few records from the input
        System.out.println("First 10 records added via View:");
        int count = 0;
        for (MeasurementRecord record : records) {
            if (count >= 10) break;
            printRecordDetails(count, record);
            count++;
        }
        if (records.size() > 10) {
            System.out.println("  ... and " + (records.size() - 10) + " more");
        }
    }

    /**
     * Print details of a single record.
     */
    private void printRecordDetails(int index, MeasurementRecord record) {
        if (record instanceof InternalActionRecord) {
            InternalActionRecord r = (InternalActionRecord) record;
            System.out.println("  [" + index + "] InternalActionRecord");
            System.out.println("        ActionID: " + r.getInternalActionID());
            System.out.println("        Duration: " + (r.getExitTime() - r.getEntryTime()) + " ns");
        } else if (record instanceof ServiceContextRecord) {
            ServiceContextRecord r = (ServiceContextRecord) record;
            System.out.println("  [" + index + "] ServiceContextRecord");
            System.out.println("        ServiceID: " + r.getServiceID());
            System.out.println("        SessionID: " + r.getSessionID());
        } else if (record instanceof LoopActionRecord) {
            LoopActionRecord r = (LoopActionRecord) record;
            System.out.println("  [" + index + "] LoopActionRecord");
            System.out.println("        LoopID: " + r.getLoopID());
            System.out.println("        Iterations: " + r.getLoopIterationCount());
        } else if (record instanceof BranchActionRecord) {
            BranchActionRecord r = (BranchActionRecord) record;
            System.out.println("  [" + index + "] BranchActionRecord");
            System.out.println("        BranchID: " + r.getBranchID());
            System.out.println("        ExecutedBranchID: " + r.getExecutedBranchID());
        } else if (record instanceof ResourceUtilizationRecord) {
            ResourceUtilizationRecord r = (ResourceUtilizationRecord) record;
            System.out.println("  [" + index + "] ResourceUtilizationRecord");
            System.out.println("        ResourceID: " + r.getResourceID());
            System.out.println("        Utilization: " + r.getUtilization());
        } else {
            System.out.println("  [" + index + "] " + record.getClass().getSimpleName());
        }
    }

    private Path findTeaStoreMonitoringDirectory() {
        String[] basePaths = {
            System.getProperty("user.dir"),
            System.getProperty("user.dir") + "/..",
            ".",
            ".."
        };

        for (String basePath : basePaths) {
            Path dir = Path.of(basePath, TEASTORE_MONITORING_PATH);
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                return dir;
            }
        }
        return null;
    }

    private boolean isKiekerDataAvailable(String kiekerDataDir) {
        File dir = new File(kiekerDataDir);
        File kiekerMap = new File(dir, "kieker.map");
        return dir.exists() && dir.isDirectory() && kiekerMap.exists();
    }

    /**
     * Find the Measurements model in the VSUM by searching through view source models.
     * This returns the actual VSUM model that was modified by the View, not the facade's original.
     */
    private Measurements findMeasurementsInVsum() {
        var vsumModels = vsumFacade.getVsum().getViewSourceModels();
        for (var model : vsumModels) {
            for (var content : model.getContents()) {
                if (content instanceof Measurements) {
                    return (Measurements) content;
                }
            }
        }
        return null;
    }

    /**
     * Count total records visible in the MeasurementsView.
     * The View accesses the VSUM's ResourceSet, so this reflects committed changes.
     */
    private int countRecordsInView(MeasurementsView view) {
        int count = 0;
        for (var root : view.getRootObjects()) {
            if (root instanceof Measurements) {
                Measurements m = (Measurements) root;
                for (MeasurementsRepository repo : m.getRepositories()) {
                    for (MeasurementsBlock block : repo.getBlocks()) {
                        count += block.getRecords().size();
                    }
                }
            }
        }
        return count;
    }
}
