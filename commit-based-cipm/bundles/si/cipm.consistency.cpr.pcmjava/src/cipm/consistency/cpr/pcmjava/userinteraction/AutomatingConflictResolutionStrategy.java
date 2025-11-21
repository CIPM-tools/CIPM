package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import org.emftext.language.java.commons.CommonsPackage;

public class AutomatingConflictResolutionStrategy extends ConflictResolutionStrategy {
	private List<String> defaultNss;

	public AutomatingConflictResolutionStrategy(List<String> defaultNss) {
		this.defaultNss = defaultNss;
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		if (userInteraction instanceof NamespaceUserInteraction) {
			var castedUI = (NamespaceUserInteraction) userInteraction;
			reportDesiredFeatureValue(castedUI,
					new FeatureEntry(castedUI.getTriggeringPCMelements().get(0),
							castedUI.getAffectedJavaElements().get(0),
							CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, List.copyOf(defaultNss)));
		} else if (userInteraction instanceof JavaCorrespondentDecisionUserInteraction) {
			var castedUI = (JavaCorrespondentDecisionUserInteraction) userInteraction;
			reportDesiredCorrespondence(castedUI, new CorrespondenceEntry(castedUI.getTriggeringPCMelements().get(0),
					castedUI.getAffectedJavaElements().get(0), ""));
		}
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return true;
	}
}
