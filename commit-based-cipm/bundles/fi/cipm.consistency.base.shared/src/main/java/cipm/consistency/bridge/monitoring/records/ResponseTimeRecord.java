package cipm.consistency.bridge.monitoring.records;

import java.nio.BufferOverflowException;

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
public class ResponseTimeRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, ServiceContextRecord {			
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_STRING // RecordWithSession.sessionId
			 + TYPE_SIZE_STRING // ServiceContextRecord.serviceExecutionId
			 + TYPE_SIZE_STRING // ResponseTimeRecord.internalActionId
			 + TYPE_SIZE_STRING // ResponseTimeRecord.resourceId
			 + TYPE_SIZE_LONG // ResponseTimeRecord.startTime
			 + TYPE_SIZE_LONG; // ResponseTimeRecord.stopTime
	
	public static final Class<?>[] TYPES = {
		String.class, // RecordWithSession.sessionId
		String.class, // ServiceContextRecord.serviceExecutionId
		String.class, // ResponseTimeRecord.internalActionId
		String.class, // ResponseTimeRecord.resourceId
		long.class, // ResponseTimeRecord.startTime
		long.class, // ResponseTimeRecord.stopTime
	};
	
	/** default constants. */
	public static final String SESSION_ID = "<not set>";
	public static final String SERVICE_EXECUTION_ID = "<not set>";
	public static final String INTERNAL_ACTION_ID = "<not set>";
	public static final String RESOURCE_ID = "<not set>";
	private static final long serialVersionUID = 7481475336565010059L;
	
	/** property name array. */
	private static final String[] PROPERTY_NAMES = {
		"sessionId",
		"serviceExecutionId",
		"internalActionId",
		"resourceId",
		"startTime",
		"stopTime",
	};
	
	/** property declarations. */
	private final String sessionId;
	private final String serviceExecutionId;
	private final String internalActionId;
	private final String resourceId;
	private final long startTime;
	private final long stopTime;
	
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param sessionId
	 *            sessionId
	 * @param serviceExecutionId
	 *            serviceExecutionId
	 * @param internalActionId
	 *            internalActionId
	 * @param resourceId
	 *            resourceId
	 * @param startTime
	 *            startTime
	 * @param stopTime
	 *            stopTime
	 */
	public ResponseTimeRecord(final String sessionId, final String serviceExecutionId, final String internalActionId, final String resourceId, final long startTime, final long stopTime) {
		this.sessionId = sessionId == null?SESSION_ID:sessionId;
		this.serviceExecutionId = serviceExecutionId == null?SERVICE_EXECUTION_ID:serviceExecutionId;
		this.internalActionId = internalActionId == null?INTERNAL_ACTION_ID:internalActionId;
		this.resourceId = resourceId == null?RESOURCE_ID:resourceId;
		this.startTime = startTime;
		this.stopTime = stopTime;
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
	public ResponseTimeRecord(final Object[] values) { // NOPMD (direct store of values)
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.internalActionId = (String) values[2];
		this.resourceId = (String) values[3];
		this.startTime = (Long) values[4];
		this.stopTime = (Long) values[5];
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
	protected ResponseTimeRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.internalActionId = (String) values[2];
		this.resourceId = (String) values[3];
		this.startTime = (Long) values[4];
		this.stopTime = (Long) values[5];
	}

	
	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException 
	 *            when the record could not be deserialized
	 */
	public ResponseTimeRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
		this.sessionId = deserializer.getString();
		this.serviceExecutionId = deserializer.getString();
		this.internalActionId = deserializer.getString();
		this.resourceId = deserializer.getString();
		this.startTime = deserializer.getLong();
		this.stopTime = deserializer.getLong();
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
			this.getInternalActionId(),
			this.getResourceId(),
			this.getStartTime(),
			this.getStopTime(),
		};
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
		stringRegistry.get(this.getSessionId());
		stringRegistry.get(this.getServiceExecutionId());
		stringRegistry.get(this.getInternalActionId());
		stringRegistry.get(this.getResourceId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		//super.serialize(serializer);
		serializer.putString(this.getSessionId());
		serializer.putString(this.getServiceExecutionId());
		serializer.putString(this.getInternalActionId());
		serializer.putString(this.getResourceId());
		serializer.putLong(this.getStartTime());
		serializer.putLong(this.getStopTime());
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
		
		final ResponseTimeRecord castedRecord = (ResponseTimeRecord) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
			return false;
		}
		if (!this.getSessionId().equals(castedRecord.getSessionId())) {
			return false;
		}
		if (!this.getServiceExecutionId().equals(castedRecord.getServiceExecutionId())) {
			return false;
		}
		if (!this.getInternalActionId().equals(castedRecord.getInternalActionId())) {
			return false;
		}
		if (!this.getResourceId().equals(castedRecord.getResourceId())) {
			return false;
		}
		if (this.getStartTime() != castedRecord.getStartTime()) {
			return false;
		}
		if (this.getStopTime() != castedRecord.getStopTime()) {
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
	
	
	public final String getInternalActionId() {
		return this.internalActionId;
	}
	
	
	public final String getResourceId() {
		return this.resourceId;
	}
	
	
	public final long getStartTime() {
		return this.startTime;
	}
	
	
	public final long getStopTime() {
		return this.stopTime;
	}
	
}
