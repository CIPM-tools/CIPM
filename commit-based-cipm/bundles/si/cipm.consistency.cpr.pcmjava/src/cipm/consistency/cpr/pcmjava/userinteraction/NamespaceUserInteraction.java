package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.commons.CommonsPackage;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class NamespaceUserInteraction extends AbstractUserInteraction {
	private static final String noneChoiceText = "None of the above";
	private final static String namespaceSeparator = "\\.";

	private final static EStructuralFeature clsNamespacesFeat = CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES;

	private boolean allowNoNamespace = false;
	private boolean shouldUseNoNamespaces = false;

	private List<String> nsSuggestions;

	private EObject toBeAssignedNamespace;
	private EObject triggeringPCMElement;

	public NamespaceUserInteraction(EObject triggeringPCMElement, EObject toBeAssignedNamespace) {
		this.triggeringPCMElement = triggeringPCMElement;
		this.toBeAssignedNamespace = toBeAssignedNamespace;
	}

	@Override
	public void performManualUserInteraction() {
		Object uiResult = null;
		if (nsSuggestions == null || nsSuggestions.isEmpty()) {
			uiResult = UserInteractionFactory.instance.createDialogUserInteractor().getTextInputDialogBuilder()
					.message(String.format("Full namespace (without name) of the correspondent of %s (name: %s)",
							triggeringPCMElement,
							triggeringPCMElement instanceof org.palladiosimulator.pcm.core.entity.NamedElement
									? ((org.palladiosimulator.pcm.core.entity.NamedElement) triggeringPCMElement)
											.getEntityName()
									: "NO Name"))
					.startInteraction();
		} else {
			var choices = new ArrayList<String>();
			choices.addAll(nsSuggestions);
			choices.add(noneChoiceText);

			uiResult = UserInteractionFactory.instance.createDialogUserInteractor().getSingleSelectionDialogBuilder()
					.message(String.format("Full namespace (without name) of the correspondent of %s (name: %s)",
							triggeringPCMElement,
							triggeringPCMElement instanceof org.palladiosimulator.pcm.core.entity.NamedElement
									? ((org.palladiosimulator.pcm.core.entity.NamedElement) triggeringPCMElement)
											.getEntityName()
									: "NO Name"))
					.choices(choices).startInteraction();
		}

		var entry = new FeatureEntry(this.triggeringPCMElement, toBeAssignedNamespace, clsNamespacesFeat);

		var name = uiResult instanceof String ? (String) uiResult : null;
		if (uiResult != null && uiResult instanceof Integer && nsSuggestions.size() > (Integer) uiResult) {
			name = nsSuggestions.get((Integer) uiResult);
		} else if (name == null && nsSuggestions != null && !nsSuggestions.isEmpty() && !allowNoNamespace) {
			nsSuggestions = null;
			performManualUserInteraction();
			return;
		}

		if (name != null && !name.isBlank()) {
			var namespaces = new ArrayList<String>();
			for (var ns : name.split(namespaceSeparator)) {
				namespaces.add(ns);
			}

			entry.addValues(namespaces);
		} else {
			entry.unset();
		}
		this.reportDesiredFeatureValue(entry);
	}

	public void useNoNamespace() {
		this.shouldUseNoNamespaces = true;
	}

	public void setAllowNoNamespace(boolean allowNoNamespace) {
		this.allowNoNamespace = allowNoNamespace;
	}

	public boolean allowsNoNamespaces() {
		return this.allowNoNamespace;
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

	private boolean checkNameValue(Object namespaces) {
		return namespaces instanceof List;
	}

	public void setSuggestions(List<String> nsSuggestions) {
		this.nsSuggestions = nsSuggestions;
	}

	@Override
	public boolean isResolved() {
		return (this.allowNoNamespace && this.shouldUseNoNamespaces)
				|| isDesiredFeatureValuePresent(triggeringPCMElement, clsNamespacesFeat);
	}

	@Override
	public void resolveAll() {
		if (!this.allowNoNamespace || !this.shouldUseNoNamespaces) {
			resolveForFeature(triggeringPCMElement, clsNamespacesFeat);
		}
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
