package cipm.consistency.fitests.similarity;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * An abstract class for similarity checking tests to extend.
 * 
 * @author Alp Torac Genc
 * 
 * @param <T> The type of the elements that will be similarity checked.
 */
public abstract class AbstractSimilarityTest<T> {
	/**
	 * @see {@link #getSCC()}
	 */
	private ISimilarityCheckerContainer<T> scc;

	/**
	 * Sets up the necessary variables before tests are run. <br>
	 * <br>
	 * It is suggested to have a call to {@code super.setUp()} as the FIRST
	 * statement in overriding implementations. Doing so circumvents potential
	 * errors caused by the order of set up operations, such as logging not working
	 * as expected. <b><i>Sub-types are still allowed to perform other preparatory
	 * steps prior to {@code super.setUp()}, however</b></i>. <br>
	 * <br>
	 * {@link AbstractSimilarityTest}: Sets up the underlying
	 * {@link ISimilarityCheckerContainer}, which will be used for similarity
	 * checking through {@link #isSimilar(Object, Object)} and
	 * {@link #areSimilar(Collection, Collection)}. Also sets up logging via
	 * {@link SimilarityTestLogger#setUpLogger()}.
	 */
	@BeforeEach
	public void setUp() {
		SimilarityTestLogger.setUpLogger();

		this.setSCC(this.initSCC());
	}

	/**
	 * Cleans up the variables set up with {@link #setUp()} and performs other
	 * necessary clean up operations. <br>
	 * <br>
	 * It is suggested to have a call to {@code super.tearDown()} as the LAST
	 * statement in overriding implementations. Doing so circumvents potential
	 * errors caused by the order of clean up operations. <b><i> Sub-types are still
	 * allowed to perform other finalisation steps after {@code super.tearDown()},
	 * however </b></i>. <br>
	 * <br>
	 * {@link AbstractSimilarityTest}: Cleans up the underlying
	 * {@link ISimilarityCheckerContainer}
	 */
	@AfterEach
	public void tearDown() {
		this.cleanUpSCC();
	}

	/**
	 * Provides the implementors access to the underlying
	 * {@link ISimilarityCheckerContainer} (SCC).
	 * 
	 * @return The {@link ISimilarityCheckerContainer} (SCC) that will be used to
	 *         store the similarity checker under test.
	 */
	protected ISimilarityCheckerContainer<T> getSCC() {
		return this.scc;
	}

	/**
	 * Sets the used {@link ISimilarityCheckerContainer} to null. Used by
	 * {@link #tearDown()}, in order to ensure that each test method starts with a
	 * fresh {@link ISimilarityCheckerContainer}.
	 */
	protected void cleanUpSCC() {
		this.scc = null;
	}

	/**
	 * Creates the concrete {@link ISimilarityCheckerContainer} that will be used to
	 * store the similarity checker under test. <br>
	 * <br>
	 * If necessary, it can be overridden in tests to change the said similarity
	 * checker during set up.
	 */
	protected abstract ISimilarityCheckerContainer<T> initSCC();

	/**
	 * Sets the used {@link ISimilarityCheckerContainer} to the given one. <br>
	 * <br>
	 * If necessary, it can be called in tests to change the used similarity checker
	 * container to the given one.
	 * 
	 * @see {@link #initSCC()} for setting the {@link ISimilarityCheckerContainer}
	 *      during set up.
	 */
	protected void setSCC(ISimilarityCheckerContainer<T> scc) {
		this.scc = scc;
	}

	/**
	 * Delegates similarity checking to the underlying
	 * {@link ISimilarityCheckerContainer}.
	 */
	public Boolean isSimilar(T element1, T element2) {
		return this.getSCC().isSimilar(element1, element2);
	}

	/**
	 * Delegates similarity checking to the underlying
	 * {@link ISimilarityCheckerContainer}.
	 */
	public Boolean areSimilar(Collection<T> elements1, Collection<T> elements2) {
		return this.getSCC().areSimilar(elements1, elements2);
	}

	/**
	 * Use this method to retrieve the currently running test class' name for
	 * consistency.
	 * 
	 * @return The name of the currently running test class.
	 */
	public String getCurrentTestClassName() {
		return this.getClass().getSimpleName();
	}
}