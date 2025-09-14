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

import cipm.consistency.fitests.similarity.SimilarityTestLogger;

/**
 * A utility class that provides basic operations on {@link Resource} instances.
 * 
 * @author Alp Torac Genc
 */
public class ResourceHelper {
	/**
	 * @return The resource registry, which will be modified by this instance.
	 */
	private static Resource.Factory.Registry getResourceRegistry() {
		return Resource.Factory.Registry.INSTANCE;
	}

	/**
	 * @return An empty {@link ResourceSetImpl}
	 */
	public static ResourceSet createResourceSet() {
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
	public static Resource createResource(Collection<? extends EObject> eos, ResourceSet rSet, URI resURI) {
		var res = rSet.createResource(resURI);

		if (eos != null) {
			for (var eo : eos) {

				/*
				 * Make sure to not add an EObject, which has already been added to a Resource,
				 * to another Resource. Doing so will detach it from its former Resource and add
				 * it to the second one.
				 */
				if (eo.eResource() != null) {
					SimilarityTestLogger.logErrorMsg(
							"An EObject's resource was set and shifted during resource creation", ResourceHelper.class);
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
	 * Said entry denotes that resources with the given extension are created using
	 * the given factory.
	 * 
	 * @see {@link #setDefaultResourceRegistry()}
	 */
	public static void setResourceRegistry(String extension, Object factory) {
		getResourceRegistry().getExtensionToFactoryMap().put(extension, factory);
	}

	/**
	 * Attempts to save the given resource instance. Instead of throwing exceptions,
	 * returns true/false to indicate success/failure.
	 * 
	 * TODO Log the error message
	 */
	public static boolean saveResource(Resource res) {
		var uri = res.getURI();
		if (uri.isFile()) {
			try {
				res.save(null);
				return resourceFileExists(uri);
			} catch (IOException excep) {
				excep.printStackTrace();
				return resourceFileExists(uri);
			}
		}
		return resourceFileExists(uri);
	}

	/**
	 * Attempts to save the given resource instance. Instead of throwing exceptions,
	 * returns true/false to indicate success/failure.
	 */
	public static boolean saveResourceIfNotSaved(Resource res) {
		var uri = res.getURI();
		if (uri.isFile() && !resourceFileExists(uri)) {
			return saveResource(res);
		}
		return resourceFileExists(uri);
	}

	/**
	 * Loads the given resource
	 * 
	 * TODO Log error message
	 */
	public static void loadResource(Resource res) {
		try {
			SimilarityTestLogger.logDebugMsg(String.format("Loading resource at: %s", res.getURI()),
					ResourceHelper.class);
			res.load(null);
			SimilarityTestLogger.logDebugMsg(String.format("Loaded %s", res.getURI()), ResourceHelper.class);
		} catch (IOException e) {
			e.printStackTrace();
			SimilarityTestLogger.logInfoMsg(String.format("Could not load resource at: %s", res.getURI()),
					ResourceHelper.class);
		}
	}

	/**
	 * @return A resource instance, which has the contents of the saved resource
	 *         file at the given URI
	 */
	public static Resource loadResource(URI resourceURI) {
		Resource res = null;

		if (resourceURI.isFile() && new File(resourceURI.toFileString()).exists()) {
			res = createResource(resourceURI);
			loadResource(res);
		}

		return res;
	}

	/**
	 * @return The loaded resource located at the given path
	 */
	public static Resource loadResource(Path resourcePath) {
		return loadResource(URI.createFileURI(resourcePath.toString()));
	}

	/**
	 * @param resSet      The resource set, which will contain the created resource
	 * @param resourceURI The URI, where the resource points at
	 * @return An empty resource inside the given resource set, with the given URI
	 */
	public static Resource createResource(ResourceSet resSet, URI resourceURI) {
		return createResource(null, resSet, resourceURI);
	}

	/**
	 * @param resourceURI The URI, where the resource points at
	 * @return An empty resource inside a freshly created resource set, with the
	 *         given URI
	 */
	public static Resource createResource(URI resourceURI) {
		return createResource(createResourceSet(), resourceURI);
	}

	/**
	 * Unloads the given {@link Resource} instance.
	 */
	public static boolean unloadResource(Resource res) {
		res.unload();
		return !res.isLoaded();
	}

	/**
	 * @param resURI The URI that points at the potentially existing resource file.
	 * @return Whether the resource file exists. Will return false if the given URI
	 *         does not point at a file, regardless of whether the resource exists.
	 */
	public static boolean resourceFileExists(URI resURI) {
		return resURI.isFile() && new File(resURI.toFileString()).exists();
	}

	/**
	 * Deletes the given resource
	 * 
	 * TODO Log error message
	 * 
	 * @return Whether the file of the given resource is deleted.
	 */
	public static boolean deleteResource(Resource res) {
		var uri = res.getURI();
		if (resourceFileExists(uri)) {
			try {
				res.delete(null);
				return !resourceFileExists(uri);
			} catch (IOException e) {
				var isResourceDeleted = !resourceFileExists(uri);
				SimilarityTestLogger.logInfoMsg(
						String.format("Could not delete resource as expected: %s (is deleted: %s) %s %s",
								res.getURI().toString(), isResourceDeleted, System.lineSeparator(), e.getMessage()),
						ResourceHelper.class);
				return isResourceDeleted;
			}
		}
		return !resourceFileExists(uri);
	}

	/**
	 * Removes the entry matching to the given {@code resourceFileExtension} from
	 * the resource factory.
	 */
	public static void removeFromRegistry(String resourceFileExtension) {
		if (resourceFileExtension == null)
			return;

		var regMap = getResourceRegistry().getExtensionToFactoryMap();
		regMap.remove(resourceFileExtension);
	}
}
