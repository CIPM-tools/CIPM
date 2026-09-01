package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * A concrete implementation that considers one top-most directory and discovers
 * its contents for models, similar to depth first search but with a single
 * start point. Uses a given filter to determine whether a given directory is a
 * model source file directory and encompasses one (and only one) model.
 * 
 * @author Alp Torac Genc
 */
public class ModelDiscoveryStrategy implements IModelDiscoveryStrategy {
	private Predicate<File> modelSourceFileDirFilter;

	/**
	 * Constructs an instance.
	 * 
	 * @param modelSourceFileDirFilter A filter for determining whether a given
	 *                                 directory is a model source file directory
	 *                                 and encompasses one (and only one) model
	 */
	public ModelDiscoveryStrategy(Predicate<File> modelSourceFileDirFilter) {
		this.modelSourceFileDirFilter = modelSourceFileDirFilter;
	}

	/**
	 * @param modelSourceParentDirPath A potential model source parent directory,
	 *                                 which potentially contains model source file
	 *                                 directories
	 * @return All model source file directories under the given path to potential
	 *         model source parent directory
	 * 
	 * @see {@link #isModelSourceFileDirectory(File)}
	 */
	protected Collection<Path> getAllModelSourceFileDirsUnder(Path modelSourceParentDirPath) {
		var modelSourceFileDirPaths = new ArrayList<Path>();
		var dirs = modelSourceParentDirPath.toFile().listFiles();
		for (var dir : dirs) {
			if (this.isModelSourceFileDirectory(dir)) {
				modelSourceFileDirPaths.add(dir.toPath());
			}
		}
		return modelSourceFileDirPaths;
	}

	/**
	 * @implSpec Determines whether a discovered directory is a model source file
	 *           directory using the filter from the constructor
	 *           ({@link #ModelDirDiscoveryStrategy(Path, Predicate)}). Any
	 *           directory that is considered a model source file directory is
	 *           assumed to encapsulate one (and only one) model, even if it
	 *           contains multiple models in reality.
	 */
	@Override
	public boolean isModelSourceFileDirectory(File dir) {
		if (this.modelSourceFileDirFilter == null) {
			return true;
		} else {
			return this.modelSourceFileDirFilter.test(dir);
		}
	}

	/**
	 * Recursively searches for model source parent directories, starting from the
	 * given directory, and returns a list of all model source parent directories.
	 * 
	 * @see {@link #findModelSourceParentDirs(File, Collection)} for more
	 *      information.
	 */
	protected Collection<Path> findModelSourceParentDirs(File dirToDiscover) {
		var foundModelDirs = new ArrayList<Path>();
		findModelSourceParentDirs(dirToDiscover, foundModelDirs);
		return foundModelDirs;
	}

	/**
	 * Recursively searches for model source parent directories. All directories
	 * containing models (determined via {@link #isModelSourceFileDirectory(File)})
	 * will be added to foundModelSourceParentDirs, if not already there.
	 * 
	 * @param dirToDiscover              The directory, whose contents will be
	 *                                   explored
	 * @param foundModelSourceParentDirs A collection of model source parent
	 *                                   directories. The found model source parent
	 *                                   directories' path is the parent directory
	 *                                   of nested model source file directories
	 */
	protected void findModelSourceParentDirs(File dirToDiscover, Collection<Path> foundModelSourceParentDirs) {
		if (dirToDiscover != null && dirToDiscover.isDirectory()) {
			var discovered = new ArrayList<File>();

			for (var f : dirToDiscover.listFiles()) {
				if (!this.isModelSourceFileDirectory(f)) {
					discovered.add(f);
				} else if (!foundModelSourceParentDirs.contains(dirToDiscover.toPath())) {
					foundModelSourceParentDirs.add(dirToDiscover.toPath());
				}
			}

			discovered.forEach((d) -> findModelSourceParentDirs(d, foundModelSourceParentDirs));
		}
	}

	/**
	 * @implSpec Check {@link #isModelSourceFileDirectory(File)} for more details on
	 *           how model source file directories are filtered.
	 */
	@Override
	public Collection<Path> discoverModelSourceFileDirs(File modelSourceParentDirToExplore) {
		return this.getAllModelSourceFileDirsUnder(modelSourceParentDirToExplore.toPath());
	}

	/**
	 * @implSpec Check {@link #isModelSourceFileDirectory(File)} for more details on
	 *           how model source file directories are filtered.
	 */
	@Override
	public Collection<Path> discoverModelSourceParentDirs(File dirToDiscover) {
		return this.findModelSourceParentDirs(dirToDiscover);
	}
}
