package org.splevo.jamopp.diffing.similarity.requests;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest}, which contains a flag (checkStatementPosition)
 * that denotes, whether the new similarity switch created by processing this
 * request should care about positions of statements while computing
 * similarity.<br>
 * <br>
 * Instances can be passed to a matching {@link ISimilarityRequestHandler} for
 * the creation of a new similarity switch ({@link JavaSimilaritySwitch}).
 * 
 * @author Alp Torac Genc
 */
public class NewSimilaritySwitchRequest implements ISimilarityRequest {
	/**
	 * The flag that denotes, whether the resulting new similarity switch should
	 * care about positions of statements while computing similarity.
	 */
	private boolean checkStatementPosition;

	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @param checkStatementPosition The flag that denotes, whether the resulting
	 *                               new similarity switch should care about
	 *                               positions of statements while computing
	 *                               similarity.
	 */
	public NewSimilaritySwitchRequest(boolean checkStatementPosition) {
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
	public Object getParams() {
		return this.checkStatementPosition;
	}
}
