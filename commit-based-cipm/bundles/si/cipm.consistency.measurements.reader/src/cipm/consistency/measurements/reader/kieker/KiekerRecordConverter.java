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

import cipm.consistency.measurements.BranchActionRecord;
import cipm.consistency.measurements.InternalActionRecord;
import cipm.consistency.measurements.LoopActionRecord;
import cipm.consistency.measurements.MeasurementRecord;
import cipm.consistency.measurements.MeasurementsFactory;
import cipm.consistency.measurements.ResourceUtilizationRecord;
import cipm.consistency.measurements.ServiceContextRecord;
import kieker.common.record.IMonitoringRecord;

/**
 * Utility class to convert Kieker monitoring records to EMF-based measurement records.
 *
 * <p>This converter bridges the gap between Kieker's monitoring record format and
 * the EMF-based measurement model used in CIPM. It uses reflection to access
 * Kieker record properties, making it compatible with various Kieker versions.</p>
 *
 * <h2>Record Type Mapping</h2>
 * <table border="1">
 *   <tr><th>Kieker Record Type</th><th>EMF Measurement Record Type</th></tr>
 *   <tr><td>ResponseTimeRecord</td><td>{@link InternalActionRecord}</td></tr>
 *   <tr><td>LoopRecord</td><td>{@link LoopActionRecord}</td></tr>
 *   <tr><td>BranchRecord</td><td>{@link BranchActionRecord}</td></tr>
 *   <tr><td>ServiceCallRecord</td><td>{@link ServiceContextRecord}</td></tr>
 *   <tr><td>ResourceUtilizationRecord</td><td>{@link ResourceUtilizationRecord}</td></tr>
 * </table>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * IMonitoringRecord kiekerRecord = ...; // from Kieker
 * MeasurementRecord emfRecord = KiekerRecordConverter.convert(kiekerRecord);
 * if (emfRecord != null) {
 *     measurementsBlock.getRecords().add(emfRecord);
 * }
 * </pre>
 *
 * @author Manar Mazkatli
 * @see MeasurementRecord
 * @see IMonitoringRecord
 */
public final class KiekerRecordConverter {

    private static final org.apache.log4j.Logger LOGGER =
            org.apache.log4j.Logger.getLogger(KiekerRecordConverter.class.getName());

    private KiekerRecordConverter() {
        // Utility class
    }

    /**
     * Converts a Kieker monitoring record to an EMF measurement record.
     *
     * @param kiekerRecord the Kieker monitoring record
     * @return the converted EMF measurement record, or null if the record type is not supported
     */
    public static MeasurementRecord convert(IMonitoringRecord kiekerRecord) {
        if (kiekerRecord == null) {
            return null;
        }

        String className = kiekerRecord.getClass().getSimpleName();
        LOGGER.debug("Converting Kieker record of type: " + className);

        switch (className) {
            case "ResponseTimeRecord":
                return convertResponseTimeRecord(kiekerRecord);
            case "LoopRecord":
                return convertLoopRecord(kiekerRecord);
            case "BranchRecord":
                return convertBranchRecord(kiekerRecord);
            case "ServiceCallRecord":
                return convertServiceCallRecord(kiekerRecord);
            case "ResourceUtilizationRecord":
                return convertResourceUtilizationRecord(kiekerRecord);
            default:
                LOGGER.warn("Unknown Kieker record type: " + className);
                return null;
        }
    }

    /**
     * Converts a Kieker ResponseTimeRecord to an EMF InternalActionRecord.
     */
    private static InternalActionRecord convertResponseTimeRecord(IMonitoringRecord kiekerRecord) {
        InternalActionRecord record = MeasurementsFactory.eINSTANCE.createInternalActionRecord();

        try {
            // Use reflection to access Kieker record properties
            Object internalActionId = invokeGetter(kiekerRecord, "getInternalActionId");
            Object resourceId = invokeGetter(kiekerRecord, "getResourceId");
            Object startTime = invokeGetter(kiekerRecord, "getStartTime");
            Object stopTime = invokeGetter(kiekerRecord, "getStopTime");

            if (internalActionId != null) {
                record.setInternalActionID(internalActionId.toString());
            }
            if (resourceId != null) {
                record.setRequestedResourceID(resourceId.toString());
            }
            if (startTime != null) {
                record.setEntryTime((Long) startTime);
            }
            if (stopTime != null) {
                record.setExitTime((Long) stopTime);
            }
        } catch (Exception e) {
            // Log and return partially filled record
        }

        return record;
    }

    /**
     * Converts a Kieker LoopRecord to an EMF LoopActionRecord.
     */
    private static LoopActionRecord convertLoopRecord(IMonitoringRecord kiekerRecord) {
        LoopActionRecord record = MeasurementsFactory.eINSTANCE.createLoopActionRecord();

        try {
            Object loopId = invokeGetter(kiekerRecord, "getLoopId");
            Object loopIterationCount = invokeGetter(kiekerRecord, "getLoopIterationCount");

            if (loopId != null) {
                record.setLoopID(loopId.toString());
            }
            if (loopIterationCount != null) {
                record.setLoopIterationCount(((Number) loopIterationCount).longValue());
            }
        } catch (Exception e) {
            // Log and return partially filled record
        }

        return record;
    }

    /**
     * Converts a Kieker BranchRecord to an EMF BranchActionRecord.
     */
    private static BranchActionRecord convertBranchRecord(IMonitoringRecord kiekerRecord) {
        BranchActionRecord record = MeasurementsFactory.eINSTANCE.createBranchActionRecord();

        try {
            Object branchId = invokeGetter(kiekerRecord, "getBranchId");
            Object executedBranchId = invokeGetter(kiekerRecord, "getExecutedBranchId");

            if (branchId != null) {
                record.setBranchID(branchId.toString());
            }
            if (executedBranchId != null) {
                record.setExecutedBranchID(executedBranchId.toString());
            }
        } catch (Exception e) {
            // Log and return partially filled record
        }

        return record;
    }

    /**
     * Converts a Kieker ServiceCallRecord to an EMF ServiceContextRecord.
     */
    private static ServiceContextRecord convertServiceCallRecord(IMonitoringRecord kiekerRecord) {
        ServiceContextRecord record = MeasurementsFactory.eINSTANCE.createServiceContextRecord();

        try {
            Object serviceId = invokeGetter(kiekerRecord, "getServiceId");
            Object serviceExecutionId = invokeGetter(kiekerRecord, "getServiceExecutionId");
            Object callerServiceExecutionId = invokeGetter(kiekerRecord, "getCallerServiceExecutionId");
            Object sessionId = invokeGetter(kiekerRecord, "getSessionId");
            Object hostId = invokeGetter(kiekerRecord, "getHostId");
            Object hostName = invokeGetter(kiekerRecord, "getHostName");
            Object entryTime = invokeGetter(kiekerRecord, "getEntryTime");
            Object exitTime = invokeGetter(kiekerRecord, "getExitTime");
            Object parameters = invokeGetter(kiekerRecord, "getParameters");
            Object returnValue = invokeGetter(kiekerRecord, "getReturnValue");

            if (serviceId != null) {
                record.setServiceID(serviceId.toString());
            }
            if (serviceExecutionId != null) {
                record.setServiceExecutionID(serviceExecutionId.toString());
            }
            if (callerServiceExecutionId != null) {
                record.setCallerExecutionID(callerServiceExecutionId.toString());
            }
            if (sessionId != null) {
                record.setSessionID(sessionId.toString());
            }
            if (hostId != null) {
                record.setHostID(hostId.toString());
            }
            if (hostName != null) {
                record.setHostName(hostName.toString());
            }
            if (entryTime != null) {
                record.setEntryTime((Long) entryTime);
            }
            if (exitTime != null) {
                record.setExitTime((Long) exitTime);
            }
            if (parameters != null) {
                record.setParameters(parameters.toString());
            }
            if (returnValue != null) {
                record.setReturnValue(returnValue.toString());
            }
        } catch (Exception e) {
            // Log and return partially filled record
        }

        return record;
    }

    /**
     * Converts a Kieker ResourceUtilizationRecord to an EMF ResourceUtilizationRecord.
     */
    private static ResourceUtilizationRecord convertResourceUtilizationRecord(IMonitoringRecord kiekerRecord) {
        ResourceUtilizationRecord record = MeasurementsFactory.eINSTANCE.createResourceUtilizationRecord();

        try {
            Object resourceId = invokeGetter(kiekerRecord, "getResourceId");
            Object utilization = invokeGetter(kiekerRecord, "getUtilization");
            Object timestamp = invokeGetter(kiekerRecord, "getTimestamp");

            if (resourceId != null) {
                record.setResourceID(resourceId.toString());
            }
            if (utilization != null) {
                record.setUtilization(((Number) utilization).doubleValue());
            }
            if (timestamp != null) {
                record.setTimestamp((Long) timestamp);
            }
        } catch (Exception e) {
            // Log and return partially filled record
        }

        return record;
    }

    /**
     * Helper method to invoke a getter method on an object using reflection.
     */
    private static Object invokeGetter(Object obj, String methodName) {
        try {
            return obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
