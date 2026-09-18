package cipm.consistency.fitests.repositorytests.util.commentremoval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * Contains tests for (approximative) commentary removal from code snippets that
 * are provided in form of Strings. <br>
 * <br>
 * Tests within this class are supposed to simulate cases; where the text is
 * only a snippet of the code containing commentary and string literals, some of
 * which have been cut off. Since there are different ways to interpret the
 * given code without having access to all of it, adaptations to test results
 * may be necessary in the future. <br>
 * <br>
 * Each test method consists of a beginning, where the exemplary code snippet is
 * constructed (in form of text), then processed with an {@link ICommentRemover}
 * and tested.
 * 
 * @author Alp Torac Genc
 */
public class BrokenCommentTest {
	private static final String multiLineStringToken = "\"\"\"";
	private static final DiffFilter filter = new DiffFilter();

	private ICommentRemover cr = new QuickCommentRemover();

	@Test
	public void handleBlockComment_LeadingBrokenComment() {
		var line1 = "abc";
		var line2 = "*/";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertFalse(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingBrokenComment_BeforeMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = "*/";
		var line3 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line3, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertFalse(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingBrokenComment_AfterMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = multiLineStringToken;
		var line3 = "*/";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertFalse(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingBrokenComment_AfterFullMultiLineStringLiteral() {
		var line1 = multiLineStringToken;
		var line2 = "abc";
		var line3 = multiLineStringToken;
		var line4 = "*/";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertFalse(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingBrokenComment_InMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = multiLineStringToken;
		var line3 = "*/";
		var line4 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line4, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertFalse(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingBrokenComment_MultiLineStringTokenCheck() {
		var line1 = multiLineStringToken;
		var line2 = "abc";
		var line3 = multiLineStringToken;
		var line4 = "*/";
		var line5 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3, line4, line5);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line5, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertFalse(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_TrailingBrokenComment() {
		var line1 = "/*";
		var line2 = "abc";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
		Assertions.assertFalse(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_TrailingBrokenComment_BeforeMultiLineStringLiteral() {
		var line1 = "/*";
		var line2 = multiLineStringToken;
		var line3 = "abc";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
		Assertions.assertFalse(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_TrailingBrokenComment_BeforeFullMultiLineStringLiteral() {
		var line1 = "/*";
		var line2 = multiLineStringToken;
		var line3 = "abc";
		var line4 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
		Assertions.assertFalse(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_TrailingBrokenComment_AfterMultiLineStringLiteral() {
		var line1 = multiLineStringToken;
		var line2 = "/*";
		var line3 = "abc";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertFalse(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_TrailingBrokenComment_InMultiLineStringLiteral() {
		var line1 = multiLineStringToken;
		var line2 = "/*";
		var line3 = multiLineStringToken;
		var line4 = "abc";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertFalse(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_TrailingBrokenComment_MultiLineStringLiteralTokenCheck() {
		var line1 = multiLineStringToken;
		var line2 = "/*";
		var line3 = multiLineStringToken;
		var line4 = "abc";
		var line5 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3, line4, line5);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertFalse(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments() {
		var line1 = "abc";
		var line2 = "*/";
		var line3 = "def";
		var line4 = "/*";
		var line5 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line3, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments_BeforeMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = "*/";
		var line3 = "def";
		var line4 = "/*";
		var line5 = multiLineStringToken;
		var line6 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line3, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments_AfterMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = multiLineStringToken;
		var line3 = "*/";
		var line4 = "def";
		var line5 = "/*";
		var line6 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line4, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments_AroundMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = "*/";
		var line3 = "def";
		var line4 = multiLineStringToken;
		var line5 = "/*";
		var line6 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line3, filteredText.get(0));
		Assertions.assertEquals(line4, filteredText.get(1));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments_LeadingCommentSurroundedByMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = multiLineStringToken;
		var line3 = "*/";
		var line4 = multiLineStringToken;
		var line5 = "def";
		var line6 = "/*";
		var line7 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6, line7);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line4, filteredText.get(0));
		Assertions.assertEquals(line5, filteredText.get(1));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments_TrailingCommentSurroundedByMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = "*/";
		var line3 = "def";
		var line4 = multiLineStringToken;
		var line5 = "/*";
		var line6 = multiLineStringToken;
		var line7 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6, line7);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line3, filteredText.get(0));
		Assertions.assertEquals(line4, filteredText.get(1));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}

	@Test
	public void handleBlockComment_LeadingAndTrailingBrokenComments_AllCommentsSurroundedByMultiLineStringLiteral() {
		var line1 = "abc";
		var line2 = multiLineStringToken;
		var line3 = "*/";
		var line4 = "def";
		var line5 = "/*";
		var line6 = multiLineStringToken;
		var line7 = "hgf";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6, line7);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line4, filteredText.get(0));
		Assertions.assertTrue(cr.hasLeadingBrokenComment(text));
		Assertions.assertTrue(cr.hasTrailingBrokenComment(text));
	}
}
