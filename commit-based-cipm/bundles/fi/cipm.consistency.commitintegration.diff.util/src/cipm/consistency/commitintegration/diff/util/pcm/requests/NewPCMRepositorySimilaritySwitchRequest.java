package cipm.consistency.commitintegration.diff.util.pcm.requests;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest}, which contains a flag that denotes, whether
 * the new similarity switch created by processing this request should care
 * about positions of statements while computing similarity.
 * 
 * @author Alp Torac Genc
 */
public class NewPCMRepositorySimilaritySwitchRequest implements ISimilarityRequest {
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
	public NewPCMRepositorySimilaritySwitchRequest(boolean checkStatementPosition) {
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
	public Object getParams() {
		return this.checkStatementPosition;
	}
}
