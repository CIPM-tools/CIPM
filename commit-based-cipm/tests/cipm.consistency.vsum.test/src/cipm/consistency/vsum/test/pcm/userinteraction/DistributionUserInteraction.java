package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class DistributionUserInteraction extends AbstractUserInteraction {
	private final static String correspondenceTag = "";
	private final EObject deletedElement;
	private final List<EObject> correspondingContentsToDistribute;
	private final List<EObject> possibleDistributionTargets;

	public DistributionUserInteraction(EObject deletedElement, List<EObject> correspondingContentsToDistribute,
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
	public void getDesiredFeatureChangedValue(EStructuralFeature feat, Object newValues) {
	}

	@Override
	public boolean hasDesiredFeature(EStructuralFeature feat) {
		return false;
	}

	@Override
	public List<EStructuralFeature> getDesiredFeatures() {
		return List.of();
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

	public EObject getDeletedElement() {
		return deletedElement;
	}

	public List<EObject> getCorrespondingContentsToDistribute() {
		return List.copyOf(correspondingContentsToDistribute);
	}

	public List<EObject> getPossibleDistributionTargets() {
		return List.copyOf(possibleDistributionTargets);
	}
}
