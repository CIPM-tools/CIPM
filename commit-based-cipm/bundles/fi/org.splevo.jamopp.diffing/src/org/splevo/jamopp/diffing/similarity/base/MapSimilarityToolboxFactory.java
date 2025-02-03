package org.splevo.jamopp.diffing.similarity.base;

/**
 * A factory that creates {@link MapSimilarityToolbox} instances.
 * 
 * @author Alp Torac Genc
 */
public class MapSimilarityToolboxFactory implements ISimilarityToolboxFactory {
	@Override
	public MapSimilarityToolbox createSimilarityToolbox() {
		return new MapSimilarityToolbox();
	}
}
