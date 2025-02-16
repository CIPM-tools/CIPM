package org.splevo.jamopp.diffing.similarity.base.ecore;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * A {@link ISimilarityRequestHandler} that processes
 * {@link SingleSimilarityCheckRequest} instances.
 * 
 * @author atora
 */
public class SingleSimilarityCheckHandler implements ISimilarityRequestHandler {
	/**
	 * Uses the given switch to compute the similarity of the given elements.
	 * 
	 * @param element1 The first element to check.
	 * @param element2 The second element to check.
	 * @param ss       The switch that will be used during the similarity checking
	 * @return TRUE, if they are similar; FALSE if not, NULL if it can't be decided.
	 */
	public Boolean isSimilar(EObject element1, EObject element2, IComposedSwitchAdapter ss) {
		// If no switch is given, similarity cannot be computed
		if (ss == null) {
			return null;
		}

		// check that either both or none of them is null
		if (element1 == element2) {
			return Boolean.TRUE;
		}

		if (onlyOneIsNull(element1, element2)) {
			return Boolean.FALSE;
		}

		// if a proxy is present try to resolve it
		// the other element is used as a context.
		// TODO Clarify why it can happen that one proxy is resolved and the other is
		// not
		// further notes available with the issue
		// https://sdqbuild.ipd.kit.edu/jira/browse/SPLEVO-279
		if (element2.eIsProxy() && !element1.eIsProxy()) {
			element2 = EcoreUtil.resolve(element2, element1);
		} else if (element1.eIsProxy() && !element2.eIsProxy()) {
			element1 = EcoreUtil.resolve(element1, element2);
		}

		// check the elements to be of the same type
		if (!element1.getClass().equals(element2.getClass())) {
			return Boolean.FALSE;
		}

		// check type specific similarity
		return ss.compare(element1, element2);
	}

	/**
	 * Method to check if only one of the provided elements is null.
	 *
	 * @param element1 The first element.
	 * @param element2 The second element.
	 * @return True if only one element is null and the other is not.
	 */
	protected Boolean onlyOneIsNull(final EObject element1, final EObject element2) {
		Boolean onlyOneIsNull = false;
		if (element1 != null && element2 == null) {
			onlyOneIsNull = Boolean.TRUE;
		} else if (element1 == null && element2 != null) {
			onlyOneIsNull = Boolean.TRUE;
		}
		return onlyOneIsNull;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Check two objects if they are similar.
	 *
	 * @param element1 The first element to check.
	 * @param element2 The second element to check.
	 * @return TRUE, if they are similar; FALSE if not, NULL if it can't be decided.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		SingleSimilarityCheckRequest castedR = (SingleSimilarityCheckRequest) req;
		Object[] params = (Object[]) castedR.getParams();
		EObject elem1 = (EObject) params[0];
		EObject elem2 = (EObject) params[1];
		IComposedSwitchAdapter ss = (IComposedSwitchAdapter) params[2];

		return this.isSimilar(elem1, elem2, ss);
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(SingleSimilarityCheckRequest.class);
	}
}
