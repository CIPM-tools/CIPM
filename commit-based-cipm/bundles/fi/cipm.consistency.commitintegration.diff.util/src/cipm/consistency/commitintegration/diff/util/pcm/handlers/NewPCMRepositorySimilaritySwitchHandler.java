package cipm.consistency.commitintegration.diff.util.pcm.handlers;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

import cipm.consistency.commitintegration.diff.util.pcm.PCMRepositorySimilaritySwitch;
import cipm.consistency.commitintegration.diff.util.pcm.requests.NewPCMRepositorySimilaritySwitchRequest;

/**
 * An {@link ISimilarityRequestHandler} that processes incoming
 * {@link NewPCMRepositorySimilaritySwitchRequest} instances.
 * 
 * @author Alp Torac Genc
 */
public class NewPCMRepositorySimilaritySwitchHandler implements ISimilarityRequestHandler {
	/**
	 * The {@link ISimilarityRequestHandler}, which will be passed onto the
	 * similarity switches created in
	 * {@link #handleSimilarityRequest(ISimilarityRequest)}.
	 */
	private ISimilarityRequestHandler srh;

	/**
	 * Constructs an instance with the given {@link ISimilarityRequestHandler}.
	 * 
	 * @param srh The {@link ISimilarityRequestHandler}, which will be passed onto
	 *            the similarity switches created in
	 *            {@link #handleSimilarityRequest(ISimilarityRequest)}.
	 */
	public NewPCMRepositorySimilaritySwitchHandler(ISimilarityRequestHandler srh) {
		this.srh = srh;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Constructs a new {@link PCMRepositorySimilaritySwitch} (with its
	 * {@link #srh}) and returns it.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		NewPCMRepositorySimilaritySwitchRequest castedR = (NewPCMRepositorySimilaritySwitchRequest) req;
		Boolean csp = (Boolean) castedR.getParams();
		return new PCMRepositorySimilaritySwitch(this.srh, csp);
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(NewPCMRepositorySimilaritySwitchRequest.class);
	}
}
