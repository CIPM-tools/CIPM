package org.splevo.jamopp.diffing.similarity.requests;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest}, which contains all namespaces of a
 * {@link NamespaceAwareElement} instance as String:
 * {@code nae.getNamespacesAsString()}
 * 
 * @author Alp Torac Genc
 */
public class NamespaceNormalizationRequest implements ISimilarityRequest {
	/**
	 * All namespaces of the {@link NamespaceAwareElement} instance as String.
	 */
	private String toBeNormalized;

	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @param toBeNormalized All namespaces of the {@link NamespaceAwareElement}
	 *                       instance as String ({@code nae.getNamespacesAsString()})
	 */
	public NamespaceNormalizationRequest(String toBeNormalized) {
		this.toBeNormalized = toBeNormalized;
	}

	@Override
	public Object getParams() {
		return this.toBeNormalized;
	}
}
