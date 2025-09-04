package cipm.consistency.vsum.test.pcm.userinteraction;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamedElement;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class NameUserInteraction extends AbstractUserInteraction {
	private EStructuralFeature nameField;
	private NamedElement toBeNamed;

	public NameUserInteraction(NamedElement toBeNamed) {
		super();
		this.toBeNamed = toBeNamed;
		this.nameField = this.toBeNamed.eClass().getEStructuralFeature(CommonsPackage.NAMED_ELEMENT__NAME);
		this.getModifiableDesiredFeaturesList().add(this.nameField);
	}

	@Override
	public void performManualUserInteraction() {
		var name = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
				.message("Name of the correspondent").inputValidator((n) -> checkNameValue(n), "Invalid name")
				.startInteraction();

		if (this.checkNameValue(name)) {
			this.toBeNamed.setName(name);
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
}
