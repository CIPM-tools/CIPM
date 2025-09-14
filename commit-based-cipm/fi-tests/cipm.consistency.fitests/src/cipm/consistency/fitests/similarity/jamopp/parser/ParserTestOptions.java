package cipm.consistency.fitests.similarity.jamopp.parser;

import cipm.consistency.fitests.similarity.eobject.ResourceTestOptions;

/**
 * A class that contains various options for test classes that parse
 * {@link Resource} instances and cache them:
 * <ul>
 * <li>shouldSaveCachedModelResources: Whether the cached model resources should
 * be saved after tests
 * <li>shouldRemoveModelResourcesFromCache: Whether cached model resources
 * should be removed after each test deleted after tests
 * </ul>
 * 
 * @see {@link ResourceTestOptions} for other options
 * @author Alp Torac Genc
 */
public class ParserTestOptions extends ResourceTestOptions {
	private boolean shouldSaveCachedModelResources;
	private boolean shouldRemoveModelResourcesFromCache;

	/**
	 * @see {@link ParserTestOptions}
	 */
	public void setShouldSaveCachedModelResources(boolean shouldSaveCachedModelResources) {
		this.shouldSaveCachedModelResources = shouldSaveCachedModelResources;
	}

	/**
	 * @see {@link ParserTestOptions}
	 */
	public void setShouldRemoveModelResourcesFromCache(boolean shouldRemoveModelResourcesFromCache) {
		this.shouldRemoveModelResourcesFromCache = shouldRemoveModelResourcesFromCache;
	}

	/**
	 * @see {@link ParserTestOptions}
	 */
	public boolean shouldSaveCachedModelResources() {
		return shouldSaveCachedModelResources;
	}

	/**
	 * @see {@link ParserTestOptions}
	 */
	public boolean shouldRemoveModelResourcesFromCache() {
		return shouldRemoveModelResourcesFromCache;
	}

	/**
	 * Copies all options from the given instance; i.e. after calling this method,
	 * all options inside the given instance will override the corresponding options
	 * in this.
	 */
	public void copyOptionsFrom(ParserTestOptions opts) {
		super.copyOptionsFrom(opts);
		this.shouldSaveCachedModelResources = opts.shouldSaveCachedModelResources;
		this.shouldRemoveModelResourcesFromCache = opts.shouldRemoveModelResourcesFromCache;
	}
}
