package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Provides expected similarity results for model resources by comparing the
 * file contents under their paths.
 * 
 * @author Alp Torac Genc
 */
public class FileContentSimilarityResultProvider implements IExpectedSimilarityResultProvider {
	private FileUtil fileUtil = new FileUtil();

	/**
	 * @implSpec Determines the expected similarity result purely based on the given
	 *           paths. Compares the contents of files under both given paths
	 *           pairwise. If all files are present on both sides, and have the same
	 *           content (up to whitespaces).
	 */
	@Override
	public boolean getExpectedSimilarityResultFor(Resource lhsRes, Path lhsResPath, Resource rhsRes, Path rhsResPath) {
		return fileUtil.areContentsEqual(lhsResPath, rhsResPath);
	}
}
