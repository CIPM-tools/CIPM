package cipm.consistency.fitests.repositorytests.util.difffilter;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Contains tests for filtering out non-patch-script lines from diffs. <br>
 * <br>
 * Each test method starts with the construction of an exemplary diff (as
 * String) with some lines that should be removed. This diff is then processed
 * via {@link DiffFilter} and the result is tested.
 * 
 * @author Alp Torac Genc
 */
public class DiffFilterTest {
	private static final DiffFilter filter = new DiffFilter();

	@Test
	public void testSplitLines_MultipleLines() {
		var line1 = "a";
		var line2 = "b";
		var line3 = "c";

		var text = String.format("%s%s%s%s%s", line1, System.lineSeparator(), line2, System.lineSeparator(), line3);

		var lines = filter.splitLines(text);
		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(line1, lines.get(0));
		Assertions.assertEquals(line2, lines.get(1));
		Assertions.assertEquals(line3, lines.get(2));
	}

	@Test
	public void testSplitLines_SingleLine() {
		var line = "a";

		var lines = filter.splitLines(line);
		Assertions.assertEquals(1, lines.size());
		Assertions.assertEquals(line, lines.get(0));
	}

	@Test
	public void testConcatLines_MultipleLines() {
		var line1 = "a";
		var line2 = "b";
		var line3 = "c";

		Assertions.assertEquals(
				String.format("%s%s%s%s%s", line1, System.lineSeparator(), line2, System.lineSeparator(), line3),
				filter.concatLines(line1, line2, line3));
		Assertions.assertEquals(
				String.format("%s%s%s%s%s", line1, System.lineSeparator(), line2, System.lineSeparator(), line3),
				filter.concatLines(List.of(line1, line2, line3)));
		Assertions.assertEquals(filter.concatLines(line1, line2, line3),
				filter.concatLines(List.of(line1, line2, line3)));
	}

	@Test
	public void testConcatLines_SingleLine() {
		var line = "a";

		Assertions.assertEquals(line, filter.concatLines(line));
		Assertions.assertEquals(line, filter.concatLines(List.of(line)));
		Assertions.assertEquals(filter.concatLines(line), filter.concatLines(List.of(line)));
	}

	@Test
	public void filterDiffHeader_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "diff --git a/file1.txt b/file1.txt";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterDiffHeader_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc diff --git a/file1.txt b/file1.txt";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterFileMetadataMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "index f2ba8f8..4a89512 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterFileMetadataMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc index f2ba8f8..4a89512 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterOldFileStateMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "+++ b/file1.txt";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterOldFileStateMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc +++ b/file1.txt";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterOldFileStateMessage_DevNull_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "+++ /dev/null";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterOldFileStateMessage_DevNull_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc +++ /dev/null";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterNewFileStateMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "--- a/file1.txt";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterNewFileStateMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc --- a/file1.txt";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterHunkHeaderMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "@@ -1 +1,6 @@";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterHunkHeaderMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc @@ -1 +1,6 @@";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterHunkHeaderMessage_NotLineEnd() {
		var linePred = "someText";
		var cmdLine = "@@ -1 +1,6 @@ abc";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterHunkHeaderMessage_SurroundedInLine() {
		var linePred = "someText";
		var cmdLine = "def @@ -1 +1,6 @@ abc";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterNoNewlineMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "\\ No newline at end of file";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterNoNewlineMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc \\ No newline at end of file";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterDeletedFileModeMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "deleted file mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterDeletedFileModeMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc deleted file mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterOldModeMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "old mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterOldModeMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc old mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterNewModeMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "new mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterNewModeMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc new mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterNewFileModeMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "new file mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterNewFileModeMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc new file mode 100644";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterSimilarityIndexMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "similarity index 10%";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterSimilarityIndexMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc similarity index 12.34%";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterDissimilarityIndexMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "dissimilarity index 10%";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterDissimilarityIndexMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc dissimilarity index 12.34%";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterRenamedToMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "rename to somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterRenamedToMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc rename to somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterRenamedFromMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "rename from somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterRenamedFromMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc rename from somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterCoppiedToMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "copy to somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterCoppiedToMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc copy to somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}

	@Test
	public void filterCoppiedFromMessage_IsolatedOnOneLine() {
		var linePred = "someText";
		var cmdLine = "copy from somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(lineSucc, lines.get(1));
	}

	@Test
	public void filterCoppiedFromMessage_NotLineStart() {
		var linePred = "someText";
		var cmdLine = "abc copy from somefile.someext";
		var lineSucc = "someMoreText";

		var text = filter.concatLines(linePred, cmdLine, lineSucc);

		var lines = filter.removeNonPatchScript(filter.splitLines(text));

		Assertions.assertEquals(3, lines.size());
		Assertions.assertEquals(linePred, lines.get(0));
		Assertions.assertEquals(cmdLine, lines.get(1));
		Assertions.assertEquals(lineSucc, lines.get(2));
	}
}
