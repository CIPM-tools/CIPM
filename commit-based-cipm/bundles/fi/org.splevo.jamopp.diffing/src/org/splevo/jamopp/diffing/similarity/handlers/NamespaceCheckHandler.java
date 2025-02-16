package org.splevo.jamopp.diffing.similarity.handlers;

import org.splevo.jamopp.diffing.similarity.requests.NamespaceCheckRequest;
import org.emftext.language.java.commons.NamespaceAwareElement;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * An {@link ISimilarityRequestHandler} that processes incoming
 * {@link NamespaceCheckRequest} instances.
 * 
 * @author atora
 */
public class NamespaceCheckHandler implements ISimilarityRequestHandler {

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Checks the given namespaces for similarity and returns the similarity result.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		NamespaceCheckRequest castedR = (NamespaceCheckRequest) req;
		NamespaceAwareElement[] params = (NamespaceAwareElement[]) castedR.getParams();

		var ele1 = params[0];
		var ele2 = params[1];

		// Null check to avoid NullPointerExceptions
		if (ele1 == ele2) {
			return true;
		} else if (ele1 == null ^ ele2 == null) {
			return false;
		}
		
		var nss1 = ele1.getNamespaces();
		var nss2 = ele2.getNamespaces();

		// Null check to avoid NullPointerExceptions
		if (nss1 == nss2) {
			return true;
		} else if (nss1 == null ^ nss2 == null) {
			return false;
		}

		if (nss1.size() != nss2.size()) {
			return false;
		}

		for (int idx = 0; idx < nss1.size(); idx++) {
			if (!nss1.get(idx).equals(nss2.get(idx))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(NamespaceCheckRequest.class);
	}
}
