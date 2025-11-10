package cipm.consistency.vsum.test.pcm.experiment;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;

public class JavaToPcmPropagationWrapper implements AutoCloseable {
	private ResourceSet resSet;

	private Resource javaModel;

	private Resource pcmRepository;
	private Resource pcmSystem;
	private Resource pcmAllocation;
	private Resource pcmResEnv;
	private Resource pcmUsage;

	private Resource im;

	private Resource javaChanges;
	private Resource pcmChanges;
	private Resource imChanges;

	private Resource correspondences;

	public JavaToPcmPropagationWrapper(ResourceSet resSet) {
		this.resSet = resSet;
	}

	public void initialise(JavaToPcmPropagationDirLayout layout) {
		javaModel = loadResource(resSet, pathToURI(layout.getCodeDirPath()));

		pcmRepository = loadResource(resSet, pathToURI(layout.getPcmRepositoryPath()));
		pcmSystem = loadResource(resSet, pathToURI(layout.getPcmSystemPath()));
		pcmAllocation = loadResource(resSet, pathToURI(layout.getPcmAllocationPath()));
		pcmResEnv = loadResource(resSet, pathToURI(layout.getPcmResourceEnvironmentPath()));
		pcmUsage = loadResource(resSet, pathToURI(layout.getPcmUsagePath()));

		im = loadResource(resSet, pathToURI(layout.getIMSavePath()));

		javaChanges = loadResource(resSet, pathToURI(layout.getJavaChangesSaveFilePath()));
		pcmChanges = loadResource(resSet, pathToURI(layout.getPcmChangesSaveFilePath()));
		imChanges = loadResource(resSet, pathToURI(layout.getImChangesSaveFilePath()));

		correspondences = loadResource(resSet, pathToURI(layout.getVSUMCorrespondencesPath()));
	}

	public JavaToPcmPropagationWrapper copyTo(Path copyPath) {
		var newResSet = new ResourceSetImpl();
		var newLayout = new JavaToPcmPropagationDirLayout(copyPath);
		var newWrapper = new JavaToPcmPropagationWrapper(newResSet);

		newWrapper.javaModel = copyAndSaveResource(newResSet, javaModel, pathToURI(newLayout.getCodeDirPath()));

		newWrapper.pcmRepository = copyAndSaveResource(newResSet, pcmRepository,
				pathToURI(newLayout.getPcmRepositoryPath()));
		newWrapper.pcmSystem = copyAndSaveResource(newResSet, pcmSystem, pathToURI(newLayout.getPcmSystemPath()));
		newWrapper.pcmAllocation = copyAndSaveResource(newResSet, pcmAllocation,
				pathToURI(newLayout.getPcmAllocationPath()));
		newWrapper.pcmResEnv = copyAndSaveResource(newResSet, pcmResEnv,
				pathToURI(newLayout.getPcmResourceEnvironmentPath()));
		newWrapper.pcmUsage = copyAndSaveResource(newResSet, pcmUsage, pathToURI(newLayout.getPcmUsagePath()));

		newWrapper.im = copyAndSaveResource(newResSet, im, pathToURI(newLayout.getIMSavePath()));

		newWrapper.javaChanges = copyAndSaveResource(newResSet, javaChanges,
				pathToURI(newLayout.getJavaChangesSaveFilePath()));
		newWrapper.pcmChanges = copyAndSaveResource(newResSet, pcmChanges,
				pathToURI(newLayout.getPcmChangesSaveFilePath()));
		newWrapper.imChanges = copyAndSaveResource(newResSet, imChanges,
				pathToURI(newLayout.getImChangesSaveFilePath()));

		newWrapper.correspondences = copyAndSaveResource(newResSet, correspondences,
				pathToURI(newLayout.getVSUMCorrespondencesPath()));

		return newWrapper;
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	protected Resource loadResource(URI uri) {
		return this.loadResource(new ResourceSetImpl(), uri);
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	protected Resource loadResource(ResourceSet resSet, URI uri) {
		return this.loadResource(resSet.createResource(uri));
	}

	/**
	 * Creates, loads and returns a new Resource instance for the same URI. Can be
	 * used to create a separate Resource instance for the given resource.
	 */
	protected Resource loadNewResourceInstance(Resource resource) {
		return this.loadResource(resource.getURI());
	}

	/**
	 * Loads and returns the given resource. Does not create a new resource
	 * instance.
	 */
	protected Resource loadResource(Resource resource) {
		createDirs(Path.of(resource.getURI().path()).toAbsolutePath());

		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
		return resource;
	}

	private void createDirs(Path path) {
		var f = path.toFile();

		if (f.isDirectory()) {
			f.mkdirs();
		} else {
			f.getParentFile().mkdirs();
		}
	}

	private void removeResource(Resource res) {
		res.unload();
		res.getContents().clear();
		if (res.getResourceSet() != null) {
			res.getResourceSet().getResources().remove(res);
		}
	}

	private URI pathToURI(Path path) {
		return URI.createFileURI(path.toAbsolutePath().toString());
	}

	private Resource copyAndSaveResource(ResourceSet resSet, Resource resToCopy, URI copyLocationURI) {
		var res = resSet.createResource(copyLocationURI);
		res.getContents().addAll(EcoreUtil.copyAll(resToCopy.getContents()));
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
		return res;
	}

	@Override
	public void close() {
		removeResource(javaModel);

		removeResource(pcmRepository);
		removeResource(pcmSystem);
		removeResource(pcmAllocation);
		removeResource(pcmResEnv);
		removeResource(pcmUsage);

		removeResource(im);

		removeResource(javaChanges);
		removeResource(pcmChanges);
		removeResource(imChanges);

		removeResource(correspondences);

	}

	public ResourceSet getResSet() {
		return resSet;
	}

	public Resource getJavaModel() {
		return javaModel;
	}

	public Resource getPcmRepository() {
		return pcmRepository;
	}

	public Resource getPcmSystem() {
		return pcmSystem;
	}

	public Resource getPcmAllocation() {
		return pcmAllocation;
	}

	public Resource getPcmResEnv() {
		return pcmResEnv;
	}

	public Resource getPcmUsage() {
		return pcmUsage;
	}

	public Resource getIm() {
		return im;
	}

	public Resource getJavaChanges() {
		return javaChanges;
	}

	public Resource getPcmChanges() {
		return pcmChanges;
	}

	public Resource getImChanges() {
		return imChanges;
	}

	public Resource getCorrespondences() {
		return correspondences;
	}

	
}
