package org.splevo.jamopp.diffing.similarity.base;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link ISimilarityToolbox} implementor, that uses a {@link Map} to contain
 * 1 to 1 mappings between request-handler pairs (see
 * {@link ISimilarityToolbox}). <br>
 * <br>
 * <b>This means that only one {@link ISimilarityRequestHandler} stored in this
 * instance will process incoming {@link ISimilarityRequest}.</b><br>
 * <br>
 * Therefore, if a request is to be handled by further handlers, either the
 * delegation to those handlers must be implemented in the initial handler or
 * another concrete {@link ISimilarityToolbox} implementation should be used.
 * 
 * @author atora
 */
public class MapSimilarityToolbox implements ISimilarityToolbox {
	/**
	 * The map that contains the request-handler pairs.
	 */
	private Map<Class<? extends ISimilarityRequest>, ISimilarityRequestHandler> rhMap;

	/**
	 * Constructs an instance and initialises the map it uses to store
	 * request-handler pairs.
	 */
	public MapSimilarityToolbox() {
		this.rhMap = new HashMap<Class<? extends ISimilarityRequest>, ISimilarityRequestHandler>();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * If there is already a request-handler pair for reqClass, it will be replaced
	 * by the given pair (reqClass, srh).
	 */
	@Override
	public void addRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass, ISimilarityRequestHandler srh) {
		this.rhMap.put(reqClass, srh);
	}

	@Override
	public void removeRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass) {
		this.rhMap.remove(reqClass);
	}

	@Override
	public void clearRequestHandlerPairs() {
		this.rhMap.clear();
	}

	/**
	 * Attempts to handle the given {@link ISimilarityRequest} with a matching
	 * {@link ISimilarityRequestHandler} and returns the output.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		var handler = this.rhMap.get(req.getClass());

		return handler != null ? handler.handleSimilarityRequest(req) : null;
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return this.rhMap.containsKey(reqClass);
	}
}
