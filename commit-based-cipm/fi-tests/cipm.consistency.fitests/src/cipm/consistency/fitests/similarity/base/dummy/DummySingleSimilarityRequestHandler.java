package cipm.consistency.fitests.similarity.base.dummy;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * An exemplary custom handler for similarity checking 2 objects. <br>
 * <br>
 * Note: Providing a reference to the {@link ISimilarityRequestHandler} instance
 * containing this handler allows it to send out similarity checking requests
 * while processing the request it initially received. This can be used in real
 * handlers, in order to re-use code and to keep consistency, if needed.
 * 
 * @author Alp Torac Genc
 */
public class DummySingleSimilarityRequestHandler implements ISimilarityRequestHandler {
	private ISimilarityRequestHandler srh;

	public DummySingleSimilarityRequestHandler(ISimilarityRequestHandler srh) {
		this.srh = srh;
	}

	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		var castedReq = (DummySingleSimilarityRequest) req;
		var elems = (Object[]) castedReq.getParams();
		var elem1 = elems[0];
		var elem2 = elems[1];

		/*
		 * Perform reference check (==) and return true, if successful. If not, make
		 * sure that none of the elements is null and compare them using .equals()
		 */
		if ((Boolean) srh.handleSimilarityRequest(new ReferenceCheckRequest(elem1, elem2))) {
			return Boolean.TRUE;
		} else if (elem1 == null || elem2 == null) {
			return Boolean.FALSE;
		}

		return srh.handleSimilarityRequest(new EqualsCheckRequest(elem1, elem2));
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(DummySingleSimilarityRequest.class);
	}
}
