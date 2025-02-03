package org.splevo.jamopp.diffing.similarity.requests;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest}, which contains the name of a
 * {@link CompilationUnit} instance normalised via
 * {@link CompilationUnitNormalizationRequest} first.
 * 
 * @author Alp Torac Genc
 */
public class PackageNormalizationRequest implements ISimilarityRequest {
	/**
	 * The name of the {@link CompilationUnit} instance normalised via
	 * {@link CompilationUnitNormalizationRequest} first.
	 */
	private String toBeNormalized;

	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @param toBeNormalized The name of the {@link CompilationUnit} instance
	 *                       normalised via
	 *                       {@link CompilationUnitNormalizationRequest} first.
	 */
	public PackageNormalizationRequest(String toBeNormalized) {
		this.toBeNormalized = toBeNormalized;
	}

	@Override
	public Object getParams() {
		return this.toBeNormalized;
	}
}
