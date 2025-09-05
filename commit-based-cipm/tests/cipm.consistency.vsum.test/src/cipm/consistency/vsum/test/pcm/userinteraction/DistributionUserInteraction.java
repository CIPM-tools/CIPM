package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.util.collection.Pair;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;

public class DistributionUserInteraction extends AbstractUserInteraction {
	private final static String correspondenceTag = "";
	private final EObject deletedElement;
	private final List<EObject> correspondingContentsToDistribute;
	private final List<EObject> possibleDistributionTargets;
	private final EditableCorrespondenceModelView<?> correspondenceModel;

	public DistributionUserInteraction(EObject deletedElement, List<EObject> correspondingContentsToDistribute,
			List<EObject> possibleDistributionTargets, EditableCorrespondenceModelView<?> correspondenceModel) {
		this.deletedElement = deletedElement;
		this.correspondingContentsToDistribute = correspondingContentsToDistribute;
		this.possibleDistributionTargets = possibleDistributionTargets;
		this.correspondenceModel = correspondenceModel;
	}

	@Override
	public void performManualUserInteraction() {
		final var choices = possibleDistributionTargets.stream().map((pt) -> pt.toString())
				.collect(Collectors.toUnmodifiableList());

		for (var contentToDistribute : this.correspondingContentsToDistribute) {
			if (this.isDesiredCorrespondencePresent(contentToDistribute, correspondenceTag)) {
//				if (this.correspondenceModel.getCorrespondingEObjects(contentToDistribute, correspondenceTag).stream()
//						.noneMatch((otherSide) -> possibleDistributionTargets.contains(otherSide))) {
//					var cor = this.retrieveDesiredCorrespondenceIfPresent(contentToDistribute, correspondenceTag);
//					var os = cor.getElement1() == contentToDistribute ? cor.getElement2() : cor.getElement1();
//					this.correspondenceModel.addCorrespondenceBetween(contentToDistribute, os, correspondenceTag);
//				}
				continue;
			}
			var choice = UserInteractionFactory.instance.createDialogUserInteractor().getSingleSelectionDialogBuilder()
					.message(String.format("Where should %s be moved?", contentToDistribute)).choices(choices)
					.startInteraction();
			var otherSide = possibleDistributionTargets.get(choice);
//			this.correspondenceModel.addCorrespondenceBetween(contentToDistribute, otherSide, correspondenceTag);
			this.reportDesiredCorrespondence(contentToDistribute, otherSide, correspondenceTag);
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
	public Set<Pair<EObject, String>> getDesiredCorrespondences() {
		return this.correspondingContentsToDistribute.stream().map((c) -> new Pair<>(c, correspondenceTag))
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public void getDesiredCorrespondenceChange(EObject knownSide, EObject otherSide, String correspondenceTag) {
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
}
