package org.splevo.jamopp.diffing.similarity.requests;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest}, which contains the name of a
 * {@link CompilationUnit} instance: {@code compUnit.name()}.<br>
 * <br>
 * Instances can be passed to a matching {@link ISimilarityRequestHandler} for
 * the above to be normalised.
 * 
 * @author Alp Torac Genc
 */
public class CompilationUnitNormalizationRequest implements ISimilarityRequest {
	/**
	 * The name of the {@link CompilationUnit}.
	 */
	private String toBeNormalized;

	/**
	 * Constructs an instance.
	 * 
	 * @param toBeNormalized The name of the {@link CompilationUnit}
	 *                       ({@code compUnit.name()}).
	 */
	public CompilationUnitNormalizationRequest(String toBeNormalized) {
		this.toBeNormalized = toBeNormalized;
	}

	@Override
	public Object getParams() {
		return this.toBeNormalized;
	}
}
