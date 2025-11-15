package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.commons.NamespaceAwareElement;

import com.google.common.base.Strings;

public class NamespaceConflictResolutionStrategy extends ConflictResolutionStrategy {
	private static final Logger LOGGER = Logger.getLogger(NamespaceConflictResolutionStrategy.class);

	private Resource javaModelRes;
	private List<EObject> javaElems;

	public NamespaceConflictResolutionStrategy(Resource javaModelRes) {
		this.javaModelRes = javaModelRes;
		this.javaElems = new ArrayList<>();
		this.javaModelRes.getAllContents().forEachRemaining(javaElems::add);
	}

	// TODO Intercept manual user interactions for generic types
	
	@Override
	protected void applyStrategy(AbstractUserInteraction userInteraction) {
		var castedUI = (NamespaceUserInteraction) userInteraction;
		for (var javaObj : castedUI.getAffectedJavaElements()) {
			var castedNE = (org.emftext.language.java.commons.NamedElement) javaObj;

			LOGGER.info(String.format("Seeking namespaces of %s (name: %s)", castedNE,
					Strings.nullToEmpty(castedNE.getName())));

			var elemsWithSameName = javaElems.stream()
					.filter((je) -> je instanceof org.emftext.language.java.commons.NamedElement)
					.map((je) -> (org.emftext.language.java.commons.NamedElement) je)
					.filter((je) -> je.getName() != null && je.getName().equals(castedNE.getName()))
					.collect(Collectors.toSet());

			var possibleNamespaces = new HashSet<String>();

			elemsWithSameName.stream().filter((je) -> je instanceof ConcreteClassifier)
					.map((je) -> (ConcreteClassifier) je).forEach((je) -> {
						if (je.getParentConcreteClassifier() != null && je != je.getParentConcreteClassifier()) {
							// An inner class' (je) name matches the name of the Java object to be created
							// Account for its parent class' name in possible namespaces
							possibleNamespaces.add(je.getParentConcreteClassifier().getQualifiedName());
						} else if (je.getContainingCompilationUnit() != null) {
							// A class' (je) name matches the name of the Java object to be created
							// Add its CompilationUnit's namespaces as possible namespaces
							var nss = getNamespacesAsString(je.getContainingCompilationUnit());
							if (!nss.isBlank())
								possibleNamespaces.add(nss);
						} else if (je.getPackage() != null) {
							// A class' (je) name matches the name of the Java object to be created
							// je has no CompilationUnit containing it, it is contained in a Package
							// Add its Package's namespaces as possible namespaces
							var nss = getNamespacesAsString(je.getPackage());
							if (!nss.isBlank())
								possibleNamespaces.add(nss);
						}
					});

			possibleNamespaces.forEach((nss) -> LOGGER
					.info(String.format("Found namespace \"%s\" as potential match", Strings.nullToEmpty(nss))));

			if (possibleNamespaces.size() == 1) {
				var nss = getNamespacesAsList(possibleNamespaces.iterator().next());
				LOGGER.info(String.format("Reporting namespace \"%s\" for %s (name: %s)", nss, castedNE,
						Strings.nullToEmpty(castedNE.getName())));

				reportDesiredFeatureValue(userInteraction, new FeatureEntry(castedUI.getTriggeringPCMelements().get(0), javaObj,
						CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss));
			} else {
				castedUI.setSuggestions(List.copyOf(possibleNamespaces));
			}
		}
	}

	private String getNamespacesAsString(NamespaceAwareElement nae) {
		var nss = nae.getNamespacesAsString();
		// Trim the unnecessary "." at the end of nss
		return nss.length() > 0 ? nss.substring(0, nss.length() - 1) : nss;
	}

	private static final String namespaceSeparatorRegex = "\\.";

	private List<String> getNamespacesAsList(String nss) {
		return List.of(nss.split(namespaceSeparatorRegex));
	}

	@Override
	protected boolean checkInternalApplicationConditions(AbstractUserInteraction userInteraction) {
		return userInteraction instanceof NamespaceUserInteraction;
	}
}
