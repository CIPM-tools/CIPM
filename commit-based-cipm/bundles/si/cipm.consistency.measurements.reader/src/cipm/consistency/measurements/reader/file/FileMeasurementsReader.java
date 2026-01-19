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
package cipm.consistency.measurements.reader.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.InternalCallRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsBlock;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.ResourceUtilizationRecord;
import cipm.consistency.measurements.ServiceContextRecord;
import cipm.consistency.measurements.reader.MeasurementsReader;
import cipm.consistency.measurements.reader.MeasurementsReaderException;

/**
 * Implementation of {@link MeasurementsReader} that reads measurements from JSON files.
 *
 * <p>This reader is primarily used for testing and experiments where measurements
 * are stored in JSON format. It parses the JSON file and creates EMF-based
 * measurement records.</p>
 *
 * <h2>Supported Record Types</h2>
 * <ul>
 *   <li>{@link InternalActionRecord} - Internal action response time measurements</li>
 *   <li>{@link LoopActionRecord} - Loop iteration count measurements</li>
 *   <li>{@link BranchActionRecord} - Branch execution measurements</li>
 *   <li>{@link ServiceContextRecord} - Service context measurements</li>
 *   <li>{@link InternalCallRecord} - Internal call measurements</li>
 *   <li>{@link ResourceUtilizationRecord} - Resource utilization measurements</li>
 * </ul>
 *
 * <h2>Expected JSON Format</h2>
 * <pre>
 * {
 *   "records": [
 *     {
 *       "type": "InternalActionRecord",
 *       "internalActionID": "action1",
 *       "entryTime": 1000,
 *       "exitTime": 1500,
 *       "requestedResourceID": "cpu"
 *     },
 *     {
 *       "type": "LoopActionRecord",
 *       "loopID": "loop1",
 *       "loopIterationCount": 10
 *     }
 *   ]
 * }
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * FileMeasurementsReader reader = new FileMeasurementsReader();
 * if (reader.canHandle("/path/to/measurements.json")) {
 *     List&lt;MeasurementRecord&gt; records = reader.readRecords("/path/to/measurements.json");
 * }
 * </pre>
 *
 * @author Manar Mazkatli
 * @see MeasurementsReader
 * @see MeasurementRecord
 */
public class FileMeasurementsReader implements MeasurementsReader {

    private static final Logger LOGGER = Logger.getLogger(FileMeasurementsReader.class.getName());
    private static final String ID = "file-reader";
    private static final String NAME = "File Measurements Reader";

    private final Gson gson;

    public FileMeasurementsReader() {
        this.gson = new GsonBuilder().create();
    }

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
        File file = new File(source);
        return file.exists() && file.isFile() &&
               (source.endsWith(".json") || source.endsWith(".measurements"));
    }

    @Override
    public List<MeasurementRecord> readRecords(String source) throws MeasurementsReaderException {
        LOGGER.debug("Reading measurements from file: " + source);

        List<MeasurementRecord> records = new ArrayList<>();

        try {
            String content = Files.readString(Path.of(source));
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();

            if (root.has("records")) {
                JsonArray recordsArray = root.getAsJsonArray("records");
                for (JsonElement element : recordsArray) {
                    MeasurementRecord record = parseRecord(element.getAsJsonObject());
                    if (record != null) {
                        records.add(record);
                    }
                }
            }

            LOGGER.info("Read " + records.size() + " measurement records from " + source);
        } catch (IOException e) {
            throw new MeasurementsReaderException("Failed to read file: " + source, e);
        } catch (Exception e) {
            throw new MeasurementsReaderException("Failed to parse measurements file: " + source, e);
        }

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
     * Parses a single measurement record from JSON.
     *
     * @param json the JSON object representing the record
     * @return the parsed measurement record, or null if parsing fails
     */
    private MeasurementRecord parseRecord(JsonObject json) {
        if (!json.has("type")) {
            LOGGER.warn("Record missing 'type' field, skipping");
            return null;
        }

        String type = json.get("type").getAsString();

        switch (type) {
            case "InternalActionRecord":
                return parseInternalActionRecord(json);
            case "LoopActionRecord":
                return parseLoopActionRecord(json);
            case "BranchActionRecord":
                return parseBranchActionRecord(json);
            case "ServiceContextRecord":
                return parseServiceContextRecord(json);
            case "InternalCallRecord":
                return parseInternalCallRecord(json);
            case "ResourceUtilizationRecord":
                return parseResourceUtilizationRecord(json);
            default:
                LOGGER.warn("Unknown record type: " + type);
                return null;
        }
    }

    private InternalActionRecord parseInternalActionRecord(JsonObject json) {
        InternalActionRecord record = MeasurementsFactory.eINSTANCE.createInternalActionRecord();

        if (json.has("internalActionID")) {
            record.setInternalActionID(json.get("internalActionID").getAsString());
        }
        if (json.has("entryTime")) {
            record.setEntryTime(json.get("entryTime").getAsLong());
        }
        if (json.has("exitTime")) {
            record.setExitTime(json.get("exitTime").getAsLong());
        }
        if (json.has("requestedResourceID")) {
            record.setRequestedResourceID(json.get("requestedResourceID").getAsString());
        }

        return record;
    }

    private LoopActionRecord parseLoopActionRecord(JsonObject json) {
        LoopActionRecord record = MeasurementsFactory.eINSTANCE.createLoopActionRecord();

        if (json.has("loopID")) {
            record.setLoopID(json.get("loopID").getAsString());
        }
        if (json.has("loopIterationCount")) {
            record.setLoopIterationCount(json.get("loopIterationCount").getAsLong());
        }

        return record;
    }

    private BranchActionRecord parseBranchActionRecord(JsonObject json) {
        BranchActionRecord record = MeasurementsFactory.eINSTANCE.createBranchActionRecord();

        if (json.has("branchID")) {
            record.setBranchID(json.get("branchID").getAsString());
        }
        if (json.has("executedBranchID")) {
            record.setExecutedBranchID(json.get("executedBranchID").getAsString());
        }

        return record;
    }

    private ServiceContextRecord parseServiceContextRecord(JsonObject json) {
        ServiceContextRecord record = MeasurementsFactory.eINSTANCE.createServiceContextRecord();

        if (json.has("serviceID")) {
            record.setServiceID(json.get("serviceID").getAsString());
        }
        if (json.has("serviceExecutionID")) {
            record.setServiceExecutionID(json.get("serviceExecutionID").getAsString());
        }
        if (json.has("callerExecutionID")) {
            record.setCallerExecutionID(json.get("callerExecutionID").getAsString());
        }
        if (json.has("externalCallID")) {
            record.setExternalCallID(json.get("externalCallID").getAsString());
        }
        if (json.has("entryTime")) {
            record.setEntryTime(json.get("entryTime").getAsLong());
        }
        if (json.has("exitTime")) {
            record.setExitTime(json.get("exitTime").getAsLong());
        }
        if (json.has("parameters")) {
            record.setParameters(json.get("parameters").getAsString());
        }
        if (json.has("returnValue")) {
            record.setReturnValue(json.get("returnValue").getAsString());
        }
        if (json.has("sessionID")) {
            record.setSessionID(json.get("sessionID").getAsString());
        }
        if (json.has("hostID")) {
            record.setHostID(json.get("hostID").getAsString());
        }
        if (json.has("hostName")) {
            record.setHostName(json.get("hostName").getAsString());
        }

        return record;
    }

    private InternalCallRecord parseInternalCallRecord(JsonObject json) {
        InternalCallRecord record = MeasurementsFactory.eINSTANCE.createInternalCallRecord();

        if (json.has("internalCallID")) {
            record.setInternalCallID(json.get("internalCallID").getAsString());
        }
        if (json.has("parameters")) {
            record.setParameters(json.get("parameters").getAsString());
        }
        if (json.has("entryTime")) {
            record.setEntryTime(json.get("entryTime").getAsLong());
        }
        if (json.has("exitTime")) {
            record.setExitTime(json.get("exitTime").getAsLong());
        }
        if (json.has("returnValue")) {
            record.setReturnValue(json.get("returnValue").getAsString());
        }

        return record;
    }

    private ResourceUtilizationRecord parseResourceUtilizationRecord(JsonObject json) {
        ResourceUtilizationRecord record = MeasurementsFactory.eINSTANCE.createResourceUtilizationRecord();

        if (json.has("resourceID")) {
            record.setResourceID(json.get("resourceID").getAsString());
        }
        if (json.has("utilization")) {
            record.setUtilization(json.get("utilization").getAsDouble());
        }
        if (json.has("timestamp")) {
            record.setTimestamp(json.get("timestamp").getAsLong());
        }

        return record;
    }
}
