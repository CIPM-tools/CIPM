package org.splevo.jamopp.diffing.similarity.handlers;

import java.util.Map;
import java.util.regex.Pattern;

import org.splevo.diffing.util.NormalizationUtil;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.requests.PackageNormalizationRequest;

/**
 * An {@link ISimilarityRequestHandler} that processes incoming
 * {@link PackageNormalizationRequest} instances.
 * 
 * @author Alp Torac Genc
 */
public class PackageNormalizationHandler implements ISimilarityRequestHandler {
	/**
	 * @see {@link JaMoPPDiffer#initSimilarityChecker(Map)}
	 */
	private Map<Pattern, String> packageNormalizations;

	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @see {@link JaMoPPDiffer#initSimilarityChecker(Map)}
	 */
	public PackageNormalizationHandler(Map<Pattern, String> packageNormalizations) {
		this.packageNormalizations = packageNormalizations;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Performs the requested normalisation and returns the result.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		PackageNormalizationRequest castedR = (PackageNormalizationRequest) req;

		return NormalizationUtil.normalize((String) castedR.getParams(), this.packageNormalizations);
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(PackageNormalizationRequest.class);
	}
}
