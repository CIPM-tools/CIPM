package cipm.consistency.commitintegration.lang.detection.strategy;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.containers.CompilationUnit;

import cipm.consistency.commitintegration.lang.detection.ComponentCandidates;
import cipm.consistency.commitintegration.lang.detection.ComponentState;

/**
 * A component detection strategy in which sets of packages are mapped to components.
 * 
 * @author Martin Armbruster
 */
public abstract class PackageBasedComponentDetectionStrategy implements ComponentDetectionStrategy {
	private static final class PackageModuleMapping {
		private String packageRegex;
		private String moduleName;
		private ComponentState moduleClassification;
		
		private PackageModuleMapping(String packageRegex, String moduleName, ComponentState moduleClassification) {
			this.packageRegex = packageRegex;
			this.moduleName = moduleName;
			this.moduleClassification = moduleClassification;
		}
	}
	
	private ArrayList<PackageModuleMapping> mappings = new ArrayList<>();
	
	protected PackageBasedComponentDetectionStrategy() {
		this.initializeMappings();
	}
	
	/**
	 * Initializes the mapping of packages to modules / components.
	 */
	protected abstract void initializeMappings();
	
	/**
	 * Adds a mapping.
	 * 
	 * @param packageRegex a regular expression which identifies the packages for a module / component.
	 * @param moduleName the name of the module / component.
	 * @param moduleClassification the type of the module / component.
	 */
	protected void addPackageModuleMapping(String packageRegex, String moduleName, ComponentState moduleClassification) {
		mappings.add(new PackageModuleMapping(packageRegex, moduleName, moduleClassification));
	}
	
	@Override
	public void detectComponent(Resource res, Path container, ComponentCandidates candidate) {
		if (!res.getContents().isEmpty()) {
			if (res.getContents().get(0) instanceof CompilationUnit) {
				var cu = (CompilationUnit) res.getContents().get(0);
				var packName = cu.getClassifiers().get(0).getPackage().getNamespacesAsString();
				mappings.forEach(map -> {
					if (packName.matches(map.packageRegex)) {
						candidate.addModuleClassifier(map.moduleClassification, map.moduleName, res);
					}
				});
			}
		}
	}
}
