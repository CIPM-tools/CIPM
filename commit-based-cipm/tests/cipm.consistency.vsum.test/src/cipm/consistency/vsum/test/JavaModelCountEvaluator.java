package cipm.consistency.vsum.test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

public class JavaModelCountEvaluator {
	public static class JavaModelCountResult {
		private JavaModelCounts overallCounts;
		private EnumMap<Origin, JavaModelCounts> countsPerOrigin;
		
		public JavaModelCounts getOverallCounts() {
			return overallCounts;
		}
		
		public EnumMap<Origin, JavaModelCounts> getCountsPerOrigin() {
			return countsPerOrigin;
		}
	}
	
	public static class JavaModelCounts {
		private int noRootElements = 0;
		private int noModelElements = 0;
		private int noContainmentReferences = 0;
		private int noNonContainmentReferences = 0;
		private int noReferences = 0;
		private int noRecoveredReferences = 0;
		
		public void merge(JavaModelCounts counts) {
			noModelElements += counts.noModelElements;
			noContainmentReferences += counts.noContainmentReferences;
			noNonContainmentReferences += counts.noNonContainmentReferences;
			noRootElements += counts.noRootElements;
			noRecoveredReferences += counts.noRecoveredReferences;
			noReferences += counts.noReferences;
		}
		
		@Override
		public String toString() {
			return String.format("Root: %d - Model: %d - Contain: %d - Non-Contain: %d - Refs: %d - Recovered: %d",
				noRootElements,
				noModelElements,
				noContainmentReferences,
				noNonContainmentReferences,
				noReferences,
				noRecoveredReferences
			);
		}

		public int getNoRootElements() {
			return noRootElements;
		}

		public int getNoModelElements() {
			return noModelElements;
		}

		public int getNoContainmentReferences() {
			return noContainmentReferences;
		}

		public int getNoNonContainmentReferences() {
			return noNonContainmentReferences;
		}

		public int getNoReferences() {
			return noReferences;
		}

		public int getNoRecoveredReferences() {
			return noRecoveredReferences;
		}
	}
	
	public JavaModelCountResult countJavaModelProperties(Path modelPath) {
		var set = new ResourceSetImpl();
		var resource = set.getResource(URI.createFileURI(modelPath.toAbsolutePath().toString()), true);
		var originToCounts = new EnumMap<Origin, JavaModelCounts>(Origin.class);
		
		for (var root : resource.getContents()) {
			JavaRoot javaRoot = (JavaRoot) root;
			var counts = originToCounts.getOrDefault(javaRoot.getOrigin(), new JavaModelCounts());
			originToCounts.put(javaRoot.getOrigin(), counts);
			
			counts.noRootElements++;
			var modelCounts = ModelElementsCounter.countModelElements(javaRoot);
			counts.noModelElements += modelCounts.getNumberOfElements();
			counts.noContainmentReferences += modelCounts.getNumberOfContainmentReferences();
			counts.noNonContainmentReferences += modelCounts.getNumberOfNonContainmentReferences();
			counts.noReferences += modelCounts.getNumberOfContainmentReferences() + modelCounts.getNumberOfNonContainmentReferences();
			counts.noRecoveredReferences += this.countRevoceredReferences(javaRoot);
		}
		
		var overall = new JavaModelCounts();
		for (var singleCounts : originToCounts.entrySet()) {
			overall.merge(singleCounts.getValue());
		}
		
		var result = new JavaModelCountResult();
		result.overallCounts = overall;
		result.countsPerOrigin = originToCounts;
		return result;
	}
	
	private int countRevoceredReferences(JavaRoot root) {
		var counter = new AtomicInteger();
		root.eAllContents().forEachRemaining(child -> {
			child.eCrossReferences().forEach(referenced -> {
				if (referenced.eContainer() instanceof CompilationUnit) {
					var cu = (CompilationUnit) referenced.eContainer();
					if (cu.getName().equals("")) {
						counter.incrementAndGet();
					}
				} else if (referenced.eContainer() instanceof org.emftext.language.java.classifiers.Class) {
					Class cls = (Class) referenced.eContainer();
					if (cls.getName().equals("SyntheticClass")) {
						counter.incrementAndGet();
					}
				}
			});
		});
		return counter.get();
	}
	
	public void convertPcmRepositoryToGraphViz(Path repositoryPath) {
		System.out.println("Now loading " + repositoryPath.toString());
		var set = new ResourceSetImpl();
		var repoRes = set.getResource(URI.createFileURI(repositoryPath.toString()), true);
		var repo = (Repository) repoRes.getContents().get(0);
		System.out.println("Loaded repository with " + ModelElementsCounter.countModelElements(repoRes) + " elements.");
		
		// Create a mapping of interfaces to providing components.
		var providedInterfaceToComponent = new HashMap<Interface, List<RepositoryComponent>>();
		for (var inter : repo.getInterfaces__Repository()) {
			providedInterfaceToComponent.put(inter, new ArrayList<>());
		}
		for (var component : repo.getComponents__Repository()) {
			for (var provided : component.getProvidedRoles_InterfaceProvidingEntity()) {
				if (provided instanceof OperationProvidedRole) {
					var inter = ((OperationProvidedRole) provided).getProvidedInterface__OperationProvidedRole();
					providedInterfaceToComponent.get(inter).add(component);
				}
			}
		}
		
		// Look for all required interfaces and their connections to providing components.
		var needsProvidingInterface = new HashSet<String>(); // Set of components, which need to provide an interface circle.
		var connections = "";
		var builder = new StringBuilder();
		builder.append("digraph  {\n"
				+ "    node [shape=component];\n"
				+ "    edge [arrowhead=icurve];\n");
		for (var component : repo.getComponents__Repository()) {
			builder.append("    " + component.getEntityName() + ";");
			
			// Search for all components, which provide required interfaces of the current considered component.
			var componentsProvidingRequiredInterface = new HashSet<RepositoryComponent>();
			for (var required : component.getRequiredRoles_InterfaceRequiringEntity()) {
				if (required instanceof OperationRequiredRole) {
					var requiredRole = (OperationRequiredRole) required;
					for (var providingComs : providedInterfaceToComponent.get(requiredRole.getRequiredInterface__OperationRequiredRole())) {
						componentsProvidingRequiredInterface.add(providingComs);
					}
				}
			}
			// Create the connections.
			for (var provComponent : componentsProvidingRequiredInterface) {
				connections += "    " + component.getEntityName() + " -> " + provComponent.getEntityName() + "Prov;\n";
				needsProvidingInterface.add(provComponent.getEntityName());
			}
			connections += "\n";
		}
		// Create the actual providing interface circles.
		for (var component : needsProvidingInterface) {
			builder.append("    " + component + "Prov [shape=circle,label=\"\",height=0.2];");
			builder.append("    " + component + " -> " + component + "Prov [shape=none];");
		}
		builder.append("\n");
		builder.append(connections);
		builder.append("}\n");
		
		System.out.println(builder.toString());
	}
}
