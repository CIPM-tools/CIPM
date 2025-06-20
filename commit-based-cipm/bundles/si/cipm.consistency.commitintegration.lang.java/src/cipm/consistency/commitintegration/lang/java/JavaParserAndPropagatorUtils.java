package cipm.consistency.commitintegration.lang.java;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.types.PrimitiveType;

import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import jamopp.options.ParserOptions;
import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import jamopp.recovery.trivial.TrivialRecovery;

/**
 * A utility class for the integration and change propagation of Java code into
 * Vitruvius.
 * 
 * @author Martin Armbruster
 */
public final class JavaParserAndPropagatorUtils {
	private static final Logger LOGGER = Logger.getLogger("cipm." + JavaParserAndPropagatorUtils.class.getSimpleName());
	private static Configuration config = new Configuration(true);

	private JavaParserAndPropagatorUtils() {
	}

	/**
	 * Parses all Java code and creates one Resource with all models.
	 * 
	 * @param dir       directory in which the Java code resides.
	 * @param target    target file of the Resource with all models.
	 * @param modConfig file which contains the stored module configuration.
	 * @return the Resource with all models.
	 */
	public static Resource parseJavaCodeIntoOneModel(Path dir, Path target, Path modConfig, ComponentDetector detector) {
		// 1. Parse the code.
		ParserOptions.CREATE_LAYOUT_INFORMATION.setValue(Boolean.FALSE);
		ParserOptions.REGISTER_LOCAL.setValue(Boolean.TRUE);
		if (config.resolveAll) {
			ParserOptions.RESOLVE_EVERYTHING.setValue(Boolean.TRUE);
			ParserOptions.RESOLVE_ALL_BINDINGS.setValue(Boolean.TRUE);
		} else {
			ParserOptions.RESOLVE_ALL_BINDINGS.setValue(Boolean.FALSE);
			ParserOptions.RESOLVE_EVERYTHING.setValue(Boolean.FALSE);
		}
		
		JaMoPPJDTSingleFileParser parser = new JaMoPPJDTSingleFileParser();
		parser.setResourceSet(new ResourceSetImpl());
		parser.setExclusionPatterns(CommitIntegrationSettingsContainer.getSettingsContainer()
				.getProperty(SettingKeys.JAVA_PARSER_EXCLUSION_PATTERNS).split(";"));
		LOGGER.debug("Parsing " + dir.toString());
		ResourceSet resourceSet = parser.parseDirectory(dir);
		
		if (!config.resolveAll) {
			// Wrap all primitive types to ensure that their wrapper classes are loaded.
			for (var resource : new ArrayList<>(resourceSet.getResources())) {
				resource.getAllContents().forEachRemaining(obj -> {
					if (obj instanceof PrimitiveType) {
						var type = (PrimitiveType) obj;
						type.wrapPrimitiveType();
					}
				});
			}
			new TrivialRecovery(resourceSet).recover();
		}
		
		LOGGER.debug("Parsed " + resourceSet.getResources().size() + " files.");

		// 2. Filter the resources and create modules for components.
		detector.detectModules(resourceSet, dir.toAbsolutePath(), modConfig);

		// 3. Create one resource with all Java models.
		LOGGER.debug("Creating one resource with all Java models.");
		ResourceSet next = new ResourceSetImpl();
		Resource all = next.createResource(URI.createFileURI(target.toAbsolutePath().toString()));
		for (Resource r : new ArrayList<>(resourceSet.getResources())) {
			all.getContents().addAll(r.getContents());
		}
		all.getContents().forEach(content -> JavaClasspath.get().registerJavaRoot((JavaRoot) content, all.getURI()));
		return all;
	}
	
	/**
	 * Sets the configuration for the Java parsing and module / component detection.
	 * 
	 * @param config the configuration.
	 */
	public static void setConfiguration(Configuration config) {
		JavaParserAndPropagatorUtils.config = config;
	}
	
	public static class Configuration {
		private boolean resolveAll;

		/**
		 * Creates a new instance.
		 * 
		 * @param resolveAll true if all dependencies for the Java code are available and should be parsed into models.
		 *                         Otherwise, only direct dependencies are resolved.
		 */
		public Configuration(boolean resolveAll) {
			this.resolveAll = resolveAll;
		}
	}
}
