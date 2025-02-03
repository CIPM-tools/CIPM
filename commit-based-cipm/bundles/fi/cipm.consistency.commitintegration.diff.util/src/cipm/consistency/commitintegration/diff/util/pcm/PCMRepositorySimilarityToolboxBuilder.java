package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.jamopp.diffing.similarity.base.AbstractSimilarityToolboxBuilder;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.MultipleSimilarityCheckHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.MultipleSimilarityCheckRequest;
import org.splevo.jamopp.diffing.similarity.base.ecore.SingleSimilarityCheckHandler;
import org.splevo.jamopp.diffing.similarity.base.ecore.SingleSimilarityCheckRequest;

import cipm.consistency.commitintegration.diff.util.pcm.handlers.IDBasedSingleSimilarityCheckHandler;
import cipm.consistency.commitintegration.diff.util.pcm.handlers.NewPCMRepositorySimilaritySwitchHandler;
import cipm.consistency.commitintegration.diff.util.pcm.requests.NewPCMRepositorySimilaritySwitchRequest;

/**
 * Concrete implementation of {@link AbstractSimilarityToolboxBuilder} for
 * constructing {@link ISimilarityToolbox} instances for computing similarity of
 * Palladio Component Model (PCM) repositories.
 * 
 * @author Alp Torac Genc
 */
public class PCMRepositorySimilarityToolboxBuilder extends AbstractSimilarityToolboxBuilder {
	@Override
	public PCMRepositorySimilarityToolboxBuilder instantiate() {
		return (PCMRepositorySimilarityToolboxBuilder) super.instantiate();
	}

	@Override
	public PCMRepositorySimilarityToolboxBuilder buildRequestHandlerPair(Class<? extends ISimilarityRequest> req,
			ISimilarityRequestHandler srh) {
		return (PCMRepositorySimilarityToolboxBuilder) super.buildRequestHandlerPair(req, srh);
	}

	/**
	 * Adds the handlers required to handle similarity checking related
	 * {@link ISimilarityRequest} instances. <br>
	 * <br>
	 * <b>Pairs added by this method will override those added by
	 * {@link #buildIDBasedComparisonPairs()} </b>
	 * 
	 * @return this
	 */
	public PCMRepositorySimilarityToolboxBuilder buildComparisonPairs() {
		this.buildRequestHandlerPair(SingleSimilarityCheckRequest.class, new SingleSimilarityCheckHandler());
		this.buildRequestHandlerPair(MultipleSimilarityCheckRequest.class,
				new MultipleSimilarityCheckHandler(this.getCurrentToolbox()));

		return this;
	}

	/**
	 * Adds the handlers required to handle ID based similarity checking related
	 * {@link ISimilarityRequest} instances. <br>
	 * <br>
	 * <b>Pairs added by this method will override those added by
	 * {@link #buildComparisonPairs()} </b>
	 * 
	 * @return this
	 */
	public PCMRepositorySimilarityToolboxBuilder buildIDBasedComparisonPairs() {
		this.buildRequestHandlerPair(SingleSimilarityCheckRequest.class, new IDBasedSingleSimilarityCheckHandler());
		this.buildRequestHandlerPair(MultipleSimilarityCheckRequest.class,
				new MultipleSimilarityCheckHandler(this.getCurrentToolbox()));

		return this;
	}

	/**
	 * Adds the handler needed to handle {@link ISimilarityRequest} instances, which
	 * request new similarity switch instances.
	 * 
	 * @return this
	 * @see {@link IPCMRepositorySimilaritySwitch}
	 */
	public PCMRepositorySimilarityToolboxBuilder buildNewPCMRepositorySimilaritySwitch() {
		this.buildRequestHandlerPair(NewPCMRepositorySimilaritySwitchRequest.class,
				new NewPCMRepositorySimilaritySwitchHandler(this.getCurrentToolbox()));

		return this;
	}
}
