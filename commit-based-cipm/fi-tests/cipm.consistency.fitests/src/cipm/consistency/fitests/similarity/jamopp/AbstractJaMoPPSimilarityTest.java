package cipm.consistency.fitests.similarity.jamopp;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.eobject.AbstractEObjectSimilarityTest;

/**
 * An abstract test class that extends {@link AbstractEObjectSimilarityTest}
 * with concrete method implementations for JaMoPP context, as well as static
 * methods that can be used in parameterised tests to generate initialiser
 * instances.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractJaMoPPSimilarityTest extends AbstractEObjectSimilarityTest {
	@Override
	protected JaMoPPResourceHelper initResourceHelper() {
		return new JaMoPPResourceHelper();
	}

	@Override
	protected ISimilarityCheckerContainer initSCC() {
		return new JaMoPPSimilarityCheckerContainer();
	}

	@Override
	protected JaMoPPResourceParsingStrategy initResourceParsingStrategy() {
		return new JaMoPPResourceParsingStrategy();
	}

	@Override
	protected JaMoPPResourceParsingStrategy getResourceParsingStrategy() {
		return (JaMoPPResourceParsingStrategy) super.getResourceParsingStrategy();
	}
}
