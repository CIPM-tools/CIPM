package cipm.consistency.fitests.similarity.jamopp.parser.resultprovider;

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
	 *           equality of model resources, i.e.
	 *           {@code lhsModelResource == rhsModelResource}
	 */
	@Override
	public boolean getExpectedSimilarityResultFor(Resource lhsModelResource, Path lhsModelSourceFileDirPath,
			Resource rhsModelResource, Path rhsModelSourceFileDirPath) {
		return lhsModelResource == rhsModelResource;
	}
}
