package org.splevo.jamopp.diffing.similarity.base.ecore;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * An interface for the classes to implement, which extend
 * {@link org.eclipse.emf.ecore.util.Switch} and are nested in
 * {@link org.eclipse.emf.ecore.util.ComposedSwitch}. This interface contains
 * getters and delegation methods that are mutual among its implementors.
 * 
 * @author Alp Torac Genc
 */
public interface IInnerSwitch extends ISimilarityRequestHandler {
	/**
	 * @return The {@link ISimilarityRequestHandler}, to which all incoming
	 *         {@link ISimilarityRequest} instances will be delegated.
	 */
	public ISimilarityRequestHandler getSimilarityRequestHandler();

	/**
	 * @return The {@link IComposedSwitchAdapter} containing this switch
	 */
	public IComposedSwitchAdapter getContainingSwitch();

	/**
	 * @return The current compare element.
	 * @see {@link IComposedSwitchAdapter}
	 */
	public default EObject getCompareElement() {
		return this.getContainingSwitch().getCompareElement();
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Here, the incoming requests are delegated to
	 * {@link #getSimilarityRequestHandler()}.
	 */
	@Override
	public default Object handleSimilarityRequest(ISimilarityRequest req) {
		return this.getSimilarityRequestHandler().handleSimilarityRequest(req);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Here, this method is delegated to {@link #getSimilarityRequestHandler()}.
	 */
	@Override
	public default boolean canHandleSimilarityRequest(Class<? extends ISimilarityRequest> reqClass) {
		return this.getSimilarityRequestHandler().canHandleSimilarityRequest(reqClass);
	}
}
