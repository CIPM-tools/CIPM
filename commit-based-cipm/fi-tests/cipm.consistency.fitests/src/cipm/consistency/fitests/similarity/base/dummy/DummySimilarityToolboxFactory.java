package cipm.consistency.fitests.similarity.base.dummy;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolboxFactory;

/**
 * A minimal toolbox factory.
 * 
 * @author Alp Torac Genc
 */
public class DummySimilarityToolboxFactory implements ISimilarityToolboxFactory {
	@Override
	public ISimilarityToolbox createSimilarityToolbox() {
		return new DummySimilarityToolbox();
	}
}
