package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

/**
 * Contains layout-related information for the corresponding parser test.
 * 
 * @author Alp Torac Genc
 */
public class ParserTestFileLayout {
	/**
	 * @see {@link #getModelSourceFileRootDirPath()}
	 */
	private Path modelSourceFileRootDirPath;
	/**
	 * @see {@link #setTestModelResourceFilesSaveDirPath(Path)}
	 */
	private Path testModelResourceFilesSaveDirPath;

	/**
	 * @see {@link #setCacheSaveDirPath(Path)}
	 */
	private Path cacheSaveDirPath;

	/**
	 * @see {@link #getTimeMeasurementsFileSavePath()}
	 */
	private Path timeMeasurementsFileSavePath;

	/**
	 * @see {@link #getModelResourceFileExtension()}
	 */
	private String modelResourceFileExtension;

	public ParserTestFileLayout() {
	}

	/**
	 * Constructs a new instance and copies the attributes of the given layout
	 * instance.
	 */
	public ParserTestFileLayout(ParserTestFileLayout layout) {
		this.testModelResourceFilesSaveDirPath = layout.testModelResourceFilesSaveDirPath;
		this.cacheSaveDirPath = layout.cacheSaveDirPath;
		this.timeMeasurementsFileSavePath = layout.timeMeasurementsFileSavePath;
		this.modelResourceFileExtension = layout.modelResourceFileExtension;
	}

	/**
	 * Sets the path to the root folder of the model source file directories, under
	 * which all models' source file directories reside
	 */
	public void setModelSourceFileRootDirPath(Path modelSourceFileRootDirPath) {
		this.modelSourceFileRootDirPath = modelSourceFileRootDirPath;
	}

	/**
	 * Sets the file extension of the Resource files (if desired to be saved)
	 */
	public void setModelResourceFileExtension(String modelResourceFileExtension) {
		this.modelResourceFileExtension = modelResourceFileExtension;
	}

	/**
	 * @return The file extension of the Resource files (if desired to be saved)
	 */
	public String getModelResourceFileExtension() {
		return this.modelResourceFileExtension;
	}

	/**
	 * Sets the absolute path, at which taken time measurements are to be saved.
	 */
	public void setTimeMeasurementsFileSavePath(Path timeMeasurementsFileSavePath) {
		this.timeMeasurementsFileSavePath = timeMeasurementsFileSavePath;
	}

	/**
	 * Sets up the relative path to the directory, where the contents of the
	 * resource cache are to be saved (if desired).
	 * 
	 * @see {@link CacheUtil}
	 */
	public void setCacheSaveDirPath(Path cacheSaveDirPath) {
		this.cacheSaveDirPath = cacheSaveDirPath;
	}

	/**
	 * Sets up the relative path to the {@link #getAbsoluteCurrentDirectory()}
	 * directory, where parsed model resource files are to be saved (if desired).
	 */
	public void setTestModelResourceFilesSaveDirPath(Path testModelResourceFilesSaveDirPath) {
		this.testModelResourceFilesSaveDirPath = testModelResourceFilesSaveDirPath;
	}

	/**
	 * @param modelDir The path to the model source file directory
	 * @return The key, with which the model resource parsed under the given path
	 *         will be added to the cache.
	 */
	public String getCacheKeyForModelSourceFileDir(Path modelDir) {
		return this.getAbsoluteCurrentDirectory().relativize(modelDir).toString();
	}

	/**
	 * @return The absolute path, at which taken time measurements are to be saved.
	 */
	public Path getTimeMeasurementsFileSavePath() {
		return this.getAbsoluteCurrentDirectory().resolve(timeMeasurementsFileSavePath);
	}

	/**
	 * @return The absolute path, at which all files generated througout the tests
	 *         should be saved.
	 */
	public Path getTestFilesSavePath() {
		return this.getAbsoluteCurrentDirectory().resolve(testModelResourceFilesSaveDirPath);
	}

	/**
	 * @param modelDir The path to a directory, which has files for one (and only
	 *                 one) model
	 * @return The path (as String), where the parsed model resource (for the model
	 *         under the given path) should be saved, if desired.
	 */
	public String getModelResourcePathFor(Path modelDir) {
		var modelSubPath = this.getAbsoluteCurrentDirectory().relativize(modelDir);
		var resPath = this.getModelResourceSaveRootDirectory().resolve(modelSubPath);

		var resPathString = resPath.toString();

		// Check if the resource path has a file extension
		// If not, append the file extension for it
		if (!resPath.getFileName().toString().contains(".")) {
			resPathString += "." + this.modelResourceFileExtension;
		}

		return resPathString;
	}

	/**
	 * @param modelDir The path to a directory, which has files for one (and only
	 *                 one) model
	 * @return The physical URI of the model resource parsed from the model at the
	 *         given path.
	 */
	public URI getModelResourceURI(Path modelDir) {
		return URI.createFileURI(this.getModelResourcePathFor(modelDir));
	}

	/**
	 * Defaults to {@link #getAbsoluteCurrentDirectory()}.
	 * 
	 * @return Path to the root folder of the model source file directories, under
	 *         which all models' source file directories reside
	 */
	public Path getModelSourceFileRootDirPath() {
		return this.modelSourceFileRootDirPath;
	}

	/**
	 * @return The root directory, under which generated test resources will be
	 *         saved.
	 */
	public Path getModelResourceSaveRootDirectory() {
		return this.getAbsoluteCurrentDirectory().resolve(cacheSaveDirPath);
	}

	/**
	 * @return The relative path between the current directory and the root
	 *         directory ({@link #getModelSourceFileRootDirPath()}).
	 * @see {@link #getModelSourceFileRootDirPath()}
	 */
	public Path getRelativeModelSourceFileRootDirPath() {
		return this.getAbsoluteCurrentDirectory().relativize(this.getModelSourceFileRootDirPath());
	}

	/**
	 * @param modelParentDirPath The parent directory for a group of models, which
	 *                           contains other directories that contain model
	 *                           files.
	 * @return The relative path between ({@link #getModelSourceFileRootDirPath()})
	 *         and the given path. If both paths are the same, returns the file name
	 *         (without extension) in the parameter.
	 */
	public Path getRelativeModelSourceParentDirPath(Path modelParentDirPath) {
		var rootPath = this.getModelSourceFileRootDirPath();
		var relPath = rootPath.relativize(modelParentDirPath);
		if (relPath.getParent() == null) {
			return modelParentDirPath.getFileName();
		}
		return relPath;
	}

	/**
	 * @return The current (absolute) position within the file system.
	 */
	private Path getAbsoluteCurrentDirectory() {
		return new File("").getAbsoluteFile().toPath();
	}
}
