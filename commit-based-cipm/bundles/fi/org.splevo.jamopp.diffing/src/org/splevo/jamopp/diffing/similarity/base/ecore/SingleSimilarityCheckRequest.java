package org.splevo.jamopp.diffing.similarity.base.ecore;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest} for checking the similarity of 2
 * {@link EObject} instances.
 * 
 * @author atora
 */
public class SingleSimilarityCheckRequest implements ISimilarityRequest {
	private EObject element1;
	private EObject element2;
	private IComposedSwitchAdapter ss;

	/**
	 * @param element1 The first element.
	 * @param element2 The second element.
	 * @param ss       The switch that will be used to compare the elements above.
	 * 
	 * @see {@link ISimilarityChecker}
	 */
	public SingleSimilarityCheckRequest(EObject element1, EObject element2, IComposedSwitchAdapter ss) {
		this.element1 = element1;
		this.element2 = element2;
		this.ss = ss;
	}

	@Override
	public Object getParams() {
		return new Object[] { this.element1, this.element2, this.ss };
	}
}
