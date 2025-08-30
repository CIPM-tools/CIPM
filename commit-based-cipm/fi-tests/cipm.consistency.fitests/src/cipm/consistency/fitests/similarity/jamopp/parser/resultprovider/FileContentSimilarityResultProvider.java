package cipm.consistency.fitests.similarity.jamopp.parser.resultprovider;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.fitests.similarity.jamopp.parser.FileUtil;

/**
 * Provides expected similarity results for model resources by comparing their
 * respective model source files' (or model source file directories') content.
 * 
 * @author Alp Torac Genc
 */
public class FileContentSimilarityResultProvider implements IExpectedSimilarityResultProvider {
	/**
	 * @implSpec Determines the expected similarity result purely based on the given
	 *           model source file (or model source file directory) paths. Compares
	 *           the contents of model source files under both given paths pairwise.
	 *           If all files are present on both sides, and have the same content
	 *           (up to whitespaces), the given resources are expected to be
	 *           similar.
	 */
	@Override
	public boolean getExpectedSimilarityResultFor(Resource lhsRes, Path lhsResPath, Resource rhsRes, Path rhsResPath) {
		return FileUtil.areContentsEqual(lhsResPath, rhsResPath);
	}
}
