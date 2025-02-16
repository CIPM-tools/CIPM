package cipm.consistency.fitests.similarity.base.dummy;

import java.util.Collection;

import org.splevo.jamopp.diffing.similarity.base.AbstractSimilarityChecker;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityComparer;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityToolbox;

/**
 * A minimal similarity checker that illustrates what a similarity checker could
 * look like.
 * 
 * @author Alp Torac Genc
 */
public class DummySimilarityChecker extends AbstractSimilarityChecker {
	/**
	 * See the corresponding constructor in {@link AbstractSimilarityChecker}.
	 */
	public DummySimilarityChecker(ISimilarityToolbox st) {
		super(st);
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Note: Keep in mind that handlers for similarity check requests can be changed
	 * dynamically, as well as during the building process of the underlying
	 * {@link ISimilarityToolbox}. This means, different ways of similarity checking
	 * can be implemented and used.
	 */
	@Override
	public Boolean isSimilar(Object element1, Object element2) {
		return (Boolean) this.handleSimilarityRequest(new DummySingleSimilarityRequest(element1, element2));
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Note: Keep in mind that this method does not necessarily need to use the same
	 * similarity request as {@link #isSimilar(Object, Object)}. This method could
	 * be encapsulated in its own similarity request and handler pair (ex:
	 * DummyMultipleSimilarityRequest and DummyMultipleSimilarityRequestHandler).
	 * Doing so would let one to define different ways to compare collections of
	 * objects (ex: check elements pair wise in order, check if both lists contain
	 * same elements regardless of order, ...)
	 */
	@Override
	public Boolean areSimilar(Collection<Object> elements1, Collection<Object> elements2) {
		if (elements1 == elements2)
			return Boolean.TRUE;
		if (elements1 == null || elements2 == null)
			return Boolean.FALSE;
		if (elements1.size() != elements2.size())
			return Boolean.FALSE;

		var result = true;
		var iter1 = elements1.iterator();
		var iter2 = elements2.iterator();

		for (int i = 0; i < elements1.size(); i++) {
			var cResult = this.isSimilar(iter1.next(), iter2.next());

			if (cResult == null)
				return null;

			result = result && cResult.booleanValue();
		}

		return Boolean.valueOf(result);
	}

	@Override
	protected ISimilarityComparer createSimilarityComparer(ISimilarityToolbox st) {
		return new DummySimilarityComparer(st);
	}
}
