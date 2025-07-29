package cipm.consistency.fitests.repositorytests.util.difffilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A class that can filter out the non-script parts of a given diff patch.
 * 
 * @author Alp Torac Genc
 */
public class DiffFilter {
	/**
	 * Pattern for the beginning of a diff script line.
	 */
	private static final Pattern diffLineSignPattern = Pattern.compile("^(?:\\+|-)");
	/**
	 * Pattern for a diff script line, which only contains whitespace characters,
	 * i.e. an empty diff script line.
	 */
	private static final Pattern diffLineWhitespacePattern = Pattern
			.compile(String.format("%s?\\s*$", diffLineSignPattern.pattern()));

	/**
	 * Pattern for a diff header, i.e. the line that contains the diff command.
	 */
	private static final Pattern diffHeaderPattern = Pattern.compile("^\\s*diff --git .*");
	/**
	 * Pattern for a hunk header in a diff patch, i.e. the part where a hunk is
	 * located.
	 */
	private static final Pattern diffHunkHeaderPattern = Pattern.compile("^@@ .* @@$");

	/**
	 * Pattern for file metadata pattern in a diff patch.
	 */
	private static final Pattern diffFileMetadataPattern = Pattern.compile("^index .*");

	/**
	 * Pattern for mode lines in a diff patch.
	 */
	private static final Pattern diffModePattern = Pattern.compile("^(?:old|new|new file|deleted file) mode .*");
	/**
	 * Pattern for rename lines in a diff patch.
	 */
	private static final Pattern diffRenamePattern = Pattern.compile("^rename (?:to|from) .*\\.\\w*");
	/**
	 * Pattern for copy lines in a diff patch.
	 */
	private static final Pattern diffCopyPattern = Pattern.compile("^copy (?:to|from) .*\\.\\w*");

	/**
	 * Pattern for a new file state line in a diff patch.
	 */
	private static final Pattern diffNewFileStatePattern = Pattern.compile("^\\+\\+\\+ (?:.*\\.\\w*|/.*)");
	/**
	 * Pattern for an old file state line in a diff patch.
	 */
	private static final Pattern diffOldFileStatePattern = Pattern.compile("^--- (?:.*\\.\\w*|/.*)");

	/**
	 * Pattern for similarity index line in a diff patch.
	 */
	private static final Pattern diffSimilarityIndexPattern = Pattern.compile("^similarity index \\d*\\.?\\d+%");
	/**
	 * Pattern for dissimilarity index line in a diff patch.
	 */
	private static final Pattern diffDissimilarityIndexPattern = Pattern.compile("^dissimilarity index \\d*\\.?\\d+%");

	/**
	 * The line in a diff patch that indicates the absence of a newline character at
	 * the end of a file.
	 */
	private static final String diffNoNewLineMessage = "\\ No newline at end of file";

	/**
	 * The line separator used by the current OS.
	 */
	private static final String lineSeparator = System.lineSeparator();

	/**
	 * @return Splits the given (multi-line) text into its lines, where lines are
	 *         separated via the line separator used by the system.
	 */
	public List<String> splitLines(String text) {
		var lines = new ArrayList<String>();

		// Do not use System.lineSeparator since GIT uses UNIX terminal
		// UNIX terminal uses "\n" for new line
		var diffLines = text.split(lineSeparator);

		for (var l : diffLines) {
			lines.add(l);
		}

		return lines;
	}

	/**
	 * Concatenates the given lines into a single String by gluing them with the
	 * line separator used by the system.
	 * 
	 * @return All lines as one String. Returns empty String if lines is null.
	 */
	public String concatLines(String... lines) {
		var result = "";

		if (lines == null)
			return result;

		for (int i = 0; i < lines.length - 1; i++)
			result += lines[i] + lineSeparator;

		result += lines[lines.length - 1];

		return result;
	}

	/**
	 * Concatenates the given lines into a single String by gluing them with the
	 * line separator used by the system.
	 * 
	 * @return All lines as one String. Returns empty String if lines is null.
	 */
	public String concatLines(List<String> lines) {
		if (lines == null)
			return "";

		return this.concatLines(lines.toArray(String[]::new));
	}

	/**
	 * Modifies and returns the given list.
	 * 
	 * @return The given list with only diff patch script lines.
	 */
	public List<String> removeNonPatchScript(List<String> lines) {
		lines.removeIf((l) -> !isPatchScriptLine(l));
		return lines;
	}

	/**
	 * Modifies and returns the given list.
	 * 
	 * @return The given list without blank diff patch script lines.
	 */
	public List<String> removeBlankLines(List<String> lines) {
		lines.removeIf((l) -> diffLineWhitespacePattern.matcher(l).matches());
		return lines;
	}

	/**
	 * Modifies and returns the given list.
	 * 
	 * @return The given list without context lines, i.e. lines in the diff patch
	 *         script that are only there to provide context.
	 */
	public List<String> removeContextLines(List<String> lines) {
		lines.removeIf((l) -> !diffLineSignPattern.matcher(l).find());
		return lines;
	}

	/**
	 * @return Whether the given line is a part of the diff patch script.
	 */
	public boolean isPatchScriptLine(String line) {
		if (diffHeaderPattern.matcher(line).find()) {
			return false;
		} else if (diffNewFileStatePattern.matcher(line).find()) {
			return false;
		} else if (diffOldFileStatePattern.matcher(line).find()) {
			return false;
		} else if (diffFileMetadataPattern.matcher(line).find()) {
			return false;
		} else if (diffHunkHeaderPattern.matcher(line).find()) {
			return false;
		} else if (diffModePattern.matcher(line).find()) {
			return false;
		} else if (diffSimilarityIndexPattern.matcher(line).find()) {
			return false;
		} else if (diffCopyPattern.matcher(line).find()) {
			return false;
		} else if (diffDissimilarityIndexPattern.matcher(line).find()) {
			return false;
		} else if (diffRenamePattern.matcher(line).find()) {
			return false;
		} else if (line.equals(diffNoNewLineMessage)) {
			return false;
		} else {
			return true;
		}
	}
}
