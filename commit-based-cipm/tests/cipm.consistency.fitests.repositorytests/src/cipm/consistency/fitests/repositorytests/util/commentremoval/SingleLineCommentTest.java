package cipm.consistency.fitests.repositorytests.util.commentremoval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * Contains tests for (approximative) commentary removal from code snippets that
 * are provided in form of Strings. <br>
 * <br>
 * Tests within this class are supposed to simulate cases; where the text is
 * only a snippet of the code containing commentary, some of which containing
 * string tokens. <br>
 * <br>
 * Each test method consists of a beginning, where the exemplary code snippet is
 * constructed (in form of text), then processed with an {@link ICommentRemover}
 * and tested.
 * 
 * @author Alp Torac Genc
 */
public class SingleLineCommentTest {
	private static final String multiLineStringToken = "\"\"\"";

	private ICommentRemover cr = new QuickCommentRemover();
	private static final DiffFilter filter = new DiffFilter();

	@Test
	public void removeSingleLineComment_PrecedingCode() {
		var code = "def ";
		var line1 = code + "// abc";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void removeSingleLineComment_NoContext() {
		var line1 = "// abc";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeSingleLineComment_PrecedingContext() {
		var line1 = "def ";
		var line2 = "// abc ";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void removeSingleLineComment_FollowingContext() {
		var line1 = "// abc ";
		var line2 = "def ";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line2, filteredText.get(0));
	}

	@Test
	public void removeSingleLineComment_SurroundingContext() {
		var line1 = "def ";
		var line2 = "// abc ";
		var line3 = "hgf ";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line3, filteredText.get(1));
	}

	@Test
	public void removeSingleLineComment_MultipleComments_PrecedingCode() {
		var code = "def ";
		var line1 = code + "// abc";
		var line2 = "// hgf";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void removeSingleLineComment_MultipleComments_NoContext() {
		var line1 = "// abc";
		var line2 = "// def";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeSingleLineComment_MultipleComments_PrecedingContext() {
		var line1 = "def ";
		var line2 = "// abc ";
		var line3 = "// hgf ";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void removeSingleLineComment_MultipleComments_FollowingContext() {
		var line1 = "// abc ";
		var line2 = "// hgf ";
		var line3 = "def ";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line3, filteredText.get(0));
	}

	@Test
	public void removeSingleLineComment_MultipleComments_SurroundingContext() {
		var line1 = "def ";
		var line2 = "// abc ";
		var line3 = "// jkl ";
		var line4 = "hgf ";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line4, filteredText.get(1));
	}

	@Test
	public void removeSingleLineComment_RepeatingSlashes() {
		var line1 = "// // abc";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeSingleLineComment_SurroundingSlashes() {
		var line1 = "// abc //";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void handleStringLiteralInComment_SingleLineStringLiteral() {
		var line1 = "// \"abc\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void handleStringLiteralInComment_MultiLineStringLiteral() {
		var line1 = "// " + multiLineStringToken + "abc" + multiLineStringToken;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void handleStringLiteralInComment_MultiLineStringLiteral_Split() {
		var line1 = "// " + multiLineStringToken + "abc";
		var line2 = "// " + multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}
}
