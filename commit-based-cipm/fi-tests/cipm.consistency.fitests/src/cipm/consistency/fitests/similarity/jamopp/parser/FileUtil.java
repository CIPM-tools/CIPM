package cipm.consistency.fitests.similarity.jamopp.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import cipm.consistency.fitests.similarity.ILoggable;

/**
 * A utility class that contains file-related operations.
 * 
 * @author Alp Torac Genc
 */
public class FileUtil implements ILoggable {
	/**
	 * @return Whether the content of both dirs are similar.
	 * 
	 * @see {@link #filesEqual(File, File)}
	 * @see {@link #dirsEqual(File, File)}
	 */
	public boolean areContentsEqual(Path path1, Path path2) {
		return dirsEqual(path1.toFile(), path2.toFile());
	}

	/**
	 * Reads the given file and removes line breaks and whitespaces. <br>
	 * <br>
	 * If the given file cannot be read (due to IOException), returns an empty
	 * string.
	 */
	public String readEffectiveText(File f) {
		var content = "";

		try {
			content = Files.readString(f.toPath());
		} catch (IOException e) {
			this.logDebugMsg(String.format("Could not read: %s, returning empty string", f.toPath().toString()));
		}

		return content.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\s", "");
	}

	/**
	 * Compares the equality of the given files based on their effective content,
	 * i.e. their content without whitespaces. <br>
	 * <br>
	 * If both files cannot be read, they are ignored and this method returns true.
	 * 
	 * @see {@link #readEffectiveText(File)}
	 */
	public boolean filesEqual(File f1, File f2) {
		var f1Content = readEffectiveText(f1);
		var f2Content = readEffectiveText(f2);

		if (f1Content.isBlank() && f2Content.isBlank()) {
			return true;
		}

		return f1Content.equals(f2Content);
	}

	/**
	 * Recursively checks the equality of the given directories, based on their
	 * effective content (i.e. the files/sub-directories they contain and the
	 * contents of those files without whitespaces).
	 * 
	 * @see {@link #filesEqual(File, File)}, {@link #readEffectiveText(File)}
	 */
	public boolean dirsEqual(File dir1, File dir2) {
		this.logDebugMsg("Comparing: " + dir1.getName() + " and " + dir2.getName());

		// There cannot be 2 files with the same path, name and extension
		// so using TreeSet, which sorts the files spares doing so here
		var files1 = new TreeSet<File>();
		var files2 = new TreeSet<File>();

		var dir1Files = dir1.listFiles();
		if (dir1Files != null) {
			for (var f : dir1Files) {
				files1.add(f);
			}
		}

		var dir2Files = dir2.listFiles();
		if (dir2Files != null) {
			for (var f : dir2Files) {
				files2.add(f);
			}
		}

		if (files1.size() != files2.size()) {
			return false;
		}

		var fileIter1 = files1.iterator();
		var fileIter2 = files2.iterator();

		for (int i = 0; i < files1.size(); i++) {
			var f1 = fileIter1.next();
			var f2 = fileIter2.next();

			if (f1.isDirectory() && f2.isDirectory()) {
				if (!dirsEqual(f1, f2)) {
					this.logDebugMsg("Directories " + f1.getName() + " and " + f2.getName() + " are not equal");
					return false;
				}
			} else if (f1.isFile() && f2.isFile()) {
				if (!filesEqual(f1, f2)) {
					this.logDebugMsg("Files " + f1.getName() + " and " + f2.getName() + " are not equal");
					return false;
				}
			} else {
				this.logErrorMsg("Unexpected case there is a file and a directory");
				return false;
			}
		}

		return true;
	}

	/**
	 * Recursively cleans files. If a file or directory cannot be deleted, requests
	 * its deletion upon termination of JVM.
	 * 
	 * @param file The file or directory to delete
	 * @see {@link File#deleteOnExit()}
	 */
	public void deleteAll(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				var children = file.listFiles();

				if (children != null) {
					for (var cf : children) {
						this.deleteAll(cf);
					}
				}
			}

			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
	}

	/**
	 * A variant of {@link #deleteAll(File)} that converts the given path to a file.
	 */
	public void deleteAll(Path path) {
		this.deleteAll(path.toFile());
	}
}
