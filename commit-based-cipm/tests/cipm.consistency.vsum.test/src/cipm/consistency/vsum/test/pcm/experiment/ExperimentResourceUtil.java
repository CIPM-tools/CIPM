package cipm.consistency.vsum.test.pcm.experiment;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.cpr.pcmjava.preprocessing.ChangeUtil;

public final class ExperimentResourceUtil {
	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	public static Resource loadResource(URI uri) {
		if (uri == null)
			return null;
		return loadResource(new ResourceSetImpl(), uri);
	}

	/**
	 * Creates, loads and returns a Resource instance for the given path.
	 */
	public static Resource loadResource(ResourceSet resSet, Path path) {
		if (path == null)
			return null;
		return loadResource(resSet, pathToURI(path));
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	public static Resource loadResource(ResourceSet resSet, URI uri) {
		if (uri == null)
			return null;
		return loadResource(resSet.createResource(uri));
	}

	/**
	 * Loads and returns the given resource. Does not create a new resource
	 * instance.
	 */
	public static Resource loadResource(Resource resource) {
		var path = Path.of(resource.getURI().path()).toAbsolutePath();
		createDirs(path);

		if (path.toFile().exists()) {
			try {
				resource.load(null);
			} catch (IOException e) {
				e.printStackTrace();
				Assertions.fail(e);
			}
		}
		return resource;
	}

	public static void adaptURIsInChanges(Resource changeResource, Resource targetModel) {
		ChangeUtil.adaptChangeURIs(changeResource, targetModel);
	}

	public static void createDirs(Path path) {
		var f = path.toFile();

		if (f.isDirectory()) {
			f.mkdirs();
		} else {
			f.getParentFile().mkdirs();
		}
	}

	public static void removeResource(Resource res) {
		if (res != null) {
			res.unload();
			res.getContents().clear();
			if (res.getResourceSet() != null) {
				res.getResourceSet().getResources().remove(res);
			}
		}
	}

	public static URI pathToURI(Path path) {
		if (path == null)
			return null;
		return URI.createFileURI(path.toAbsolutePath().toString());
	}

	public static Resource copyAndSaveResource(ResourceSet resSet, Resource resToCopy, Path copyLocationPath) {
		return copyAndSaveResource(resSet, resToCopy, pathToURI(copyLocationPath));
	}

	public static Resource copyAndSaveResource(ResourceSet resSet, Resource resToCopy, URI copyLocationURI) {
		if (copyLocationURI == null)
			return null;

		var res = resSet.createResource(copyLocationURI);
		if (resToCopy != null) {
			res.getContents().addAll(EcoreUtil.copyAll(resToCopy.getContents()));
		}
		saveResource(res);
		return res;
	}

	public static void saveResource(Resource res) {
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
	}
}
