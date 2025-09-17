package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class FeatureEntryContainer {
	private final Set<FeatureEntry> desiredFeatureValues = new HashSet<FeatureEntry>();

	public void addDesiredFeature(FeatureEntry entry) {
		desiredFeatureValues.add(entry);
	}

	public void addDesiredFeature(EObject triggeringPCMElement, EStructuralFeature feat) {
		addDesiredFeature(triggeringPCMElement, null, feat);
	}

	public void addDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement, EStructuralFeature feat) {
		desiredFeatureValues.add(new FeatureEntry(triggeringPCMElement, affectedJavaElement, feat));
	}

	public FeatureEntry getDesiredFeatureValue(EObject triggeringPCMElement, EStructuralFeature feat) {
		FeatureEntry entry = null;
		var valOpt = getAssignedDesiredFeatureEntry(triggeringPCMElement, feat);
		if (valOpt.isPresent()) {
			entry = valOpt.get();
		} else if ((valOpt = getDesiredFeatureEntry(triggeringPCMElement, feat)).isPresent()) {
			entry = valOpt.get();
		}

		return entry;
	}

	public boolean hasDesiredFeatureValue(EObject triggeringPCMElement, EStructuralFeature feat) {
		return getAssignedDesiredFeatureEntry(triggeringPCMElement, feat).isPresent();
	}

	public Object removeDesiredFeatureValue(EObject triggeringPCMElement, EStructuralFeature feat, Object value) {
		var valOpt = getAssignedDesiredFeatureEntry(triggeringPCMElement, feat);
		if (valOpt.isEmpty())
			return null;

		var val = valOpt.get();

		if (val.hasValue(value)) {
			val.unsetOrRemoveValue(value);
			return value;
		}

		return null;
	}

	public void setDesiredFeatureValue(AbstractUserInteraction userInteraction, FeatureEntry featEntry) {
		var valOpt = getAssignedDesiredFeatureEntry(featEntry.getTriggeringPCMElement(),
				featEntry.getAffectedJavaElementFeature());
		if (valOpt.isPresent()) {
			valOpt.get().setValuesFrom(featEntry);
		} else if ((valOpt = getDesiredFeatureEntry(featEntry.getTriggeringPCMElement(),
				featEntry.getAffectedJavaElementFeature())).isPresent()) {
			valOpt.get().setValuesFrom(featEntry);
		} else {
			desiredFeatureValues.add(featEntry);
		}
	}

	public Optional<FeatureEntry> getDesiredFeatureEntry(EObject triggeringPCMElement, EStructuralFeature feat) {
		return desiredFeatureValues.stream().filter((e) -> e.isFeatureEntryFor(triggeringPCMElement, feat)).findFirst();
	}

	public Optional<FeatureEntry> getAssignedDesiredFeatureEntry(EObject triggeringPCMElement,
			EStructuralFeature feat) {
		return desiredFeatureValues.stream()
				.filter((e) -> e.isFeatureEntryFor(triggeringPCMElement, feat) && e.hasAssignedValue()).findFirst();
	}

	public Optional<FeatureEntry> getDesiredFeatureEntry(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return desiredFeatureValues.stream().filter((e) -> e.isFeatureEntryFor(triggeringPCMElement, feat)).findFirst();
	}

	public Optional<FeatureEntry> getAssignedDesiredFeatureEntry(EObject triggeringPCMElement,
			EObject affectedJavaElement, EStructuralFeature feat) {
		return desiredFeatureValues.stream()
				.filter((e) -> e.isFeatureEntryFor(triggeringPCMElement, feat) && e.hasAssignedValue()).findFirst();
	}

	public Set<FeatureEntry> getAllAssignedFeatures() {
		return desiredFeatureValues.stream().filter((v) -> v.hasAssignedValue())
				.collect(Collectors.toCollection(Set::of));
	}

	public void clear() {
		desiredFeatureValues.clear();
	}
}
