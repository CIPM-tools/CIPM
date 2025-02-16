package org.splevo.jamopp.diffing.similarity.handlers;

import org.splevo.jamopp.diffing.similarity.JavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.requests.NewSimilaritySwitchRequest;

/**
 * An {@link ISimilarityRequestHandler} that processes incoming
 * {@link NewSimilaritySwitchRequest} instances.
 * 
 * @author Alp Torac Genc
 */
public class NewSimilaritySwitchHandler implements ISimilarityRequestHandler {
	/**
	 * The {@link ISimilarityRequestHandler}, which will be passed onto the
	 * similarity switch created in
	 * {@link #handleSimilarityRequest(ISimilarityRequest)}.
	 */
	private ISimilarityRequestHandler srh;

	/**
	 * Constructs an instance with the given {@link ISimilarityRequestHandler}.
	 * 
	 * @param srh The {@link ISimilarityRequestHandler}, which will be passed onto
	 *            the similarity switch created in
	 *            {@link #handleSimilarityRequest(ISimilarityRequest)}.
	 */
	public NewSimilaritySwitchHandler(ISimilarityRequestHandler srh) {
		this.srh = srh;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Constructs a new {@link JavaSimilaritySwitch}.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		NewSimilaritySwitchRequest castedR = (NewSimilaritySwitchRequest) req;
		Boolean csp = (Boolean) castedR.getParams();
		return new JavaSimilaritySwitch(this.srh, csp);
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(NewSimilaritySwitchRequest.class);
	}
}
