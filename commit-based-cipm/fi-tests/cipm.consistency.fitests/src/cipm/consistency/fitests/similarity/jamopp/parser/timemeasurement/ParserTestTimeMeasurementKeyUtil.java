package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

/**
 * TODO Commentary
 */
public final class ParserTestTimeMeasurementKeyUtil {
	private static final File currentPathFile = new File("").getAbsoluteFile();
	private static final Path currentPath = currentPathFile.toPath().toAbsolutePath();
	private static final URI currentURI = URI.createFileURI(currentPath.toString());

	private static final String absolutePathSeparatorRegex = "\\\\";
	private static final String portablePathSeparator = "/";

	private static URI baseURI = currentURI;
	private static Path relativizationPath = currentPath;

	public static String getAdaptedURIString(URI uri) {
		return uri.deresolve(baseURI).toString();
	}

	public static String getAdaptedPathString(Path path) {
		return relativizationPath.relativize(path.toAbsolutePath()).toString().replaceAll(absolutePathSeparatorRegex,
				portablePathSeparator);
	}

	public static void setRelativizationPath(Path pathOfReference) {
		relativizationPath = pathOfReference.toAbsolutePath();
		baseURI = URI.createFileURI(relativizationPath.toAbsolutePath().toString());
	}
}
