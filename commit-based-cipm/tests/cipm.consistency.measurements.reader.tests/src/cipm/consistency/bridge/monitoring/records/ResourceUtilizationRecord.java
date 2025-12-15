package cipm.consistency.bridge.monitoring.records;

import java.nio.BufferOverflowException;

import cipm.consistency.bridge.monitoring.records.HostContextRecord;
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
public class ResourceUtilizationRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, HostContextRecord {			
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_STRING // HostContextRecord.hostId
			 + TYPE_SIZE_STRING // HostContextRecord.hostName
			 + TYPE_SIZE_STRING // ResourceUtilizationRecord.resourceId
			 + TYPE_SIZE_DOUBLE // ResourceUtilizationRecord.utilization
			 + TYPE_SIZE_LONG; // ResourceUtilizationRecord.timestamp
	
	public static final Class<?>[] TYPES = {
		String.class, // HostContextRecord.hostId
		String.class, // HostContextRecord.hostName
		String.class, // ResourceUtilizationRecord.resourceId
		double.class, // ResourceUtilizationRecord.utilization
		long.class, // ResourceUtilizationRecord.timestamp
	};
	
	/** default constants. */
	public static final String HOST_ID = "<not set>";
	public static final String HOST_NAME = "<not set>";
	public static final String RESOURCE_ID = "<not set>";
	private static final long serialVersionUID = 7798211021053080483L;
	
	/** property name array. */
	private static final String[] PROPERTY_NAMES = {
		"hostId",
		"hostName",
		"resourceId",
		"utilization",
		"timestamp",
	};
	
	/** property declarations. */
	private final String hostId;
	private final String hostName;
	private final String resourceId;
	private final double utilization;
	private final long timestamp;
	
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param hostId
	 *            hostId
	 * @param hostName
	 *            hostName
	 * @param resourceId
	 *            resourceId
	 * @param utilization
	 *            utilization
	 * @param timestamp
	 *            timestamp
	 */
	public ResourceUtilizationRecord(final String hostId, final String hostName, final String resourceId, final double utilization, final long timestamp) {
		this.hostId = hostId == null?HOST_ID:hostId;
		this.hostName = hostName == null?HOST_NAME:hostName;
		this.resourceId = resourceId == null?RESOURCE_ID:resourceId;
		this.utilization = utilization;
		this.timestamp = timestamp;
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
	public ResourceUtilizationRecord(final Object[] values) { // NOPMD (direct store of values)
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.hostId = (String) values[0];
		this.hostName = (String) values[1];
		this.resourceId = (String) values[2];
		this.utilization = (Double) values[3];
		this.timestamp = (Long) values[4];
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
	protected ResourceUtilizationRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.hostId = (String) values[0];
		this.hostName = (String) values[1];
		this.resourceId = (String) values[2];
		this.utilization = (Double) values[3];
		this.timestamp = (Long) values[4];
	}

	
	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException 
	 *            when the record could not be deserialized
	 */
	public ResourceUtilizationRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
		this.hostId = deserializer.getString();
		this.hostName = deserializer.getString();
		this.resourceId = deserializer.getString();
		this.utilization = deserializer.getDouble();
		this.timestamp = deserializer.getLong();
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
			this.getHostId(),
			this.getHostName(),
			this.getResourceId(),
			this.getUtilization(),
			this.getTimestamp(),
		};
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
		stringRegistry.get(this.getHostId());
		stringRegistry.get(this.getHostName());
		stringRegistry.get(this.getResourceId());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		//super.serialize(serializer);
		serializer.putString(this.getHostId());
		serializer.putString(this.getHostName());
		serializer.putString(this.getResourceId());
		serializer.putDouble(this.getUtilization());
		serializer.putLong(this.getTimestamp());
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
		
		final ResourceUtilizationRecord castedRecord = (ResourceUtilizationRecord) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
			return false;
		}
		if (!this.getHostId().equals(castedRecord.getHostId())) {
			return false;
		}
		if (!this.getHostName().equals(castedRecord.getHostName())) {
			return false;
		}
		if (!this.getResourceId().equals(castedRecord.getResourceId())) {
			return false;
		}
		if (isNotEqual(this.getUtilization(), castedRecord.getUtilization())) {
			return false;
		}
		if (this.getTimestamp() != castedRecord.getTimestamp()) {
			return false;
		}
		
		return true;
	}
	
	public final String getHostId() {
		return this.hostId;
	}
	
	
	public final String getHostName() {
		return this.hostName;
	}
	
	
	public final String getResourceId() {
		return this.resourceId;
	}
	
	
	public final double getUtilization() {
		return this.utilization;
	}
	
	
	public final long getTimestamp() {
		return this.timestamp;
	}
	
}
