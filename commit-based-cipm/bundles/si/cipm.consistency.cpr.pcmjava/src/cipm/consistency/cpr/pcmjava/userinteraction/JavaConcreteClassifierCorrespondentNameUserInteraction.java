package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.CommonsPackage;
import org.palladiosimulator.pcm.repository.DataType;

import tools.vitruv.change.interaction.UserInteractionFactory;

public class JavaConcreteClassifierCorrespondentNameUserInteraction extends AbstractUserInteraction {
	private final DataType triggeringDataType;
	private final ConcreteClassifier uninitialisedJavaClsfier;

	private final static String namespaceSeparator = "\\.";

	private final static EStructuralFeature clsNameFeat = CommonsPackage.Literals.NAMED_ELEMENT__NAME;
	private final static EStructuralFeature clsNamespacesFeat = CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES;

	private String clsName;
	private List<String> clsNamespaces;

	public JavaConcreteClassifierCorrespondentNameUserInteraction(DataType triggeringDataType,
			ConcreteClassifier uninitialisedJavaClsfier) {
		this.triggeringDataType = triggeringDataType;
		this.uninitialisedJavaClsfier = uninitialisedJavaClsfier;
	}

	@Override
	public void performManualUserInteraction() {
		if (clsName == null || (clsNamespaces == null || clsNamespaces.isEmpty())) {
			var fullyQualifiedName = UserInteractionFactory.instance.createDialogUserInteractor()
					.getTextInputDialogBuilder().message("Fully qualified name of the Java classifier")
					.startInteraction();

			var nssAndName = fullyQualifiedName.split(namespaceSeparator);

			if (clsNamespaces == null) {
				clsNamespaces = new ArrayList<>();
			}

			for (int i = 0; i < nssAndName.length - 1; i++) {
				clsNamespaces.add(nssAndName[i]);
			}
			clsName = nssAndName[nssAndName.length - 1];

			var nameFeat = new FeatureEntry(triggeringDataType, uninitialisedJavaClsfier, clsNameFeat);
			nameFeat.setOrAddValue(clsName);
			var namespacesFeat = new FeatureEntry(triggeringDataType, uninitialisedJavaClsfier, clsNamespacesFeat);
			namespacesFeat.addValues(clsNamespaces);

			reportDesiredFeatureValue(nameFeat);
			reportDesiredFeatureValue(namespacesFeat);
		}
	}

	@Override
	public void getDesiredFeatureChangedValue(FeatureEntry featEntry) {
		if (featEntry.isFeatureEntryFor(triggeringDataType, uninitialisedJavaClsfier, clsNameFeat)) {
			clsName = (String) featEntry.getValue();
		} else if (featEntry.isFeatureEntryFor(triggeringDataType, uninitialisedJavaClsfier, clsNamespacesFeat)) {
			clsNamespaces = (List<String>) featEntry.getValue();
		}

		if (clsName != null && clsNamespaces != null) {
			finaliseUserInteraction();
		}
	}

	@Override
	public void getDesiredCorrespondenceChange(CorrespondenceEntry corEntry) {
	}

	@Override
	public boolean hasDesiredFeature(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return triggeringPCMElement == triggeringDataType && affectedJavaElement == uninitialisedJavaClsfier
				&& (feat == clsNameFeat || feat == clsNamespacesFeat);
	}

	@Override
	public boolean hasDesiredCorrespondence(EObject knownSide, String correspondenceTag) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<FeatureEntry> getDesiredFeatures() {
		return Set.of(new FeatureEntry(triggeringDataType, uninitialisedJavaClsfier, clsNameFeat),
				new FeatureEntry(triggeringDataType, uninitialisedJavaClsfier, clsNamespacesFeat));
	}

	@Override
	public Set<CorrespondenceEntry> getDesiredCorrespondences() {
		return Set.of();
	}

	@Override
	public boolean isResolved() {
		return isDesiredFeatureValuePresent(triggeringDataType, uninitialisedJavaClsfier, clsNameFeat)
				&& isDesiredFeatureValuePresent(triggeringDataType, uninitialisedJavaClsfier, clsNamespacesFeat);
	}

	@Override
	public void resolveAll() {
		resolveForFeature(triggeringDataType, uninitialisedJavaClsfier, clsNameFeat);
		resolveForFeature(triggeringDataType, uninitialisedJavaClsfier, clsNamespacesFeat);
	}

}
