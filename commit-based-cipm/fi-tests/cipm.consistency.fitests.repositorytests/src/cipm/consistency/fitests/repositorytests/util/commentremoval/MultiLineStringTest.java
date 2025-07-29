package cipm.consistency.fitests.repositorytests.util.commentremoval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * Contains tests for (approximative) commentary removal from code snippets that
 * are provided in form of Strings. <br>
 * <br>
 * Tests within this class are supposed to simulate cases; where the text is
 * only a snippet of the code containing multi-line strings, which potentially
 * contains commentary tokens. Since there are different ways to interpret the
 * given code without having access to all of it (ex: string tokens could in
 * reality be a part of a preceding/proceeding string literal, meaning that the
 * commentary that is supposedly a part of a string literal is in fact outside
 * the string literal), adaptations to test results may be necessary in the
 * future. <br>
 * <br>
 * Each test method consists of a beginning, where the exemplary code snippet is
 * constructed (in form of text), then processed with an {@link ICommentRemover}
 * and tested.
 * 
 * @author Alp Torac Genc
 */
public class MultiLineStringTest {
	private ICommentRemover cr = new QuickCommentRemover();
	private static final DiffFilter filter = new DiffFilter();

	@Test
	public void handleStringLiteral_SingleLineString_OnSameLine() {
		var line1 = "\"\"\"abc\"\"\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_SingleLineString_StartAndStringOnSameLine() {
		var line1 = "\"\"\"abc";
		var line2 = "\"\"\"";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_SingleLineString_EndAndStringOnSameLine() {
		var line1 = "\"\"\"";
		var line2 = "abc\"\"\"";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_SingleLineString_SurroundingStartAndEnd() {
		var line1 = "\"\"\"";
		var line2 = "abc";
		var line3 = "\"\"\"";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(3, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
		Assertions.assertEquals(line3, filteredText.get(2));
	}

	@Test
	public void handleStringLiteral_MultiLineString_OnSameLine() {
		var line1 = "\"\"\"abc";
		var line2 = "def\"\"\"";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_MultiLineString_StartAndStringOnSameLine() {
		var line1 = "\"\"\"abc";
		var line2 = "def";
		var line3 = "\"\"\"";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(3, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
		Assertions.assertEquals(line3, filteredText.get(2));
	}

	@Test
	public void handleStringLiteral_MultiLineString_EndAndStringOnSameLine() {
		var line1 = "\"\"\"";
		var line2 = "abc";
		var line3 = "def\"\"\"";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(3, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
		Assertions.assertEquals(line3, filteredText.get(2));
	}

	@Test
	public void handleStringLiteral_MultiLineString_SurroundingStartAndEnd() {
		var line1 = "\"\"\"";
		var line2 = "abc";
		var line3 = "def";
		var line4 = "\"\"\"";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(4, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
		Assertions.assertEquals(line3, filteredText.get(2));
		Assertions.assertEquals(line4, filteredText.get(3));
	}

	@Test
	public void handleStringLiteral_SingleLineComment_OnSameLine() {
		var line1 = "\"\"\"//abc\"\"\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}
}
