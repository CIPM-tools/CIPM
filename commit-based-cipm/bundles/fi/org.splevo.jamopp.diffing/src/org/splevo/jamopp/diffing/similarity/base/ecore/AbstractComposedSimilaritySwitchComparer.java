package org.splevo.jamopp.diffing.similarity.base.ecore;

import org.splevo.jamopp.diffing.similarity.base.AbstractSimilarityComparer;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;

/**
 * An abstract class provided for integrating future {@link EObject} constructs
 * that may complement {@link AbstractSimilarityComparer}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractComposedSimilaritySwitchComparer extends AbstractSimilarityComparer {
	/**
	 * Constructs an instance with the given {@link ISimilarityToolbox}.
	 * 
	 * @param st The {@link ISimilarityToolbox}, to which all incoming
	 *           {@link ISimilarityRequest} instances should be delegated to.
	 */
	public AbstractComposedSimilaritySwitchComparer(ISimilarityToolbox st) {
		super(st);
	}
}
