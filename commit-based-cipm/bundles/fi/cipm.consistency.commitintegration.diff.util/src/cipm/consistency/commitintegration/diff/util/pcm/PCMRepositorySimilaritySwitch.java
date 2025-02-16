package cipm.consistency.commitintegration.diff.util.pcm;

import java.util.Collection;

import org.eclipse.emf.ecore.util.Switch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.AbstractComposedSimilaritySwitch;

import cipm.consistency.commitintegration.diff.util.pcm.switches.SimilarityRepositorySwitch;
import cipm.consistency.commitintegration.diff.util.pcm.switches.SimilaritySeffSwitch;

/**
 * Concrete implementation of {@link AbstractComposedSimilaritySwitch} for
 * computing the similarity of Palladio Component Model (PCM) repositories.
 * 
 * @author atora
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

	/**
	 * Constructs an instance with the given {@link ISimilarityRequestHandler}.
	 * 
	 * @param srh The {@link ISimilarityRequestHandler}, to which incoming
	 *            {@link ISimilarityRequest} instances will be delegated.
	 */
	protected PCMRepositorySimilaritySwitch(ISimilarityRequestHandler srh) {
		super(srh);
	}

	/**
	 * Variation of
	 * {@link #PCMRepositorySimilaritySwitch(ISimilarityRequestHandler)} that
	 * constructs an instance with the given switches.
	 */
	protected PCMRepositorySimilaritySwitch(ISimilarityRequestHandler srh, Collection<Switch<Boolean>> switches) {
		super(srh, switches);
	}

	/**
	 * Variation of
	 * {@link #PCMRepositorySimilaritySwitch(ISimilarityRequestHandler)} that
	 * constructs an instance with the given switches.
	 */
	protected PCMRepositorySimilaritySwitch(ISimilarityRequestHandler srh, Switch<Boolean>[] switches) {
		super(srh, switches);
	}
}