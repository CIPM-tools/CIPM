package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

/**
 * An interface for classes meant to find model source parent directories and
 * model source file directories. The method of discovery depends on the
 * concrete implementor.
 * 
 * @author Alp Torac Genc
 */
public interface IModelDiscoveryStrategy {
	/**
	 * Finds and returns paths to all model source parent directories. Use
	 * {@link #discoverModelSourceFileDirs(File)} on the directories found here to
	 * get the model source file directories.
	 * 
	 * @param dirToDiscover The top-most directory, whose contents will be scanned
	 *                      for model source parent directories
	 * 
	 * @return All model source parent directory paths containing model source file
	 *         directories that are found according to the concrete implementor.
	 *         Returned paths are the longest common model source file directory
	 *         paths.
	 */
	public Collection<Path> discoverModelSourceParentDirs(File dirToDiscover);

	/**
	 * Finds and returns paths to all model source file directories. Should be used
	 * in conjunction with {@link #discoverModelSourceParentDirs(File)}, unless
	 * model source parent directories are already known.
	 * 
	 * @param modelSourceParentDirToExplore The top-most directory, whose contents
	 *                                      will be scanned for model source file
	 *                                      directories. Should be a model source
	 *                                      parent directory.
	 * 
	 * @return All model source file directories that are found according to the
	 *         concrete implementor.
	 */
	public Collection<Path> discoverModelSourceFileDirs(File modelSourceParentDirToExplore);

	/**
	 * @param dir A potential model source file directory that contains model source
	 *            files of one (and only one) model
	 * @return Whether the given directory is a model source file directory
	 */
	public boolean isModelSourceFileDirectory(File dir);
}
