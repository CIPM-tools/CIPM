package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * An interface for classes meant to find model directories. How models' source
 * files' directories are discovered and filtered depends on the concrete
 * implementor. <br>
 * <br>
 * Implemented minimally, as discovering model directories may span across
 * multiple "root" directories, and may have to be adapted heavily for concrete
 * scenarios.
 * 
 * @author Alp Torac Genc
 */
public interface IModelDirDiscoveryStrategy {
	/**
	 * Finds and returns paths to all parent directories, which have nested model
	 * source file directories. Use {@link #discoverModelSourceDirs(File)} on the
	 * directories found here to get the actual model source file directories.
	 * 
	 * @param dirToDiscover The top-most directory, whose contents will be scanned
	 * 
	 * @return All parent directories containing model source file directories that
	 *         are found according to the concrete implementor.
	 */
	public Collection<Path> discoverModelSourceParentDirs(File dirToDiscover);

	/**
	 * Finds and returns paths to all model source file directories.
	 * 
	 * @param dirToDiscover The top-most directory, whose contents will be scanned
	 * 
	 * @return All model source file directories that are found according to the
	 *         concrete implementor
	 */
	public Collection<Path> discoverModelSourceDirs(File dirToDiscover);

	/**
	 * @param dir A directory that potentially contains source files of one (and
	 *            only one) model
	 * @return Whether the given directory is contains source files of one (and only
	 *         one) model
	 */
	public boolean isModelSourceDirectory(File dir);
}
