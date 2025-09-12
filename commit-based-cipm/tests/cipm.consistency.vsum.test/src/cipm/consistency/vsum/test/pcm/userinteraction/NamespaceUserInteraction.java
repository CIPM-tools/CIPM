package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamespaceAwareElement;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class NamespaceUserInteraction extends AbstractUserInteraction {
	private EStructuralFeature namespaceField;
	private NamespaceAwareElement toBeAssignedNamespace;
	private EObject triggeringElement;

	public NamespaceUserInteraction(EObject triggeringElement, NamespaceAwareElement toBeAssignedNamespace) {
		this.triggeringElement = triggeringElement;
		this.toBeAssignedNamespace = toBeAssignedNamespace;
		this.namespaceField = this.toBeAssignedNamespace.eClass()
				.getEStructuralFeature(CommonsPackage.NAMESPACE_AWARE_ELEMENT__NAMESPACES);
	}

	@Override
	public void performManualUserInteraction() {
		var name = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
				.message("Full namespace of the correspondent (without name)").startInteraction();

		var namespaces = new ArrayList<Object>();
		for (var ns : name.split("\\.")) {
			namespaces.add(ns);
		}

		var entry = new FeatureEntry(this.triggeringElement, namespaceField);
		entry.addValues(namespaces);
		this.reportDesiredFeatureValue(entry);
	}

	@Override
	public void getDesiredFeatureChangedValue(FeatureEntry featEntry) {
		if (featEntry.featureEquals(this.namespaceField) && this.checkNameValue(featEntry.getValue())) {
			this.finaliseUserInteraction();
		}
	}

	@Override
	public void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry) {
	}

	@Override
	public boolean hasDesiredFeature(EObject obj, EStructuralFeature feat) {
		return this.namespaceField == feat;
	}

	@Override
	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return false;
	}

	@Override
	public Set<FeatureEntry> getDesiredFeatures() {
		return Set.of(new FeatureEntry(this.toBeAssignedNamespace, this.namespaceField));
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return Set.of();
	}

	private boolean checkNameValue(Object namespaces) {
		return namespaces instanceof List;
	}
}
