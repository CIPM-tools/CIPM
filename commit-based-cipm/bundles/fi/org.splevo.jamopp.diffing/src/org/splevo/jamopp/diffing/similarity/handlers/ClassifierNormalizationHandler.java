package org.splevo.jamopp.diffing.similarity.handlers;

import java.util.Map;
import java.util.regex.Pattern;

import org.splevo.diffing.util.NormalizationUtil;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.requests.ClassifierNormalizationRequest;

/**
 * An {@link ISimilarityRequestHandler} that processes incoming
 * {@link ClassifierNormalizationRequest} instances.
 * 
 * @author atora
 */
public class ClassifierNormalizationHandler implements ISimilarityRequestHandler {
	/**
	 * @see {@link JaMoPPDiffer#initSimilarityChecker(Map)}
	 */
	private Map<Pattern, String> classifierNormalizations;

	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @see {@link JaMoPPDiffer#initSimilarityChecker(Map)}
	 */
	public ClassifierNormalizationHandler(Map<Pattern, String> classifierNormalizations) {
		this.classifierNormalizations = classifierNormalizations;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Performs the requested normalisation and returns the result.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		ClassifierNormalizationRequest castedR = (ClassifierNormalizationRequest) req;

		return NormalizationUtil.normalize((String) castedR.getParams(), this.classifierNormalizations);
	}

	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return reqClass.equals(ClassifierNormalizationRequest.class);
	}
}
