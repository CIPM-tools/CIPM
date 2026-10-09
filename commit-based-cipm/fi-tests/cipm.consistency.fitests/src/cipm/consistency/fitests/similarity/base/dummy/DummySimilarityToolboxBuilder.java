package cipm.consistency.fitests.similarity.base.dummy;

import org.splevo.jamopp.diffing.similarity.base.AbstractSimilarityToolboxBuilder;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * An exemplary toolbox builder that illustrates what a real toolbox could look
 * like. <br>
 * <br>
 * Provides methods for individual request-handler pairs as well as convenience
 * methods, similar to real toolbox implementations. <br>
 * <br>
 * Note: If there are methods that add conflicting request-handler pairs or
 * replace existing ones (depends on the toolbox factory the toolbox builder
 * uses}, make sure to document them or provide ways to determine what will be
 * done in such cases.
 * 
 * @author Alp Torac Genc
 */
public class DummySimilarityToolboxBuilder extends AbstractSimilarityToolboxBuilder {
	@Override
	public DummySimilarityToolboxBuilder instantiate() {
		return (DummySimilarityToolboxBuilder) super.instantiate();
	}

	@Override
	public DummySimilarityToolboxBuilder buildRequestHandlerPair(Class<? extends ISimilarityRequest> req,
			ISimilarityRequestHandler srh) {
		return (DummySimilarityToolboxBuilder) super.buildRequestHandlerPair(req, srh);
	}

	/**
	 * Adds the request-handler pair for {@link EqualsCheckRequest}.
	 * 
	 * @return this
	 */
	public DummySimilarityToolboxBuilder buildEqualsCheckHandler() {
		return this.buildRequestHandlerPair(EqualsCheckRequest.class, new EqualsCheckRequestHandler());
	}

	/**
	 * Adds the request-handler pair for {@link ReferenceCheckRequest}.
	 * 
	 * @return this
	 */
	public DummySimilarityToolboxBuilder buildReferenceCheckHandler() {
		return this.buildRequestHandlerPair(ReferenceCheckRequest.class, new ReferenceCheckRequestHandler());
	}

	/**
	 * Combines all buildX methods introduced within this class for convenience.
	 * 
	 * @return this
	 */
	public DummySimilarityToolboxBuilder buildEqualityCheckingHandlers() {
		this.buildEqualsCheckHandler();
		return this.buildReferenceCheckHandler();
	}
}
