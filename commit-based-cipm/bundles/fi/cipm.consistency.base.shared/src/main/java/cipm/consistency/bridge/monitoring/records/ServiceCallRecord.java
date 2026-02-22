package cipm.consistency.bridge.monitoring.records;

import java.nio.BufferOverflowException;

import cipm.consistency.bridge.monitoring.records.HostContextRecord;
import cipm.consistency.bridge.monitoring.records.ServiceContextRecord;
import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

/**
 * @author Generic Kieker
 * API compatibility: Kieker 1.13.0
 * 
 * @since 1.13
 */
public class ServiceCallRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, ServiceContextRecord, HostContextRecord {			
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_STRING // RecordWithSession.sessionId
			 + TYPE_SIZE_STRING // ServiceContextRecord.serviceExecutionId
			 + TYPE_SIZE_STRING // HostContextRecord.hostId
			 + TYPE_SIZE_STRING // HostContextRecord.hostName
			 + TYPE_SIZE_STRING // ServiceCallRecord.serviceId
			 + TYPE_SIZE_STRING // ServiceCallRecord.parameters
			 + TYPE_SIZE_STRING // ServiceCallRecord.callerServiceExecutionId
			 + TYPE_SIZE_STRING // ServiceCallRecord.externalCallId
			 + TYPE_SIZE_STRING // ServiceCallRecord.executionContextId
			 + TYPE_SIZE_LONG // ServiceCallRecord.entryTime
			 + TYPE_SIZE_LONG; // ServiceCallRecord.exitTime
	
	public static final Class<?>[] TYPES = {
		String.class, // RecordWithSession.sessionId
		String.class, // ServiceContextRecord.serviceExecutionId
		String.class, // HostContextRecord.hostId
		String.class, // HostContextRecord.hostName
		String.class, // ServiceCallRecord.serviceId
		String.class, // ServiceCallRecord.parameters
		String.class, // ServiceCallRecord.callerServiceExecutionId
		String.class, // ServiceCallRecord.externalCallId
		String.class, // ServiceCallRecord.executionContextId
		long.class, // ServiceCallRecord.entryTime
		long.class, // ServiceCallRecord.exitTime
	};
	
	/** default constants. */
	public static final String SESSION_ID = "<not set>";
	public static final String SERVICE_EXECUTION_ID = "<not set>";
	public static final String HOST_ID = "<not set>";
	public static final String HOST_NAME = "<not set>";
	public static final String SERVICE_ID = "<not set>";
	public static final String PARAMETERS = "<not set>";
	public static final String CALLER_SERVICE_EXECUTION_ID = "<not set>";
	public static final String EXTERNAL_CALL_ID = "<not set>";
	public static final String EXECUTION_CONTEXT_ID = "<not set>";
	private static final long serialVersionUID = -6920581464474330860L;
	
	/** property name array. */
	private static final String[] PROPERTY_NAMES = {
		"sessionId",
		"serviceExecutionId",
		"hostId",
		"hostName",
		"serviceId",
		"parameters",
		"callerServiceExecutionId",
		"externalCallId",
		"executionContextId",
		"entryTime",
		"exitTime",
	};
	
	/** property declarations. */
	private final String sessionId;
	private final String serviceExecutionId;
	private final String hostId;
	private final String hostName;
	private final String serviceId;
	private final String parameters;
	private final String callerServiceExecutionId;
	private final String externalCallId;
	private final String executionContextId;
	private final long entryTime;
	private final long exitTime;
	
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param sessionId
	 *            sessionId
	 * @param serviceExecutionId
	 *            serviceExecutionId
	 * @param hostId
	 *            hostId
	 * @param hostName
	 *            hostName
	 * @param serviceId
	 *            serviceId
	 * @param parameters
	 *            parameters
	 * @param callerServiceExecutionId
	 *            callerServiceExecutionId
	 * @param externalCallId
	 *            externalCallId
	 * @param executionContextId
	 *            executionContextId
	 * @param entryTime
	 *            entryTime
	 * @param exitTime
	 *            exitTime
	 */
	public ServiceCallRecord(final String sessionId, final String serviceExecutionId, final String hostId, final String hostName, final String serviceId, final String parameters, final String callerServiceExecutionId, final String externalCallId, final String executionContextId, final long entryTime, final long exitTime) {
		this.sessionId = sessionId == null?SESSION_ID:sessionId;
		this.serviceExecutionId = serviceExecutionId == null?SERVICE_EXECUTION_ID:serviceExecutionId;
		this.hostId = hostId == null?HOST_ID:hostId;
		this.hostName = hostName == null?HOST_NAME:hostName;
		this.serviceId = serviceId == null?SERVICE_ID:serviceId;
		this.parameters = parameters == null?PARAMETERS:parameters;
		this.callerServiceExecutionId = callerServiceExecutionId == null?CALLER_SERVICE_EXECUTION_ID:callerServiceExecutionId;
		this.externalCallId = externalCallId == null?EXTERNAL_CALL_ID:externalCallId;
		this.executionContextId = executionContextId == null?EXECUTION_CONTEXT_ID:executionContextId;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
	}

	/**
	 * This constructor converts the given array into a record.
	 * It is recommended to use the array which is the result of a call to {@link #toArray()}.
	 * 
	 * @param values
	 *            The values for the record.
	 *
	 * @deprecated to be removed 1.15
	 */
	@Deprecated
	public ServiceCallRecord(final Object[] values) { // NOPMD (direct store of values)
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.hostId = (String) values[2];
		this.hostName = (String) values[3];
		this.serviceId = (String) values[4];
		this.parameters = (String) values[5];
		this.callerServiceExecutionId = (String) values[6];
		this.externalCallId = (String) values[7];
		this.executionContextId = (String) values[8];
		this.entryTime = (Long) values[9];
		this.exitTime = (Long) values[10];
	}

	/**
	 * This constructor uses the given array to initialize the fields of this record.
	 * 
	 * @param values
	 *            The values for the record.
	 * @param valueTypes
	 *            The types of the elements in the first array.
	 *
	 * @deprecated to be removed 1.15
	 */
	@Deprecated
	protected ServiceCallRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.hostId = (String) values[2];
		this.hostName = (String) values[3];
		this.serviceId = (String) values[4];
		this.parameters = (String) values[5];
		this.callerServiceExecutionId = (String) values[6];
		this.externalCallId = (String) values[7];
		this.executionContextId = (String) values[8];
		this.entryTime = (Long) values[9];
		this.exitTime = (Long) values[10];
	}

	
	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException 
	 *            when the record could not be deserialized
	 */
	public ServiceCallRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
		this.sessionId = deserializer.getString();
		this.serviceExecutionId = deserializer.getString();
		this.hostId = deserializer.getString();
		this.hostName = deserializer.getString();
		this.serviceId = deserializer.getString();
		this.parameters = deserializer.getString();
		this.callerServiceExecutionId = deserializer.getString();
		this.externalCallId = deserializer.getString();
		this.executionContextId = deserializer.getString();
		this.entryTime = deserializer.getLong();
		this.exitTime = deserializer.getLong();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated to be removed in 1.15
	 */
	@Override
	@Deprecated
	public Object[] toArray() {
		return new Object[] {
			this.getSessionId(),
			this.getServiceExecutionId(),
			this.getHostId(),
			this.getHostName(),
			this.getServiceId(),
			this.getParameters(),
			this.getCallerServiceExecutionId(),
			this.getExternalCallId(),
			this.getExecutionContextId(),
			this.getEntryTime(),
			this.getExitTime(),
		};
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
		stringRegistry.get(this.getSessionId());
		stringRegistry.get(this.getServiceExecutionId());
		stringRegistry.get(this.getHostId());
		stringRegistry.get(this.getHostName());
		stringRegistry.get(this.getServiceId());
		stringRegistry.get(this.getParameters());
		stringRegistry.get(this.getCallerServiceExecutionId());
		stringRegistry.get(this.getExternalCallId());
		stringRegistry.get(this.getExecutionContextId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		//super.serialize(serializer);
		serializer.putString(this.getSessionId());
		serializer.putString(this.getServiceExecutionId());
		serializer.putString(this.getHostId());
		serializer.putString(this.getHostName());
		serializer.putString(this.getServiceId());
		serializer.putString(this.getParameters());
		serializer.putString(this.getCallerServiceExecutionId());
		serializer.putString(this.getExternalCallId());
		serializer.putString(this.getExecutionContextId());
		serializer.putLong(this.getEntryTime());
		serializer.putLong(this.getExitTime());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getValueNames() {
		return PROPERTY_NAMES; // NOPMD
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return SIZE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated to be rmeoved in 1.15
	 */
	@Override
	@Deprecated
	public void initFromArray(final Object[] values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		
		final ServiceCallRecord castedRecord = (ServiceCallRecord) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
			return false;
		}
		if (!this.getSessionId().equals(castedRecord.getSessionId())) {
			return false;
		}
		if (!this.getServiceExecutionId().equals(castedRecord.getServiceExecutionId())) {
			return false;
		}
		if (!this.getHostId().equals(castedRecord.getHostId())) {
			return false;
		}
		if (!this.getHostName().equals(castedRecord.getHostName())) {
			return false;
		}
		if (!this.getServiceId().equals(castedRecord.getServiceId())) {
			return false;
		}
		if (!this.getParameters().equals(castedRecord.getParameters())) {
			return false;
		}
		if (!this.getCallerServiceExecutionId().equals(castedRecord.getCallerServiceExecutionId())) {
			return false;
		}
		if (!this.getExternalCallId().equals(castedRecord.getExternalCallId())) {
			return false;
		}
		if (!this.getExecutionContextId().equals(castedRecord.getExecutionContextId())) {
			return false;
		}
		if (this.getEntryTime() != castedRecord.getEntryTime()) {
			return false;
		}
		if (this.getExitTime() != castedRecord.getExitTime()) {
			return false;
		}
		
		return true;
	}
	
	public final String getSessionId() {
		return this.sessionId;
	}
	
	
	public final String getServiceExecutionId() {
		return this.serviceExecutionId;
	}
	
	
	public final String getHostId() {
		return this.hostId;
	}
	
	
	public final String getHostName() {
		return this.hostName;
	}
	
	
	public final String getServiceId() {
		return this.serviceId;
	}
	
	
	public final String getParameters() {
		return this.parameters;
	}
	
	
	public final String getCallerServiceExecutionId() {
		return this.callerServiceExecutionId;
	}
	
	
	public final String getExternalCallId() {
		return this.externalCallId;
	}
	
	
	public final String getExecutionContextId() {
		return this.executionContextId;
	}
	
	
	public final long getEntryTime() {
		return this.entryTime;
	}
	
	
	public final long getExitTime() {
		return this.exitTime;
	}
	
}
