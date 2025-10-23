package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.palladiosimulator.pcm.core.entity.Entity;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;
import tools.vitruv.change.interaction.UserInteractionFactory;

public class DistributionUserInteraction extends AbstractUserInteraction {
	private final static String correspondenceTag = "";
	private final Entity deletedElement;
	private final List<EObject> correspondingContentsToDistribute;
	private final List<EObject> possibleDistributionTargets;

	public DistributionUserInteraction(Entity deletedElement, List<EObject> correspondingContentsToDistribute,
			List<EObject> possibleDistributionTargets) {
		this.deletedElement = deletedElement;
		this.correspondingContentsToDistribute = correspondingContentsToDistribute;
		this.possibleDistributionTargets = possibleDistributionTargets;
	}

	@Override
	public void performManualUserInteraction() {
		final var choices = possibleDistributionTargets.stream().map((pt) -> pt.toString())
				.collect(Collectors.toUnmodifiableList());

		for (var contentToDistribute : this.correspondingContentsToDistribute) {
			if (this.isDesiredCorrespondencePresent(contentToDistribute, correspondenceTag)) {
				continue;
			}
			var choice = UserInteractionFactory.instance.createDialogUserInteractor().getSingleSelectionDialogBuilder()
					.message(String.format("Where should %s be moved?", contentToDistribute)).choices(choices)
					.startInteraction();
			var otherSide = possibleDistributionTargets.get(choice);

			this.reportDesiredCorrespondence(
					new CorrespondenceEntry(contentToDistribute, otherSide, correspondenceTag));
		}
	}

	@Override
	public void getDesiredFeatureChangedValue(FeatureEntry featEntry) {
	}

	@Override
	public Set<FeatureEntry> getDesiredFeatures() {
		return Set.of();
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return this.correspondingContentsToDistribute.stream().map((c) -> new CorrespondenceEntry(c, correspondenceTag))
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry) {
		if (this.correspondingContentsToDistribute.stream()
				.allMatch((c) -> isDesiredCorrespondencePresent(c, correspondenceTag))) {
			this.finaliseUserInteraction();
		}
	}

	@Override
	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return this.correspondingContentsToDistribute.contains(knownSide)
				&& correspondenceTag.equals(DistributionUserInteraction.correspondenceTag);
	}

	public Entity getDeletedElement() {
		return deletedElement;
	}

	public List<EObject> getCorrespondingContentsToDistribute() {
		return List.copyOf(correspondingContentsToDistribute);
	}

	public List<EObject> getPossibleDistributionTargets() {
		return List.copyOf(possibleDistributionTargets);
	}

	public Map<EObject, Set<EObject>> getCorrespondences() {
		if (!isResolved())
			return null;
		var result = new HashMap<EObject, Set<EObject>>();
		for (var ks : this.correspondingContentsToDistribute) {
			result.put(ks,
					retrieveDesiredCorrespondenceIfPresent(ks, correspondenceTag).getCorrespondentsForKnownElement());
		}
		return result;
	}

	public String getCorrespondenceTag() {
		return correspondenceTag;
	}

	@Override
	public boolean isResolved() {
		return this.correspondingContentsToDistribute.stream()
				.allMatch((c) -> isDesiredCorrespondencePresent(c, correspondenceTag));
	}

	@Override
	public void resolveAll() {
		for (var c : this.correspondingContentsToDistribute) {
			resolveForCorrespondence(c, correspondenceTag);
		}
	}

	@Override
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return false;
	}
}
