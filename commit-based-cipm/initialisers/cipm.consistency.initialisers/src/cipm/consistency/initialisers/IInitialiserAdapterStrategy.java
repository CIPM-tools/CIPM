package cipm.consistency.initialisers;

/**
 * An interface for classes and interfaces to implement, which encapsulate
 * initialisation logic for complex objects instantiated by
 * {@link IInitialiserBase} implementors. <br>
 * <br>
 * Although it is ultimately {@link IInitialiser}'s responsibility to
 * instantiate and initialise objects, this interface allows extracting parts of
 * it, especially the parts that revolve around other {@link IInitialiser}s. By
 * extracting that logic from {@link IInitialiser} implementors, dependencies to
 * other {@link IInitialiser}s can be spared.
 * 
 * @author Alp Torac Genc
 */
public interface IInitialiserAdapterStrategy {
	/**
	 * Applies the initialisation logic contained in this instance to the given
	 * object. Check the concrete class' documentation for more information, since
	 * the semantics of this method is tightly coupled with it.
	 * 
	 * @return Whether this method did what it was supposed to do.
	 */
	public boolean apply(IInitialiser init, Object obj);

	/**
	 * @return Creates a deep clone of this instance.
	 */
	public IInitialiserAdapterStrategy newStrategy();
}
