package cipm.consistency.fitests.repositorytests.util.commentremoval;

import java.util.regex.Pattern;

/**
 * A comment remover that gives precedence to block-comment tokens (such as
 * {@code /*, * /}) over string tokens (i.e. {@code " and """}), unless the
 * comment is guaranteed to be a part of a string literal. That means, all
 * potentially broken block-comments are removed, even if they are a part of a
 * string literal in reality. <b><i>The comment removal offered by this class is
 * an approximation</i></b>. <br>
 * <br>
 * Note: <b><i>Currently does not support parsing multi-line strings as
 * is</i></b>; due to them having the same token mark their start and end, as
 * well as potentially spanning over multiple lines. This makes it so that one
 * cannot determine whether a multi-line string token is supposed to mark the
 * start or the end of the string literal better than guessing, unlike tokens
 * from block commentaries that are different. Therefore; <b><i>multi-line
 * strings are interpreted as 3 consecutive, single-line strings, where the
 * first and the last single-line strings are blank</i></b>.
 * 
 * @author Alp Torac Genc
 */
public class QuickCommentRemover implements ICommentRemover {
	/**
	 * Pattern that matches a single line string literal's start or end, i.e. an
	 * unescaped quotation mark.
	 */
	private static final Pattern singleLineStringLiteralQuotation = Pattern.compile("(?<!\\\\)\"");
	/**
	 * Pattern that matches the start of a single line comment, i.e. double forward
	 * slashes.
	 */
	private static final Pattern singleLineComment = Pattern.compile("//");
	/**
	 * Pattern that matches the start of a block comment (and JavaDoc), i.e. a
	 * forward slash followed by a star (this also includes JavaDoc starts).
	 */
	private static final Pattern multiLineBlockCommentStart = Pattern.compile("/\\*");
	/*
	 * Pattern that matches the end of a block comment (and JavaDoc), i.e. a star
	 * followed by a forward slash (this also includes JavaDoc ends).
	 */
	private static final Pattern multiLineBlockCommentEnd = Pattern.compile("\\*/");

	private static final String multiLineStringToken = "\"\"\"";
	private static final String blockCommentStart = "/*";
	private static final String blockCommentEnd = "*/";
	private static final String lineSeparator = System.lineSeparator();

	/**
	 * Single line string literals (such as {@code "abc"}) cannot be nested. Start
	 * from quotationIdx and look for the next quotation mark, which marks the end
	 * of the string literal. Exclude escaped quotation marks (i.e. {@code \"}) in
	 * the process. <br>
	 * <br>
	 * <b><i>Does not account for potential multi-line string declarations
	 * {@code """..."""}. Multi-line string declarations are handled, as if they
	 * were consecutive single line string literals</i></b>. This means, multi-line
	 * strings that are declared on the same line are still detected as string
	 * literals. However, multi-line strings that are declared over multiple lines
	 * are detected as faulty string literals, i.e. this method returns -1.
	 * 
	 * @param quotationIdx The index of the quotation mark, which starts the string
	 *                     literal. In {@code "abc"}, it is 0.
	 * @param text         The text that should be analysed for single line string
	 *                     literals
	 * @return The first index at the end of the single line string literal. In
	 *         {@code "abc"}, it is 4. Returns -1 if the string literal never ends.
	 */
	private int parseSingleLineStringLiteral(int quotationIdx, String text) {
		var matcher = singleLineStringLiteralQuotation.matcher(text);
		if (!matcher.find(quotationIdx) || matcher.start() != quotationIdx)
			// quotationIdx does not mark the start of a single line string literal
			return -1;

		/*
		 * If this line is not the last line of the text, limit the matcher's range to
		 * the index of the line separator, since single line string literals have to
		 * start and end on the same line.
		 */
		var newLineIdx = text.indexOf(lineSeparator, quotationIdx);
		var rangeEnd = newLineIdx != -1 ? newLineIdx : text.length();

		/*
		 * Start from quotationIdx + 1 to not re-match the starting quotation mark,
		 * since the starting and ending tokens (") are the same.
		 * 
		 * Single line strings have to start and end on the same line, so only consider
		 * the char sequence between the starting quotation mark and the end of the line
		 * (either line break or end of text).
		 */
		return matcher.region(quotationIdx + 1, rangeEnd).find() ?
		// End of the string literal found, return index after closing quotation mark
				matcher.end() :
				// Single line string literal is never closed => Problem with text
				-1;
	}

	/**
	 * Multi line string literals (such as {@code """abc"""}) cannot be nested.
	 * Start from quotationIdx and look for the end of the string literal.
	 * 
	 * @param quotationIdx The starting index of the multi line string token
	 *                     {@code """}, which starts the string literal. In
	 *                     {@code """abc"""}, it is 0.
	 * @param text         The text that should be analysed for multi line string
	 *                     literals
	 * @return The first index at the end of the multi line string literal. . In
	 *         {@code """abc"""}, it is 8. Returns -1 if the string literal never
	 *         ends.
	 */
	@SuppressWarnings("unused")
	private int parseMultiLineStringLiteral(int quotationIdx, String text) {
		var mlstLen = multiLineStringToken.length();

		// Make sure that there are enough characters left for a multi-line string
		if (text.length() < quotationIdx + mlstLen
				|| !text.substring(quotationIdx, quotationIdx + mlstLen).equals(multiLineStringToken))
			// quotationIdx does not mark the start of a multi line string literal
			return -1;

		// Skip quotationIdx, since the starting and ending tokens (""") are the same
		for (int i = quotationIdx + mlstLen; i <= text.length() - mlstLen; i++) {
			if (text.substring(i, i + mlstLen).equals(multiLineStringToken)) {
				// End of the multi-line string found, return index after multiLineStringToken
				// (""")
				return i + mlstLen;
			}
		}

		// Multi line string literal is never closed => Problem with text
		return -1;
	}

	/**
	 * Single line comments (such as {@code // abc}) cannot be nested. Start from
	 * doubleSlashIdx and look for the end of the line.
	 * 
	 * @param doubleSlashIdx The starting index of the single line comment
	 * @param text           The text that should be analysed for single line
	 *                       comments
	 * @return The first index at the end of the single line comment, i.e. the
	 *         beginning of the next line.
	 */
	private int parseSingleLineComment(int doubleSlashIdx, String text) {
		var matcher = singleLineComment.matcher(text);

		if (!matcher.find(doubleSlashIdx) || matcher.start() != doubleSlashIdx) {
			return -1;
		}

		var lineSepIdx = text.indexOf(lineSeparator, doubleSlashIdx);
		if (lineSepIdx != -1)
			return lineSepIdx;

		// No line break found, single line comment goes till the end of the text
		return text.length();
	}

	/**
	 * Multi line comments (such as {@code // abc}) cannot be nested. Start from
	 * slashStarIdx and look for the end of the multi line comment. <br>
	 * <br>
	 * Accounts for both multi line comments (i.e. {@code /* ... * /} ) and JavaDoc
	 * (i.e. {@code /** .... * /}).
	 * 
	 * @param slashStarIdx The starting index of the multi line comment
	 * @param text         The text that should be analysed for multi line comments
	 * @return The first index at the end of the multi line comment. Returns -1 if
	 *         the comment never ends.
	 */
	private int parseBlockComment(int slashStarIdx, String text) {
		var startMatcher = multiLineBlockCommentStart.matcher(text);

		if (!startMatcher.find(slashStarIdx) || startMatcher.start() != slashStarIdx) {
			return -1;
		}

		var endMatcher = multiLineBlockCommentEnd.matcher(text);
		if (endMatcher.find(startMatcher.end())) {
			// Comment end found, return index after comment
			return endMatcher.end();
		}

		// Block comment never closed => Problem with text
		return -1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link QuickCommentRemover} for more information.
	 */
	public String removeComments(String text) {
		var result = "";

		var currentCharIdx = 0;
		while (currentCharIdx < text.length()) {
			var parseEndIdx = -1;

			/*
			 * Check order:
			 * 
			 * 1) Single line string literals: Can contain tokens of comments, which turns
			 * them into substrings as opposed to commentary tokens
			 * 
			 * 2) Block comments: May start and end in a single line (can end before the end
			 * of the line), includes JavaDocs as well. Could contain single line comment
			 * token "//", which makes it a part of the block comment as opposed to making
			 * the rest of the line commentary. In this case, the commentary ends with the
			 * end of the block comment and not with the end of the line.
			 * 
			 * 3) Single line comments (can only end with newline)
			 */

			if ((parseEndIdx = this.parseSingleLineStringLiteral(currentCharIdx, text)) != -1) {
				result += text.substring(currentCharIdx, parseEndIdx);
			} else if ((parseEndIdx = this.parseBlockComment(currentCharIdx, text)) != -1) {
			} else if ((parseEndIdx = this.parseSingleLineComment(currentCharIdx, text)) != -1) {
			} else {
				result += text.charAt(currentCharIdx);
			}

			if (parseEndIdx != -1) {
				currentCharIdx = parseEndIdx;
			} else {
				currentCharIdx++;
			}
		}

		if (this.hasLeadingBrokenComment(result)) {
			// There is leading broken commentary, cut it out
			var blockCommentEndIdx = result.indexOf(blockCommentEnd);
			result = result.substring(blockCommentEndIdx + blockCommentEnd.length(), result.length());
		}
		if (this.hasTrailingBrokenComment(result)) {
			// There is trailing broken commentary, cut it out
			result = result.substring(0, result.lastIndexOf(blockCommentStart));
		}

		return result;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Does not account for the broken comments to be a part of a multi-line string.
	 */
	public boolean hasLeadingBrokenComment(String text) {
		var blockCommentStartIdx = text.indexOf(blockCommentStart);
		var blockCommentEndIdx = text.indexOf(blockCommentEnd);

		return blockCommentEndIdx != -1 && (blockCommentStartIdx == -1 || blockCommentStartIdx > blockCommentEndIdx);

	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * Does not account for the broken comments to be a part of a multi-line string.
	 */
	public boolean hasTrailingBrokenComment(String text) {
		var blockCommentStartIdx = text.lastIndexOf(blockCommentStart);
		var blockCommentEndIdx = text.lastIndexOf(blockCommentEnd);

		return blockCommentStartIdx != -1 && (blockCommentEndIdx == -1 || blockCommentStartIdx > blockCommentEndIdx);
	}
}
