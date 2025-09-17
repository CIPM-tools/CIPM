package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

public class CorrespondenceEntryContainer {
	private final Set<CorrespondenceEntry> desiredCorrespondences = new HashSet<CorrespondenceEntry>();

	public void addDesiredCorrespondenceEntry(CorrespondenceEntry corEntry) {
		desiredCorrespondences.add(corEntry);
	}

	public Optional<CorrespondenceEntry> getDesiredCorrespondenceEntry(EObject obj, String correspondenceTag) {
		return desiredCorrespondences.stream().filter((t) -> t.hasElement(obj) && t.isCorrespondenceTagEqual(correspondenceTag))
				.findFirst();
	}

	public CorrespondenceEntry getCompleteDesiredCorrespondence(EObject obj, String correspondenceTag) {
		var result = new CorrespondenceEntry(obj, correspondenceTag);
		desiredCorrespondences.stream().filter((t) -> t.hasAnyCompleteCorrespondencesWith(obj, correspondenceTag))
				.forEach((ce) -> result.addCorrespondences(ce));
		return result;
	}

	public Optional<CorrespondenceEntry> getCompleteDesiredCorrespondence(EObject knownSide, EObject otherSide,
			String correspondenceTag) {
		return desiredCorrespondences.stream()
				.filter((t) -> t.hasCorrespondence(knownSide, otherSide) && t.isCorrespondenceTagEqual(correspondenceTag))
				.findFirst();
	}

	public CorrespondenceEntry getDesiredCorrespondence(EObject knownSide, String correspondenceTag,
			boolean computeIfAbsent) {
		var corEntry = getCompleteDesiredCorrespondence(knownSide, correspondenceTag);
		if (corEntry.hasAnyCompleteCorrespondences())
			return corEntry;

		return null;
	}

	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return getCompleteDesiredCorrespondence(knownSide, correspondenceTag).hasAnyCompleteCorrespondences();
	}

	public Object removeDesiredCorrespondence(EObject knownSide, EObject otherSide, String correspondenceTag) {
		var optCor = getCompleteDesiredCorrespondence(knownSide, otherSide, correspondenceTag);
		if (optCor.isEmpty())
			return null;

		var cor = optCor.get();
		cor.removeCorrespondent(otherSide);

		if (!cor.hasAnyCompleteCorrespondences()) {
			desiredCorrespondences.remove(cor);
		}

		return cor;
	}

	public void setDesiredCorrespondence(AbstractUserInteraction userInteraction, CorrespondenceEntry corEntry) {
		var corEntryOpt = getDesiredCorrespondenceEntry(corEntry.getKnownElement(), corEntry.getCorrespondenceTag());
		var ce = corEntryOpt.orElseGet(() -> null);
		if (ce != null) {
			ce.addCorrespondences(corEntry);
		} else {
			desiredCorrespondences.add(corEntry);
		}
	}

	public Set<CorrespondenceEntry> getAllCompleteCorrespondences() {
		return desiredCorrespondences.stream().filter((c) -> c.hasAnyCompleteCorrespondences())
				.collect(Collectors.toCollection(Set::of));
	}

	public void clear() {
		desiredCorrespondences.clear();
	}
}
