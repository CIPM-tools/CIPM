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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.palladiosimulator.pcm.seff.InternalAction;

import cipm.consistency.base.shared.pcm.util.PCMUtils;
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
 * Integration test that processes ALL Kieker records with bounded memory.
 *
 * <p>Records are split into time-based blocks. Each record is committed
 * individually (triggering reactions). At the end of each block, all records
 * are persisted to a separate file on disk and cleared from VSUM memory.
 * A new block is created for the next batch of records.</p>
 *
 * <p>Result: all records stored in numbered block files, VSUM memory stays bounded.</p>
 *
 * @author Manar Mazkatli
 */
public class BlockProcessingMeasurementsTest {

    private static final Logger LOGGER = Logger.getLogger(BlockProcessingMeasurementsTest.class);

    /** Pre-prepared PCM models (input) */
    private static final Path TEST_INPUT_PATH = Path.of("testData", "shortMeasurementsTest");

    /** Results directory (output) */
    private static final Path TEST_RESULTS_PATH = Path.of("testData", "blockProcessingTest_results");

    /** Directory where block files are persisted */
    private static final Path BLOCKS_OUTPUT_PATH = TEST_RESULTS_PATH.resolve("blocks");

    /** TeaStore Kieker monitoring data path */
    private static final String TEASTORE_MONITORING_PATH =
        "../../bundles/Calibration/CIPM-Pipeline/cipm.consistency.root/cipm.consistency.runtime.pipeline.pcm/src/test/resources/teastore/monitoring";

    /** Block duration in nanoseconds (15 seconds per block) */
    private static final long BLOCK_DURATION_NS = 15_000_000_000L;

    private VsumFacadeImpl vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private MeasurementsFacade measurementsFacade;

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

        pcmFacade = new PcmFacade();
        imFacade = new ImFacade();
        measurementsFacade = new MeasurementsFacade();
        vsumFacade = new VsumFacadeImpl();
        vsumFacade.setHeadlessMode(true);

        pcmFacade.initialize(TEST_INPUT_PATH.resolve("pcm"));
        imFacade.initialize(TEST_RESULTS_PATH.resolve("im"));
        measurementsFacade.initialize(TEST_RESULTS_PATH.resolve("measurements"));

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

        LOGGER.info("VSUM initialized. Results: " + TEST_RESULTS_PATH.toAbsolutePath());
        verifyCorrespondences();
    }

    @AfterEach
    public void tearDown() {
        if (vsumFacade != null && vsumFacade.getVsum() != null) {
            vsumFacade.getVsum().dispose();
        }
    }

    @Test
    public void testProcessAllRecordsInBlocks() throws MeasurementsReaderException, IOException {
        System.out.println("========================================");
        System.out.println("BLOCK PROCESSING MEASUREMENTS TEST");
        System.out.println("========================================");
        System.out.println();

        // Step 1: Read ALL Kieker records
        List<MeasurementRecord> allRecords = readAllKiekerRecords();
        if (allRecords.isEmpty()) {
            System.out.println("WARNING: No Kieker records found. Skipping test.");
            return;
        }

        // Step 2: Split into time-based blocks
        List<List<MeasurementRecord>> blocks = splitIntoBlocks(allRecords);
        System.out.println("Split " + allRecords.size() + " records into " + blocks.size() + " blocks");
        System.out.println("Block duration: " + (BLOCK_DURATION_NS / 1_000_000_000L) + "s");
        System.out.println();

        // Free the original list — blocks hold the references now
        allRecords = null;

        // Step 3: Create MeasurementsView
        CipmMeasurementsViewType viewType = new CipmMeasurementsViewType("MeasurementsView");
        var viewSelector = viewType.createSelector(vsumFacade.getVsum());
        MeasurementsView measurementsView = viewType.createView(viewSelector);
        assertNotNull(measurementsView, "MeasurementsView should be created");

        // Step 4: Process each block
        int totalPropagatedChanges = 0;
        int totalRecordsProcessed = 0;

        for (int blockIdx = 0; blockIdx < blocks.size(); blockIdx++) {
            List<MeasurementRecord> blockRecords = blocks.get(blockIdx);
            long blockStart = System.currentTimeMillis();

            System.out.println("--- Block " + (blockIdx + 1) + "/" + blocks.size()
                + " (" + blockRecords.size() + " records) ---");

            // 4a: Add each record and commit individually to trigger reactions
            int blockChanges = 0;
            for (int i = 0; i < blockRecords.size(); i++) {
                measurementsView.addMeasurement(blockRecords.get(i));

                try {
                    var propagatedChanges = measurementsView.commitChangesAndUpdate();
                    blockChanges += propagatedChanges.size();
                } catch (IllegalArgumentException e) {
                    // Empty change — continue
                }

                // Log progress every 100 records within the block
                if ((i + 1) % 100 == 0) {
                    System.out.println("  " + (i + 1) + "/" + blockRecords.size() + " records...");
                }
            }

            totalPropagatedChanges += blockChanges;
            totalRecordsProcessed += blockRecords.size();

            // 4b: Persist this block to disk from the view (fresh copies, not stale refs)
            persistBlockFromView(blockIdx, measurementsView);

            // 4c: Clear records from VSUM memory and commit the removal
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

            System.out.println("  Committed: " + blockChanges + " changes, cleared " + cleared + " records");
            System.out.println("  Persisted: block_" + String.format("%04d", blockIdx) + ".measurements");
            System.out.println("  Duration: " + blockDuration + "ms, Total: " + totalRecordsProcessed + " records");
            System.out.println();

            // Release block references for GC
            blocks.set(blockIdx, null);
        }

        // Step 5: Summary
        System.out.println("========================================");
        System.out.println("SUMMARY");
        System.out.println("========================================");
        System.out.println("  Total records processed: " + totalRecordsProcessed);
        System.out.println("  Total blocks persisted: " + blocks.size());
        System.out.println("  Total propagated changes: " + totalPropagatedChanges);
        System.out.println("  Block files: " + BLOCKS_OUTPUT_PATH.toAbsolutePath());

        long blockFileCount = Files.list(BLOCKS_OUTPUT_PATH)
            .filter(p -> p.toString().endsWith(".measurements"))
            .count();
        System.out.println("  Block files on disk: " + blockFileCount);
        assertTrue(blockFileCount > 0, "Block files should be persisted to disk");

        // Step 6: Replace empty blocks in VSUM with proxy references to block files
        replaceVsumBlocksWithProxies(blocks.size());

        // Step 7: Export VSUM's measurements model (blocks now have proxy hrefs)
        exportVsumMeasurementsToResults();

        // Step 8: Check PCM updates in VSUM (not the facade's original)
        System.out.println();
        System.out.println("--- Resource Demand Changes (VSUM vs Initial) ---");
        Repository vsumRepo = findRepositoryInVsum();
        int updatedCount = checkAndPrintResourceDemandChanges(vsumRepo);
        System.out.println("  InternalActions updated: " + updatedCount);

        // Export VSUM's PCM to results folder for inspection
        if (vsumRepo != null) {
            exportVsumPcmToResults(vsumRepo);
            System.out.println("  VSUM PCM exported to: " + TEST_RESULTS_PATH.resolve("pcm").toAbsolutePath());
        }

        System.out.println("========================================");
        System.out.println("TEST COMPLETED SUCCESSFULLY");
        System.out.println("========================================");
    }

    /**
     * Read ALL Kieker records from the TeaStore monitoring directory.
     */
    private List<MeasurementRecord> readAllKiekerRecords() throws MeasurementsReaderException {
        Path monitoringDir = findMonitoringDirectory();
        if (monitoringDir == null) {
            return new ArrayList<>();
        }

        System.out.println("Reading all Kieker records from: " + monitoringDir);
        KiekerFileMeasurementsReader reader = new KiekerFileMeasurementsReader();
        List<MeasurementRecord> records = reader.readRecords(monitoringDir.toString());
        System.out.println("Total records read: " + records.size());

        int internal = 0, service = 0, loop = 0, branch = 0, resource = 0, other = 0;
        for (MeasurementRecord r : records) {
            if (r instanceof InternalActionRecord) internal++;
            else if (r instanceof ServiceContextRecord) service++;
            else if (r instanceof cipm.consistency.measurements.LoopActionRecord) loop++;
            else if (r instanceof cipm.consistency.measurements.BranchActionRecord) branch++;
            else if (r instanceof cipm.consistency.measurements.ResourceUtilizationRecord) resource++;
            else other++;
        }
        System.out.println("  InternalAction: " + internal + ", Service: " + service
            + ", Loop: " + loop + ", Branch: " + branch
            + ", Resource: " + resource + ", Other: " + other);
        System.out.println();

        return records;
    }

    /**
     * Split records into time-based blocks using entry timestamps.
     */
    private List<List<MeasurementRecord>> splitIntoBlocks(List<MeasurementRecord> records) {
        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        long minTimestamp = Long.MAX_VALUE;
        for (MeasurementRecord record : records) {
            long ts = getRecordTimestamp(record);
            if (ts > 0 && ts < minTimestamp) {
                minTimestamp = ts;
            }
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

    /**
     * Persist the current block's records from the view to a standalone file on disk.
     * Uses getRootObjects() which returns EcoreUtil.copyAll copies — fully standalone,
     * no hrefs back to the VSUM resource.
     */
    private void persistBlockFromView(int blockIndex, MeasurementsView view) throws IOException {
        // getRootObjects() returns deep copies (EcoreUtil.copyAll), not VSUM references
        Measurements viewCopy = null;
        for (var root : view.getRootObjects()) {
            if (root instanceof Measurements) {
                viewCopy = (Measurements) root;
                break;
            }
        }

        if (viewCopy == null || viewCopy.getRepositories().isEmpty()) {
            LOGGER.warn("No measurements found in view for block " + blockIndex);
            return;
        }

        // The view copy already has standalone records — save directly
        String fileName = String.format("block_%04d.measurements", blockIndex);
        Path filePath = BLOCKS_OUTPUT_PATH.resolve(fileName);

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("measurements", new XMIResourceFactoryImpl());

        Resource resource = resourceSet.createResource(
            URI.createFileURI(filePath.toAbsolutePath().toString()));
        resource.getContents().add(viewCopy);
        resource.save(null);

        // Unload immediately to free memory
        resource.unload();
        resourceSet.getResources().clear();
    }

    /**
     * Assemble a master measurements model that references all block files via EMF proxies.
     * The master file contains a Measurements/MeasurementsRepository with proxy blocks
     * that resolve (via href) to the actual block data in the individual block files.
     *
     * When loaded, accessing a block in this model transparently loads the block file.
     */
    private void assembleMeasurementsIndex(int blockCount) throws IOException {
        Path indexPath = TEST_RESULTS_PATH.resolve("measurements").resolve("measurements_all.measurements");

        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("measurements", new XMIResourceFactoryImpl());

        URI indexURI = URI.createFileURI(indexPath.toAbsolutePath().toString());
        Resource indexResource = rs.createResource(indexURI);

        Measurements measurements = MeasurementsFactory.eINSTANCE.createMeasurements();
        cipm.consistency.measurements.MeasurementsRepository repo =
            MeasurementsFactory.eINSTANCE.createMeasurementsRepository();
        repo.setUri("default");
        measurements.getRepositories().add(repo);
        indexResource.getContents().add(measurements);

        int referencedBlocks = 0;
        for (int i = 0; i < blockCount; i++) {
            String blockFileName = String.format("block_%04d.measurements", i);
            Path blockFilePath = BLOCKS_OUTPUT_PATH.resolve(blockFileName);

            if (Files.exists(blockFilePath)) {
                // Create a proxy block that points to the block inside the block file
                cipm.consistency.measurements.MeasurementsBlock proxyBlock =
                    MeasurementsFactory.eINSTANCE.createMeasurementsBlock();

                URI blockURI = URI.createFileURI(blockFilePath.toAbsolutePath().toString());
                URI fragmentURI = blockURI.appendFragment("//@repositories.0/@blocks.0");
                ((InternalEObject) proxyBlock).eSetProxyURI(fragmentURI);

                repo.getBlocks().add(proxyBlock);
                referencedBlocks++;
            }
        }

        indexResource.save(null);
        indexResource.unload();
        rs.getResources().clear();

        System.out.println();
        System.out.println("--- Measurements Index ---");
        System.out.println("  Master file: " + indexPath.toAbsolutePath());
        System.out.println("  Block references: " + referencedBlocks);
        System.out.println("  (Each block resolves via href to its block file with full record data)");
    }

    /**
     * Replace empty blocks in the VSUM's Measurements model with EMF proxy references
     * to the persisted block files on disk. Called after all processing is done,
     * so the change recorder state doesn't matter.
     */
    private void replaceVsumBlocksWithProxies(int totalBlocks) {
        Measurements vsumMeas = findMeasurementsInVsum();
        if (vsumMeas == null || vsumMeas.getRepositories().isEmpty()) {
            LOGGER.warn("No Measurements model in VSUM for proxy replacement");
            return;
        }

        MeasurementsRepository repo = vsumMeas.getRepositories().get(0);
        // Clear all empty blocks and replace with proxy references
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
            // Add the VSUM model directly (it already has proxy blocks)
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

    /**
     * Find the PCM Repository inside the VSUM (the copy that reactions update).
     */
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

    /**
     * Compare the VSUM's PCM resource demands with the original input PCM.
     */
    private int checkAndPrintResourceDemandChanges(Repository vsumRepo) {
        if (vsumRepo == null) {
            System.out.println("  No PCM Repository in VSUM");
            return 0;
        }

        // Get initial demands from the input PCM (facade's original)
        Repository initialRepo = pcmFacade.getInMemoryPCM().getRepository();
        Map<String, String> initialDemands = new java.util.HashMap<>();
        if (initialRepo != null) {
            initialRepo.getComponents__Repository().forEach(component -> {
                component.eAllContents().forEachRemaining(obj -> {
                    if (obj instanceof InternalAction) {
                        InternalAction action = (InternalAction) obj;
                        String spec = getResourceDemandSpec(action);
                        initialDemands.put(action.getId(), spec);
                    }
                });
            });
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
                    String initialSpec = initialDemands.get(action.getId());

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

    private String getResourceDemandSpec(InternalAction action) {
        if (action.getResourceDemand_Action().isEmpty()) {
            return "EMPTY";
        }
        return action.getResourceDemand_Action().stream()
            .map(rd -> rd.getSpecification_ParametericResourceDemand() != null
                ? rd.getSpecification_ParametericResourceDemand().getSpecification()
                : "null")
            .collect(java.util.stream.Collectors.joining(","));
    }

    /**
     * Export the VSUM's PCM Repository to the results folder for inspection.
     */
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
}
