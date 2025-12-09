package cipm.consistency.cpr.pcmjava.userinteraction;

/**
 * An interface for classes that modify {@link FeatureEntry} and/or
 * {@link CorrespondenceEntry} instances.
 * 
 * @author Alp Torac Genc
 */
public interface CanModifyEntries {
	/**
	 * @return If present, the unique identifier for this instance, which can be
	 *         used to uniquely identify it among other {@link CanModifyEntries}
	 *         instances. Otherwise returns null.
	 */
	public String getID();

	/**
	 * Sets the unique identifier for this instance.
	 * 
	 * @param id       The unique identifier to be assigned to this instance
	 * @param forceSet Whether the given id should replace the potentially existing
	 *                 id.
	 */
	public void setID(String id, boolean forceSet);

	/**
	 * Declaration in {@link CanModifyEntries} is only for documentation purposes.
	 * 
	 * @return {@link #getID()} if there exists an assigned id, otherwise calls the
	 *         super method
	 */
	public String toString();
}
