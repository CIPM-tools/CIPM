package cipm.consistency.vsum.test.pcm.cprunittests.userinteraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.Maps;

public abstract class AbstractUserInteractionResult implements IUserInteractionResult {
	private Collection<IUserInteractionWrapper> relevantUserInteractions;
	private Map<EStructuralFeature, Object> results;

	public AbstractUserInteractionResult() {
		this.relevantUserInteractions = new ArrayList<IUserInteractionWrapper>();
		this.results = new HashMap<EStructuralFeature, Object>();
	}

	protected AbstractUserInteractionResult(Collection<IUserInteractionWrapper> relevantUserInteractions,
			Map<EStructuralFeature, Object> results) {
		this.relevantUserInteractions = new ArrayList<IUserInteractionWrapper>(relevantUserInteractions);
		this.results = new HashMap<EStructuralFeature, Object>(results);
	}

	protected Collection<IUserInteractionWrapper> getModifiableRelevantUserInteractions() {
		return this.relevantUserInteractions;
	}

	protected Map<EStructuralFeature, Object> getModifiableResults() {
		return this.results;
	}

	@Override
	public List<EStructuralFeature> getRequiredFeatures() {
		var feats = new HashSet<EStructuralFeature>();
		this.relevantUserInteractions.stream().map((ui) -> ui.getInvolvedFeatures()).forEach((fl) -> feats.addAll(fl));
		return new ArrayList<EStructuralFeature>(feats);
	}

	/**
	 * TODO Maybe use UserInteractor's and VitruviusChange's methods in sub-classes
	 */
	@Override
	public List<EStructuralFeature> getAffectedFeatures() {
		var affectedFeats = new ArrayList<EStructuralFeature>();
		for (var rf : this.getRequiredFeatures()) {
			affectedFeats.add(rf);
			// TODO Find and add all further relevant features
		}
		return affectedFeats;
	}

	/**
	 * TODO Maybe use UserInteractor's and VitruviusChange's methods in sub-classes
	 */
	@Override
	public List<EObject> getAffectedEObjects() {
		var affectedEObjs = new ArrayList<EObject>();
		for (var obj : this.getRequiredFeatures()) {
			affectedEObjs.add(obj.eContainer());
			// TODO Find and add all further relevant EObjects
		}
		return affectedEObjs;
	}

	/**
	 * @return A user interaction result is assumed to be complete, if all affected
	 *         attributes have been addressed. For freshly created EObject
	 *         instances, their designated EReference should be used.
	 */
	@Override
	public boolean isComplete() {
		return results.keySet().containsAll(this.getRequiredFeatures());
	}

	@Override
	public boolean containsResultFor(EStructuralFeature affectedFeature) {
		return results.containsKey(affectedFeature);
	}

	@Override
	public Object getResultFor(EStructuralFeature affectedFeature) {
		return this.results.containsKey(affectedFeature) ? this.results.get(affectedFeature) : null;
	}

	@Override
	public UnmodifiableUserInteractionResult getPresentResults() {
		return new UnmodifiableUserInteractionResult(this.relevantUserInteractions, this.results);
	}

	@Override
	public List<EStructuralFeature> getAddressedFeatures() {
		return new ArrayList<EStructuralFeature>(this.results.keySet());
	}

	@Override
	public List<EStructuralFeature> getUnaddressedFeatures() {
		var feats = new ArrayList<EStructuralFeature>();
		for (var rf : this.getRequiredFeatures()) {
			if (!this.results.containsKey(rf))
				feats.add(rf);
		}
		return feats;
	}

	@Override
	public UnmodifiableUserInteractionResult getSubResult(List<EStructuralFeature> features) {
		return new UnmodifiableUserInteractionResult(
				this.relevantUserInteractions.stream()
						.filter((ui) -> ui.getInvolvedFeatures().stream().anyMatch((uiF) -> features.contains(uiF)))
						.collect(Collectors.toUnmodifiableList()),
				Maps.filterEntries(this.results, (e) -> features.contains(e.getKey())));
	}

	@Override
	public List<IUserInteractionWrapper> getRelevantUserInteractions() {
		return new ArrayList<>(this.relevantUserInteractions);
	}
}
