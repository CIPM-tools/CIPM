package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamedElement;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class NameUserInteraction extends AbstractUserInteraction {
	private EStructuralFeature nameField;
	private NamedElement toBeNamed;
	private EObject triggeringElement;

	public NameUserInteraction(EObject triggeringElement, NamedElement toBeNamed) {
		this.triggeringElement = triggeringElement;
		this.toBeNamed = toBeNamed;
		this.nameField = this.toBeNamed.eClass().getEStructuralFeature(CommonsPackage.NAMED_ELEMENT__NAME);
	}

	@Override
	public void performManualUserInteraction() {
		var name = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
				.message("Name of the correspondent").inputValidator((n) -> checkNameValue(n), "Invalid name")
				.startInteraction();

		if (this.checkNameValue(name)) {
			this.reportDesiredFeatureValue(nameField, name);
		}
	}

	@Override
	public void getDesiredFeatureChangedValue(EStructuralFeature feat, Object newValues) {
		if (feat == this.nameField && this.checkNameValue(newValues)) {
			this.finaliseUserInteraction();
		}
	}

	private boolean checkNameValue(Object name) {
		return name instanceof String && !((String) name).isBlank();
	}

	@Override
	public boolean hasDesiredFeature(EStructuralFeature feat) {
		return this.nameField == feat;
	}

	@Override
	public List<EStructuralFeature> getDesiredFeatures() {
		return List.of(this.nameField);
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
		return this.triggeringElement;
	}

	public NamedElement getElementToBeNamed() {
		return this.toBeNamed;
	}
}
