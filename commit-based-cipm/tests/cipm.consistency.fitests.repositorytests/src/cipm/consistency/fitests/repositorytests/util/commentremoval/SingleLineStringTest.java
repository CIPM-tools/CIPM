package cipm.consistency.fitests.repositorytests.util.commentremoval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * Contains tests for (approximative) commentary removal from code snippets that
 * are provided in form of Strings. <br>
 * <br>
 * Tests within this class are supposed to simulate cases; where the text is
 * only a snippet of the code containing string literals, some of which
 * containing commentary tokens. <br>
 * <br>
 * Each test method consists of a beginning, where the exemplary code snippet is
 * constructed (in form of text), then processed with an {@link ICommentRemover}
 * and tested.
 * 
 * @author Alp Torac Genc
 */
public class SingleLineStringTest {
	private static final String multiLineStringToken = "\"\"\"";

	private static final DiffFilter filter = new DiffFilter();
	private ICommentRemover cr = new QuickCommentRemover();

	@Test
	public void handleStringLiteral() {
		var line1 = "\"abc\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_WithSingleEscapedQuotation() {
		var line1 = "\"\\\"abc\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_WithMultipleEscapedQuotations() {
		var line1 = "\"\\\"abc\\\"\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_MultiLineStringAsSingleLineString() {
		var line1 = multiLineStringToken + "//abc" + multiLineStringToken;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleSingleLineCommentInStringLiteral() {
		var line1 = "\"// abc\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleMultiLineCommentInStringLiteral() {
		var line1 = "\"/* abc */\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleJavaDocInStringLiteral() {
		var line1 = "\"/** abc */\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleSingleLineCommentInStringLiteral_WithEscapedQuotation() {
		var line1 = "\"\\\"// abc\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleMultiLineCommentInStringLiteral_WithEscapedQuotation() {
		var line1 = "\"\\\"/* abc */\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleJavaDocInStringLiteral_WithEscapedQuotation() {
		var line1 = "\"\\\"/** abc */\"";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleUnclosedStringLiteral() {
		var code = "\"abc";
		var comment = "//def";
		var line1 = code + comment;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void handleUnclosedStringLiteral_WithEscapedQuotationStart() {
		var code = "\\\"abc";
		var comment = "//def\"";
		var line1 = code + comment;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void handleUnclosedStringLiteral_WithEscapedQuotationEnd() {
		var code = "\"abc";
		var comment = "//def\\\"";
		var line1 = code + comment;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void handleUnclosedStringLiteral_WithSingleLineComment() {
		var start = "\"";
		var line1 = start + "//abc";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(start, filteredText.get(0));
	}

	@Test
	public void handleUnclosedStringLiteral_WithMultiLineComment() {
		var start = "\"";
		var line1 = start + "/*abc*/";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(start, filteredText.get(0));
	}

	@Test
	public void handleUnclosedStringLiteral_WithJavaDoc() {
		var start = "\"";
		var line1 = start + "/**abc*/";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(start, filteredText.get(0));
	}

	@Test
	public void handleBrokenStringLiteral_SingleLineString_AcrossTwoLines() {
		var comment = "//abc";

		var line1 = "\"" + comment;
		var line2 = "\"";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals("\"", filteredText.get(0));
		Assertions.assertEquals("\"", filteredText.get(1));
	}

	@Test
	public void handleBrokenStringLiteral_SingleLineString_AcrossThreeLines() {
		var line1 = "\"";
		var line2 = "//abc";
		var line3 = "\"";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals("\"", filteredText.get(0));
		Assertions.assertEquals("\"", filteredText.get(1));
	}
}
