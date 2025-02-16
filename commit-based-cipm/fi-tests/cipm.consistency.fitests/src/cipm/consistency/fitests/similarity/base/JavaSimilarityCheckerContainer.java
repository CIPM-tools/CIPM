package cipm.consistency.fitests.similarity.base;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;

/**
 * A concrete implementation of {@link ISimilarityCheckerContainer} that creates
 * and works with {@link JavaSimilarityChecker} instances.
 * 
 * @author Alp Torac Genc
 */
public class JavaSimilarityCheckerContainer implements ISimilarityCheckerContainer {
	private SimilarityChecker sc;

	private SimilarityChecker getSimilarityChecker() {
		if (this.sc == null) {
			this.newSimilarityChecker();
		}
		return this.sc;
	}

	@Override
	public void newSimilarityChecker() {
		this.sc = new SimilarityChecker();
	}

	@Override
	public Boolean isSimilar(Object element1, Object element2) {
		return this.getSimilarityChecker().isSimilar((EObject) element1, (EObject) element2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean areSimilar(Collection<?> elements1, Collection<?> elements2) {
		return this.getSimilarityChecker().areSimilar((List<? extends EObject>) elements1,
				(List<? extends EObject>) elements2);
	}
}
