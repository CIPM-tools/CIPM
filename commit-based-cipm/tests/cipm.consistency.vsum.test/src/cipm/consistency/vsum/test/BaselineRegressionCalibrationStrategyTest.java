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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.seff.InternalAction;

import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.bridge.monitoring.records.PCMContextRecord;
import cipm.consistency.cpr.measurementshelper.MeasurementsHelper;
import cipm.consistency.cpr.measurementshelper.RegressionMeasurementsHelper;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.Measurements;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.MeasurementsPackage;
import cipm.consistency.measurements.MeasurementsRepository;
import cipm.consistency.measurements.ServiceContextRecord;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import cipm.consistency.measurements.reader.kieker.KiekerFileMeasurementsReader;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.measurements.MeasurementsFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.VsumFacadeImpl;
import cipm.consistency.vsum.test.validation.SelfValidationExecutor;
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
 * Baseline integration test for the Exponential Moving Average (EMA) regression
 * calibration strategy.
 *
 * <p>Uses very small window size and trigger time so that every single
 * {@link InternalActionRecord} triggers a PCM update and only the current
 * record is analysed. This provides a baseline where each record directly
 * updates the smoothed estimate without any windowed aggregation.</p>
 *
 * @author Manar Mazkatli
 */
public class BaselineRegressionCalibrationStrategyTest {

    private static final Logger LOGGER = Logger.getLogger(BaselineRegressionCalibrationStrategyTest.class);

    /** Pre-prepared PCM models (input) */
    private static final Path TEST_INPUT_PATH = Path.of("testData", "shortMeasurementsTest");

    /** Results directory (output) */
    private static final Path TEST_RESULTS_PATH = Path.of("testData", "baselineRegressionCalibrationTest_results");

    /** Directory where block files are persisted */
    private static final Path BLOCKS_OUTPUT_PATH = TEST_RESULTS_PATH.resolve("blocks");

    /** TeaStore Kieker monitoring data path */
    private static final String TEASTORE_MONITORING_PATH =
        "testData" + File.separator + "kieker";

    /** Block duration in nanoseconds (15 seconds per block) */
    private static final long BLOCK_DURATION_NS = 15_000_000_000L;

    /** Very small sliding window so only the current record is analysed */
    private static final long WINDOW_SIZE_MS = 1L;

    /** Very small trigger time so every record triggers an update */
    private static final long TRIGGER_TIME_MS = 1L;

    /** Exponential smoothing factor (0.0–1.0) */
    private static final double ALPHA = 0.3;

    private VsumFacadeImpl vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private MeasurementsFacade measurementsFacade;
    private RegressionMeasurementsHelper regressionHelper;

    /** Initial resource demands for comparison */
    private Map<String, String> initialResourceDemands = new HashMap<>();

    @BeforeAll
    public static void setupLogging() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        Logger.getLogger("tools.vitruv").setLevel(Level.WARN);
        Logger.getLogger("mir.reactions").setLevel(Level.DEBUG);
        Logger.getLogger("cipm.consistency.cpr").setLevel(Level.DEBUG);

        PCMUtils.loadPCMModels();
        PCMUtils.initVitruvius();
    }

    @BeforeEach
    public void setUp() throws IOException {
        System.out.println("Working directory: " + Path.of(".").toAbsolutePath());
        System.out.println("Results path: " + TEST_RESULTS_PATH.toAbsolutePath());
        cleanupResultsDirectory();

        Files.createDirectories(TEST_RESULTS_PATH.resolve("im"));
        Files.createDirectories(TEST_RESULTS_PATH.resolve("measurements"));
        Files.createDirectories(TEST_RESULTS_PATH.resolve("vsum"));
        Files.createDirectories(BLOCKS_OUTPUT_PATH);

        // Configure baseline regression calibration strategy with tiny window/trigger
        regressionHelper = new RegressionMeasurementsHelper(WINDOW_SIZE_MS, TRIGGER_TIME_MS, ALPHA);
        MeasurementsHelper.setImplementation(regressionHelper);
        LOGGER.info("Baseline regression calibration strategy configured: window=" + WINDOW_SIZE_MS
            + "ms, trigger=" + TRIGGER_TIME_MS + "ms, alpha=" + ALPHA);

        pcmFacade = new PcmFacade();
        imFacade = new ImFacade();
        measurementsFacade = new MeasurementsFacade();
        vsumFacade = new VsumFacadeImpl();
        vsumFacade.setHeadlessMode(true);

        pcmFacade.initialize(TEST_INPUT_PATH.resolve("pcm"));
        imFacade.initialize(TEST_RESULTS_PATH.resolve("im"));
        measurementsFacade.initialize(TEST_RESULTS_PATH.resolve("measurements"));

        // Capture initial resource demands before VSUM processing
        captureInitialResourceDemands();

        List<ChangePropagationSpecification> changeSpecs = new ArrayList<>();
        changeSpecs.add(new PcmInitChangePropagationSpecification());
        changeSpecs.add(new MeasurementsInitChangePropagationSpecification());
        changeSpecs.add(new MeasurementsPcmUpdateChangePropagationSpecification());

        vsumFacade.initialize(
            TEST_RESULTS_PATH.resolve("vsum"),
            List.of(pcmFacade, imFacade, measurementsFacade),
            changeSpecs,
            new DefaultStateBasedChangeResolutionStrategy()
        );

        LOGGER.info("VSUM initialized with baseline regression calibration strategy");
        verifyCorrespondences();
    }

    @AfterEach
    public void tearDown() {
        // Reset to default strategy
        MeasurementsHelper.resetToDefault();
        if (vsumFacade != null && vsumFacade.getVsum() != null) {
            vsumFacade.getVsum().dispose();
        }
    }

    @Test
    public void testBaselineRegressionCalibrationStrategy() throws Exception, IOException {
        System.out.println("========================================");
        System.out.println("BASELINE REGRESSION (EMA) CALIBRATION STRATEGY TEST");
        System.out.println("========================================");
        System.out.println("Strategy: RegressionMeasurementsHelper (baseline — every record triggers update)");
        System.out.println("Window: " + WINDOW_SIZE_MS + "ms, Trigger: " + TRIGGER_TIME_MS
            + "ms, Alpha: " + ALPHA);
        System.out.println();

        // Step 1: Read ALL Kieker records
        List<MeasurementRecord> allRecords = readAllKiekerRecords();
        if (allRecords.isEmpty()) {
            System.out.println("WARNING: No Kieker records found. Skipping test.");
            return;
        }
        SelfValidationExecutor selfValidation = new SelfValidationExecutor(readAllOriginalKiekerRecords());

        // Count InternalActionRecords
        long internalCount = allRecords.stream()
            .filter(r -> r instanceof InternalActionRecord).count();
        System.out.println("InternalActionRecords (will use regression): " + internalCount);
        System.out.println();

        // Step 2: Split into time-based blocks
        List<List<MeasurementRecord>> blocks = splitIntoBlocks(allRecords);
        System.out.println("Split into " + blocks.size() + " blocks (" + (BLOCK_DURATION_NS / 1_000_000_000L) + "s each)");
        System.out.println();
        allRecords = null;

        // Step 3: Create MeasurementsView
        CipmMeasurementsViewType viewType = new CipmMeasurementsViewType("MeasurementsView");
        var viewSelector = viewType.createSelector(vsumFacade.getVsum());
        MeasurementsView measurementsView = viewType.createView(viewSelector);
        assertNotNull(measurementsView, "MeasurementsView should be created");

        // Step 4: Process each block with baseline regression strategy
        int totalPropagatedChanges = 0;
        int totalRecordsProcessed = 0;
        int totalInternalActions = 0;
        RecordPropagationPerformanceData performanceData = new RecordPropagationPerformanceData();
        selfValidation.validate(this.pcmFacade.getInMemoryPCM(), 0);

        for (int blockIdx = 0; blockIdx < blocks.size(); blockIdx++) {
            List<MeasurementRecord> blockRecords = blocks.get(blockIdx);
            long blockStart = System.currentTimeMillis();

            int blockInternalCount = (int) blockRecords.stream()
                .filter(r -> r instanceof InternalActionRecord).count();

            System.out.println("--- Block " + (blockIdx + 1) + "/" + blocks.size()
                + " (" + blockRecords.size() + " records, " + blockInternalCount + " internal actions) ---");

            // 4a: Add each record and commit individually
            int blockChanges = 0;
            for (int i = 0; i < blockRecords.size(); i++) {
                measurementsView.addMeasurement(blockRecords.get(i));

                try {
                	long time = System.currentTimeMillis();
                    var propagatedChanges = measurementsView.commitChangesAndUpdate();
                    time = System.currentTimeMillis() - time;
                    
                    performanceData.addPerformance(totalRecordsProcessed + i, time);
                    blockChanges += propagatedChanges.size();
                } catch (IllegalArgumentException e) {
                    // Empty change
                }

                if ((i + 1) % 100 == 0) {
                	if (MeasurementsHelper.wasTriggered()) {
                    	selfValidation.validate(this.pcmFacade.getInMemoryPCM(), totalRecordsProcessed + i);
            		}
                    System.out.println("  " + (i + 1) + "/" + blockRecords.size() + " records...");
                }
            }

            totalPropagatedChanges += blockChanges;
            totalRecordsProcessed += blockRecords.size();
            totalInternalActions += blockInternalCount;

            // 4b: Persist block to disk from view
            persistBlockFromView(blockIdx, measurementsView);

            // 4c: Clear records from VSUM memory
            int cleared = measurementsView.clearCurrentBlock();
            try {
                measurementsView.commitChangesAndUpdate();
            } catch (IllegalArgumentException e) {
                // Empty change after clear
            }

            // 4d: Start a new block for the next interval
            if (blockIdx < blocks.size() - 1) {
                measurementsView.startNewBlock();
                try {
                    measurementsView.commitChangesAndUpdate();
                } catch (IllegalArgumentException e) {
                    // Empty change
                }
            }

            long blockDuration = System.currentTimeMillis() - blockStart;
            System.out.println("  Changes: " + blockChanges + ", Cleared: " + cleared
                + ", Duration: " + blockDuration + "ms");

            // Print regression state after this block
            int trackedActions = regressionHelper.getAllEstimates().size();
            System.out.println("  Regression: " + trackedActions + " actions tracked so far");
            System.out.println();

            blocks.set(blockIdx, null);
        }
        
        selfValidation.validate(this.pcmFacade.getInMemoryPCM(), totalRecordsProcessed);
        selfValidation.saveResults(TEST_RESULTS_PATH.resolve("self-validation.json"));
        performanceData.saveData(TEST_RESULTS_PATH.resolve("record-propagation-performance.json"));

        // Step 5: Print regression results
        System.out.println("========================================");
        System.out.println("BASELINE REGRESSION CALIBRATION RESULTS");
        System.out.println("========================================");
        System.out.println("Total records processed: " + totalRecordsProcessed);
        System.out.println("Total InternalActionRecords: " + totalInternalActions);
        System.out.println("Total propagated changes: " + totalPropagatedChanges);
        System.out.println("Alpha (smoothing factor): " + ALPHA);
        System.out.println();

        printRegressionResults();

        // Step 6: Check resource demand updates in VSUM's PCM
        System.out.println();
        System.out.println("--- Resource Demand Changes (VSUM vs Initial) ---");
        Repository vsumRepo = findRepositoryInVsum();
        int updatedCount = checkAndPrintResourceDemandChanges(vsumRepo);
        System.out.println("InternalActions updated: " + updatedCount + " / " + initialResourceDemands.size());

        // Step 7: Export VSUM's PCM to results folder for inspection
        if (vsumRepo != null) {
            exportVsumPcmToResults(vsumRepo);
            System.out.println("VSUM PCM exported to: " + TEST_RESULTS_PATH.resolve("pcm").toAbsolutePath());
        }

        // Step 8: Verify block files
        long blockFileCount = Files.list(BLOCKS_OUTPUT_PATH)
            .filter(p -> p.toString().endsWith(".measurements"))
            .count();
        System.out.println();
        System.out.println("Block files on disk: " + blockFileCount);
        System.out.println("Block files path: " + BLOCKS_OUTPUT_PATH.toAbsolutePath());
        assertTrue(blockFileCount > 0, "Block files should be persisted to disk");

        // Step 9: Replace empty blocks in VSUM with proxy references to block files
        replaceVsumBlocksWithProxies(blocks.size());

        // Step 10: Export VSUM's measurements model (blocks now have proxy hrefs)
        exportVsumMeasurementsToResults();

        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST COMPLETED");
        System.out.println("========================================");
    }

    // ===============================
    // Regression Output
    // ===============================

    private void printRegressionResults() {
        Repository repo = pcmFacade.getInMemoryPCM().getRepository();
        if (repo == null) {
            System.out.println("  No PCM Repository available");
            return;
        }

        System.out.println("Regression Estimates per InternalAction:");
        System.out.println("-----------------------------------------");

        int count = 0;
        for (var component : repo.getComponents__Repository()) {
            var iter = component.eAllContents();
            while (iter.hasNext()) {
                EObject obj = iter.next();
                if (obj instanceof InternalAction) {
                    InternalAction action = (InternalAction) obj;
                    String actionId = action.getId();
                    double estimate = regressionHelper.getEstimate(actionId);

                    if (!Double.isNaN(estimate)) {
                        count++;
                        int observations = regressionHelper.getObservationCount(actionId);

                        System.out.println("  Action: " + action.getEntityName()
                            + " (ID: " + actionId + ")");
                        System.out.println("    Observations: " + observations);
                        System.out.println("    Smoothed estimate: "
                            + String.format("%.2f ns (%.2f ms)", estimate, estimate / 1_000_000.0));
                        System.out.println("    Alpha: " + regressionHelper.getAlpha());
                        System.out.println();
                    }
                }
            }
        }

        if (count == 0) {
            System.out.println("  No regression data generated for PCM InternalActions.");
            System.out.println("  (Action IDs in monitoring data may not match PCM model)");
        }
        System.out.println("Total actions with regression data: " + count);
    }

    // ===============================
    // Resource Demand Comparison
    // ===============================

    private void captureInitialResourceDemands() {
        Repository repo = pcmFacade.getInMemoryPCM().getRepository();
        if (repo == null) return;

        repo.getComponents__Repository().forEach(component -> {
            component.eAllContents().forEachRemaining(obj -> {
                if (obj instanceof InternalAction) {
                    InternalAction action = (InternalAction) obj;
                    String spec = getResourceDemandSpec(action);
                    initialResourceDemands.put(action.getId(), spec);
                }
            });
        });
        LOGGER.info("Captured " + initialResourceDemands.size() + " initial resource demands");
    }

    private Repository findRepositoryInVsum() {
        for (var model : vsumFacade.getVsum().getViewSourceModels()) {
            for (var content : model.getContents()) {
                if (content instanceof Repository) {
                    return (Repository) content;
                }
            }
        }
        LOGGER.warn("No PCM Repository found in VSUM");
        return null;
    }

    private int checkAndPrintResourceDemandChanges(Repository vsumRepo) {
        if (vsumRepo == null) {
            System.out.println("  No PCM Repository in VSUM");
            return 0;
        }

        int updatedCount = 0;
        int totalActions = 0;

        for (var component : vsumRepo.getComponents__Repository()) {
            var iter = component.eAllContents();
            while (iter.hasNext()) {
                EObject obj = iter.next();
                if (obj instanceof InternalAction) {
                    InternalAction action = (InternalAction) obj;
                    totalActions++;

                    String currentSpec = getResourceDemandSpec(action);
                    String initialSpec = initialResourceDemands.get(action.getId());

                    if (initialSpec != null && !initialSpec.equals(currentSpec)) {
                        updatedCount++;
                        System.out.println("  CHANGED: " + action.getEntityName()
                            + " (ID: " + action.getId() + ")");
                        System.out.println("    Before: " + initialSpec);
                        System.out.println("    After:  " + currentSpec);
                    }
                }
            }
        }

        System.out.println("  Total InternalActions in VSUM PCM: " + totalActions);
        return updatedCount;
    }

    private void exportVsumPcmToResults(Repository vsumRepo) {
        try {
            Path pcmOutputDir = TEST_RESULTS_PATH.resolve("pcm");
            Files.createDirectories(pcmOutputDir);

            Repository copy = (Repository) org.eclipse.emf.ecore.util.EcoreUtil.copy(vsumRepo);

            Path filePath = pcmOutputDir.resolve("Repository.repository");
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("repository", new XMIResourceFactoryImpl());

            Resource resource = resourceSet.createResource(
                URI.createFileURI(filePath.toAbsolutePath().toString()));
            resource.getContents().add(copy);
            resource.save(null);

            resource.unload();
            resourceSet.getResources().clear();
        } catch (IOException e) {
            LOGGER.warn("Failed to export VSUM PCM: " + e.getMessage());
        }
    }

    private String getResourceDemandSpec(InternalAction action) {
        if (action.getResourceDemand_Action().isEmpty()) {
            return "EMPTY";
        }
        return action.getResourceDemand_Action().stream()
            .map(rd -> rd.getSpecification_ParametericResourceDemand() != null
                ? rd.getSpecification_ParametericResourceDemand().getSpecification()
                : "null")
            .collect(Collectors.joining(","));
    }

    // ===============================
    // Kieker Records
    // ===============================

    private List<MeasurementRecord> readAllKiekerRecords() throws MeasurementsReaderException {
        Path monitoringDir = findMonitoringDirectory();
        if (monitoringDir == null) return new ArrayList<>();

        System.out.println("Reading Kieker records from: " + monitoringDir);
        KiekerFileMeasurementsReader reader = new KiekerFileMeasurementsReader();
        List<MeasurementRecord> records = reader.readRecords(monitoringDir.toString());
        System.out.println("Total records read: " + records.size());

        int internal = 0, service = 0, other = 0;
        for (MeasurementRecord r : records) {
            if (r instanceof InternalActionRecord) internal++;
            else if (r instanceof ServiceContextRecord) service++;
            else other++;
        }
        System.out.println("  InternalAction: " + internal + ", Service: " + service + ", Other: " + other);
        return records;
    }
    
    private List<PCMContextRecord> readAllOriginalKiekerRecords() throws MeasurementsReaderException {
    	return new KiekerFileMeasurementsReader()
    		.readKiekerRecords(findMonitoringDirectory().toString())
			.stream()
			.filter(PCMContextRecord.class::isInstance)
			.map(PCMContextRecord.class::cast)
			.collect(Collectors.toList());
    }

    private List<List<MeasurementRecord>> splitIntoBlocks(List<MeasurementRecord> records) {
        if (records.isEmpty()) return new ArrayList<>();

        long minTimestamp = Long.MAX_VALUE;
        for (MeasurementRecord record : records) {
            long ts = getRecordTimestamp(record);
            if (ts > 0 && ts < minTimestamp) minTimestamp = ts;
        }

        List<List<MeasurementRecord>> blocks = new ArrayList<>();
        List<MeasurementRecord> currentBlock = new ArrayList<>();
        long currentBlockEnd = minTimestamp + BLOCK_DURATION_NS;

        for (MeasurementRecord record : records) {
            long ts = getRecordTimestamp(record);

            if (ts <= 0) {
                currentBlock.add(record);
                continue;
            }

            while (ts > currentBlockEnd) {
                if (!currentBlock.isEmpty()) {
                    blocks.add(currentBlock);
                    currentBlock = new ArrayList<>();
                }
                currentBlockEnd += BLOCK_DURATION_NS;
            }

            currentBlock.add(record);
        }

        if (!currentBlock.isEmpty()) {
            blocks.add(currentBlock);
        }

        return blocks;
    }

    // ===============================
    // Block Persistence
    // ===============================

    private void persistBlockFromView(int blockIndex, MeasurementsView view) throws IOException {
        Measurements viewCopy = null;
        for (var root : view.getRootObjects()) {
            if (root instanceof Measurements) {
                viewCopy = (Measurements) root;
                break;
            }
        }

        if (viewCopy == null || viewCopy.getRepositories().isEmpty()) {
            LOGGER.warn("No measurements in view for block " + blockIndex);
            return;
        }

        String fileName = String.format("block_%04d.measurements", blockIndex);
        Path filePath = BLOCKS_OUTPUT_PATH.resolve(fileName);

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("measurements", new XMIResourceFactoryImpl());

        Resource resource = resourceSet.createResource(
            URI.createFileURI(filePath.toAbsolutePath().toString()));
        resource.getContents().add(viewCopy);
        resource.save(null);

        resource.unload();
        resourceSet.getResources().clear();
    }

    /**
     * Replace empty blocks in the VSUM's Measurements model with EMF proxy references
     * to the persisted block files on disk. Called after all processing is done.
     */
    private void replaceVsumBlocksWithProxies(int totalBlocks) {
        Measurements vsumMeas = findMeasurementsInVsum();
        if (vsumMeas == null || vsumMeas.getRepositories().isEmpty()) {
            LOGGER.warn("No Measurements model in VSUM for proxy replacement");
            return;
        }

        MeasurementsRepository repo = vsumMeas.getRepositories().get(0);
        repo.getBlocks().clear();

        int proxyCount = 0;
        for (int i = 0; i < totalBlocks; i++) {
            String blockFileName = String.format("block_%04d.measurements", i);
            Path blockFilePath = BLOCKS_OUTPUT_PATH.resolve(blockFileName);

            if (Files.exists(blockFilePath)) {
                MeasurementsBlock proxyBlock = MeasurementsFactory.eINSTANCE.createMeasurementsBlock();
                URI blockURI = URI.createFileURI(blockFilePath.toAbsolutePath().toString());
                URI fragmentURI = blockURI.appendFragment("//@repositories.0/@blocks.0");
                ((InternalEObject) proxyBlock).eSetProxyURI(fragmentURI);
                repo.getBlocks().add(proxyBlock);
                proxyCount++;
            }
        }

        System.out.println();
        System.out.println("--- VSUM Proxy Replacement ---");
        System.out.println("  Replaced " + proxyCount + " empty blocks with proxy references");
    }

    /**
     * Export the VSUM's measurements model to the results directory.
     * Blocks should already contain proxy hrefs from replaceVsumBlocksWithProxies().
     */
    private void exportVsumMeasurementsToResults() {
        Measurements vsumMeas = findMeasurementsInVsum();
        if (vsumMeas == null) {
            LOGGER.warn("No Measurements model in VSUM to export");
            return;
        }

        try {
            Path measOutputPath = TEST_RESULTS_PATH.resolve("measurements").resolve("measurements_vsum.measurements");

            ResourceSet rs = new ResourceSetImpl();
            rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("measurements", new XMIResourceFactoryImpl());

            Resource resource = rs.createResource(
                URI.createFileURI(measOutputPath.toAbsolutePath().toString()));
            resource.getContents().add(vsumMeas);
            resource.save(null);

            int blockCount = 0;
            if (!vsumMeas.getRepositories().isEmpty()) {
                blockCount = vsumMeas.getRepositories().get(0).getBlocks().size();
            }

            System.out.println();
            System.out.println("--- VSUM Measurements Export ---");
            System.out.println("  File: " + measOutputPath.toAbsolutePath());
            System.out.println("  Blocks: " + blockCount + " (with proxy hrefs to block files)");
        } catch (IOException e) {
            LOGGER.warn("Failed to export VSUM measurements: " + e.getMessage());
        }
    }

    // ===============================
    // Utilities
    // ===============================

    private long getRecordTimestamp(MeasurementRecord record) {
        if (record instanceof ServiceContextRecord) {
            return ((ServiceContextRecord) record).getEntryTime();
        } else if (record instanceof InternalActionRecord) {
            return ((InternalActionRecord) record).getEntryTime();
        }
        return -1;
    }

    private Path findMonitoringDirectory() {
        String[] basePaths = {
            System.getProperty("user.dir"),
            System.getProperty("user.dir") + "/..",
            ".", ".."
        };
        for (String basePath : basePaths) {
            Path dir = Path.of(basePath, TEASTORE_MONITORING_PATH);
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                return dir;
            }
        }
        LOGGER.warn("Monitoring directory not found");
        return null;
    }

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

    private void verifyCorrespondences() {
        EditableCorrespondenceModelView<Correspondence> corrView = vsumFacade.getCorrespondenceView();
        if (corrView == null) {
            LOGGER.error("Cannot access correspondence model view");
            return;
        }

        boolean repoOk = false, measOk = false, linkOk = false;
        try {
            repoOk = corrView.getCorrespondingEObjects(RepositoryPackage.Literals.REPOSITORY, null)
                .stream().anyMatch(e -> e instanceof Repository);
        } catch (Exception e) { /* ignore */ }

        try {
            measOk = corrView.getCorrespondingEObjects(MeasurementsPackage.Literals.MEASUREMENTS, null)
                .stream().anyMatch(e -> e instanceof Measurements);
        } catch (Exception e) { /* ignore */ }

        Measurements vsumMeas = findMeasurementsInVsum();
        if (vsumMeas != null) {
            try {
                linkOk = corrView.getCorrespondingEObjects(vsumMeas, null)
                    .stream().anyMatch(e -> e instanceof Repository);
            } catch (Exception e) { /* ignore */ }
        }

        LOGGER.info("Correspondences: Repo=" + (repoOk ? "OK" : "MISSING")
            + ", Meas=" + (measOk ? "OK" : "MISSING")
            + ", Link=" + (linkOk ? "OK" : "MISSING"));
    }

    private Measurements findMeasurementsInVsum() {
        for (var model : vsumFacade.getVsum().getViewSourceModels()) {
            for (var content : model.getContents()) {
                if (content instanceof Measurements) {
                    return (Measurements) content;
                }
            }
        }
        return null;
    }
}
