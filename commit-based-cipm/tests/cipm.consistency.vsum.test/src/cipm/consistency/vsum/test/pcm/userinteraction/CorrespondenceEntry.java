package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Preconditions;

public class CorrespondenceEntry {
	private final EObject knownElement;
	private final Set<EObject> correspondents;
	private String tag;

	public CorrespondenceEntry(EObject knownSide, String tag) {
		this(knownSide, Set.of(), tag);
	}

	public CorrespondenceEntry(EObject knownSide, EObject otherSide, String tag) {
		this(knownSide, Set.of(otherSide), tag);
	}

	public CorrespondenceEntry(EObject knownElement, Set<EObject> correspondents, String tag) {
		Preconditions.checkArgument(knownElement != null, "knownElement cannot be null");
		Preconditions.checkArgument(correspondents != null, "correspondents cannot be null");
		Preconditions.checkArgument(tag != null, "tag cannot be null");

		// Correspondences are supposed to be symmetric and handled as such
		// Therefore, knownElement too belongs in correspondents
		this.correspondents = new HashSet<>(correspondents);
		this.knownElement = knownElement;

		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		Preconditions.checkArgument(tag != null, "tag cannot be null");
		this.tag = tag;
	}

	public EObject getKnownElement() {
		return knownElement;
	}

	public Set<EObject> getCorrespondentsForKnownElement() {
		return new HashSet<>(correspondents);
	}

	public Set<EObject> getCorrespondentsFor(EObject correspondent) {
		if (eObjectEquals(knownElement, correspondent))
			return this.getCorrespondentsForKnownElement();

		var result = new HashSet<EObject>();
		if (this.hasCorrespondent(correspondent))
			result.add(knownElement);
		return result;
	}

	public int getCorrespondenceCount() {
		return this.correspondents.size();
	}

	public boolean addCorrespondent(EObject correspondent) {
		if (eObjectEquals(knownElement, correspondent) || hasCorrespondent(correspondent))
			return false;

		return this.correspondents.add(correspondent);
	}

	public List<EObject> addCorrespondences(CorrespondenceEntry entry) {
		if (!isTagEqual(entry.tag)) {
			return null;
		}

		var addedCors = new ArrayList<EObject>();
		if (eObjectEquals(knownElement, entry.knownElement)) {
			for (var cor : entry.correspondents) {
				if (this.addCorrespondent(cor)) {
					addedCors.add(cor);
				}
			}
		} else if (entry.hasElement(knownElement)) {
			this.addCorrespondent(entry.knownElement);
			addedCors.add(entry.knownElement);
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
		this.correspondents.clear();
	}

	public boolean hasCorrespondence(EObject correspondent1, EObject correspondent2) {
		return eObjectEquals(knownElement, correspondent1) && hasCorrespondent(correspondent2)
				|| eObjectEquals(knownElement, correspondent2) && hasCorrespondent(correspondent1);
	}

	public boolean hasCorrespondent(EObject correspondent) {
		return this.getCorrespondentFor(correspondent).isPresent();
	}

	public boolean hasElement(EObject element) {
		return eObjectEquals(knownElement, element) || hasCorrespondent(element);
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
		return !this.correspondents.isEmpty();
	}

	public boolean hasAnyCompleteCorrespondences(String tag) {
		return isTagEqual(tag) && hasAnyCompleteCorrespondences();
	}

	public boolean hasAnyCompleteCorrespondencesWith(EObject correspondent) {
		return hasCorrespondent(correspondent)
				|| (eObjectEquals(knownElement, correspondent) && hasAnyCompleteCorrespondences());
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

		return eObjectEquals(this.knownElement, castedO.knownElement)
				&& this.correspondents.size() == castedO.correspondents.size()
				&& this.hasCorrespondents(castedO.correspondents) && this.tag.equals(castedO.tag);
	}
}
