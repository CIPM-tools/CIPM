package org.splevo.jamopp.diffing.similarity.base;

/**
 * An abstract class for similarity comparers to extend. Complements
 * {@link ISimilarityComparer} with the integration of
 * {@link ISimilarityToolbox}. <br>
 * <br>
 * Delegates incoming {@link ISimilarityRequest} instances to its
 * {@link ISimilarityToolbox}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractSimilarityComparer implements ISimilarityComparer {
	/**
	 * The {@link ISimilarityToolbox}, to which all incoming
	 * {@link ISimilarityRequest} instances should be delegated to.
	 */
	private ISimilarityToolbox st;

	/**
	 * Constructs an instance with a given {@link ISimilarityToolbox} parameter.
	 * 
	 * @param st The {@link ISimilarityToolbox}, to which all incoming
	 *           {@link ISimilarityRequest} instances should be delegated to.
	 */
	public AbstractSimilarityComparer(ISimilarityToolbox st) {
		this.st = st;
	}

	/**
	 * @return The {@link ISimilarityToolbox}, to which all incoming
	 *         {@link ISimilarityRequest} instances should be delegated to.
	 */
	protected ISimilarityToolbox getSimilarityToolbox() {
		return this.st;
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * Here, this method is delegated to {@link #getSimilarityToolbox()}.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		return this.getSimilarityToolbox().handleSimilarityRequest(req);
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * Here, this method is delegated to {@link #getSimilarityToolbox()}.
	 */
	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return this.getSimilarityToolbox().canHandleSimilarityRequest(reqClass);
	}
}