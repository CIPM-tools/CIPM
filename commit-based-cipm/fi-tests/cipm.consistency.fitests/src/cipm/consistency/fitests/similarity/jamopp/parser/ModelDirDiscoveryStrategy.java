package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * A model directory discovery strategy, which considers one top-most directory
 * and discovers its contents for model directories, similar to depth first
 * search but with a single start point. Uses a given filter to determine
 * whether a given directory encompasses one (and only one) model.
 * 
 * @author Alp Torac Genc
 */
public class ModelDirDiscoveryStrategy implements IModelDirDiscoveryStrategy {
	private Predicate<File> modelDirFilter;

	/**
	 * Constructs an instance.
	 * 
	 * @param modelDirFilter A filter for determining whether a given directory
	 *                       encompasses one (and only one) model
	 */
	public ModelDirDiscoveryStrategy(Predicate<File> modelDirFilter) {
		this.modelDirFilter = modelDirFilter;
	}

	/**
	 * @param modelParentDirPath A parent directory, which potentially contains
	 *                           model source file directories
	 * @return All model source file directories under the given path
	 * 
	 * @see {@link #isModelSourceDirectory(File)}
	 */
	protected Collection<Path> getAllModelDirsUnder(Path modelParentDirPath) {
		var result = new ArrayList<Path>();
		var dirs = modelParentDirPath.toFile().listFiles();
		for (var dir : dirs) {
			if (this.isModelSourceDirectory(dir)) {
				result.add(dir.toPath());
			}
		}
		return result;
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
	public boolean isModelSourceDirectory(File dir) {
		if (this.modelDirFilter == null) {
			return true;
		} else {
			return this.modelDirFilter.test(dir);
		}
	}

	/**
	 * Recursively searches for directories containing Java-Model files, starting
	 * from the given directory, and returns a list of all such directories.
	 */
	protected Collection<Path> discoverModels(File dirToDiscover) {
		var foundModelDirs = new ArrayList<Path>();
		discoverModels(dirToDiscover, foundModelDirs);
		return foundModelDirs;
	}

	/**
	 * Recursively searches for directories that contain model source files. All
	 * directories containing models (determined via
	 * {@link #isModelSourceDirectory(File)}) will be added to foundModelDirs, if
	 * not already there.
	 * 
	 * @param dirToDiscover  The directory, where the recursive search will begin
	 * @param foundModelDirs A collection of model source file directories
	 */
	protected void discoverModels(File dirToDiscover, Collection<Path> foundModelDirs) {
		if (dirToDiscover != null && dirToDiscover.isDirectory()) {
			var discovered = new ArrayList<File>();

			for (var f : dirToDiscover.listFiles()) {
				if (!this.isModelSourceDirectory(f)) {
					discovered.add(f);
				} else if (!foundModelDirs.contains(dirToDiscover.toPath())) {
					foundModelDirs.add(dirToDiscover.toPath());
				}
			}

			discovered.forEach((d) -> discoverModels(d, foundModelDirs));
		}
	}

	/**
	 * @implSpec Check {@link #isModelSourceDirectory(File)} for more details on how
	 *           model directories are filtered.
	 */
	@Override
	public Collection<Path> discoverModelSourceDirs(File dirToDiscover) {
		return this.getAllModelDirsUnder(dirToDiscover.toPath());
	}

	/**
	 * @implSpec Check {@link #isModelSourceDirectory(File)} for more details on how
	 *           model directories are filtered.
	 */
	@Override
	public Collection<Path> discoverModelSourceParentDirs(File dirToDiscover) {
		return this.discoverModels(dirToDiscover);
	}
}
