package org.splevo.jamopp.diffing.similarity.base.ecore;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An {@link ISimilarityRequest} for checking the similarity of 2 collections of
 * {@link EObject} instances.
 * 
 * @author atora
 */
public class MultipleSimilarityCheckRequest implements ISimilarityRequest {
	private Collection<? extends EObject> elements1;
	private Collection<? extends EObject> elements2;
	private Collection<? extends IComposedSwitchAdapter> sss;

	/**
	 * Constructs a request that encapsulates 2 {@link EObject} collections and a
	 * collection of switches.
	 * 
	 * @param elements1 The first element collection.
	 * @param elements2 The second element collection.
	 * @param sss       Collection of switches that will be used for comparing the
	 *                  elements. i-th switch in the collection will be used in the
	 *                  similarity checking of i-th elements from respective
	 *                  collections.
	 * 
	 * @see {@link MultipleSimilarityCheckHandler#handleSimilarityRequest(ISimilarityRequest)}
	 */
	public MultipleSimilarityCheckRequest(Collection<? extends EObject> elements1,
			Collection<? extends EObject> elements2, Collection<? extends IComposedSwitchAdapter> sss) {
		this.elements1 = elements1;
		this.elements2 = elements2;
		this.sss = sss;
	}

	@Override
	public Object getParams() {
		return new Object[] { this.elements1, this.elements2, this.sss };
	}
}
