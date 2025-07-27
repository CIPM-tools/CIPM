package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * An interface meant for classes that encapsulate resources parsed from models.
 * The main purpose of such classes is to make operations on parsed model
 * resources, and other resources parsed in the process, cleaner. <br>
 * <br>
 * Concrete implementors should consider only one primary model resource
 * (referred as model resource in methods), which contains all direct contents
 * of all model files, and grant access to it via {@link #getModelResource()}.
 * They may, however, choose to split contents that are required by the model
 * resource into separate Resources. Such Resources should also be parsed within
 * {@link #parseModelResource(Path, URI)}. They are then loaded automatically on
 * demand by the internals of EMF-Framework. Therefore,
 * {@link #loadModelResource(URI)} should only load the model resource.
 * 
 * @author Alp Torac Genc
 */
public interface IModelResourceWrapper {
	/**
	 * Parses all Java-Model files under the given directory. The parsed model
	 * resource can be accessed via {@link #getModelResource()}.
	 * 
	 * @param modelDir         A directory that contains all files of a model
	 * @param modelResourceURI The URI that the parsed model resource will reside
	 *                         at, once saved
	 * @param parser           The parser that will be used to parse the model
	 *                         resource and all other necessary resources
	 */
	public void parseModelResource(Path modelDir, URI modelResourceURI);

	/**
	 * Loads a model resource that was previously parsed.
	 * 
	 * @param modelResourceURI The URI, at which a previously parsed model resource
	 *                         resides
	 */
	public void loadModelResource(URI modelResourceURI);

	/**
	 * Saves all parsed model resources.
	 * 
	 * @return Whether all parsed resources have been saved. If no resources have
	 *         been parsed, no resources will be saved and this method returns true.
	 */
	public boolean saveResources();

	/**
	 * Deletes all parsed model resources.
	 * 
	 * @return Whether all parsed resources have been deleted. If no resources have
	 *         been parsed, no resource will be deleted and this method returns
	 *         true.
	 */
	public boolean deleteResources();

	/**
	 * Unloads all parsed model resources.
	 * 
	 * @return Whether all parsed resources have been unloaded. If no resources have
	 *         been parsed, no resource will be unloaded and this method returns
	 *         true.
	 */
	public boolean unloadResources();

	/**
	 * Only checks whether the model resource is loaded, which contains the direct
	 * contents of model files, because other resources will be automatically loaded
	 * on demand.
	 * 
	 * @return Whether the model resource is loaded
	 */
	public boolean isModelResourceLoaded();

	/**
	 * Loads all parsed model resources.
	 * 
	 * @return Whether all parsed model resources have been loaded. If no resources
	 *         have been parsed, no resource will be loaded.
	 */
	public boolean loadParsedResources();

	/**
	 * Sets the URIs of all parsed resources with respect to the given URI, which
	 * will be assigned to the parsed model resource that contains all direct
	 * contents of the model files.
	 * 
	 * @param newParsedModelResourceURI The new URI of the parsed model resource
	 *                                  ({@link #getModelResource()} in this case)
	 */
	public void setModelResourcesURI(URI newParsedModelResourceURI);

	/**
	 * @return The model resource, which contains all contents of all (directly)
	 *         parsed model files
	 */
	public Resource getModelResource();

	/**
	 * @return Whether there is a parsed model resource
	 *         ({@link #getModelResource()}) currently present in this instance.
	 */
	public boolean modelResourceExists();
}
