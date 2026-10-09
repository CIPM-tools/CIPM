package org.splevo.jamopp.diffing.similarity.base;

/**
 * An interface for encapsulating similarity operations, whose implementors take
 * {@link ISimilarityRequest} instances and process them. Implementors of this
 * interface are meant to be used to extract such operations from other
 * classes/interfaces, as well as their relevant parameters, if plausible. Doing
 * so keeps other classes/interfaces clear of functionality that does not
 * concern them. <br>
 * <br>
 * Implementors can contain further {@link ISimilarityRequestHandler} instances,
 * such as an {@link ISimilarityToolbox} or request-specific
 * {@link ISimilarityRequestHandler} instances, if they have to process requests
 * that need further processing.
 * 
 * @author Alp Torac Genc
 */
public interface ISimilarityRequestHandler {
	/**
	 * Processes the given {@link ISimilarityRequest} and returns the result. Check
	 * the matching request type for more information. <br>
	 * <br>
	 * It is recommended to not override the return type, especially if the concrete
	 * {@link ISimilarityRequestHandler} instances are planned to be extended.
	 * 
	 * @param req The incoming request.
	 */
	public Object handleSimilarityRequest(ISimilarityRequest req);

	/**
	 * @param req A given {@link ISimilarityRequest} instance
	 * 
	 * @return Whether {@code req} can be handled.
	 */
	public default boolean canHandleSimilarityRequest(ISimilarityRequest req) {
		return this.canHandleSimilarityRequest(req.getClass());
	}

	/**
	 * @param reqClass The class of the {@link ISimilarityRequest} instance
	 * 
	 * @return Whether instances of {@code reqClass} can be handled.
	 */
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass);
}
