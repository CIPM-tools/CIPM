package org.splevo.jamopp.diffing.similarity.switches;

import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.base.ecore.IPositionInnerSwitch;
import org.splevo.jamopp.diffing.similarity.requests.NewSimilaritySwitchRequest;

/**
 * An interface that bundles and complements {@link IJavaSimilarityInnerSwitch}
 * and {@link IPositionInnerSwitch} interfaces. Contains methods, which are
 * specific to computing similarity in the context of Java model elements.
 * 
 * @author atora
 *
 */
public interface IJavaSimilarityPositionInnerSwitch extends IJavaSimilarityInnerSwitch, IPositionInnerSwitch {
	/**
	 * Sends out a {@link NewSimilaritySwitchRequest} and returns the result.
	 * 
	 * @see {@link #handleSimilarityRequest(org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest)}
	 */
	public default IJavaSimilaritySwitch requestNewSwitch(boolean checkStatementPosition) {
		return (IJavaSimilaritySwitch) this
				.handleSimilarityRequest(new NewSimilaritySwitchRequest(checkStatementPosition));
	}
}
