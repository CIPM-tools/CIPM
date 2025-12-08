package cipm.consistency.vsum.test.pcm.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Assertions;

/**
 * Contains various utility methods for Resource-related operations.
 * 
 * @author Alp Torac Genc
 */
public class ResourceOperationsUtil {
	public static Resource createEmptyResource(ResourceSet resSet, Path path) {
		return createEmptyResource(resSet, pathToURI(path));
	}

	public static Resource createEmptyResource(ResourceSet resSet, URI uri) {
		return resSet.createResource(uri);
	}

	public static Resource loadResource(Path path) {
		return loadResource(pathToURI(path));
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	public static Resource loadResource(URI uri) {
		return loadResource(new ResourceSetImpl(), uri);
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	public static Resource loadResource(ResourceSet resSet, Path path) {
		return loadResource(resSet, pathToURI(path));
	}

	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	public static Resource loadResource(ResourceSet resSet, URI uri) {
		return loadResource(resSet.createResource(uri));
	}

	/**
	 * Loads and returns the given resource. Does not create a new resource
	 * instance.
	 */
	public static Resource loadResource(Resource resource) {
		createDirs(Path.of(resource.getURI().path()).toAbsolutePath());

		try {
			resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
		return resource;
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
		res.unload();
		res.getContents().clear();
		if (res.getResourceSet() != null) {
			res.getResourceSet().getResources().remove(res);
		}
	}

	public static URI pathToURI(Path path) {
		return URI.createFileURI(path.toAbsolutePath().toString());
	}

	public static Resource copyAndSaveResource(ResourceSet resSet, Resource resToCopy, Path copyLocationPath) {
		return copyAndSaveResource(resSet, resToCopy, pathToURI(copyLocationPath));
	}

	public static Resource copyAndSaveResource(ResourceSet resSet, Resource resToCopy, URI copyLocationURI) {
		var res = resToCopy;
		if (!resToCopy.getURI().equals(copyLocationURI)) {
			try {
				FileUtils.copyFile(new File(resToCopy.getURI().toFileString()),
						new File(copyLocationURI.toFileString()));
				res = loadResource(resSet, copyLocationURI);
			} catch (IOException e) {
				e.printStackTrace();
				Assertions.fail(e);
			}
		}
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

	public static void reload(Resource res) {
		res.unload();
		loadResource(res);
	}
}
