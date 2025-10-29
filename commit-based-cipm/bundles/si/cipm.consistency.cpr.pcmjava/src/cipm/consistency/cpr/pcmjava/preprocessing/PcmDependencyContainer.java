package cipm.consistency.cpr.pcmjava.preprocessing;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class PcmDependencyContainer {
	private final Set<PcmDependency> deps = new HashSet<>();

	public void addDependency(PcmDependency dep) {
		this.deps.add(dep);
	}

	public void removeDependency(PcmDependency dep) {
		this.deps.remove(dep);
	}

	public Set<PcmDependency> getDependencies(EObject elem1, EObject elem2) {
		return deps.stream().filter((dep) -> dep.hasElement(elem1) && dep.hasElement(elem2))
				.collect(Collectors.toCollection(HashSet::new));
	}

	public Set<PcmDependency> getDependencies(EStructuralFeature feat) {
		return deps.stream().filter((dep) -> dep.hasFeature(feat)).collect(Collectors.toCollection(HashSet::new));
	}

	public Set<PcmDependency> getDependencies(EObject elem, EStructuralFeature elemFeat) {
		return deps.stream().filter((dep) -> dep.hasFeature(elem, elemFeat))
				.collect(Collectors.toCollection(HashSet::new));
	}

	public Set<PcmDependency> getDependencies(String correspondenceTag) {
		return deps.stream().filter((dep) -> dep.tagEquals(correspondenceTag))
				.collect(Collectors.toCollection(HashSet::new));
	}

	public void replaceDependencies(EObject oldElem, EObject newElem) {
		var depsToReplace = this.getDependencies(oldElem, newElem);
		deps.removeAll(depsToReplace);
		for (var dep : depsToReplace) {
			this.addDependency(new PcmDependency(newElem, dep.getFeatureOf(oldElem), dep.getOtherElement(oldElem),
					dep.getOtherFeature(dep.getFeatureOf(oldElem)), dep.getCorrespondenceTag()));
		}
	}

	public void replaceDependencies(EObject oldElem, EStructuralFeature oldElemFeat, EObject newElem,
			EStructuralFeature newElemFeat) {
		var depsToReplace = this.getDependencies(oldElem, oldElemFeat);
		deps.removeAll(depsToReplace);
		for (var dep : depsToReplace) {
			this.addDependency(new PcmDependency(newElem, newElemFeat, dep.getOtherElement(oldElem),
					dep.getOtherFeature(oldElemFeat), dep.getCorrespondenceTag()));
		}
	}
}
