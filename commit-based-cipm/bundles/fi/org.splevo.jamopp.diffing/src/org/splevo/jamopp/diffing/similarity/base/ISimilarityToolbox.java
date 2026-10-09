package org.splevo.jamopp.diffing.similarity.base;

/**
 * An interface for classes that contain ({@link ISimilarityRequest} (class),
 * {@link ISimilarityRequestHandler} (instance)) pairs (or request-handler
 * pairs). <br>
 * <br>
 * Since the implementors contain request-handler pairs specified above, they
 * themselves can be used as {@link ISimilarityRequestHandler} to handle
 * {@link ISimilarityRequest} instances. In such cases, they delegate the
 * received {@link ISimilarityRequest} to their matching
 * {@link ISimilarityRequestHandler}. <br>
 * <br>
 * Implementors of this interface allow adding and removing the said pairs
 * dynamically. In doing so, they make dynamic changes to similarity checking
 * possible. Additionally, new similarity operations can be integrated without
 * modifying the implementors themselves by calling the
 * {@link #addRequestHandlerPair(Class, ISimilarityRequestHandler)} method. <br>
 * <br>
 * This interface makes no assumptions on the data structure used in its
 * concrete implementors to store request-handler pairs. Neither does it
 * explicitly specify how incoming {@link ISimilarityRequest} instances are
 * internally handled.
 * 
 * @see {@link ISimilarityToolboxBuilder}, {@link ISimilarityToolboxFactory}
 * @author Alp Torac Genc
 */
public interface ISimilarityToolbox extends ISimilarityRequestHandler {
	/**
	 * Adds a ({@link ISimilarityRequest} (class), {@link ISimilarityRequestHandler}
	 * (instance)) pair (or request-handler pair). <br>
	 * <br>
	 * <b>Note that the first parameter (reqClass) is the class of the
	 * {@link ISimilarityRequest} and the second parameter (srh) is an
	 * {@link ISimilarityRequestHandler} instance.</b>
	 * 
	 * @param reqClass The class of the {@link ISimilarityRequest} instances, which
	 *                 should be processed using the given
	 *                 {@link ISimilarityRequestHandler} instance.
	 * @param srh      The {@link ISimilarityRequestHandler} instance, which should
	 *                 be used to process the {@link ISimilarityRequest} instances
	 *                 of the given class.
	 */
	public void addRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass, ISimilarityRequestHandler srh);

	/**
	 * Removes the pairs added for {@link ISimilarityRequest}.
	 * 
	 * @param reqClass The class of the {@link ISimilarityRequest} instances
	 * 
	 * @see {@link #addRequestHandlerPair(Class, ISimilarityRequestHandler)}
	 */
	public void removeRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass);

	/**
	 * Removes all pairs added to this instance.
	 * 
	 * @see {@link #addRequestHandlerPair(Class, ISimilarityRequestHandler)}
	 */
	public void clearRequestHandlerPairs();

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * The data structure used in the {@link ISimilarityToolbox} implementation to
	 * store request-handler pairs can influence the way {@code req} is handled. For
	 * example, in cases where there is no corresponding handler or if there are
	 * multiple corresponding handlers.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req);

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass);
}
