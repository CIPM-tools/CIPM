package cipm.consistency.fitests.similarity.eobject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.fitests.similarity.AbstractSimilarityTest;

/**
 * An abstract class meant to be implemented by EObject-related test classes.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractEObjectSimilarityTest extends AbstractSimilarityTest {
	/**
	 * @see {@link #getResourceParsingStrategy()}
	 */
	private AbstractResourceParsingStrategy parsingStrat;

	/**
	 * @see {@link #getResourceTestOptions()}
	 */
	private ResourceTestOptions resourceTestOptions;

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractEObjectSimilarityTest}: Sets up Resource parsing strategy
	 * {@link AbstractResourceParsingStrategy} and Resource-related test options
	 * {@link ResourceTestOptions}.
	 */
	@BeforeEach
	@Override
	public void setUp() {
		super.setUp();

		this.setResourceParsingStrategy(this.initResourceParsingStrategy());
		this.setResourceTestOptions(this.initResourceTestOptions());
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * {@link AbstractEObjectSimilarityTest}: Sets
	 * {@link #getResourceParsingStrategy()} and {@link #getResourceTestOptions()}
	 * to null, in order to ensure that each test has freshly created instances.
	 */
	@AfterEach
	@Override
	public void tearDown() {
		this.cleanUpResourceParsingStrategy();
		this.cleanUpResourceTestOptions();

		super.tearDown();
	}

	/**
	 * @return An object that has the means to parse {@link ResourceSet} instances
	 *         from model source files
	 */
	protected AbstractResourceParsingStrategy getResourceParsingStrategy() {
		return this.parsingStrat;
	}

	/**
	 * @param parsingStrat {@link #getResourceParsingStrategy()}
	 */
	protected void setResourceParsingStrategy(AbstractResourceParsingStrategy parsingStrat) {
		this.parsingStrat = parsingStrat;
	}

	/**
	 * @return An object that contains various test configurations, especially those
	 *         concerning {@link Resource} instances
	 */
	protected ResourceTestOptions getResourceTestOptions() {
		return this.resourceTestOptions;
	}

	/**
	 * @param resourceTestOptions {@link #getResourceTestOptions()}
	 */
	protected void setResourceTestOptions(ResourceTestOptions resourceTestOptions) {
		this.resourceTestOptions = resourceTestOptions;
	}

	/**
	 * Cleans all status information regarding how {@link Resource} instances are
	 * parsed from model source files
	 */
	protected void cleanUpResourceParsingStrategy() {
		this.parsingStrat = null;
	}

	/**
	 * Cleans all test configurations, especially those concerning {@link Resource}
	 * instances
	 */
	protected void cleanUpResourceTestOptions() {
		this.resourceTestOptions = null;
	}

	/**
	 * @return {@link #getResourceTestOptions()}
	 */
	protected abstract ResourceTestOptions initResourceTestOptions();

	/**
	 * @return {@link #getResourceParsingStrategy()}
	 */
	protected abstract AbstractResourceParsingStrategy initResourceParsingStrategy();
}
