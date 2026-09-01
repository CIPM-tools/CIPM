/**
 * Contains constructs that encapsulate various aspects of repository parser
 * tests:
 * <p>
 * {@link RepoTestSimilarityResultCache} stores expected similarity results
 * required by repository parser tests.
 * {@link RepoCacheSimilarityResultProvider} serves as a bridge between
 * repository parser tests and RepoTestSimilarityResultCache by providing access
 * to the expected similarity results stored therein.
 * <p>
 * {@link RepoTestSimilarityValueEstimator} integrates the contents of this
 * package's sub-packages and computes approximative expected similarity results
 * for repository parser tests.
 */
package cipm.consistency.fitests.repositorytests.util;