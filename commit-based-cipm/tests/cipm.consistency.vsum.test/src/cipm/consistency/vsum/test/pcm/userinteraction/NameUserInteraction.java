package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamedElement;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class NameUserInteraction extends AbstractUserInteraction {
	private EStructuralFeature nameField;
	private NamedElement toBeNamed;
	private EObject triggeringPCMElement;

	public NameUserInteraction(EObject triggeringPCMElement, NamedElement toBeNamed) {
		this.triggeringPCMElement = triggeringPCMElement;
		this.toBeNamed = toBeNamed;
		this.nameField = CommonsPackage.Literals.NAMED_ELEMENT__NAME;
	}

	@Override
	public void performManualUserInteraction() {
		var name = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
				.message("Name of the correspondent").inputValidator((n) -> checkNameValue(n), "Invalid name")
				.startInteraction();

		if (this.checkNameValue(name)) {
			var entry = new FeatureEntry(this.triggeringPCMElement, toBeNamed, nameField, name);
			this.reportDesiredFeatureValue(entry);
		}
	}

	@Override
	public void getDesiredFeatureChangedValue(FeatureEntry featEntry) {
		if (featEntry.featureEquals(this.nameField) && this.checkNameValue(featEntry.getValue())) {
			this.finaliseUserInteraction();
		}
	}

	private boolean checkNameValue(Object name) {
		return name instanceof String && !((String) name).isBlank();
	}

	@Override
	public Set<FeatureEntry> getDesiredFeatures() {
		return Set.of(new FeatureEntry(this.triggeringPCMElement, toBeNamed, this.nameField));
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return Set.of();
	}

	@Override
	public void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry) {
	}

	@Override
	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return false;
	}

	public EObject getTriggeringElement() {
		return this.triggeringPCMElement;
	}

	public NamedElement getElementToBeNamed() {
		return this.toBeNamed;
	}

	public String getName() {
		return isResolved() ? (String) retrieveDesiredFeatureValueIfPresent(triggeringPCMElement, nameField) : null;
	}

	@Override
	public boolean isResolved() {
		return this.isDesiredFeatureValuePresent(triggeringPCMElement, nameField);
	}

	@Override
	public void resolveAll() {
		this.resolveForFeature(triggeringPCMElement, nameField);
	}

	@Override
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return this.nameField == feat && this.triggeringPCMElement == triggeringPCMElement
				&& (affectedJavaElement == null || this.toBeNamed == affectedJavaElement);
	}
}
