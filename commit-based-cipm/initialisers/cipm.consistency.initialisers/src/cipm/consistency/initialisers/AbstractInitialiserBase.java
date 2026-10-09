package cipm.consistency.initialisers;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An abstract class for {@link IInitialiserBase} implementors, which extends
 * them with infrastructure and concrete methods for
 * {@link IInitialiserAdapterStrategy}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractInitialiserBase implements IInitialiserBase {
	/**
	 * Stores the added {@link IInitialiserAdapterStrategy} instances.
	 */
	private Collection<IInitialiserAdapterStrategy> adaptingStrats;

	/**
	 * A variant of {@link #AbstractInitialiserBase(IInitialiserAdapterStrategy[])}
	 * that takes no {@link IInitialiserAdapterStrategy} instances.
	 */
	public AbstractInitialiserBase() {
		this(null);
	}

	/**
	 * Constructs an instance, which is adapted by the passed
	 * {@link IInitialiserAdapterStrategy} array.
	 */
	public AbstractInitialiserBase(IInitialiserAdapterStrategy[] adaptingStrats) {
		this.adaptingStrats = this.createAdaptingStrategyCol();
		this.addAdaptingStrategies(adaptingStrats);
	}

	/**
	 * @return A collection to store the added {@link IInitialiserAdapterStrategy}
	 *         instances. Only creates and returns a collection instance, it still
	 *         has to be assigned to relevant attributes and undergo additional
	 *         setup steps (if any).
	 */
	protected Collection<IInitialiserAdapterStrategy> createAdaptingStrategyCol() {
		return new ArrayList<IInitialiserAdapterStrategy>();
	}

	@Override
	public void addAdaptingStrategy(IInitialiserAdapterStrategy strat) {
		if (strat != null)
			this.adaptingStrats.add(strat);
	}

	@Override
	public void removeAdaptingStrategy(IInitialiserAdapterStrategy strat) {
		if (strat != null)
			this.adaptingStrats.remove(strat);
	}

	@Override
	public void cleanAdaptingStrategy() {
		this.adaptingStrats.clear();
	}

	@Override
	public Collection<IInitialiserAdapterStrategy> getAdaptingStrategies() {
		var res = this.createAdaptingStrategyCol();
		res.addAll(this.adaptingStrats);
		return res;
	}
}
