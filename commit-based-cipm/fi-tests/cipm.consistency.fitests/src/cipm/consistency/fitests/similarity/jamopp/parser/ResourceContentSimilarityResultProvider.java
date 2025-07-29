package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;

/**
 * Provides expected similarity results based on the similarity of model
 * resources' contents. Meant for deep model comparison tests that compute the
 * actual differences between model resources, as opposed to only computing
 * whether 2 given model resources are similar.
 * 
 * @author Alp Torac Genc
 */
public class ResourceContentSimilarityResultProvider implements IExpectedSimilarityResultProvider {
	private ISimilarityCheckerContainer scc;
	/**
	 * Whether the order of the contents of the model resources should be accounted
	 * for
	 */
	private boolean contentOrderMatters;

	/**
	 * @param scc                 The object that will be used to determine whether
	 *                            contents of model resources are similar
	 * @param contentOrderMatters Whether the order of the contents of the model
	 *                            resources should be accounted for
	 */
	public ResourceContentSimilarityResultProvider(ISimilarityCheckerContainer scc, boolean contentOrderMatters) {
		this.scc = scc;
		this.contentOrderMatters = contentOrderMatters;
	}

	/**
	 * Checks if both sides' contents ({@code res.getAllContents()}) are similar, if
	 * their order does not matter. Makes sure that the result is the same as
	 * {@code allContentSimilar(rhs, lhs)}.
	 * 
	 * @return Whether all contents of lhs and rhs are similar, i.e. if all contents
	 *         of lhs have a corresponding similar content on rhs.
	 */
	private boolean contentwiseSimilar(Resource lhs, Resource rhs) {
		var lhsContent = new ArrayList<EObject>();
		lhs.getAllContents().forEachRemaining((e) -> lhsContent.add(e));
		var rhsContent = new ArrayList<EObject>();
		rhs.getAllContents().forEachRemaining((e) -> rhsContent.add(e));

		return this.contentwiseSimilar(lhsContent, rhsContent) && this.contentwiseSimilar(rhsContent, lhsContent);
	}

	/**
	 * Checks if both sides' contents ({@code obj.eAllContents()}) are similar, if
	 * their order does not matter. Makes sure that the result is the same as
	 * {@code allContentSimilar(rhs, lhs)}.
	 * 
	 * @return Whether all contents of lhs and rhs are similar, i.e. if all contents
	 *         of lhs have a corresponding similar content on rhs.
	 */
	private boolean contentwiseSimilar(EObject lhs, EObject rhs) {
		if (!this.scc.isSimilar(lhs, rhs) || !this.scc.isSimilar(rhs, lhs)) {
			return false;
		}

		var lhsContent = new ArrayList<EObject>();
		lhs.eAllContents().forEachRemaining((e) -> lhsContent.add(e));
		var rhsContent = new ArrayList<EObject>();
		rhs.eAllContents().forEachRemaining((e) -> rhsContent.add(e));

		return this.contentwiseSimilar(lhsContent, rhsContent) && this.contentwiseSimilar(rhsContent, lhsContent);
	}

	/**
	 * Variant of {@link #contentwiseSimilar(EObject, EObject)} for collections.
	 */
	private boolean contentwiseSimilar(Collection<EObject> lhs, Collection<EObject> rhs) {
		var lhsContent = new ArrayList<EObject>(lhs);
		var rhsContent = new ArrayList<EObject>(rhs);

		if (lhsContent.size() != rhsContent.size()) {
			return false;
		}

		while (!lhsContent.isEmpty() && !rhsContent.isEmpty()) {
			var lhsElem = lhsContent.get(0);
			final var rhsElem = new EObject[] { null };
			for (var e : rhsContent) {
				if (this.contentwiseSimilar(lhsElem, e)) {
					rhsElem[0] = e;
					break;
				}
			}
			if (rhsElem[0] != null) {
				lhsContent.remove(lhsElem);
				rhsContent.remove(rhsElem[0]);
			} else {
				return false;
			}
		}
		return lhsContent.isEmpty() && rhsContent.isEmpty();
	}

	/**
	 * @implSpec Determines expected similarity results based on the given
	 *           {@link ISimilarityCheckerContainer} and content order (if desired
	 *           in
	 *           {@link #ResourceContentSimilarityResultProvider(ISimilarityCheckerContainer, boolean)}).
	 */
	@Override
	public boolean getExpectedSimilarityResultFor(Resource lhsRes, Path lhsResPath, Resource rhsRes, Path rhsResPath) {
		var pathsEqual = lhsResPath.toString().equals(rhsResPath.toString());
		return pathsEqual || (!this.contentOrderMatters && this.contentwiseSimilar(lhsRes, rhsRes));
	}
}
