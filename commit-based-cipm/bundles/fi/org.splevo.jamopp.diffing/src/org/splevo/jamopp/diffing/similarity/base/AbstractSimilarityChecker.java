package org.splevo.jamopp.diffing.similarity.base;

/**
 * An abstract class for similarity checkers to extend. Complements
 * {@link ISimilarityChecker} with the integration of
 * {@link ISimilarityComparer}. <br>
 * <br>
 * Does not implement {@link ISimilarityRequestHandler}, because its concrete
 * implementors are thought to serve as facades to the outside. They are thus
 * not intended to be used during the similarity checking process, other than a
 * call to similarity checking methods, such as
 * {@link #isSimilar(Object, Object)}, at the start.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractSimilarityChecker implements ISimilarityChecker {
	/**
	 * The {@link ISimilarityComparer}, to which incoming {@link ISimilarityRequest}
	 * instances are to be delegated.
	 */
	private ISimilarityComparer sc;

	/**
	 * Constructs an {@link AbstractSimilarityChecker} instance with an
	 * {@link ISimilarityComparer}, which delegates all incoming
	 * {@link ISimilarityRequest} to the given parameter.
	 * 
	 * @param st {@link ISimilarityToolbox} to which all incoming
	 *           {@link ISimilarityRequest} instances will be delegated to.
	 */
	public AbstractSimilarityChecker(ISimilarityToolbox st) {
		this.sc = this.createSimilarityComparer(st);
	}

	/**
	 * Declared as protected only to allow access to the underlying
	 * {@link ISimilarityComparer} from concrete implementors.
	 * 
	 * @return {@link ISimilarityToolbox} to which all incoming
	 *         {@link ISimilarityRequest} instances will be delegated to.
	 */
	protected ISimilarityComparer getSimilarityComparer() {
		return this.sc;
	}

	/**
	 * Delegates the incoming {@link ISimilarityRequest} to the underlying
	 * {@link ISimilarityComparer}. <br>
	 * <br>
	 * Declared as protected only to let concrete implementors delegate incoming
	 * {@link ISimilarityRequest} instances to their {@link ISimilarityComparer}.
	 * <br>
	 * <br>
	 * This is necessary, because creating further internal constructs that contain
	 * similarity checking logic, such as similarity switches, may involve using
	 * {@link ISimilarityRequestHandler} instances and that in return requires
	 * delegating {@link ISimilarityRequest} instances.
	 */
	protected Object handleSimilarityRequest(ISimilarityRequest req) {
		return this.getSimilarityComparer().handleSimilarityRequest(req);
	}

	/**
	 * Creates an {@link ISimilarityComparer} with the given
	 * {@link ISimilarityToolbox}.
	 * 
	 * @param st The {@link ISimilarityToolbox} that the constructed
	 *           {@link ISimilarityComparer} will use.
	 */
	protected abstract ISimilarityComparer createSimilarityComparer(ISimilarityToolbox st);
}