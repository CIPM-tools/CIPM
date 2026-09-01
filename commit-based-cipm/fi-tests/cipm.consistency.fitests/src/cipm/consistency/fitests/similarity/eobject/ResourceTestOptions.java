package cipm.consistency.fitests.similarity.eobject;

/**
 * A class that contains various options for test classes that use
 * {@link Resource} instances:
 * <ul>
 * <li>shouldUnloadAllModelResources: Whether all created model resource
 * instances should be unloaded after each test
 * <li>shouldDeleteAllModelResources: Whether all created model resource files
 * should be deleted after each test
 * </ul>
 * <b><i>Note: This class is only responsible for containing (model)
 * Resource-related options. Giving these options meanings and applying them is
 * not the concern of this class. </i></b>
 * 
 * @author Alp Torac Genc
 */
public class ResourceTestOptions {
	private boolean shouldUnloadAllModelResources;
	private boolean shouldDeleteAllModelResources;

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public void setShouldUnloadAllModelResources(boolean shouldUnloadAllModelResources) {
		this.shouldUnloadAllModelResources = shouldUnloadAllModelResources;
	}

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public void setShouldDeleteAllModelResources(boolean shouldDeleteAllModelResources) {
		this.shouldDeleteAllModelResources = shouldDeleteAllModelResources;
	}

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public boolean shouldUnloadAllModelResources() {
		return shouldUnloadAllModelResources;
	}

	/**
	 * @see {@link ResourceTestOptions}
	 */
	public boolean shouldDeleteAllModelResources() {
		return shouldDeleteAllModelResources;
	}

	/**
	 * Copies all options from the given instance; i.e. after calling this method,
	 * all options inside the given instance will override the corresponding options
	 * in this. All sub-types should implement a version of this method for their
	 * own type, in order to enable partially copying options from super-types.
	 */
	public void copyOptionsFrom(ResourceTestOptions opts) {
		this.shouldUnloadAllModelResources = opts.shouldUnloadAllModelResources;
		this.shouldDeleteAllModelResources = opts.shouldDeleteAllModelResources;
	}
}
