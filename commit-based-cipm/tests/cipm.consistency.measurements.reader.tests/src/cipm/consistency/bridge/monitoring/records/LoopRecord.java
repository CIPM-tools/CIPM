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
public class LoopRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, ServiceContextRecord {			
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_STRING // RecordWithSession.sessionId
			 + TYPE_SIZE_STRING // ServiceContextRecord.serviceExecutionId
			 + TYPE_SIZE_STRING // LoopRecord.loopId
			 + TYPE_SIZE_LONG; // LoopRecord.loopIterationCount
	
	public static final Class<?>[] TYPES = {
		String.class, // RecordWithSession.sessionId
		String.class, // ServiceContextRecord.serviceExecutionId
		String.class, // LoopRecord.loopId
		long.class, // LoopRecord.loopIterationCount
	};
	
	/** default constants. */
	public static final String SESSION_ID = "<not set>";
	public static final String SERVICE_EXECUTION_ID = "<not set>";
	public static final String LOOP_ID = "<not set>";
	private static final long serialVersionUID = -2382675201395801969L;
	
	/** property name array. */
	private static final String[] PROPERTY_NAMES = {
		"sessionId",
		"serviceExecutionId",
		"loopId",
		"loopIterationCount",
	};
	
	/** property declarations. */
	private final String sessionId;
	private final String serviceExecutionId;
	private final String loopId;
	private final long loopIterationCount;
	
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param sessionId
	 *            sessionId
	 * @param serviceExecutionId
	 *            serviceExecutionId
	 * @param loopId
	 *            loopId
	 * @param loopIterationCount
	 *            loopIterationCount
	 */
	public LoopRecord(final String sessionId, final String serviceExecutionId, final String loopId, final long loopIterationCount) {
		this.sessionId = sessionId == null?SESSION_ID:sessionId;
		this.serviceExecutionId = serviceExecutionId == null?SERVICE_EXECUTION_ID:serviceExecutionId;
		this.loopId = loopId == null?LOOP_ID:loopId;
		this.loopIterationCount = loopIterationCount;
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
	public LoopRecord(final Object[] values) { // NOPMD (direct store of values)
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.loopId = (String) values[2];
		this.loopIterationCount = (Long) values[3];
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
	protected LoopRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.sessionId = (String) values[0];
		this.serviceExecutionId = (String) values[1];
		this.loopId = (String) values[2];
		this.loopIterationCount = (Long) values[3];
	}

	
	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException 
	 *            when the record could not be deserialized
	 */
	public LoopRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
		this.sessionId = deserializer.getString();
		this.serviceExecutionId = deserializer.getString();
		this.loopId = deserializer.getString();
		this.loopIterationCount = deserializer.getLong();
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
			this.getLoopId(),
			this.getLoopIterationCount(),
		};
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
		stringRegistry.get(this.getSessionId());
		stringRegistry.get(this.getServiceExecutionId());
		stringRegistry.get(this.getLoopId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		//super.serialize(serializer);
		serializer.putString(this.getSessionId());
		serializer.putString(this.getServiceExecutionId());
		serializer.putString(this.getLoopId());
		serializer.putLong(this.getLoopIterationCount());
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
		
		final LoopRecord castedRecord = (LoopRecord) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
			return false;
		}
		if (!this.getSessionId().equals(castedRecord.getSessionId())) {
			return false;
		}
		if (!this.getServiceExecutionId().equals(castedRecord.getServiceExecutionId())) {
			return false;
		}
		if (!this.getLoopId().equals(castedRecord.getLoopId())) {
			return false;
		}
		if (this.getLoopIterationCount() != castedRecord.getLoopIterationCount()) {
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
	
	
	public final String getLoopId() {
		return this.loopId;
	}
	
	
	public final long getLoopIterationCount() {
		return this.loopIterationCount;
	}
	
}
