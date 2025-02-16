package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.jamopp.diffing.similarity.base.ecore.AbstractComposedSimilaritySwitchComparer;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;

/**
 * Concrete implementation of {@link AbstractComposedSimilaritySwitchComparer}
 * for for comparing Palladio Component Model (PCM) repositories.
 * 
 * @author Alp Torac Genc
 */
public class PCMRepositorySimilarityComparer extends AbstractComposedSimilaritySwitchComparer {
	/**
	 * Constructs an instance with a given {@link ISimilarityToolbox}.
	 * 
	 * @param st The {@link ISimilarityToolbox}, to which all incoming
	 *           {@link ISimilarityRequest} instances should be delegated to.
	 */
	public PCMRepositorySimilarityComparer(ISimilarityToolbox st) {
		super(st);
	}
}
