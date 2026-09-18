package cipm.consistency.fitests.similarity.jamopp;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;

/**
 * A concrete implementation of {@link ISimilarityCheckerContainer} that creates
 * and works with {@link SimilarityChecker} instances.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPSimilarityCheckerContainer implements ISimilarityCheckerContainer<EObject> {
	private SimilarityChecker sc;

	private SimilarityChecker getSimilarityChecker() {
		if (this.sc == null) {
			this.resetSimilarityChecker();
		}
		return this.sc;
	}

	@Override
	public void resetSimilarityChecker() {
		this.sc = new SimilarityChecker();
	}

	@Override
	public Boolean isSimilar(EObject element1, EObject element2) {
		return this.getSimilarityChecker().isSimilar(element1, element2);
	}

	@Override
	public Boolean areSimilar(Collection<EObject> elements1, Collection<EObject> elements2) {
		return this.getSimilarityChecker().areSimilar((List<EObject>) elements1, (List<EObject>) elements2);
	}
}
