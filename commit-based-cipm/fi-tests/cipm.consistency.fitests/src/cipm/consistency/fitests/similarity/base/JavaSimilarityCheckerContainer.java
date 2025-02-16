package cipm.consistency.fitests.similarity.base;

import java.util.Collection;

import org.splevo.jamopp.diffing.similarity.JavaSimilarityChecker;
import org.splevo.jamopp.diffing.similarity.JavaSimilarityToolboxBuilder;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityChecker;
import org.splevo.jamopp.diffing.similarity.base.MapSimilarityToolboxFactory;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;

/**
 * A concrete implementation of {@link ISimilarityCheckerContainer} that creates
 * and works with {@link JavaSimilarityChecker} instances.
 * 
 * @author Alp Torac Genc
 */
public class JavaSimilarityCheckerContainer implements ISimilarityCheckerContainer {
	private ISimilarityChecker sc;

	private ISimilarityChecker getSimilarityChecker() {
		if (this.sc == null) {
			this.newSimilarityChecker();
		}
		return this.sc;
	}

	@Override
	public void newSimilarityChecker() {
		var builder = new JavaSimilarityToolboxBuilder();
		builder.setSimilarityToolboxFactory(new MapSimilarityToolboxFactory());

		var toolbox = builder.instantiate().buildNewSimilaritySwitchHandler().buildNormalizationHandlers()
				.buildComparisonHandlers().build();

		this.sc = new JavaSimilarityChecker(toolbox);
	}

	@Override
	public Boolean isSimilar(Object element1, Object element2) {
		return this.getSimilarityChecker().isSimilar(element1, element2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean areSimilar(Collection<?> elements1, Collection<?> elements2) {
		return this.getSimilarityChecker().areSimilar((Collection<Object>) elements1, (Collection<Object>) elements2);
	}
}
