package cipm.consistency.commitintegration.diff.util.pcm.switches;

import org.splevo.jamopp.diffing.similarity.base.ecore.IPositionInnerSwitch;

import cipm.consistency.commitintegration.diff.util.pcm.IPCMRepositorySimilaritySwitch;
import cipm.consistency.commitintegration.diff.util.pcm.requests.NewPCMRepositorySimilaritySwitchRequest;

/**
 * An interface that bundles and complements {@link IPCMInnerSwitch} and
 * {@link IPositionInnerSwitch} interfaces. Contains methods, which are specific
 * to computing similarity in the context of Palladio Component Model (PCM)
 * repositories.
 * 
 * @author Alp Torac Genc
 */
public interface IPCMPositionInnerSwitch extends IPCMInnerSwitch, IPositionInnerSwitch {
	/**
	 * Sends out a {@link NewPCMRepositorySimilaritySwitchRequest} and returns the
	 * result.
	 * 
	 * @see {@link #handleSimilarityRequest(org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest)}
	 */
	public default IPCMRepositorySimilaritySwitch requestNewSwitch(boolean checkStatementPosition) {
		return (IPCMRepositorySimilaritySwitch) this
				.handleSimilarityRequest(new NewPCMRepositorySimilaritySwitchRequest(checkStatementPosition));
	}
}
