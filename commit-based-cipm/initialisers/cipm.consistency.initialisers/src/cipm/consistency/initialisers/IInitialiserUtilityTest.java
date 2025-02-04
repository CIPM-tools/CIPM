package cipm.consistency.initialisers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An interface that contains various methods that can be used in utility tests,
 * which ensure that all initialisers and/or tests associated with them are
 * present. Not implemented as an abstract class, since utility tests are likely
 * to have to inherit from an abstract test class anyway.
 * 
 * @author Alp Torac Genc
 */
public interface IInitialiserUtilityTest {
	/**
	 * @return The {@link IInitialiserPackage} that will be used within the utility
	 *         test.
	 */
	public IInitialiserPackage getUsedInitialiserPackage();

	/**
	 * @return A list of all files under the given path and its sub-directories.
	 */
	public default Collection<File> getAllFiles(File currentPath) {
		var result = new ArrayList<File>();

		if (currentPath.isFile()) {
			result.add(currentPath);
		} else {
			var files = currentPath.listFiles();
			if (files != null) {
				for (var f : files) {
					result.addAll(this.getAllFiles(f));
				}
			}
		}

		return result;
	}

	/**
	 * @return Whether the given file's name (without extension) is equal to the
	 *         given fileName.
	 */
	public default boolean fileNameEquals(File file, String fileName) {
		return file != null && file.getName().split("\\.")[0].equals(fileName);
	}

	/**
	 * @return A String representing the given stream. The provided toStringFunc
	 *         will be used to transform stream elements into Strings.
	 */
	public default <T extends Object> String streamToString(Stream<T> stream, Function<T, String> toStringFunc) {
		return stream.map((e) -> toStringFunc.apply(e)).reduce("", (s1, s2) -> s1 + ", " + s2).substring(2);
	}

	/**
	 * Opens a stream on the given list and delegates to
	 * {@link #clsStreamToString(Stream)}.
	 */
	public default String clsListToString(List<? extends Class<?>> list) {
		return this.clsStreamToString(list.stream());
	}

	/**
	 * A variant of {@link #streamToString(Stream, Function)} for Class streams.
	 * 
	 * Maps stream elements (classes) to String by returning their simple name.
	 */
	public default String clsStreamToString(Stream<? extends Class<?>> list) {
		return this.streamToString(list, (cls) -> cls.getSimpleName());
	}

	/**
	 * @return An instance of all initialisers accessible from
	 *         {@link #getUsedInitialiserPackage()}.
	 */
	public default Collection<IInitialiser> getAllInitialiserInstances() {
		return this.getUsedInitialiserPackage().getAllInitialiserInstances();
	}

	/**
	 * @return Types of all initialisers accessible from
	 *         {@link #getUsedInitialiserPackage()}.
	 */
	public default Collection<Class<? extends IInitialiser>> getAllInitialiserInterfaceTypes() {
		return this.getUsedInitialiserPackage().getAllInitialiserInterfaceTypes();
	}

	/**
	 * @return The type of the initialiser meant to instantiate objClass, which is
	 *         accessible from {@link #getUsedInitialiserPackage()}..
	 */
	public default Class<? extends IInitialiser> getInitialiserInterfaceFor(Class<?> objClass) {
		return this.getUsedInitialiserPackage().getInitialiserInterfaceTypeFor(objClass);
	}
}