package org.splevo.jamopp.diffing.similarity.base.ecore;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNullCheckUtil;

/**
 * A {@link ISimilarityRequestHandler} that processes
 * {@link SingleSimilarityCheckRequest} instances.
 * 
 * @author Alp Torac Genc
 */
public class SingleSimilarityCheckHandler implements ISimilarityRequestHandler {
	/**
	 * Uses the given switch to compute the similarity of the given elements.
	 * 
	 * @param element1 The first parameter, which will be passed to the underlying
	 *                 {@link org.eclipse.emf.ecore.util.Switch#doSwitch(EObject)}
	 *                 method.
	 * @param element2 The compare element (check ss parameter for more
	 *                 information).
	 * @param ss       The switch that will be used during the similarity checking
	 * @return TRUE, if they are similar; FALSE if not, NULL if it can't be decided.
	 * 
	 * @see {@link IComposedSwitchAdapter#getCompareElement()}
	 */
	public Boolean isSimilar(EObject element1, EObject element2, IComposedSwitchAdapter ss) {
		// If no switch is given, similarity cannot be computed
		if (ss == null) {
			return null;
		}

		// check that either both or none of them is null
		if (JaMoPPNullCheckUtil.allNull(element1, element2)) {
			return Boolean.TRUE;
		}

		if (JaMoPPNullCheckUtil.onlyOneIsNull(element1, element2)) {
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
	 * {@inheritDoc} <br>
	 * <br>
	 * Check two objects if they are similar.
	 * 
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
