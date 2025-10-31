package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public class CorrespondenceInputConflictResolutionStrategy extends ConflictResolutionStrategy {
	private final List<EObject> triggeringPCMelement;
	private final List<EObject> javaContext;
	private final Map<String, List<String>> pcmToJavaCorrespondentURIFragments;

	public CorrespondenceInputConflictResolutionStrategy(List<EObject> triggeringPCMelement, List<EObject> javaContext,
			Map<String, List<String>> pcmToJavaCorrespondentURIFragments) {
		this.triggeringPCMelement = triggeringPCMelement;
		this.javaContext = javaContext;
		this.pcmToJavaCorrespondentURIFragments = pcmToJavaCorrespondentURIFragments;
	}

	@Override
	public void applyFor(AbstractUserInteraction userInteraction) {
		if (pcmToJavaCorrespondentURIFragments != null
				&& isApplicableFor(userInteraction, triggeringPCMelement, javaContext, null)) {
			for (var entry : userInteraction.getDesiredCorrespondences()) {
				var knownElement = entry.getKnownElement();
				var corIDs = pcmToJavaCorrespondentURIFragments
						.get(knownElement.eResource().getURIFragment(knownElement));
				if (corIDs != null) {
					for (var corID : corIDs) {
						var corElem = getEObjectWithURIFragment(corID);
						if (corElem != null)
							entry.addCorrespondent(corElem);
					}
				}

				PcmUserInteractionManager.setDesiredCorrespondence(null, entry);
			}
		}
	}

	private EObject getEObjectWithURIFragment(String uriFragment) {
		var pcmElem = triggeringPCMelement.stream().filter((o) -> o.eResource().getURIFragment(o).equals(uriFragment))
				.findFirst().orElse(null);

		if (pcmElem != null)
			return pcmElem;

		var javaElem = javaContext.stream().filter((o) -> o.eResource().getURIFragment(o).equals(uriFragment))
				.findFirst().orElse(null);

		if (javaElem != null)
			return javaElem;

		return null;
	}
}
