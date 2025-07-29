package cipm.consistency.fitests.similarity;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * An abstract class for similarity checking tests to extend. <br>
 * <br>
 * Contains methods that provide information on the next test method to be run
 * and various delegation methods that spare call chains.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractSimilarityTest implements ILoggable {
	/**
	 * @see {@link #getSCC()}
	 */
	private ISimilarityCheckerContainer scc;

	/**
	 * Sets up the necessary variables before tests are run. <br>
	 * <br>
	 * It is suggested to have a call to {@code super.setUp()} as the FIRST
	 * statement in overriding implementations. Doing so circumvents potential
	 * errors caused by the order of set up operations. <br>
	 * <br>
	 * {@link AbstractSimilarityTest}: Sets up the underlying
	 * {@link ISimilarityCheckerContainer}, which will be used for
	 * {@link #isSimilar(Object, Object)} and
	 * {@link #areSimilar(Collection, Collection)}.
	 * 
	 * @param info An object that contains information about the current test to be
	 *             run (ex: the test method instance, test class, ...)
	 */
	@BeforeEach
	public void setUp() {
		ILoggable.setUpLogger();

		this.setSCC(this.initSCC());
	}

	/**
	 * Cleans up the variables set up with {@link #setUp()} and performs other
	 * necessary clean up operations. <br>
	 * <br>
	 * It is suggested to have a call to {@code super.tearDown()} as the LAST
	 * statement in overriding implementations. Doing so circumvents potential
	 * errors caused by the order of clean up operations. <br>
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
	protected ISimilarityCheckerContainer getSCC() {
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
	protected abstract ISimilarityCheckerContainer initSCC();

	/**
	 * Sets the used {@link ISimilarityCheckerContainer} to the given one. <br>
	 * <br>
	 * If necessary, it can be called in tests to change the used similarity checker
	 * container to the given one.
	 * 
	 * @see {@link #initSCC()} for setting the {@link ISimilarityCheckerContainer}
	 *      during set up.
	 */
	protected void setSCC(ISimilarityCheckerContainer scc) {
		this.scc = scc;
	}

	/**
	 * Delegates similarity checking to the underlying
	 * {@link ISimilarityCheckerContainer}.
	 */
	public Boolean isSimilar(Object element1, Object element2) {
		return this.getSCC().isSimilar(element1, element2);
	}

	/**
	 * Delegates similarity checking to the underlying
	 * {@link ISimilarityCheckerContainer}.
	 */
	public Boolean areSimilar(Collection<?> elements1, Collection<?> elements2) {
		return this.getSCC().areSimilar(elements1, elements2);
	}

	/**
	 * @return The prefix of the {@link Resource} file names created from within the
	 *         current test class. Defaults to the name of the current test class.
	 */
	public String getCurrentTestClassName() {
		return this.getClass().getSimpleName();
	}
}