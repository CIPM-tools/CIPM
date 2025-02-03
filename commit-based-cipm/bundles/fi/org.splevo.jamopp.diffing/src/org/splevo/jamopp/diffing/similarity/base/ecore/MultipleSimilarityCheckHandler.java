package org.splevo.jamopp.diffing.similarity.base.ecore;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * A {@link ISimilarityRequestHandler} that processes
 * {@link MultipleSimilarityCheckRequest} instances.
 * 
 * @author Alp Torac Genc
 */
public class MultipleSimilarityCheckHandler implements ISimilarityRequestHandler {
	/**
	 * The {@link ISimilarityRequestHandler}, to which
	 * {@link SingleSimilarityCheckRequest} instances will be delegated.
	 */
	private ISimilarityRequestHandler srh;

	/**
	 * Constructs an instance with the given {@link ISimilarityRequestHandler},
	 * which is able to handle {@link SingleSimilarityCheckRequest} instances.
	 * 
	 * @param srh The {@link ISimilarityRequestHandler}, to which
	 *            {@link SingleSimilarityCheckRequest} instances will be delegated.
	 */
	public MultipleSimilarityCheckHandler(ISimilarityRequestHandler srh) {
		this.srh = srh;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Compares the similarity of the {@link EObject} collections from the incoming
	 * request pairwise. To do so, it uses the switch at index i to compare elements
	 * at the index i in their respective lists.
	 * 
	 * @return
	 *         <ul>
	 *         <li>True (as Boolean), if they are all similar
	 *         <li>False (as Boolean), if a different number of elements is
	 *         submitted or at least one pair of elements is not similar to each
	 *         other
	 *         <li>Null, if sizes of both collections of {@link EObjects} match but
	 *         the amount of provided similarity switches in {@code req} does not
	 *         match their size
	 *         </ul>
	 * @see {@link ISimilarityChecker}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		MultipleSimilarityCheckRequest castedR = (MultipleSimilarityCheckRequest) req;

		Object[] params = (Object[]) castedR.getParams();
		Collection<? extends EObject> elements1 = (Collection<? extends EObject>) params[0];
		Collection<? extends EObject> elements2 = (Collection<? extends EObject>) params[1];
		Collection<? extends IComposedSwitchAdapter> sss = (Collection<? extends IComposedSwitchAdapter>) params[2];

		// Null check to avoid NullPointerExceptions
		if (elements1 == elements2) {
			return Boolean.TRUE;
		} else if (elements1 == null ^ elements2 == null) {
			return Boolean.FALSE;
		}

		int size = elements1.size();

		if (size != elements2.size()) {
			return Boolean.FALSE;
		}

		if (sss == null || size != sss.size()) {
			return null;
		}

		var es1 = elements1.toArray(EObject[]::new);
		var es2 = elements2.toArray(EObject[]::new);
		var ssA = sss.toArray(IComposedSwitchAdapter[]::new);

		for (int i = 0; i < size; i++) {
			Boolean childSimilarity = (Boolean) this.srh
					.handleSimilarityRequest(new SingleSimilarityCheckRequest(es1[i], es2[i], ssA[i]));

			if (childSimilarity == Boolean.FALSE) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(MultipleSimilarityCheckRequest.class);
	}
}
