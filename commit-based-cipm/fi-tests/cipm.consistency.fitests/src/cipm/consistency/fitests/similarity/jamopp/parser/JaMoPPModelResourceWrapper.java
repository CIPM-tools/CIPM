package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import cipm.consistency.fitests.similarity.SimilarityTestLogger;
import cipm.consistency.fitests.similarity.eobject.ResourceHelper;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPResourceParsingStrategy;

/**
 * A class that wraps a (merged) Java model resource, which is either already
 * parsed or is to be parsed. It encapsulates the desired model resource, as
 * well as all other resources it requires:
 * <ul>
 * <li>Merged model resource: Contains all direct contents of the model files
 * (i.e. the Java code directly present in model files)
 * <li>Artificial resource: Contains all contents that are required by the
 * merged model resource, but are not directly present in model source files,
 * such as contents of native Java libraries and synthetic elements. Whether
 * Artificial resource is split from merged resource is controlled with
 * {@link #setSplitArtificialResource(boolean)}.
 * </ul>
 * The main purpose of this class is to make operations on parsed model
 * resources, and other resources parsed in the process, tidier.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPModelResourceWrapper implements IModelResourceWrapper {
	/**
	 * @see {@link #isResolveAllProxies()}
	 */
	private boolean resolveAllProxies = false;
	/**
	 * @see {@link #isSplitArtificialResource()}
	 */
	private boolean splitArtificialResource = false;

	/**
	 * The name of the ArtificialResource (i.e. the last segment of its URI) without
	 * file extension
	 */
	private static final String artificialResourceName = "ArtificialResource";

	/**
	 * @see {@link #JaMoPPModelResourceWrapper(AbstractResourceHelper, JaMoPPResourceParsingStrategy)}
	 */
	private JaMoPPResourceParsingStrategy modelResourceParsingStrat;

	/**
	 * @see {@link #getModelResource()}
	 */
	private Resource mergedModelResource;
	/**
	 * @see {@link #prepareArtificialResource(ResourceSet, List, URI)}
	 */
	private Resource artificialResource;

	/**
	 * Constructs an instance.
	 * 
	 * @param modelResourceParsingStrat The parser that will be used to parse the
	 *                                  model resource and all other necessary
	 *                                  resources
	 */
	public JaMoPPModelResourceWrapper(JaMoPPResourceParsingStrategy modelResourceParsingStrat) {
		this.modelResourceParsingStrat = modelResourceParsingStrat;
	}

	/**
	 * Constructs an instance.
	 */
	public JaMoPPModelResourceWrapper() {
		this(null);
	}

	/**
	 * @param correspondingModelResourceFileNameWithoutExt The name of the model
	 *                                                     resource, whose
	 *                                                     corresponding
	 *                                                     ArtificialResource's name
	 *                                                     (without file extension)
	 *                                                     is to be computed
	 * @return The name of the ArtificialResource corresponding to the model
	 *         resource with the given name.
	 */
	protected String getArtificialResourceFileName(String correspondingModelResourceFileNameWithoutExt) {
		return correspondingModelResourceFileNameWithoutExt + artificialResourceName + "."
				+ this.modelResourceParsingStrat.getResourceFileExtension();
	}

	/**
	 * @param correspondingModelResourceURI The URI of the model resource, whose
	 *                                      ArtificialResource's URI is to be
	 *                                      computed
	 * @return The URI of the ArtificialResource of the model resource with the
	 *         given URI
	 */
	protected URI getArtificialResourceURI(URI correspondingModelResourceURI) {
		var fileNameWithoutExt = correspondingModelResourceURI.trimFileExtension().lastSegment();
		var arName = this.getArtificialResourceFileName(fileNameWithoutExt);
		var arURI = correspondingModelResourceURI.trimSegments(1);
		return arURI.appendSegment(arName);
	}

	/**
	 * Creates and prepares an ArtificialResource for modelResourceSet, according to
	 * {@link JaMoPPResourceParsingStrategy#performTrivialRecovery(ResourceSet)}.
	 * The created ArtificialResource will contain synthetic model elements for the
	 * proxy objects within directModelResources, as well as the native Java library
	 * resources that are needed by modelResourceSet. <br>
	 * <br>
	 * If {@link #isSplitArtificialResource()}, moves all non-direct model resources
	 * (i.e. model resources that are not a part of directModelResources), into the
	 * created ArtificialResource. This way, direct model resource contents inside
	 * directModelResources can be compared more efficiently, since the non-direct
	 * model resources will likely be excluded from the comparison.
	 * 
	 * @param modelResourceSet      The model resource set that was the result of
	 *                              parsing a model. An ArtificialResource will be
	 *                              created for this ResourceSet, within this
	 *                              ResourceSet.
	 * @param directModelResources  A list of model resources, which were directly
	 *                              parsed from model source files. This list should
	 *                              not include any dependency, which is not a
	 *                              direct part of the model (such as Java native
	 *                              libraries).
	 * @param artificialResourceURI The URI, which the created ArtificialResource
	 *                              will have. ArtificialResource will be saved at
	 *                              that URI, if desired.
	 * @return The created ArtificialResource for modelResourceSet
	 */
	protected Resource prepareArtificialResource(ResourceSet modelResourceSet, List<Resource> directModelResources,
			URI artificialResourceURI) {
		// Create the ArtificialResource
		modelResourceParsingStrat.performTrivialRecovery(modelResourceSet);

		Resource artificialResourceForModelResSet = null;

		if (isSplitArtificialResource()) {
			artificialResourceForModelResSet = modelResourceSet.getResources().stream()
					.filter((r) -> r.getURI().toString().contains(artificialResourceName)).findFirst().orElse(null);

			if (artificialResourceForModelResSet != null) {
				artificialResourceForModelResSet.setURI(artificialResourceURI);

				SimilarityTestLogger.logDebugMsg(String.format("ArtificialResource is parsed and has its URI set to %s",
						artificialResourceForModelResSet.getURI()), this.getClass());

				// Use an array to avoid modifications while iterating, which lead to exceptions
				var resArr = modelResourceSet.getResources().toArray(Resource[]::new);

				/*
				 * Iterate over all resources under modelResourceSet and look for resources of
				 * native Java libraries. Place each such resource's contents into the
				 * ArtificialResource and remove the native Java library resource from
				 * modelResourceSet (as it will be empty afterward). This moves all
				 * CompilationUnits housing the Classifiers required by the parsed model
				 * resource into ArtificialResource.
				 */
				for (int i = 0; i < resArr.length; i++) {
					var r = resArr[i];
					if (!r.getURI().isFile() && r != artificialResourceForModelResSet
							&& !directModelResources.contains(r)) {
						SimilarityTestLogger.logDebugMsg(
								String.format("Adding Resource %s to ArtificialResource", r.getURI()), this.getClass());
						artificialResourceForModelResSet.getContents().addAll(r.getContents());
						SimilarityTestLogger.logDebugMsg(
								String.format("Added Resource %s to ArtificialResource", r.getURI()), this.getClass());
						modelResourceSet.getResources().remove(r);
						SimilarityTestLogger.logDebugMsg(
								String.format("Removed (empty) Resource %s from ResourceSet", r.getURI()),
								this.getClass());
					}
				}

				// "-2" to exclude modelResource and artificialResource from resource count
				SimilarityTestLogger.logDebugMsg(
						String.format("%d/%d resources have been added to ArtificialResource",
								(resArr.length - modelResourceSet.getResources().size()) - 2, resArr.length - 2),
						this.getClass());

				// Do not handle potential proxies in ArtificialResource, because they belong to
				// internals of native classes, which are irrelevant for the model. Normally
				// there should be no proxies, if the code represented in the model resource is
				// valid.
			}
		}

		return artificialResourceForModelResSet;
	}

	/**
	 * Parses all Java-Model source files under the given model source file
	 * directory into a {@link Resource} instance (merged model resource). Uses no
	 * means of caching. The parsed merged model resource can be accessed via
	 * {@link #getModelResource()}. <br>
	 * <br>
	 * <b>Note: This method will parse ALL such files. Therefore, the given model
	 * source file directory should only contain one Java-Model.</b>
	 * 
	 * @param modelDir         A model source file directory that contains all files
	 *                         of a model
	 * @param modelResourceURI The URI that the parsed model resource will reside
	 *                         at, once saved
	 */
	@Override
	public void parseModelResource(Path modelDir, URI modelResourceURI) {
		var modelResourceSet = ResourceHelper.createResourceSet();

		// Parser returns the same ResourceSet it was previously given
		// via setResourceSet(...)
		modelResourceSet = modelResourceParsingStrat.parseModelResource(modelDir);

		var resCount = modelResourceSet.getResources().size();
		SimilarityTestLogger.logDebugMsg(String.format("%d resources have been parsed under %s", resCount, modelDir),
				this.getClass());

		// Find the model resource (i.e. the resource that contains the direct contents
		// of model files)
		var directModelResources = modelResourceSet.getResources().stream()
				.filter((r) -> r.getURI().isFile() && r.getURI().toFileString().contains(modelDir.toString()))
				.collect(Collectors.toList());

		if (isResolveAllProxies()) {
			/*
			 * Attempt to resolve potential proxies that can be resolved prior to
			 * TrivialRecovery, so that it constructs less synthetic elements that are
			 * redundant.
			 * 
			 * This is necessary, because synthetic elements' type can vary and can cause
			 * typing issues during similarity checking, as the (cached) model resource will
			 * use the synthetic elements, even though they are present directly in the
			 * model resource.
			 * 
			 * Examples to this are LocalVariableStatements; which are declared within the
			 * model, are accessible and referenced by IdentifierReferences. Due to the
			 * absence of context information during parsing, they are considered Fields,
			 * unless they are resolved (via EcoreUtil.resolveAll(...) for instance)
			 * directly after being parsed. Not resolving them causes the
			 * IdentifierReferences to point at their synthetic element correspondents
			 * (Fields), as opposed to their declaration in the model resource.
			 */
			for (var r : directModelResources) {
				EcoreUtil.resolveAll(r);
			}
		}

		mergedModelResource = ResourceHelper.createResource(modelResourceURI);

		artificialResource = this.prepareArtificialResource(modelResourceSet, directModelResources,
				this.getArtificialResourceURI(modelResourceURI));

		SimilarityTestLogger.logDebugMsg(String.format("Merging non-ArtificialResources"), this.getClass());

		for (var r : modelResourceSet.getResources()) {
			if (r != artificialResource) {
				SimilarityTestLogger.logDebugMsg(String.format("Including %s into the merged resource", r.getURI()),
						this.getClass());
				mergedModelResource.getContents().addAll(r.getContents());
				SimilarityTestLogger.logDebugMsg(String.format("Included %s into the merged resource", r.getURI()),
						this.getClass());
			}
		}

		SimilarityTestLogger.logDebugMsg(String.format("Merged non-ArtificialResources"), this.getClass());

		SimilarityTestLogger.logDebugMsg(String.format("%s parsed (uncached)", modelDir), this.getClass());

		// Add ArtificialResource to mergedResource's resource set, so that it can be
		// found by the model resource's contents that have been moved
		if (artificialResource != null) {
			mergedModelResource.getResourceSet().getResources().add(artificialResource);
		}
	}

	/**
	 * @implSpec Loads the resource with the given URI as
	 *           {@link #getModelResource()}
	 */
	@Override
	public void loadModelResource(URI modelResourceURI) {
		this.mergedModelResource = ResourceHelper.loadResource(modelResourceURI);
	}

	/**
	 * @implSpec Saves both {@link #getModelResource()} and the ArtificialResource
	 *           (if existent, see {@link JaMoPPModelResourceWrapper})
	 */
	@Override
	public boolean saveResources() {
		var result = true;
		if (mergedModelResource != null) {
			SimilarityTestLogger.logDebugMsg("Merged model resource exists, saving it now", this.getClass());
			result = ResourceHelper.saveResourceIfNotSaved(mergedModelResource);
			SimilarityTestLogger.logDebugMsg(String.format("%s merged model resource at %s",
					result ? "Saved" : "Could not save", mergedModelResource.getURI()), this.getClass());
		}
		if (artificialResource != null) {
			SimilarityTestLogger.logDebugMsg("Artificial resource exists, saving it now", this.getClass());
			result = result && ResourceHelper.saveResourceIfNotSaved(artificialResource);
			SimilarityTestLogger.logDebugMsg(String.format("%s artificial resource at %s",
					result ? "Saved" : "Could not save", artificialResource.getURI()), this.getClass());
		}
		return result;
	}

	/**
	 * @implSpec Deletes both {@link #getModelResource()} and the ArtificialResource
	 *           (if existent, see {@link JaMoPPModelResourceWrapper})
	 */
	@Override
	public boolean deleteResources() {
		var result = false;
		if (mergedModelResource != null) {
			SimilarityTestLogger.logDebugMsg("Merged model resource exists, deleting it now", this.getClass());
			result = ResourceHelper.deleteResource(mergedModelResource);
			SimilarityTestLogger.logDebugMsg(String.format("%s merged model resource at %s",
					result ? "Deleted" : "Could not delete", mergedModelResource.getURI()), this.getClass());

		}
		if (artificialResource != null) {
			SimilarityTestLogger.logDebugMsg("Artificial resource exists, deleting it now", this.getClass());
			result = result && ResourceHelper.deleteResource(artificialResource);
			SimilarityTestLogger.logDebugMsg(String.format("%s artificial resource at %s",
					result ? "Saved" : "Could not save", artificialResource.getURI()), this.getClass());
		}
		return result;
	}

	/**
	 * @implSpec Unloads both {@link #getModelResource()} and the ArtificialResource
	 *           (if existent, see {@link JaMoPPModelResourceWrapper})
	 */
	@Override
	public boolean unloadResources() {
		var result = false;
		if (mergedModelResource != null) {
			SimilarityTestLogger.logDebugMsg("Merged model resource exists, unloading it now", this.getClass());
			result = ResourceHelper.unloadResource(mergedModelResource);
			SimilarityTestLogger.logDebugMsg(String.format("%s merged model resource at %s",
					result ? "Unloaded" : "Could not unload", mergedModelResource.getURI()), this.getClass());

		}
		if (artificialResource != null) {
			SimilarityTestLogger.logDebugMsg("Artificial resource exists, unloading it now", this.getClass());
			result = result && ResourceHelper.unloadResource(artificialResource);
			SimilarityTestLogger.logDebugMsg(String.format("%s artificial resource at %s",
					result ? "Unloaded" : "Could not unload", artificialResource.getURI()), this.getClass());
		}
		return result;
	}

	/**
	 * @implSpec Only checks whether the merged model resource is loaded, because
	 *           other resources will be automatically loaded on demand.
	 * 
	 * @return Whether the merged model resource is loaded
	 */
	@Override
	public boolean isModelResourceLoaded() {
		return this.mergedModelResource != null && this.mergedModelResource.isLoaded();
	}

	/**
	 * @implSpec Loads both {@link #getModelResource()} and the ArtificialResource
	 *           (if existent, see {@link JaMoPPModelResourceWrapper})
	 */
	@Override
	public boolean loadParsedResources() {
		var result = true;
		if (mergedModelResource != null && !this.mergedModelResource.isLoaded()) {
			SimilarityTestLogger.logDebugMsg("Merged model resource exists, loading it now", this.getClass());
			ResourceHelper.loadResource(mergedModelResource);
			result = mergedModelResource.isLoaded();
			SimilarityTestLogger.logDebugMsg(String.format("%s merged model resource at %s",
					result ? "Loaded" : "Could not load", mergedModelResource.getURI()), this.getClass());

		}
		if (artificialResource != null && !this.artificialResource.isLoaded()) {
			SimilarityTestLogger.logDebugMsg("Artificial resource exists, loading it now", this.getClass());
			ResourceHelper.loadResource(artificialResource);
			result = artificialResource.isLoaded();
			SimilarityTestLogger.logDebugMsg(String.format("%s artificial resource at %s",
					result ? "Loaded" : "Could not load", artificialResource.getURI()), this.getClass());
		}
		return result;
	}

	/**
	 * @implSpec Sets the URI of {@link #getModelResource()} to the given URI, then
	 *           sets URI of ArtificialResource accordingly (if existent).
	 */
	@Override
	public void setModelResourcesURI(URI newParsedModelResourceURI) {
		if (mergedModelResource != null) {
			mergedModelResource.setURI(newParsedModelResourceURI);
		}
		if (artificialResource != null) {
			artificialResource.setURI(this.getArtificialResourceURI(newParsedModelResourceURI));
		}
	}

	/**
	 * @return The merged model resource, which contains all contents of all
	 *         (directly) parsed model source files
	 */
	@Override
	public Resource getModelResource() {
		return mergedModelResource;
	}

	/**
	 * @return Whether there is a parsed model resource
	 *         ({@link #getModelResource()}) saved in this instance.
	 */
	@Override
	public boolean modelResourceExists() {
		return this.getModelResource() != null;
	}

	/**
	 * @return Whether all proxies should be resolved after a model is parsed inside
	 *         {@link #parseModelResource(Path, URI)}. Set to false by default, can
	 *         be changed via {@link #setResolveAllProxies(boolean)}.
	 */
	public boolean isResolveAllProxies() {
		return resolveAllProxies;
	}

	/**
	 * @see {@link #isResolveAllProxies()}
	 */
	public void setResolveAllProxies(boolean resolveAllProxies) {
		this.resolveAllProxies = resolveAllProxies;
	}

	/**
	 * @return Whether the synthetic contents, which were created while parsing the
	 *         model, should be split into a separate Resource. Set to false by
	 *         default, can be changed via
	 *         {@link #setSplitArtificialResource(boolean)}.
	 * @see {@link JaMoPPResourceParsingStrategy#performTrivialRecovery()}
	 */
	public boolean isSplitArtificialResource() {
		return splitArtificialResource;
	}

	/**
	 * @see {@link #isSplitArtificialResource()}
	 */
	public void setSplitArtificialResource(boolean splitArtificialResource) {
		this.splitArtificialResource = splitArtificialResource;
	}
}
