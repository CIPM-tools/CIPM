package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import cipm.consistency.cpr.pcmjava.userinteraction.AbstractUserInteraction;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;

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
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return pcmToJavaCorrespondentURIFragments != null
				&& isRelevantFor(userInteraction, triggeringPCMelement, javaContext, null);
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		for (var entry : userInteraction.getDesiredCorrespondences()) {
			var knownElement = entry.getKnownElement();
			var corIDs = pcmToJavaCorrespondentURIFragments.get(knownElement.eResource().getURIFragment(knownElement));
			if (corIDs != null) {
				for (var corID : corIDs) {
					var corElem = getEObjectWithURIFragment(corID);
					if (corElem != null)
						entry.addCorrespondent(corElem);
				}
			}

			reportDesiredCorrespondence(userInteraction, entry);
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
