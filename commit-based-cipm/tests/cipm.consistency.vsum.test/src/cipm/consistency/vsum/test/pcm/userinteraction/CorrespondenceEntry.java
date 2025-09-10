package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class CorrespondenceEntry {
	private EObject knownElement;
	private final Set<EObject> correspondents;
	private String tag;

	public CorrespondenceEntry(EObject knownSide, String tag) {
		this(knownSide, Set.of(), tag);
	}

	public CorrespondenceEntry(EObject knownSide, EObject otherSide, String tag) {
		this(knownSide, Set.of(otherSide), tag);
	}

	public CorrespondenceEntry(EObject knownElement, Set<EObject> correspondents, String tag) {
		// Correspondences are supposed to be symmetric and handled as such
		// Therefore, knownElement too belongs in correspondents
		this.correspondents = new HashSet<>(correspondents);
		this.setKnownElement(knownElement, false);

		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public EObject getKnownElement() {
		return knownElement;
	}

	public void setKnownElement(EObject newKnownElement, boolean removeCurrentKnownElement) {
		if (eObjectEquals(this.knownElement, newKnownElement))
			return;

		if (removeCurrentKnownElement) {
			this.correspondents.remove(this.knownElement);
			this.knownElement = null;
		}

		this.knownElement = newKnownElement;
		this.addCorrespondent(this.knownElement);
	}

	public EObject popKnownElement() {
		var poppedElement = this.knownElement;
		this.correspondents.remove(poppedElement);
		this.knownElement = this.correspondents.iterator().next();
		return poppedElement;
	}

	public Set<EObject> getCorrespondentsFor(EObject correspondent) {
		var cors = new HashSet<>(correspondents);
		cors.remove(correspondent);
		return cors;
	}

	public boolean addCorrespondent(EObject correspondent) {
		if (hasCorrespondent(correspondent))
			return false;

		return this.correspondents.add(correspondent);
	}

	public boolean addCorrespondence(EObject correspondent1, EObject correspondent2) {
		this.addCorrespondent(correspondent1);
		this.addCorrespondent(correspondent2);
		return hasCorrespondence(correspondent1, correspondent2);
	}

	public List<EObject> addCorrespondences(CorrespondenceEntry entry) {
		var addedCors = new ArrayList<EObject>();
		for (var cor : entry.correspondents) {
			if (this.addCorrespondent(cor)) {
				addedCors.add(cor);
			}
		}
		return addedCors;
	}

	public EObject removeCorrespondent(EObject correspondent) {
		if (eObjectEquals(knownElement, correspondent))
			return null;

		var corOpt = this.getCorrespondentFor(correspondent);
		if (corOpt.isPresent()) {
			var corToRemove = corOpt.get();
			this.correspondents.remove(corToRemove);
			return corToRemove;
		}

		return null;
	}

	public void clearCorrespondents() {
		this.correspondents.removeIf((c) -> !eObjectEquals(knownElement, c));
	}

	public boolean hasCorrespondence(EObject correspondent1, EObject correspondent2) {
		return hasCorrespondent(correspondent1) && hasCorrespondent(correspondent2);
	}

	public boolean hasCorrespondent(EObject correspondent) {
		return this.getCorrespondentFor(correspondent).isPresent();
	}

	public boolean hasCorrespondents(Collection<EObject> correspondents) {
		return correspondents.stream().allMatch((cc) -> hasCorrespondent(cc));
	}

	private Optional<EObject> getCorrespondentFor(EObject correspondent) {
		return this.correspondents.stream().filter((c) -> eObjectEquals(c, correspondent)).findFirst();
	}

	private boolean eObjectEquals(EObject obj1, EObject obj2) {
		return EcoreUtil.equals(obj1, obj2);
	}

	public boolean hasAnyCompleteCorrespondences() {
		return this.correspondents.size() > 1;
	}

	public boolean hasAnyCompleteCorrespondences(String tag) {
		return isTagEqual(tag) && hasAnyCompleteCorrespondences();
	}

	public boolean hasAnyCompleteCorrespondencesWith(EObject correspondent) {
		return hasCorrespondent(correspondent) && hasAnyCompleteCorrespondences();
	}

	public boolean hasAnyCompleteCorrespondencesWith(EObject correspondent, String tag) {
		return isTagEqual(tag) && hasAnyCompleteCorrespondencesWith(correspondent);
	}

	public boolean isTagEqual(String tag) {
		return this.tag.equals(tag);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CorrespondenceEntry))
			return false;

		var castedO = (CorrespondenceEntry) obj;

		return this.correspondents.size() == castedO.correspondents.size()
				&& this.hasCorrespondents(castedO.correspondents);
	}
}
