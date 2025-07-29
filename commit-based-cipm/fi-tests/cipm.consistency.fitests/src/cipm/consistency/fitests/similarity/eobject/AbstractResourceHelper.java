package cipm.consistency.fitests.similarity.eobject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import cipm.consistency.fitests.similarity.ILoggable;

/**
 * An abstract class that is meant to be implemented by classes, which
 * encapsulate basic operations on {@link Resource} instances.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractResourceHelper implements ILoggable {
	/**
	 * The extension of {@link Resource} files, if they are saved.
	 */
	private String resourceFileExtension;

	/**
	 * Constructs an instance with the foreseen initial resource registry entries.
	 * 
	 * @see {@link #setInitialResourceRegistries()}
	 */
	public AbstractResourceHelper() {
		this.setInitialResourceRegistries();
	}

	/**
	 * @return The resource registry, which will be modified by this instance.
	 */
	private Resource.Factory.Registry getResourceRegistry() {
		return Resource.Factory.Registry.INSTANCE;
	}

	/**
	 * Sets all resource registries foreseen for this instance.
	 */
	public abstract void setInitialResourceRegistries();

	/**
	 * Sets the extension of {@link Resource} files, if they are saved.
	 */
	public void setResourceFileExtension(String resourceFileExtension) {
		this.resourceFileExtension = resourceFileExtension;
	}

	/**
	 * @return The extension of the {@link Resource} files.
	 */
	public String getResourceFileExtension() {
		return this.resourceFileExtension;
	}

	/**
	 * @return An empty {@link ResourceSetImpl}
	 */
	public ResourceSet createResourceSet() {
		return new ResourceSetImpl();
	}

	/**
	 * Creates a {@link Resource} instance within the given resource set rSet, for
	 * the given EObject instances eos (can be null), with the given URI resURI.
	 * <br>
	 * <br>
	 * <b>!!! IMPORTANT !!!</b> <br>
	 * <br>
	 * <b>Using this method will cause the logger to log an error message, if some
	 * of the EObject instances (from eos) that are already in a Resource instance
	 * are attempted to be placed into another Resource. This should be avoided,
	 * since doing so will REMOVE the said EObject instances from their former
	 * Resource and cause side effects in tests.</b>
	 */
	public Resource createResource(Collection<? extends EObject> eos, ResourceSet rSet, URI resURI) {
		var res = rSet.createResource(resURI);

		if (eos != null) {
			for (var eo : eos) {

				/*
				 * Make sure to not add an EObject, which has already been added to a Resource,
				 * to another Resource. Doing so will detach it from its former Resource and add
				 * it to the second one.
				 */
				if (eo.eResource() != null) {
					this.logErrorMsg("An EObject's resource was set and shifted during resource creation");
				}
				res.getContents().add(eo);
			}
		}

		return res;
	}

	/**
	 * Adds the extension to factory mapping into
	 * {@link Resource.Factory.Registry}.<br>
	 * <br>
	 * Said entry denotes that resources with the given extension are saved using
	 * the given factory.
	 * 
	 * @see {@link #setDefaultResourceRegistry()}
	 */
	public void setResourceRegistry(String extension, Object factory) {
		this.getResourceRegistry().getExtensionToFactoryMap().put(extension, factory);
	}

	/**
	 * Attempts to save the given resource instance. Instead of throwing exceptions,
	 * returns true/false to indicate success/failure.
	 */
	public boolean saveResource(Resource res) {
		var uri = res.getURI();
		if (uri.isFile()) {
			try {
				res.save(null);
				return this.resourceFileExists(uri);
			} catch (IOException excep) {
				excep.printStackTrace();
				return this.resourceFileExists(uri);
			}
		}
		return this.resourceFileExists(uri);
	}

	/**
	 * Attempts to save the given resource instance. Instead of throwing exceptions,
	 * returns true/false to indicate success/failure.
	 */
	public boolean saveResourceIfNotSaved(Resource res) {
		var uri = res.getURI();
		if (uri.isFile() && !this.resourceFileExists(uri)) {
			return this.saveResource(res);
		}
		return this.resourceFileExists(uri);
	}

	/**
	 * Loads the given resource
	 */
	public void loadResource(Resource res) {
		try {
			this.logDebugMsg(String.format("Loading resource at: %s", res.getURI()));
			res.load(null);
			this.logDebugMsg(String.format("Loaded %s", res.getURI()));
		} catch (IOException e) {
			e.printStackTrace();
			this.logInfoMsg(String.format("Could not load resource at: %s", res.getURI()));
		}
	}

	/**
	 * @return A resource instance, which has the contents of the saved resource
	 *         file at the given URI
	 */
	public Resource loadResource(URI resourceURI) {
		Resource res = null;

		if (resourceURI.isFile() && new File(resourceURI.toFileString()).exists()) {
			res = this.createResource(resourceURI);
			this.loadResource(res);
		}

		return res;
	}

	/**
	 * @return The loaded resource located at the given path
	 */
	public Resource loadResource(Path resourcePath) {
		return this.loadResource(URI.createFileURI(resourcePath.toString()));
	}

	/**
	 * @param resSet      The resource ste, which will contain the created resource
	 * @param resourceURI The URI, where the resource points at
	 * @return An empty resource inside the given resource set, with the given URI
	 */
	public Resource createResource(ResourceSet resSet, URI resourceURI) {
		return this.createResource(null, resSet, resourceURI);
	}

	/**
	 * @param resourceURI The URI, where the resource points at
	 * @return An empty resource, inside a freshly created resource set, with the
	 *         given URI
	 */
	public Resource createResource(URI resourceURI) {
		return this.createResource(this.createResourceSet(), resourceURI);
	}

	/**
	 * Unloads the given {@link Resource} instance.
	 */
	public boolean unloadResource(Resource res) {
		res.unload();
		return !res.isLoaded();
	}

	/**
	 * @param resURI The URI that points at the potentially existing resource file.
	 * @return Whether the resource file exists. Will return false if the given URI
	 *         does not point at a file, regardless of whether the resource exists.
	 */
	public boolean resourceFileExists(URI resURI) {
		return resURI.isFile() && new File(resURI.toFileString()).exists();
	}

	/**
	 * Deletes the given resource
	 * 
	 * @return Whether the file of the given resource is deleted.
	 */
	public boolean deleteResource(Resource res) {
		var uri = res.getURI();
		if (this.resourceFileExists(uri)) {
			try {
				res.delete(null);
				return !this.resourceFileExists(uri);
			} catch (IOException e) {
				var isResourceDeleted = !this.resourceFileExists(uri);
				this.logInfoMsg(String.format("Could not delete resource as expected: %s (is deleted: %s) %s %s",
						res.getURI().toString(), isResourceDeleted, System.lineSeparator(), e.getMessage()));
				return isResourceDeleted;
			}
		}
		return !this.resourceFileExists(uri);
	}

	/**
	 * Removes the entry matching to the given {@code resourceFileExtension} from
	 * the resource factory.
	 */
	public void removeFromRegistry(String resourceFileExtension) {
		if (resourceFileExtension == null)
			return;

		var regMap = this.getResourceRegistry().getExtensionToFactoryMap();
		regMap.remove(resourceFileExtension);
	}
}
