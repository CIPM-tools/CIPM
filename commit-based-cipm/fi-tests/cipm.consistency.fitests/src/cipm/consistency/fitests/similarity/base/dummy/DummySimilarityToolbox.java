package cipm.consistency.fitests.similarity.base.dummy;

import java.util.ArrayList;
import java.util.List;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;

/**
 * An exemplary toolbox implementation, which also keeps track of the handling
 * history (see {@link #getHandlingHistory()}) <br>
 * <br>
 * A real toolbox implementation does not have to keep track of its handling
 * history. Here, it is implemented for testing purposes. <br>
 * <br>
 * <b>Note: How incoming similarity requests are internally handled (i.e. to
 * which handler(s) they are delegated and what is done with the received
 * output(s)) depends on the concrete implementation of the toolbox.</b>
 * 
 * @author Alp Torac Genc
 */
public class DummySimilarityToolbox implements ISimilarityToolbox {
	/**
	 * The list of similarity request-handler pairs.
	 */
	private List<RequestHandlerPairEntry> pairs;
	/**
	 * The list of handling history entries.
	 */
	private List<HandleHistoryEntry> history;

	public DummySimilarityToolbox() {
		this.init();
	}

	/**
	 * Initialises the list of pairs and history entries.
	 */
	public void init() {
		this.pairs = new ArrayList<RequestHandlerPairEntry>();
		this.history = new ArrayList<HandleHistoryEntry>();
	}

	@Override
	public void addRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass, ISimilarityRequestHandler srh) {
		var entry = new RequestHandlerPairEntry();

		entry.setRequestCls(reqClass);
		entry.setHandler(srh);

		this.pairs.add(entry);
	}

	@Override
	public void removeRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass) {
		this.pairs.removeIf((e) -> e.getRequestCls().equals(reqClass));
	}

	@Override
	public void clearRequestHandlerPairs() {
		this.pairs.clear();
	}

	/**
	 * @return The similarity request-handler pairs, whose request class matches the
	 *         given parameter.
	 */
	protected RequestHandlerPairEntry[] getPairsFor(Class<? extends ISimilarityRequest> reqClass) {
		return this.pairs.stream().filter((e) -> e.getRequestCls().equals(reqClass))
				.toArray(RequestHandlerPairEntry[]::new);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Here, req is handled only by the handlers, whose corresponding request
	 * (within its pair entry) matches req.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		var pairs = this.getPairsFor(req.getClass());
		var count = pairs.length;

		var results = new Object[count];

		for (int i = 0; i < count; i++) {
			var he = new HandleHistoryEntry();

			var handler = pairs[i].getHandler();
			var output = handler.handleSimilarityRequest(req);
			results[i] = output;

			he.setRequest(req);
			he.setHandler(handler);
			he.setHandled(handler.canHandleSimilarityRequest(req));
			he.setOutput(output);
			this.history.add(he);
		}

		var reducedResult = this.reduceResults(results);

		return reducedResult != null ? reducedResult : results;
	}

	/**
	 * @return Reduces the given output, if it is of type Boolean or Boolean[].
	 *         Returns null, if either the type of output is mismatching or output
	 *         contains elements that are not Boolean. Returns false, if output is a
	 *         Boolean array of length 0. Uses logical AND to reduce.
	 */
	public Boolean reduceResults(Object output) {
		if (output instanceof Boolean)
			return (Boolean) output;

		if (output.getClass().isArray()) {
			var resultArr = (Object[]) output;
			if (resultArr.length == 0) {
				return Boolean.FALSE;
			} else {
				var result = true;

				for (var res : resultArr) {
					if (!(res instanceof Boolean)) {
						return null;
					} else {
						result = result && ((Boolean) res).booleanValue();
					}
				}

				return result;
			}
		}

		return null;
	}

	/**
	 * @return The handling history (i.e. which request was processed by what
	 *         handlers along with the produced output. Processing DOES NOT imply
	 *         proper handling.)
	 */
	public List<HandleHistoryEntry> getHandlingHistory() {
		return this.history.subList(0, this.history.size());
	}

	/**
	 * A class that stores all aspects of a similarity checking request being
	 * handled by a handler.
	 * 
	 * @author Alp Torac Genc
	 */
	public class HandleHistoryEntry {
		private ISimilarityRequest req;
		private ISimilarityRequestHandler handler;
		private Object output;
		private boolean isHandled;

		protected HandleHistoryEntry() {
		}

		public ISimilarityRequest getRequest() {
			return req;
		}

		public void setRequest(ISimilarityRequest req) {
			this.req = req;
		}

		public ISimilarityRequestHandler getHandler() {
			return handler;
		}

		public void setHandler(ISimilarityRequestHandler handler) {
			this.handler = handler;
		}

		/**
		 * @see {@link #setOutput(Object)}
		 */
		public Object getOutput() {
			return output;
		}

		/**
		 * @param output What the {@link #getHandler()} returned after handling
		 *               {@link #getRequest()}
		 */
		public void setOutput(Object output) {
			this.output = output;
		}

		/**
		 * @see {@link #setHandled(boolean)}
		 */
		public boolean isHandled() {
			return isHandled;
		}

		/**
		 * @param isHandled Whether {@link #getHandler()} successfully handled
		 *                  {@link #getRequest()}
		 */
		public void setHandled(boolean isHandled) {
			this.isHandled = isHandled;
		}
	}

	/**
	 * Stores a pair of similarity checking request and its corresponding handler.
	 * 
	 * @author Alp Torac Genc
	 */
	protected class RequestHandlerPairEntry {
		private Class<? extends ISimilarityRequest> reqCls;
		private ISimilarityRequestHandler handler;

		public Class<? extends ISimilarityRequest> getRequestCls() {
			return reqCls;
		}

		public void setRequestCls(Class<? extends ISimilarityRequest> reqCls) {
			this.reqCls = reqCls;
		}

		public ISimilarityRequestHandler getHandler() {
			return handler;
		}

		public void setHandler(ISimilarityRequestHandler handler) {
			this.handler = handler;
		}
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return this.getPairsFor(reqClass).length > 0;
	}
}
