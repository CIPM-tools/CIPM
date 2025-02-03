package org.splevo.jamopp.diffing.similarity.handlers;

import java.util.Map;
import java.util.regex.Pattern;

import org.splevo.diffing.util.NormalizationUtil;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.requests.CompilationUnitNormalizationRequest;

/**
 * An {@link ISimilarityRequestHandler} that processes incoming
 * {@link CompilationUnitNormalizationRequest} instances.
 * 
 * @author Alp Torac Genc
 */
public class CompilationUnitNormalizationHandler implements ISimilarityRequestHandler {
	/**
	 * @see {@link JaMoPPDiffer#initSimilarityChecker(Map)}
	 */
	private Map<Pattern, String> compilationUnitNormalizations;

	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @see {@link JaMoPPDiffer#initSimilarityChecker(Map)}
	 */
	public CompilationUnitNormalizationHandler(Map<Pattern, String> compilationUnitNormalizations) {
		this.compilationUnitNormalizations = compilationUnitNormalizations;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Performs the requested normalisation and returns the result.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		CompilationUnitNormalizationRequest castedR = (CompilationUnitNormalizationRequest) req;

		return NormalizationUtil.normalize((String) castedR.getParams(), this.compilationUnitNormalizations);
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(CompilationUnitNormalizationRequest.class);
	}
}
