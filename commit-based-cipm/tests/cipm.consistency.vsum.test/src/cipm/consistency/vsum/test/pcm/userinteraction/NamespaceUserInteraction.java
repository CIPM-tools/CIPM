package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamespaceAwareElement;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureEntry;
import tools.vitruv.change.interaction.UserInteractionFactory;

public class NamespaceUserInteraction extends AbstractUserInteraction {
	private EStructuralFeature namespaceField;
	private NamespaceAwareElement toBeAssignedNamespace;
	private EObject triggeringPCMElement;

	public NamespaceUserInteraction(EObject triggeringPCMElement, NamespaceAwareElement toBeAssignedNamespace) {
		this.triggeringPCMElement = triggeringPCMElement;
		this.toBeAssignedNamespace = toBeAssignedNamespace;
		this.namespaceField = CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES;
	}

	@Override
	public void performManualUserInteraction() {
		var name = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
				.message("Full namespace of the correspondent (without name)").startInteraction();

		var namespaces = new ArrayList<Object>();
		for (var ns : name.split("\\.")) {
			namespaces.add(ns);
		}

		var entry = new FeatureEntry(this.triggeringPCMElement, toBeAssignedNamespace, namespaceField);
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
	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		return false;
	}

	@Override
	public Set<FeatureEntry> getDesiredFeatures() {
		return Set.of(new FeatureEntry(triggeringPCMElement, toBeAssignedNamespace, namespaceField));
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return Set.of();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getNamespaces() {
		return isResolved() ? (List) retrieveDesiredFeatureValueIfPresent(triggeringPCMElement, namespaceField) : null;
	}

	public EObject getTriggeringElement() {
		return this.triggeringPCMElement;
	}

	private boolean checkNameValue(Object namespaces) {
		return namespaces instanceof List;
	}

	@Override
	public boolean isResolved() {
		return isDesiredFeatureValuePresent(triggeringPCMElement, namespaceField);
	}

	@Override
	public void resolveAll() {
		resolveForFeature(triggeringPCMElement, namespaceField);
	}

	@Override
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return this.namespaceField == feat && this.triggeringPCMElement == triggeringPCMElement
				&& (affectedJavaElement == null || this.toBeAssignedNamespace == affectedJavaElement);
	}
}
