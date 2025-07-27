package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Provides expected similarity results based on the reference equality of model
 * resources.
 * 
 * @author Alp Torac Genc
 */
public class ResourceReferenceEqualitySimilarityResultProvider implements IExpectedSimilarityResultProvider {
	/**
	 * @implSpec Determines expected similarity results based on the reference
	 *           equality of model resources.
	 */
	@Override
	public boolean getExpectedSimilarityResultFor(Resource lhsRes, Path lhsResPath, Resource rhsRes, Path rhsResPath) {
		return lhsRes == rhsRes;
	}
}
