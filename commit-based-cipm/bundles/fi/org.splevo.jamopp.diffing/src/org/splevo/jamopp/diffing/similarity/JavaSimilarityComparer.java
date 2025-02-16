package org.splevo.jamopp.diffing.similarity;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;
import org.splevo.jamopp.diffing.similarity.base.ecore.AbstractComposedSimilaritySwitchComparer;

/**
 * Concrete implementation of {@link AbstractComposedSimilaritySwitchComparer}
 * for comparing Java elements.
 * 
 * @author Alp Torac Genc
 */
public class JavaSimilarityComparer extends AbstractComposedSimilaritySwitchComparer {
	/**
	 * Constructs an instance with the given parameter.
	 * 
	 * @param st {@link ISimilarityToolbox} to which all incoming
	 *           {@link ISimilarityRequest} instances will be delegated to.
	 */
	public JavaSimilarityComparer(ISimilarityToolbox st) {
		super(st);
	}
}
