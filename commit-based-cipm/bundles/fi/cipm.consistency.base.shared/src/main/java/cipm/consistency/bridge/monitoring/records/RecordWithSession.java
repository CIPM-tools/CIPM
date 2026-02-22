package cipm.consistency.bridge.monitoring.records;


/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public interface RecordWithSession extends PCMContextRecord {
	public String getSessionId();
	
}
