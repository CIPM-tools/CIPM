package cipm.consistency.fitests.similarity.jamopp.unittests;

/**
 * An interface that can be implemented by tests, which work with
 * {@link NamedElement} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link NamedElement} instances.
 */
public interface UsesNames {
	/**
	 * @return A default name that can be used while constructing
	 *         {@link NamedElement} instances
	 */
	public default String getDefaultName() {
		return "";
	}
}
