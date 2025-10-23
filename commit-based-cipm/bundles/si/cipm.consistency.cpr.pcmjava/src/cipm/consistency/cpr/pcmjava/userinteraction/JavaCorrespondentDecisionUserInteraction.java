package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class JavaCorrespondentDecisionUserInteraction extends AbstractUserInteraction {
	private final String correspondenceTagToUse;
	private final EObject triggeringPCMelement;
	private EObject javaCorrespondent;
	private final List<EObject> possibleJavaCorrespondents;

	public JavaCorrespondentDecisionUserInteraction(EObject triggeringPCMelement,
			List<EObject> possibleJavaCorrespondents, String correspondenceTagToUse) {
		this.triggeringPCMelement = triggeringPCMelement;
		this.possibleJavaCorrespondents = possibleJavaCorrespondents;
		this.correspondenceTagToUse = correspondenceTagToUse;
	}

	@Override
	public void performManualUserInteraction() {
		final var choices = possibleJavaCorrespondents.stream().map((javaCor) -> javaCor.toString())
				.collect(Collectors.toUnmodifiableList());

		if (!this.isResolved()) {
			var choice = UserInteractionFactory.instance.createDialogUserInteractor().getSingleSelectionDialogBuilder()
					.message(String.format("Which Java element corresponds to %s?", triggeringPCMelement))
					.choices(choices).startInteraction();
			var otherSide = possibleJavaCorrespondents.get(choice);
			javaCorrespondent = otherSide;

			this.reportDesiredCorrespondence(
					new CorrespondenceEntry(triggeringPCMelement, otherSide, correspondenceTagToUse));
		}
	}

	@Override
	public void getDesiredFeatureChangedValue(FeatureEntry featEntry) {
	}

	@Override
	public void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry) {
		var cor = possibleJavaCorrespondents.stream()
				.filter((javaCor) -> corEntry.hasCorrespondence(javaCor, triggeringPCMelement)
						&& corEntry.isCorrespondenceTagEqual(correspondenceTagToUse))
				.findFirst();
		if (cor.isPresent()) {
			javaCorrespondent = cor.get();
			finaliseUserInteraction();
		}
	}

	@Override
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return false;
	}

	@Override
	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return this.triggeringPCMelement == knownSide && this.correspondenceTagToUse.equals(correspondenceTag);
	}

	@Override
	public Set<FeatureEntry> getDesiredFeatures() {
		return Set.of();
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return Set.of(new CorrespondenceEntry(triggeringPCMelement, this.correspondenceTagToUse));
	}

	@Override
	public boolean isResolved() {
		return isDesiredCorrespondencePresent(triggeringPCMelement, this.correspondenceTagToUse);
	}

	@Override
	public void resolveAll() {
		resolveForCorrespondence(triggeringPCMelement, this.correspondenceTagToUse);
	}

	public EObject getJavaCorrespondent() {
		return javaCorrespondent;
	}
}
