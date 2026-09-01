package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

/**
 * A utility class for {@link ParserTestTimeMeasurementKey} building process.
 * Contains helper methods to adapt various parameters, especially to relativize
 * paths and URIs.
 * 
 * @author Alp Torac Genc
 */
public final class ParserTestTimeMeasurementKeyUtil {
	private static final File currentPathFile = new File("").getAbsoluteFile();
	private static final Path currentPath = currentPathFile.toPath().toAbsolutePath();
	private static final URI currentURI = URI.createFileURI(currentPath.toString());

	private static final String absolutePathSeparatorRegex = "\\\\";
	private static final String portablePathSeparator = "/";

	private static URI baseURI = currentURI;
	private static Path relativizationPath = currentPath;

	/**
	 * @return Relative URI (in String form), which results in the given uri upon
	 *         being appended to {@link #setRelativizationPath(Path)}.
	 */
	public static String getAdaptedURIString(URI uri) {
		return uri.deresolve(baseURI).toString();
	}

	/**
	 * Converts the given path to an absolute path and uses
	 * {@value #portablePathSeparator} as path separator for better portability.
	 * 
	 * @return Relative path (in String form), which results in the given path upon
	 *         being appended to {@link #setRelativizationPath(Path)}.
	 */
	public static String getAdaptedPathString(Path path) {
		return relativizationPath.relativize(path.toAbsolutePath()).toString().replaceAll(absolutePathSeparatorRegex,
				portablePathSeparator);
	}

	/**
	 * Sets the location, which will be used for relativization operations (on both
	 * paths and URIs) within this class.
	 */
	public static void setRelativizationPath(Path pathOfReference) {
		relativizationPath = pathOfReference.toAbsolutePath();
		baseURI = URI.createFileURI(relativizationPath.toAbsolutePath().toString());
	}
}
