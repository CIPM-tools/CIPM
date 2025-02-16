package org.splevo.jamopp.diffing.similarity.requests;

import org.emftext.language.java.commons.NamespaceAwareElement;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest}, which contains 2 {@link NamespaceAwareElement}
 * instances.
 * 
 * @author atora
 */
public class NamespaceCheckRequest implements ISimilarityRequest {
	private NamespaceAwareElement ele1;
	private NamespaceAwareElement ele2;

	/**
	 * Constructs an instance with the given parameters.
	 * 
	 * @param ele1 The first element.
	 * @param ele2 The second element.
	 */
	public NamespaceCheckRequest(NamespaceAwareElement ele1, NamespaceAwareElement ele2) {
		this.ele1 = ele1;
		this.ele2 = ele2;
	}

	@Override
	public Object getParams() {
		return new NamespaceAwareElement[] { this.ele1, this.ele2 };
	}
}
