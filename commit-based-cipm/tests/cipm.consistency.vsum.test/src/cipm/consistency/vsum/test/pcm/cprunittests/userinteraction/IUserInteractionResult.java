package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface IUserInteractionResult {
	/**
	 * @return A list of all features, for which this result has to provide a value.
	 *         Only includes features that directly have to be assigned a value.
	 */
	public List<EStructuralFeature> getRequiredFeatures();

	/**
	 * @return A list of all affected features, including those that are indirectly
	 *         affected (such as EOpposites)
	 */
	public List<EStructuralFeature> getAffectedFeatures();

	/**
	 * @return A list of all EObjects that are affected by the currently present and
	 *         future results in this instance.
	 */
	public List<EObject> getAffectedEObjects();

	/**
	 * @return Whether all answers this has to provide are present
	 */
	public boolean isComplete();

	public boolean containsResultFor(EStructuralFeature feature);

	public Object getResultFor(EStructuralFeature feature);

	public UnmodifiableUserInteractionResult getPresentResults();

	public List<EStructuralFeature> getAddressedFeatures();
	public List<EStructuralFeature> getUnaddressedFeatures();

	public UnmodifiableUserInteractionResult getSubResult(List<EStructuralFeature> features);

	public List<IUserInteractionWrapper> getRelevantUserInteractions();
}
