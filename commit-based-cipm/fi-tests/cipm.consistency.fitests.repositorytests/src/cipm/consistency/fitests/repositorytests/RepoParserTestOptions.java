package cipm.consistency.fitests.repositorytests;

import cipm.consistency.fitests.similarity.jamopp.parser.ParserTestOptions;

/**
 * A class that contains various options for test classes that parse
 * {@link Resource} instances from GIT repositories, cache those resource
 * instances, as well as compute and cache expected similarity results:
 * <ul>
 * <li>shouldDeleteRepositoryClones: Whether all cloned repositories should be
 * removed after tests
 * <li>shouldSaveCachedExpectedSimilarityResults: Whether the cached expected
 * similarity results should be saved after tests
 * <li>shouldUseCachedExpectedSimilarityResults: Whether the cached expected
 * similarity results should actually be used in tests
 * </ul>
 * 
 * @author Alp Torac Genc
 */
public class RepoParserTestOptions extends ParserTestOptions {
	private boolean shouldDeleteRepositoryClones;
	private boolean shouldSaveCachedExpectedSimilarityResults;
	private boolean shouldUseCachedExpectedSimilarityResults;

	/**
	 * @see {@link RepoParserTestOptions}
	 */
	public void setShouldDeleteRepositoryClones(boolean shouldDeleteRepositoryClones) {
		this.shouldDeleteRepositoryClones = shouldDeleteRepositoryClones;
	}

	/**
	 * @see {@link RepoParserTestOptions}
	 */
	public void setShouldSaveCachedExpectedSimilarityResults(boolean shouldSaveCachedExpectedSimilarityResults) {
		this.shouldSaveCachedExpectedSimilarityResults = shouldSaveCachedExpectedSimilarityResults;
	}

	/**
	 * @see {@link RepoParserTestOptions}
	 */
	public void setShouldUseCachedExpectedSimilarityResults(boolean shouldUseCachedExpectedSimilarityResults) {
		this.shouldUseCachedExpectedSimilarityResults = shouldUseCachedExpectedSimilarityResults;
	}

	/**
	 * @see {@link RepoParserTestOptions}
	 */
	public boolean shouldDeleteRepositoryClones() {
		return shouldDeleteRepositoryClones;
	}

	/**
	 * @see {@link RepoParserTestOptions}
	 */
	public boolean shouldSaveCachedExpectedSimilarityResults() {
		return shouldSaveCachedExpectedSimilarityResults;
	}

	/**
	 * @see {@link RepoParserTestOptions}
	 */
	public boolean shouldUseCachedExpectedSimilarityResults() {
		return shouldUseCachedExpectedSimilarityResults;
	}

	/**
	 * Copies all options from the given instance; i.e. after calling this method,
	 * all options inside the given instance will override the corresponding options
	 * in this.
	 */
	public void copyOptionsFrom(RepoParserTestOptions opts) {
		super.copyOptionsFrom(opts);
		this.shouldDeleteRepositoryClones = opts.shouldDeleteRepositoryClones;
		this.shouldSaveCachedExpectedSimilarityResults = opts.shouldSaveCachedExpectedSimilarityResults;
		this.shouldUseCachedExpectedSimilarityResults = opts.shouldUseCachedExpectedSimilarityResults;
	}
}
