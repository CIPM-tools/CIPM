package cipm.consistency.fitests.repositorytests.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

import cipm.consistency.fitests.repositorytests.util.commentremoval.QuickCommentRemover;
import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * A class that computes expected similarity checking results (or expected
 * similarity values) based on the given GIT-Diffs. Provides numerous variants
 * of its computation method to allow re-using various GIT elements.<br>
 * <br>
 * Uses {@link QuickCommentRemover}, which removes commentaries in an
 * approximative fashion. Therefore, <b><i>the computed results may be
 * misleading</i></b>.
 * 
 * @author Alp Torac Genc
 */
public class RepoTestSimilarityValueEstimator {
	private static final int defaultContextLineCount = 3;
	private int contextLineCount = defaultContextLineCount;

	/**
	 * @param os          The {@link OutputStream} used by df
	 * @param df          The {@link DiffFormatter} that created diffEntries
	 * @param diffEntries A list of {@link DiffEntry} instances from diffing 2
	 *                    commits C1 and C2
	 * @return Whether model resources parsed from C1 and C2 are similar
	 */
	public boolean getExpectedSimilarityValueFor(OutputStream os, DiffFormatter df, List<DiffEntry> diffEntries) {
		try (var outputStream = os; var diffFormatter = df) {
			for (var e : diffEntries) {
				diffFormatter.format(e);
				// Adapt all UNIX new lines to the current system
				var code = this.getEffectiveLines(outputStream.toString().replaceAll("\\n", System.lineSeparator()));
				var expectedSimVal = this.computeExpectedSimilarityValue(code);
				if (!expectedSimVal)
					return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("IOException occured while computing expected similarity value", e);
		}

		return true;
	}

	/**
	 * @param diffEntries A list of {@link DiffEntry} instances from diffing 2
	 *                    commits C1 and C2
	 * @return Whether model resources parsed from C1 and C2 are similar
	 */
	public boolean getExpectedSimilarityValueFor(List<DiffEntry> diffEntries) {
		try (var os = new ByteArrayOutputStream()) {
			return this.getExpectedSimilarityValueFor(os, new DiffFormatter(os), diffEntries);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("IOException occured while computing expected similarity value", e);
		}
	}

	/**
	 * A variant of {@link #getExpectedSimilarityValueFor(Git, String, String)},
	 * where the commit parameters are replaced with their corresponding
	 * {@link AbstractTreeIterator}.
	 * 
	 * @param git         The object enclosing the GIT-repository that contains the
	 *                    given commits
	 * @param oldTreeIter A tree iterator from a commit from git
	 * @param newTreeIter A tree iterator from another commit from git
	 */
	public boolean getExpectedSimilarityValueFor(Git git, AbstractTreeIterator oldTreeIter,
			AbstractTreeIterator newTreeIter) {
		try (var os = new ByteArrayOutputStream(); var df = new DiffFormatter(os)) {
			df.setRepository(git.getRepository());
			df.setContext(this.getContextLineCount());
			df.setPathFilter(PathSuffixFilter.create(".java"));

			var entries = df.scan(oldTreeIter, newTreeIter);

			return this.getExpectedSimilarityValueFor(os, df, entries);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("IOException occured while computing expected similarity value", e);
		}
	}

	/**
	 * @param git       The object enclosing the GIT-repository that contains the
	 *                  given commits
	 * @param commitID1 A commit from git
	 * @param commitID2 Another commit from git
	 * @return Whether model resources parsed from the given commits are similar.
	 */
	public boolean getExpectedSimilarityValueFor(Git git, String commitID1, String commitID2) {
		try (var reader = git.getRepository().newObjectReader()) {
			var oldTreeIter = new CanonicalTreeParser();
			var oldTree = git.getRepository().resolve(commitID1 + "^{tree}");
			oldTreeIter.reset(reader, oldTree);

			var newTreeIter = new CanonicalTreeParser();
			var newTree = git.getRepository().resolve(commitID2 + "^{tree}");
			newTreeIter.reset(reader, newTree);

			return this.getExpectedSimilarityValueFor(git, oldTreeIter, newTreeIter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("IOException occured while computing expected similarity value", e);
		}
	}

	/**
	 * @param text A diff patch (or a snippet thereof) as string
	 * @return All non-blank lines from the given diff patch, which contain actual
	 *         changes to the text.
	 */
	public List<String> getEffectiveLines(String text) {
		var filter = new DiffFilter();
		var cr = new QuickCommentRemover();

		var result = cr.removeComments(text);
		var lines = filter.splitLines(result);
		lines = filter.removeContextLines(lines);
		lines = filter.removeNonPatchScript(lines);
		lines = filter.removeBlankLines(lines);

		return lines;
	}

	/**
	 * Computes whether applying the changes in the given diff DOES NOT introduce
	 * any changes to the effective code. Assuming the given diff is computed by
	 * comparing the commits oldCommit and newCommit:
	 * <ul>
	 * <li>true: oldCommit is still similar to newCommit, without applying the diff
	 * patch script on oldCommit
	 * <li>false: The diff patch introduces changes to the effective code, meaning
	 * that oldCommit and newCommit are not similar
	 * </ul>
	 * 
	 * @param lines The lines from a given diff patch script, without any metadata
	 */
	public boolean computeExpectedSimilarityValue(List<String> lines) {
		var added = new ArrayList<String>();
		var removed = new ArrayList<String>();

		lines.stream().forEach((l) -> {
			if (l.startsWith("+"))
				added.add(l.substring(1).replaceAll("\\s", ""));
			if (l.startsWith("-"))
				removed.add(l.substring(1).replaceAll("\\s", ""));
		});

		var allAdded = added.stream().reduce("", (t1, t2) -> t1 + t2);
		var allRemoved = removed.stream().reduce("", (t1, t2) -> t1 + t2);

		return allAdded.equals(allRemoved);
	}

	/**
	 * @return The number of context lines that will be considered while diffing, if
	 *         no {@link DiffFormatter} is explicitly provided. Defaults to
	 *         {@value #defaultContextLineCount}, unless re-set via
	 *         {@link #setContextLineCount(int)}.
	 */
	public int getContextLineCount() {
		return this.contextLineCount;
	}

	/**
	 * Sets the number of context lines that will be considered while diffing, if no
	 * {@link DiffFormatter} is explicitly provided. Defaults to
	 * {@value #defaultContextLineCount}, unless re-set via
	 * {@link #setContextLineCount(int)}.
	 */
	public void setContextLineCount(int contextLineCount) {
		this.contextLineCount = contextLineCount;
	}
}
