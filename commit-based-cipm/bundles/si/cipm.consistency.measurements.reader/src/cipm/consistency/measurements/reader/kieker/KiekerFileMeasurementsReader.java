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
package cipm.consistency.measurements.reader.kieker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.log4j.Logger;

import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.reader.MeasurementsReader;
import cipm.consistency.measurements.reader.MeasurementsReaderException;
import kieker.analysis.AnalysisController;
import kieker.analysis.IAnalysisController;
import kieker.analysis.exception.AnalysisConfigurationException;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.analysis.plugin.reader.filesystem.FSReader;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;

/**
 * Implementation of {@link MeasurementsReader} that reads measurements from Kieker monitoring log files.
 *
 * <p>This reader uses Kieker's FSReader to read monitoring records from a directory
 * containing Kieker log files (identified by the presence of a {@code kieker.map} file)
 * and converts them to EMF-based measurement records using {@link KiekerRecordConverter}.</p>
 *
 * <p>This reader is primarily used for offline analysis of previously recorded
 * monitoring data, testing, and experiments.</p>
 *
 * <h2>Source Format</h2>
 * <p>The source must be a directory containing Kieker log files with the following structure:</p>
 * <pre>
 * /path/to/kieker-logs/
 *   ├── kieker.map           (required - Kieker mapping file)
 *   ├── kieker-*.dat         (binary log files)
 *   └── ...
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * KiekerFileMeasurementsReader reader = new KiekerFileMeasurementsReader();
 * if (reader.canHandle("/path/to/kieker-logs")) {
 *     List&lt;MeasurementRecord&gt; records = reader.readRecords("/path/to/kieker-logs");
 *     // or directly into a block:
 *     reader.readRecordsIntoBlock("/path/to/kieker-logs", measurementsBlock);
 * }
 * </pre>
 *
 * @author Manar Mazkatli
 * @see MeasurementsReader
 * @see KiekerRecordConverter
 * @see KiekerStreamMeasurementsReader
 */
public class KiekerFileMeasurementsReader implements MeasurementsReader {

    private static final Logger LOGGER = Logger.getLogger(KiekerFileMeasurementsReader.class.getName());
    private static final String ID = "kieker-file-reader";
    private static final String NAME = "Kieker File Measurements Reader";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canHandle(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }
        File dir = new File(source);
        if (!dir.exists() || !dir.isDirectory()) {
            return false;
        }
        // Check if directory contains kieker.map file (Kieker log directory indicator)
        File kiekerMap = new File(dir, "kieker.map");
        return kiekerMap.exists();
    }
    
    private <TResultType> List<TResultType> readAndConvertRecords(String source, Function<IMonitoringRecord, TResultType> converter) throws MeasurementsReaderException {
        LOGGER.debug("Reading Kieker measurements from directory: " + source);

        List<TResultType> records = new ArrayList<>();

        try {
            // Create Kieker Analysis instance
            final IAnalysisController analysisInstance = new AnalysisController();

            // Set file system monitoring log input directory for our analysis
            final Configuration fsReaderConfig = new Configuration();
            fsReaderConfig.setProperty(FSReader.CONFIG_PROPERTY_NAME_INPUTDIRS, source);
            final FSReader reader = new FSReader(fsReaderConfig, analysisInstance);

            // Create filter to collect records
            final RecordCollectorFilter<TResultType> collector = new RecordCollectorFilter<>(new Configuration(), analysisInstance, records, converter);

            // Connect reader to collector
            analysisInstance.connect(reader, FSReader.OUTPUT_PORT_NAME_RECORDS, collector, RecordCollectorFilter.INPUT_PORT_NAME_EVENTS);

            // Start reading all records
            analysisInstance.run();

        } catch (IllegalStateException | AnalysisConfigurationException e) {
            throw new MeasurementsReaderException("Failed to read Kieker monitoring logs from: " + source, e);
        }

        return records;
    }
    
    public List<IMonitoringRecord> readKiekerRecords(String source) throws MeasurementsReaderException {
    	return this.readAndConvertRecords(source, (record) -> (record));
    }

    @Override
    public List<MeasurementRecord> readRecords(String source) throws MeasurementsReaderException {
        LOGGER.debug("Reading Kieker measurements from directory: " + source);
        
        KiekerToMMRecordConverter converter = new KiekerToMMRecordConverter();

        List<MeasurementRecord> records = this.readAndConvertRecords(source, converter);

        // Print summary
        LOGGER.info("========================================");
        LOGGER.info("Kieker File Reading Summary:");
        LOGGER.info("  Raw Kieker records:    " + converter.getRawRecordCount());
        LOGGER.info("  Converted EMF records: " + converter.getConvertedCount());
        LOGGER.info("  Skipped records:       " + converter.getSkippedCount());
        LOGGER.info("  Source directory:      " + source);
        LOGGER.info("========================================");

        return records;
    }

    @Override
    public int readRecordsIntoBlock(String source, MeasurementsBlock block) throws MeasurementsReaderException {
        List<MeasurementRecord> records = readRecords(source);
        for (MeasurementRecord record : records) {
            block.getRecords().add(record);
        }
        return records.size();
    }
    
    /**
     * Internal Kieker filter plugin to collect all monitoring records from a file and converting them to another type.
     */
    private static class RecordCollectorFilter<TConvertedType> extends AbstractFilterPlugin {

        static final String INPUT_PORT_NAME_EVENTS = "inputEvents";

        private final List<TConvertedType> targetList;
        
        private final Function<IMonitoringRecord, TConvertedType> converter;
        
        public RecordCollectorFilter(Configuration configuration, IAnalysisController analysisInstance,
                List<TConvertedType> targetList, Function<IMonitoringRecord, TConvertedType> converter) {
            super(configuration, analysisInstance);
            this.targetList = targetList;
            this.converter = converter;
        }

        @Override
        public Configuration getCurrentConfiguration() {
            return configuration;
        }

        @InputPort(name = INPUT_PORT_NAME_EVENTS, description = "Input monitoring records.", eventTypes = { IMonitoringRecord.class })
        public final void inputEvent(final IMonitoringRecord kiekerRecord) {
            var convertedRecord = this.converter.apply(kiekerRecord);
            this.targetList.add(convertedRecord);
        }
    }
    
    /**
     * Internal converter for Kieker records to records from the measurements metamodel.
     */
    private static class KiekerToMMRecordConverter implements Function<IMonitoringRecord, MeasurementRecord> {
        private int rawRecordCount = 0;
        private int convertedCount = 0;
        private int skippedCount = 0;

        @Override
        public final MeasurementRecord apply(final IMonitoringRecord kiekerRecord) {
            rawRecordCount++;
            String recordType = kiekerRecord.getClass().getSimpleName();

            MeasurementRecord emfRecord = KiekerRecordConverter.convert(kiekerRecord);
            if (emfRecord != null) {
                convertedCount++;
            } else {
                skippedCount++;
                LOGGER.debug("Skipped Kieker record of type: " + recordType);
            }

            // Print progress every 10000 records
            if (rawRecordCount % 10000 == 0) {
                LOGGER.info("Progress: " + rawRecordCount + " raw records processed, "
                        + convertedCount + " converted, " + skippedCount + " skipped");
            }
            
            return emfRecord;
        }

        public int getRawRecordCount() {
            return rawRecordCount;
        }

        public int getConvertedCount() {
            return convertedCount;
        }

        public int getSkippedCount() {
            return skippedCount;
        }
    }
}
