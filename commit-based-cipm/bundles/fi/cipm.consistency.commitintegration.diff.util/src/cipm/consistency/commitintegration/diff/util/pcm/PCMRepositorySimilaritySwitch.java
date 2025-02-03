package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.AbstractComposedSimilaritySwitch;

import cipm.consistency.commitintegration.diff.util.pcm.switches.SimilarityRepositorySwitch;
import cipm.consistency.commitintegration.diff.util.pcm.switches.SimilaritySeffSwitch;

/**
 * Concrete implementation of {@link AbstractComposedSimilaritySwitch} for
 * computing the similarity of Palladio Component Model (PCM) repositories.
 * 
 * @author Alp Torac Genc
 */
public class PCMRepositorySimilaritySwitch extends AbstractComposedSimilaritySwitch
		implements IPCMRepositorySimilaritySwitch {
	/**
	 * Constructs an instance with the given request handler and the flag. Adds
	 * default inner switches to the constructed instance.
	 * 
	 * @param srh                    The request handler, to which all incoming
	 *                               {@link ISimilarityRequest} instances will be
	 *                               delegated.
	 * @param checkStatementPosition The flag, which denotes whether this switch
	 *                               should take positions of statements while
	 *                               comparing.
	 */
	public PCMRepositorySimilaritySwitch(ISimilarityRequestHandler srh, boolean checkStatementPosition) {
		super(srh);

		this.addSwitch(new SimilarityRepositorySwitch(this, checkStatementPosition));
		this.addSwitch(new SimilaritySeffSwitch(this, checkStatementPosition));
	}
}