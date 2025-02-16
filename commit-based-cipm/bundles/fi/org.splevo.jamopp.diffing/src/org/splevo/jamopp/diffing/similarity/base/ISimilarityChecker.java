package org.splevo.jamopp.diffing.similarity.base;

import java.util.Collection;

/**
 * An interface for classes that contain the means to compare elements and
 * compute their similarity. The classes implementing this interface, the
 * similarity checkers, are meant to serve as facades to outside. <br>
 * <br>
 * {@link AbstractSimilarityChecker} contains further useful methods for
 * similarity checkers. It is therefore recommended to extend
 * {@link AbstractSimilarityChecker} for similarity checker classes rather than
 * implementing this interface alone. {@link AbstractSimilarityChecker} also
 * makes use of {@link ISimilarityComparer}, which can be used to free the
 * implementors of {@link ISimilarityChecker} of internal details. <br>
 * <br>
 * If the structure defined in {@link AbstractSimilarityChecker} is not to be
 * used, this interface can be directly implemented.
 * 
 * @see {@link AbstractSimilarityChecker}, {@link ISimilarityComparer}
 * @author atora
 */
public interface ISimilarityChecker {
	/**
	 * Check the similarity of two elements.
	 *
	 * @param element1 The first element.
	 * @param element2 The second element.
	 * @return TRUE, if they are similar; FALSE if not, NULL if it can't be decided.
	 */
	public Boolean isSimilar(Object element1, Object element2);

	/**
	 * Check two lists of elements for similarity. <br>
	 * <br>
	 * The elements are compared pairwise and it is the responsibility of the
	 * provided list implementations to return them in an appropriate order by
	 * calling get(i) with a increasing index i.
	 *
	 * @return TRUE, if they are all similar; FALSE if a different number of
	 *         elements is submitted or at least one pair of elements is not similar
	 *         to each other.
	 */
	public Boolean areSimilar(Collection<Object> elements1, Collection<Object> elements2);
}