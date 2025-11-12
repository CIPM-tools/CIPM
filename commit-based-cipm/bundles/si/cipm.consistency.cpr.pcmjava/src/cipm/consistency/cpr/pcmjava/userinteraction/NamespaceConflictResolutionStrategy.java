package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.commons.CommonsPackage;

public class NamespaceConflictResolutionStrategy extends ConflictResolutionStrategy {
	private Resource javaModelRes;
	private List<EObject> javaElems;

	public NamespaceConflictResolutionStrategy(Resource javaModelRes) {
		this.javaModelRes = javaModelRes;
		this.javaElems = new ArrayList<>();
		this.javaModelRes.getAllContents().forEachRemaining(javaElems::add);
	}

	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		var castedUI = (NamespaceUserInteraction) userInteraction;
		for (var javaObj : castedUI.getAffectedJavaElements()) {
			var castedNE = (org.emftext.language.java.commons.NamedElement) javaObj;

			var matchingJavaElems = javaElems.stream().filter((je) -> javaObj.getClass().equals(je.getClass()))
					.map((je) -> (org.emftext.language.java.commons.NamedElement) je)
					.filter((je) -> je.getName() != null && je.getName().equals(castedNE.getName()))
					.map((je) -> je.getContainingCompilationUnit()).filter((je) -> je != null)
					.filter((je) -> !je.getNamespaces().isEmpty()).collect(Collectors.toList());

			if (matchingJavaElems.size() == 1) {
				PcmUserInteractionManager.setDesiredFeatureValue(this,
						new FeatureEntry(castedUI.getTriggeringPCMelements().get(0), javaObj,
								CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES,
								List.copyOf(matchingJavaElems.get(0).getNamespaces())));
			}
		}
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return userInteraction instanceof NamespaceUserInteraction;
	}
}
