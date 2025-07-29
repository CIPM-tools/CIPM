package cipm.consistency.fitests.repositorytests.util.commentremoval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * Contains tests for (approximative) commentary removal from code snippets that
 * are provided in form of Strings. <br>
 * <br>
 * Tests within this class are supposed to simulate cases; where the text is
 * only a snippet of the code containing commentary, which is placed in a
 * multi-line string literal. Since there are different ways to interpret the
 * given code without having access to all of it (ex: the multi-line string
 * tokens may belong to other preceding/proceeding string literals), adaptations
 * to test results may be necessary in the future. <br>
 * <br>
 * Each test method consists of a beginning, where the exemplary code snippet is
 * constructed (in form of text), then processed with an {@link ICommentRemover}
 * and tested.
 * 
 * @author Alp Torac Genc
 */
public class SurroundedMultiLineCommentTest {
	private static final String multiLineStringToken = "\"\"\"";

	private static final DiffFilter filter = new DiffFilter();
	private ICommentRemover cr = new QuickCommentRemover();

	@Test
	public void handleStringLiteral_SingleLineComment_StartAndStringOnSameLine() {
		var line1 = multiLineStringToken + "//abc";
		var line2 = multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(multiLineStringToken, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_SingleLineComment_EndAndStringOnSameLine() {
		var line1 = multiLineStringToken;
		var line2 = "//abc" + multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_SingleLineComment_SurroundingStartAndEnd() {
		var line1 = multiLineStringToken;
		var line2 = "//abc";
		var line3 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line3, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_MultiLineComment_OnSameLine() {
		var line1 = multiLineStringToken + "/*abc*/" + multiLineStringToken;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_MultiLineComment_StartAndStringOnSameLine() {
		var line1 = multiLineStringToken + "/*abc*/";
		var line2 = multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(multiLineStringToken, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_MultiLineComment_EndAndStringOnSameLine() {
		var line1 = multiLineStringToken;
		var line2 = "/*abc*/" + multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(multiLineStringToken, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_MultiLineComment_SurroundingStartAndEnd() {
		var line1 = multiLineStringToken;
		var line2 = "/*abc*/";
		var line3 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line3, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_JavaDoc_OnSameLine() {
		var line1 = multiLineStringToken + "/**abc*/" + multiLineStringToken;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void handleStringLiteral_JavaDoc_StartAndStringOnSameLine() {
		var line1 = multiLineStringToken + "/**abc*/";
		var line2 = multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(multiLineStringToken, filteredText.get(0));
		Assertions.assertEquals(line2, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_JavaDoc_EndAndStringOnSameLine() {
		var line1 = multiLineStringToken;
		var line2 = "/**abc*/" + multiLineStringToken;

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(multiLineStringToken, filteredText.get(1));
	}

	@Test
	public void handleStringLiteral_JavaDoc_SurroundingStartAndEnd() {
		var line1 = multiLineStringToken;
		var line2 = "/**abc*/";
		var line3 = multiLineStringToken;

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line3, filteredText.get(1));
	}
}
