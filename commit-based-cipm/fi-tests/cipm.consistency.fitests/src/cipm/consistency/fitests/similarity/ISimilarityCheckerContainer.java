package cipm.consistency.fitests.similarity;

import java.util.Collection;

/**
 * An interface meant to be implemented by classes that store the similarity
 * checker under test, in order to spare other test classes the need to add that
 * similarity checker as a dependency. <br>
 * <br>
 * This interface contains dependencies to neither similarity checker interfaces
 * nor to concrete implementations, because doing so would reduce the
 * re-usability. <br>
 * <br>
 * The underlying similarity checking mechanism(s) can be reset by using the
 * {@link #newSimilarityChecker()} method. The similarity checking mechanism(s)
 * are not automatically re-created upon calling similarity checking methods in
 * this interface, because it might be desirable to keep using them.
 * 
 * @author Alp Torac Genc
 */
public interface ISimilarityCheckerContainer {
	/**
	 * If there are no similarity checking mechanism(s) present, this method creates
	 * and sets them up. Otherwise, replaces the currently stored similarity
	 * checking mechanism(s) with new ones. <br>
	 * <br>
	 * The similarity checking mechanism(s) are not automatically re-created upon
	 * calling similarity checking methods, because it might be desirable to keep
	 * using the existing similarity checking mechanism(s).
	 */
	public void newSimilarityChecker();

	/**
	 * Delegates similarity checking to the similarity checking mechanism(s) within.
	 * Calls {@link #newSimilarityChecker()} beforehand, if there are no similarity
	 * checking mechanism(s) present.
	 */
	public Boolean isSimilar(Object element1, Object element2);

	/**
	 * Delegates similarity checking to the similarity checking mechanism(s) within.
	 * Calls {@link #newSimilarityChecker()} beforehand, if there are no similarity
	 * checking mechanism(s) present.
	 */
	public Boolean areSimilar(Collection<?> elements1, Collection<?> elements2);
}
