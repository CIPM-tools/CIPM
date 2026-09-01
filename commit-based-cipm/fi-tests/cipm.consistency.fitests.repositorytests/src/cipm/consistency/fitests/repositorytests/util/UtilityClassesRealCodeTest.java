package cipm.consistency.fitests.repositorytests.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.repositorytests.util.commentremoval.QuickCommentRemover;
import cipm.consistency.fitests.repositorytests.util.difffilter.DiffFilter;

/**
 * A test class that tests {@link QuickCommentRemover} and {@link DiffFilter} on
 * real GIT diff patches.
 * 
 * @author Alp Torac Genc
 */
public class UtilityClassesRealCodeTest {
	private static final QuickCommentRemover qr = new QuickCommentRemover();
	private static final DiffFilter filter = new DiffFilter();

	@Test
	public void parserTestRepo_Test1() {
		var classDecl = "public class Cls1 {";
		var methodDecl = "public void met1() {";

		var text = "diff --git a/Cls1.java b/Cls1.java\r\n"
				//
				+ "index 921acbe..309f622 100644\r\n"
				//
				+ "--- a/Cls1.java\r\n"
				//
				+ "+++ b/Cls1.java\r\n"
				//
				+ "@@ -1,3 +1,6 @@\r\n"
				//
				+ "+/**\r\n"
				//
				+ "+ * SomeCommentary\r\n"
				//
				+ "+ */\r\n"
				//
				+ classDecl + "\r\n"
				//
				+ methodDecl + "\r\n"
				//
				+ "				\r\n";

		var lines = filter.removeBlankLines(filter.removeNonPatchScript(filter.splitLines(qr.removeComments(text))));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(classDecl, lines.get(0));
		Assertions.assertEquals(methodDecl, lines.get(1));
	}

	@Test
	public void parserTestRepo_Test2() {
		var classDecl = "public class Cls1 {";
		var methodDecl = "public void met1() {";

		var text = "diff --git a/Cls1.java b/Cls1.java\r\n"
				//
				+ "index 309f622..921acbe 100644\r\n"
				//
				+ "--- a/Cls1.java\r\n"
				//
				+ "+++ b/Cls1.java\r\n"
				//
				+ "@@ -1,6 +1,3 @@\r\n"
				//
				+ "-/**\r\n"
				//
				+ "- * SomeCommentary\r\n"
				//
				+ "- */\r\n"
				//
				+ classDecl + "\r\n"
				//
				+ methodDecl + "\r\n"
				//
				+ "				\r\n";

		var lines = filter.removeBlankLines(filter.removeNonPatchScript(filter.splitLines(qr.removeComments(text))));

		Assertions.assertEquals(2, lines.size());
		Assertions.assertEquals(classDecl, lines.get(0));
		Assertions.assertEquals(methodDecl, lines.get(1));
	}
}
