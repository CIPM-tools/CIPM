package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class NamespaceUserInteraction extends AbstractUserInteraction {
	private final static String namespaceSeparator = "\\.";

	private final static EStructuralFeature clsNamespacesFeat = CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES;

	private EObject toBeAssignedNamespace;
	private EObject triggeringPCMElement;

	public NamespaceUserInteraction(EObject triggeringPCMElement, EObject toBeAssignedNamespace) {
		this.triggeringPCMElement = triggeringPCMElement;
		this.toBeAssignedNamespace = toBeAssignedNamespace;
	}

	@Override
	public void performManualUserInteraction() {
		var name = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
				.message("Full namespace of the correspondent (without name)").startInteraction();

		var namespaces = new ArrayList<Object>();
		for (var ns : name.split(namespaceSeparator)) {
			namespaces.add(ns);
		}

		var entry = new FeatureEntry(this.triggeringPCMElement, toBeAssignedNamespace, clsNamespacesFeat);
		entry.addValues(namespaces);
		this.reportDesiredFeatureValue(entry);
	}

	@Override
	public void getDesiredFeatureChangedValue(FeatureEntry featEntry) {
		if (featEntry.featureEquals(clsNamespacesFeat) && this.checkNameValue(featEntry.getValue())) {
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
		return Set.of(new FeatureEntry(triggeringPCMElement, toBeAssignedNamespace, clsNamespacesFeat));
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return Set.of();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getNamespaces() {
		return isResolved() ? (List) retrieveDesiredFeatureValueIfPresent(triggeringPCMElement, clsNamespacesFeat)
				: null;
	}

	public EObject getTriggeringElement() {
		return this.triggeringPCMElement;
	}

	private boolean checkNameValue(Object namespaces) {
		return namespaces instanceof List;
	}

	@Override
	public boolean isResolved() {
		return isDesiredFeatureValuePresent(triggeringPCMElement, clsNamespacesFeat);
	}

	@Override
	public void resolveAll() {
		resolveForFeature(triggeringPCMElement, clsNamespacesFeat);
	}

	@Override
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return clsNamespacesFeat == feat && this.triggeringPCMElement == triggeringPCMElement
				&& (affectedJavaElement == null || this.toBeAssignedNamespace == affectedJavaElement);
	}

	@Override
	public List<EObject> getTriggeringPCMelements() {
		return List.of(triggeringPCMElement);
	}

	@Override
	public List<EObject> getAffectedJavaElements() {
		return List.of(toBeAssignedNamespace);
	}

	@Override
	public List<EObject> getAffectedPCMElements() {
		return List.of(triggeringPCMElement);
	}
}
