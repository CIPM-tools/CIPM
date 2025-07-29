package cipm.consistency.fitests.similarity.jamopp.parser;

import cipm.consistency.fitests.similarity.eobject.ResourceTestOptions;

/**
 * A class that contains various options for test classes that parse
 * {@link Resource} instances and cache them:
 * <ul>
 * <li>shouldSaveCachedResources: Whether the cached resources should be saved
 * after tests
 * <li>shouldRemoveResourcesFromCache: Whether cached resources should be
 * removed after each test deleted after tests
 * </ul>
 * 
 * @see {@link ResourceTestOptions} for other options
 * @author Alp Torac Genc
 */
public class ParserTestOptions extends ResourceTestOptions {
	private boolean shouldSaveCachedResources;
	private boolean shouldRemoveResourcesFromCache;

	/**
	 * @see {@link ParserTestOptions}
	 */
	public void setShouldSaveCachedResources(boolean shouldSaveCachedResources) {
		this.shouldSaveCachedResources = shouldSaveCachedResources;
	}

	/**
	 * @see {@link ParserTestOptions}
	 */
	public void setShouldRemoveResourcesFromCache(boolean shouldRemoveResourcesFromCache) {
		this.shouldRemoveResourcesFromCache = shouldRemoveResourcesFromCache;
	}

	/**
	 * @see {@link ParserTestOptions}
	 */
	public boolean shouldSaveCachedResources() {
		return shouldSaveCachedResources;
	}

	/**
	 * @see {@link ParserTestOptions}
	 */
	public boolean shouldRemoveResourcesFromCache() {
		return shouldRemoveResourcesFromCache;
	}

	/**
	 * Copies all options from the given instance; i.e. after calling this method,
	 * all options inside the given instance will override the corresponding options
	 * in this.
	 */
	public void copyOptionsFrom(ParserTestOptions opts) {
		super.copyOptionsFrom(opts);
		this.shouldSaveCachedResources = opts.shouldSaveCachedResources;
		this.shouldRemoveResourcesFromCache = opts.shouldRemoveResourcesFromCache;
	}
}
