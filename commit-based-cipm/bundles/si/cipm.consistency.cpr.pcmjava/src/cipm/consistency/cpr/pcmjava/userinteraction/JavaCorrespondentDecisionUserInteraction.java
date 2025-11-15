package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class JavaCorrespondentDecisionUserInteraction extends AbstractUserInteraction {
	private static final String noneChoiceText = "None of the above";

	private final String correspondenceTagToUse;
	private final EObject triggeringPCMelement;
	private EObject javaCorrespondent;
	private final List<? extends EObject> possibleJavaCorrespondents;

	public JavaCorrespondentDecisionUserInteraction(EObject triggeringPCMelement,
			List<? extends EObject> possibleJavaCorrespondents, String correspondenceTagToUse) {
		this.triggeringPCMelement = triggeringPCMelement;
		this.possibleJavaCorrespondents = possibleJavaCorrespondents;
		this.correspondenceTagToUse = correspondenceTagToUse;
	}

	@Override
	public void performManualUserInteraction() {
		final var choices = possibleJavaCorrespondents.stream().map((javaCor) -> javaCor.toString())
				.collect(Collectors.toList());
		choices.add(noneChoiceText);

		if (!this.isResolved()) {
			var choice = UserInteractionFactory.instance.createDialogUserInteractor().getSingleSelectionDialogBuilder()
					.message(String.format("Which Java element corresponds to %s (name: %s)?", triggeringPCMelement,
							triggeringPCMelement instanceof org.palladiosimulator.pcm.core.entity.NamedElement
									? ((org.palladiosimulator.pcm.core.entity.NamedElement) triggeringPCMelement)
											.getEntityName()
									: "NO Name"))
					.choices(choices).startInteraction();
			javaCorrespondent = choice < possibleJavaCorrespondents.size() ? possibleJavaCorrespondents.get(choice)
					: null;

			var entry = javaCorrespondent != null
					? new CorrespondenceEntry(triggeringPCMelement, javaCorrespondent, correspondenceTagToUse)
					: new CorrespondenceEntry(triggeringPCMelement, correspondenceTagToUse);

			this.reportDesiredCorrespondence(entry);
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
		var corEntry = resolveForCorrespondence(triggeringPCMelement, this.correspondenceTagToUse);
		var cors = corEntry.getCorrespondentsForKnownElement();
		if (!cors.isEmpty()) {
			this.javaCorrespondent = cors.iterator().next();
		}
	}

	public EObject getJavaCorrespondent() {
		return javaCorrespondent;
	}

	@Override
	public List<EObject> getTriggeringPCMelements() {
		return List.of(triggeringPCMelement);
	}

	@Override
	public List<EObject> getAffectedJavaElements() {
		return List.copyOf(possibleJavaCorrespondents);
	}

	@Override
	public List<EObject> getAffectedPCMElements() {
		return List.of(triggeringPCMelement);
	}
}
