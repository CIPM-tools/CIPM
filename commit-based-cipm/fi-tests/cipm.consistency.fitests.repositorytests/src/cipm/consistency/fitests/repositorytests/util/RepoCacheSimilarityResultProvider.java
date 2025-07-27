package cipm.consistency.fitests.repositorytests.util;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.fitests.similarity.jamopp.parser.IExpectedSimilarityResultProvider;

/**
 * Provides expected similarity results based on a result cache.
 * 
 * @author Alp Torac Genc
 */
public class RepoCacheSimilarityResultProvider implements IExpectedSimilarityResultProvider {
	private final RepoTestResultCache cache;

	public RepoCacheSimilarityResultProvider(RepoTestResultCache cache) {
		this.cache = cache;
	}

	/**
	 * @return The (expected) key of the given resource in the cache, i.e. the
	 *         commit ID.
	 */
	private String getCacheKeyForResource(Resource res) {
		// The last segment of the resource URI is: commitID.ext
		// Remove the extension to get the commitID
		return res.getURI().trimFileExtension().lastSegment();
	}

	/**
	 * @implSpec Determines the expected similarity result based on the given result
	 *           cache.
	 */
	@Override
	public boolean getExpectedSimilarityResultFor(Resource lhsRes, Path lhsResPath, Resource rhsRes, Path rhsResPath) {
		var result = cache.getResult(this.getCacheKeyForResource(lhsRes), this.getCacheKeyForResource(rhsRes));
		return result != null ? result : false;
	}
}
