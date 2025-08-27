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
	 * {@link AbstractEObjectSimilarityTest}: Sets {@link #getResourceHelper()},
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

	protected AbstractResourceParsingStrategy getResourceParsingStrategy() {
		return this.parsingStrat;
	}

	protected void setResourceParsingStrategy(AbstractResourceParsingStrategy parsingStrat) {
		this.parsingStrat = parsingStrat;
	}

	protected ResourceTestOptions getResourceTestOptions() {
		return this.resourceTestOptions;
	}

	protected void setResourceTestOptions(ResourceTestOptions resourceTestOptions) {
		this.resourceTestOptions = resourceTestOptions;
	}

	protected void cleanUpResourceParsingStrategy() {
		this.parsingStrat = null;
	}

	protected void cleanUpResourceTestOptions() {
		this.resourceTestOptions = null;
	}

	protected abstract ResourceTestOptions initResourceTestOptions();

	protected abstract AbstractResourceParsingStrategy initResourceParsingStrategy();
}
