package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * An interface for classes, which encapsulate the logic for providing expected
 * similarity checking results to tests, where model resources are being
 * compared. <br>
 * <br>
 * Concrete implementations of this interface may also nest
 * {@link IExpectedSimilarityResultProvider}s or have certain ones override the
 * expected similarity results of others. How the expected similarity results
 * are derived and provided depends on the concrete implementor.
 * 
 * @author Alp Torac Genc
 */
public interface IExpectedSimilarityResultProvider {
	/**
	 * Model resources and their paths should be provided via separate parameters,
	 * as the original files that were parsed into model resources may reside under
	 * different paths, which cannot be determined from the model resource alone.
	 * 
	 * @param lhsRes     Left-hand side resource
	 * @param lhsResPath The path that the resource lhsRes was originally parsed
	 *                   from, i.e. the path to the files that were parsed into
	 *                   lhsRes.
	 * @param rhsRes     Right-hand side resource
	 * @param rhsResPath The path that the resource rhsRes was originally parsed
	 *                   from, i.e. the path to the files that were parsed into
	 *                   rhsRes.
	 * @return The expected result of similarity checking the given resources,
	 *         according to the concrete implementor.
	 */
	public boolean getExpectedSimilarityResultFor(Resource lhsRes, Path lhsResPath, Resource rhsRes, Path rhsResPath);
}
