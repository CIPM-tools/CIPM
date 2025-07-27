package cipm.consistency.fitests.repositorytests.util.commentremoval;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * Contains tests for (approximative) commentary removal from code snippets that
 * are provided in form of Strings. <br>
 * <br>
 * Tests within this class are supposed to simulate cases; where the text is
 * only a snippet of the code containing JavaDoc commentary, which potentially
 * contains string tokens. Since there are different ways to interpret the given
 * code without having access to all of it (ex: the JavaDoc comment could in
 * reality be a part of a preceding/proceeding string literal), adaptations to
 * test results may be necessary in the future. <br>
 * <br>
 * Each test method consists of a beginning, where the exemplary code snippet is
 * constructed (in form of text), then processed with an {@link ICommentRemover}
 * and tested.
 * 
 * @author Alp Torac Genc
 */
public class JavaDocTest {
	private static final String multiLineStringToken = "\"\"\"";

	private static final DiffFilter filter = new DiffFilter();
	private ICommentRemover cr = new QuickCommentRemover();

	@Test
	public void removeJavaDoc_SingleLine_FollowingCode() {
		var code = "def ";

		var line1 = "/** abc */" + code;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_SingleLine_SurroundingCode() {
		var code1 = "def ";
		var code2 = "hgf ";

		var line1 = code1 + "/** abc */" + code2;

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code1 + code2, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_SingleLine_NoContext() {
		var line1 = "/** abc */";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_SingleLine_PrecedingContext() {
		var line1 = "def ";
		var line2 = "/** abc */";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_SingleLine_FollowingContext() {
		var line1 = "/** abc */";
		var line2 = "def ";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line2, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_SingleLine_SurroundingContext() {
		var line1 = "def ";
		var line2 = "/** abc */";
		var line3 = "hgf ";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line3, filteredText.get(1));
	}

	@Test
	public void removeJavaDoc_MultipleLine_PrecedingCode() {
		var code = "def ";

		var line1 = code + "/**";
		var line2 = "abc";
		var line3 = "*/";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_MultipleLine_FollowingCode() {
		var code = "def ";

		var line1 = "/**";
		var line2 = "abc";
		var line3 = "*/" + code;

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_MultipleLine_SurroundingCode() {
		var code1 = "def ";
		var code2 = "hgf ";

		var line1 = code1 + "/**";
		var line2 = "abc";
		var line3 = "*/" + code2;

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		// New lines were inside the commentary
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code1 + code2, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_MultipleLine_NoContext() {
		var line1 = "/**";
		var line2 = "abc";
		var line3 = "*/";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleLine_PrecedingContext() {
		var line1 = "def ";
		var line2 = "/**";
		var line3 = "abc";
		var line4 = "*/";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_MultipleLine_FollowingContext() {
		var line1 = "/**";
		var line2 = "abc";
		var line3 = "*/";
		var line4 = "def ";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(line4, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_MultipleLine_SurroundingContext() {
		var line1 = "def ";
		var line2 = "/**";
		var line3 = "abc";
		var line4 = "*/";
		var line5 = "hgf ";

		var text = filter.concatLines(line1, line2, line3, line4, line5);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(2, filteredText.size());
		Assertions.assertEquals(line1, filteredText.get(0));
		Assertions.assertEquals(line5, filteredText.get(1));
	}

	@Test
	public void removeJavaDoc_SingleLine_PrecedingCode() {
		var code = "def ";

		var line1 = code + "/** abc */";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(1, filteredText.size());
		Assertions.assertEquals(code, filteredText.get(0));
	}

	@Test
	public void removeJavaDoc_MultipleComments_BothSingleLine() {
		var line1 = "/** abc */";
		var line2 = "/** def */";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleComments_BothInSameLineNoSpace() {
		var line1 = "/** abc *//** def */";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleComments_BothInSameLineWithSpace() {
		var line1 = "/** abc */ /** def */";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleComments_OneSingleLineOneMultipleLine() {
		var line1 = "/** abc */";
		var line2 = "/**";
		var line3 = "def";
		var line4 = " */";

		var text = filter.concatLines(line1, line2, line3, line4);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleComments_OneSingleLineOneMultipleLine_NoSpace() {
		var line1 = "/** abc *//**";
		var line2 = "def";
		var line3 = " */";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleComments_OneSingleLineOneMultipleLine_WithSpace() {
		var line1 = "/** abc */ /**";
		var line2 = "def";
		var line3 = " */";

		var text = filter.concatLines(line1, line2, line3);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void removeJavaDoc_MultipleComments_BothMultipleLine() {
		var line1 = "/**";
		var line2 = "abc";
		var line3 = " */";
		var line4 = "/**";
		var line5 = "def";
		var line6 = " */";

		var text = filter.concatLines(line1, line2, line3, line4, line5, line6);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));

		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void handleStringLiteralInComment_SingleLineStringLiteral() {
		var line1 = "/** \"abc\" */";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void handleStringLiteralInComment_MultiLineStringLiteral() {
		var line1 = "/** " + multiLineStringToken + "abc" + multiLineStringToken + " */";

		var text = filter.concatLines(line1);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}

	@Test
	public void handleStringLiteralInComment_MultiLineStringLiteral_Split() {
		var line1 = "/** " + multiLineStringToken + "abc";
		var line2 = multiLineStringToken + " */";

		var text = filter.concatLines(line1, line2);

		var filteredText = filter.removeBlankLines(filter.splitLines(cr.removeComments(text)));
		Assertions.assertEquals(0, filteredText.size());
	}
}
