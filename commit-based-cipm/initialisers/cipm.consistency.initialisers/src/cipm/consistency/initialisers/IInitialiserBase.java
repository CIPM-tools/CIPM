package cipm.consistency.initialisers;

import java.util.Collection;

/**
 * An interface meant to be implemented by {@link IInitialiser} implementors,
 * which are supposed to be adaptable by {@link IInitialiserAdapterStrategy}
 * instances.
 * 
 * @author Alp Torac Genc
 */
public interface IInitialiserBase extends IInitialiser {
	/**
	 * Adds the given {@link IInitialiserAdapterStrategy} to this instance. Does not
	 * add, if {@code strat == null}.
	 */
	public void addAdaptingStrategy(IInitialiserAdapterStrategy strat);

	/**
	 * Removes the given {@link IInitialiserAdapterStrategy} from this instance.
	 * Does nothing, if {@code strat == null}.
	 */
	public void removeAdaptingStrategy(IInitialiserAdapterStrategy strat);

	/**
	 * Removes all stored {@link IInitialiserAdapterStrategy} instances from this.
	 */
	public void cleanAdaptingStrategy();

	/**
	 * @return All {@link IInitialiserAdapterStrategy} instances added to this.
	 *         Returns an empty collection if there are none. Modifying the elements
	 *         of the collection will modify the adaptation strategies adapting
	 *         this. Modifying the returned collection itself will have no effect on
	 *         this.
	 */
	public Collection<IInitialiserAdapterStrategy> getAdaptingStrategies();

	/**
	 * The variant of {@link #addAdaptingStrategy(IInitialiserAdapterStrategy)} for
	 * arrays. Does nothing if {@code strats == null}.
	 */
	public default void addAdaptingStrategies(IInitialiserAdapterStrategy[] strats) {
		if (strats != null) {
			for (var i : strats) {
				this.addAdaptingStrategy(i);
			}
		}
	}

	/**
	 * @return Number of {@link IInitialiserAdapterStrategy} instances that are
	 *         currently adapting this.
	 */
	public default int getAdaptingStrategyCount() {
		return this.getAdaptingStrategies().size();
	}

	/**
	 * @return Whether any {@link IInitialiserAdapterStrategy} instances are
	 *         currently adapting this.
	 */
	public default boolean isAdapted() {
		return !this.getAdaptingStrategies().isEmpty();
	}

	/**
	 * <b>For the sake of clarity, only use this method, if adaptation strategies
	 * are meant to be used.</b> <br>
	 * <br>
	 * {@inheritDoc}
	 * 
	 * @return Whether all initialiser adaptation strategies in this were applied
	 *         successfully. If there are no such strategies; returns true, since no
	 *         strategy was attempted to be applied and failed.
	 * 
	 * @see {@link IInitialiserAdapterStrategy}
	 */
	@Override
	public default boolean initialise(Object obj) {
		boolean result = true;

		for (var strat : this.getAdaptingStrategies()) {
			result = result && strat.apply(this, obj);
		}

		return result;
	}

	/**
	 * @return A new instance of this {@link IInitialiserBase} along with clones of
	 *         adaptation strategies currently adapting it.
	 */
	public default IInitialiserBase newInitialiserWithStrategies() {
		var newInit = (IInitialiserBase) this.newInitialiser();

		// Create a new strategy for the new initialiser, so that neither the original
		// strategy nor its copy are modified by the other one.
		this.getAdaptingStrategies().forEach((s) -> newInit.addAdaptingStrategy(s.newStrategy()));

		return newInit;
	}
}