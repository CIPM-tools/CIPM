package cipm.consistency.fitests.similarity.eobject;

/**
 * A class that contains various options for test classes that use
 * {@link Resource} instances:
 * <ul>
 * <li>shouldUnloadAllResources: Whether all created resource instances should
 * be unloaded after each test
 * <li>shouldDeleteAllResources: Whether all created resource files should be
 * deleted after each test
 * </ul>
 * <b><i>Note: This class is only responsible for containing Resource-related
 * options. Giving these options meanings and applying them is not the concern
 * of this class. </i></b>
 * 
 * @author Alp Torac Genc
 */
public class ResourceTestOptions {
	private boolean shouldUnloadAllResources;
	private boolean shouldDeleteAllResources;

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public void setShouldUnloadAllResources(boolean shouldUnloadAllResources) {
		this.shouldUnloadAllResources = shouldUnloadAllResources;
	}

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public void setShouldDeleteAllResources(boolean shouldDeleteAllResources) {
		this.shouldDeleteAllResources = shouldDeleteAllResources;
	}

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public boolean shouldUnloadAllResources() {
		return shouldUnloadAllResources;
	}

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public boolean shouldDeleteAllResources() {
		return shouldDeleteAllResources;
	}

	/**
	 * Copies all options from the given instance; i.e. after calling this method,
	 * all options inside the given instance will override the corresponding options
	 * in this.
	 */
	public void copyOptionsFrom(ResourceTestOptions opts) {
		this.shouldUnloadAllResources = opts.shouldUnloadAllResources;
		this.shouldDeleteAllResources = opts.shouldDeleteAllResources;
	}
}
