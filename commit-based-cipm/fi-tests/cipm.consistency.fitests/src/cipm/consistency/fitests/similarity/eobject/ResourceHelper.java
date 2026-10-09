package cipm.consistency.fitests.similarity.eobject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * A class that encapsulates the means to create {@link Resource} instances.
 * 
 * @author Alp Torac Genc
 */
public class ResourceHelper {
	private static final Logger LOGGER = Logger.getLogger("cipm." + ResourceHelper.class.getSimpleName());

	/**
	 * The directory, where the created {@link Resource} instances will be stored,
	 * if they are saved.
	 */
	private String resourceSaveRootPath;

	/**
	 * The extension of {@link Resource} files, if they are saved.
	 */
	private String resourceFileExtension;

	/**
	 * The map that keeps track of the mapping inserted into
	 * {@link Resource.Factory.Registry}. Can be used to clean such mappings from
	 * the registry at the end of tests.
	 */
	private Map<String, Object> registryMappings = new HashMap<String, Object>();

	/**
	 * The list of created {@link Resource} instances. Can be used to perform clean
	 * up after tests.
	 */
	private List<Resource> createdResources = new ArrayList<Resource>();

	/**
	 * @return The {@link Logger} that can be used to log happenings in this
	 *         instance.
	 */
	protected Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Creates and returns a {@link Resource} instance, whose URI will be the given
	 * one. <br>
	 * <br>
	 * Does not save the created {@link Resource} instance.
	 */
	protected Resource initResource(URI resUri) {
		ResourceSet rSet = new ResourceSetImpl();
		return rSet.createResource(resUri);
	}

	/**
	 * Sets the directory, where the created {@link Resource} instances will be
	 * stored, if they are saved.
	 */
	public void setResourceSaveRootPath(String resourceSaveRootPath) {
		this.resourceSaveRootPath = resourceSaveRootPath;
	}

	/**
	 * Sets the extension of {@link Resource} files, if they are saved.
	 */
	public void setResourceFileExtension(String resourceFileExtension) {
		this.removeFromRegistry(this.resourceFileExtension);
		this.resourceFileExtension = resourceFileExtension;
		this.setResourceRegistry();
	}

	/**
	 * @return The extension of the {@link Resource} files.
	 */
	public String getResourceFileExtension() {
		return this.resourceFileExtension;
	}

	/**
	 * @return The directory, where the created {@link Resource} instances will be
	 *         stored, if they are saved.
	 */
	public String getResourceSaveRootPath() {
		return resourceSaveRootPath;
	}

	/**
	 * Complements {@link #getResourceSaveRootPath()} with the {@link Resource} file
	 * name and extension. The said file will only be created, if the
	 * {@link Resource} file is saved.
	 * 
	 * @param resourceFileName      The name of the file
	 * @param resourceFileExtension The extension of the file
	 * @return The {@link URI} for a {@link Resource} instance.
	 */
	protected URI createURI(String resourceFileName, String resourceFileExtension) {
		return URI.createFileURI(
				this.getResourceSaveRootPath() + File.separator + resourceFileName + "." + resourceFileExtension);
	}

	/**
	 * The variant of {@link #createURI(String, String)}, which uses
	 * {@link #getResourceFileExtension()}.
	 */
	protected URI createURI(String resourceName) {
		return this.createURI(resourceName, this.getResourceFileExtension());
	}

	/**
	 * @return The name of the {@link Resource} file with the count parameter added
	 *         to it.
	 */
	protected String getResourceNameWithCount(String resourceName, int count) {
		return resourceName + "-" + count;
	}

	/**
	 * @return Computes a unique name for the {@link Resource} file, so that it is
	 *         not overwritten if another resource file with the same name is to be
	 *         created.
	 */
	protected String computeEffectiveResourceName(String resourceName) {
		var resourceRoot = new File(this.getResourceSaveRootPath());

		var count = 0;

		if (resourceRoot.exists()) {
			var files = List.of(
					List.of(resourceRoot.listFiles()).stream().map((file) -> file.getName()).toArray(String[]::new));

			while (files.contains(this.getResourceNameWithCount(resourceName, count))) {
				count++;
			}
		}

		return this.getResourceNameWithCount(resourceName, count);
	}

	/**
	 * Creates a {@link Resource} instance for the given EObject instances. The
	 * Resource instances created with this method are tracked, so that they can be
	 * deleted later if necessary. <br>
	 * <br>
	 * <b>!!! IMPORTANT !!!</b> <br>
	 * <br>
	 * <b>Using this method will cause the logger to log an error message, if some
	 * of the EObject instances (from eos) that are already in a Resource instance
	 * are attempted to be placed into another Resource. This should be avoided,
	 * since doing so will REMOVE the said EObject instances from their former
	 * Resource and cause side effects in tests.</b>
	 */
	public Resource createResource(Collection<? extends EObject> eos, String resourceName) {
		Resource res = this.initResource(this.createURI(this.computeEffectiveResourceName(resourceName)));
		this.createdResources.add(res);

		if (eos != null) {
			for (var eo : eos) {

				/*
				 * Make sure to not add an EObject, which has already been added to a Resource,
				 * to another Resource. Doing so will detach it from its former Resource and add
				 * it to the second one.
				 */
				if (eo.eResource() != null) {
					this.getLogger().error("An EObject's resource was set and shifted during resource creation");
				}
				res.getContents().add(eo);
			}
		}

		return res;
	}

	/**
	 * Puts the necessary mapping for saving {@link Resource} files with the given
	 * file extension into {@link Resource.Factory.Registry}. The entry is also
	 * tracked so that it can be deleted later, if necessary.
	 */
	protected void setResourceRegistry() {
		var resFileExtension = this.getResourceFileExtension();
		var fac = new XMIResourceFactoryImpl();

		this.registryMappings.put(resFileExtension, fac);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(resFileExtension, fac);
	}

	/**
	 * Unloads the given {@link Resource} instance.
	 */
	public void unloadResource(Resource res) {
		res.unload();
	}

	/**
	 * Unloads and deletes all created {@link Resource} instances, if they are
	 * created with {@link #createResource(Collection)}. Stops tracking them as
	 * well.
	 */
	public void cleanAllResources() {
		this.createdResources.forEach((r) -> {
			this.unloadResource(r);

			try {
				r.delete(null);
			} catch (IOException e) {
				this.getLogger().debug("Resource either was not created as a file or has already been deleted: "
						+ r.getURI().toString());
			}
		});

		this.createdResources.clear();
	}

	/**
	 * Deletes the directory that contains all {@link Resource} instances, if it is
	 * empty.
	 */
	public void deleteResourceDir() {
		new File(this.getResourceSaveRootPath()).delete();
	}

	/**
	 * Removes the entry matching to the given {@code resourceFileExtension} from
	 * the resource factory, if it was added by this instance. Stops tracking the
	 * said entry.
	 */
	protected void removeFromRegistry(String resourceFileExtension) {
		if (resourceFileExtension == null)
			return;

		var reg = Resource.Factory.Registry.INSTANCE;
		var regMap = reg.getExtensionToFactoryMap();

		if (regMap.containsKey(resourceFileExtension)) {
			var val = regMap.get(resourceFileExtension);

			if (this.registryMappings.containsKey(resourceFileExtension)) {
				var valTracked = this.registryMappings.get(resourceFileExtension);

				if (val.equals(valTracked)) {
					regMap.remove(val);
					this.registryMappings.remove(valTracked);
				}
			}
		}
	}

	/**
	 * Cleans the mapping(s) in {@link Resource.Factory.Registry} inserted by
	 * {@link #setResourceRegistry(String)}. Stops tracking them as well.
	 */
	protected void cleanRegistry() {
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;

		for (var key : this.registryMappings.keySet()) {
			reg.getExtensionToFactoryMap().remove(key);
		}

		this.registryMappings.clear();
	}

	/**
	 * Cleans up the saved attributes and created {@link Resource} files.
	 */
	public void clean() {
		this.cleanRegistry();
		this.cleanAllResources();
		this.deleteResourceDir();
	}
}
