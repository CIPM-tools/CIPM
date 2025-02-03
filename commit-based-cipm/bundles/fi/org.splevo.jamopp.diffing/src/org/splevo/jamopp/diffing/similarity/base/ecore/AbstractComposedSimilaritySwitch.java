package org.splevo.jamopp.diffing.similarity.base.ecore;

import java.util.Collection;

import org.eclipse.emf.ecore.util.Switch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * An abstract class that complements {@link AbstractComposedSwitchAdapter} with
 * an attribute to store an {@link ISimilarityRequestHandler} instance. This
 * attribute is then used during similarity checking to delegate
 * {@link ISimilarityRequest} instances to their proper
 * {@link ISimilarityRequestHandler} instance.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractComposedSimilaritySwitch extends AbstractComposedSwitchAdapter
		implements ISimilarityRequestHandler {

	/**
	 * The {@link ISimilarityRequestHandler}, to which incoming
	 * {@link ISimilarityRequest} instances will be delegated.
	 */
	private ISimilarityRequestHandler srh;

	/**
	 * Constructs an instance with the given {@link ISimilarityRequestHandler}.
	 * 
	 * @param srh The {@link ISimilarityRequestHandler}, to which incoming
	 *            {@link ISimilarityRequest} instances will be delegated.
	 */
	public AbstractComposedSimilaritySwitch(ISimilarityRequestHandler srh) {
		super();
		this.srh = srh;
	}

	/**
	 * Variation of
	 * {@link #AbstractComposedSimilaritySwitch(ISimilarityRequestHandler)} that
	 * constructs an instance with the given switches.
	 * 
	 * @see {@link AbstractComposedSwitchAdapter#AbstractComposedSwitchWrapper(Collection)}
	 */
	protected AbstractComposedSimilaritySwitch(ISimilarityRequestHandler srh, Collection<Switch<Boolean>> switches) {
		super(switches);
		this.srh = srh;
	}

	/**
	 * Variation of
	 * {@link #AbstractComposedSimilaritySwitch(ISimilarityRequestHandler)} that
	 * constructs an instance with the given switches.
	 * 
	 * @see {@link AbstractComposedSwitchAdapter#AbstractComposedSwitchWrapper(Switch[])}
	 */
	protected AbstractComposedSimilaritySwitch(ISimilarityRequestHandler srh, Switch<Boolean>[] switches) {
		super(switches);
		this.srh = srh;
	}

	/**
	 * @return The {@link ISimilarityRequestHandler}, to which incoming
	 *         {@link ISimilarityRequest} instances will be delegated.
	 */
	protected ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.srh;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Here, it delegates the incoming {@link ISimilarityRequest} to
	 * {@link #getSimilarityRequestHandler()}.
	 */
	@Override
	public Object handleSimilarityRequest(ISimilarityRequest req) {
		return this.getSimilarityRequestHandler().handleSimilarityRequest(req);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Here, this method is delegated to {@link #getSimilarityRequestHandler()}.
	 */
	@Override
	public boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return this.getSimilarityRequestHandler().canHandleSimilarityRequest(reqClass);
	}
}
