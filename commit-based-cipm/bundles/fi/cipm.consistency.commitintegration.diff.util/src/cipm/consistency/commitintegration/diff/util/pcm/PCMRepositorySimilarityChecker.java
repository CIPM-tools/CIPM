package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;
import org.splevo.jamopp.diffing.similarity.base.ecore.AbstractComposedSwitchSimilarityChecker;

import cipm.consistency.commitintegration.diff.util.pcm.requests.NewPCMRepositorySimilaritySwitchRequest;

/**
 * A SimilarityChecker for PCM repository models.
 * 
 * @author Martin Armbruster
 */
public class PCMRepositorySimilarityChecker extends AbstractComposedSwitchSimilarityChecker {
	public PCMRepositorySimilarityChecker(ISimilarityToolbox st) {
		super(st);
	}
    
	@Override
	protected PCMRepositorySimilarityComparer createSimilarityComparer(ISimilarityToolbox st) {
		return new PCMRepositorySimilarityComparer(st);
	}

	@Override
	protected ISimilarityRequest makeDefaultSwitchRequest() {
		return new NewPCMRepositorySimilaritySwitchRequest(true);
	}
}
