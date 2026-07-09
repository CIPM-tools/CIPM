package cipm.consistency.fluentapi.gen;

import java.util.Map;

/**
 * Utility class for assembling documentation strings during fluent API code
 * generation.
 * 
 * @author Alp Torac Genc
 */
public final class FluentAPIDocumentationUtil {
	private static final String doNotUseFromOutsideDocumentationNote = "This method is not intended for outside use, but is generated as public because of code generation limitations.";
	private static final String documentationParagraphSeparator = "<p><p>";

	private static final String classMethodOverviewIntroTemplate = "In the following, replace '"
			+ ModelConstants.PLACEHOLDER.get() + "' in method names with the concrete feature name:"
			+ getDocParagraphSeparator() + "<ul>%s</ul>";

	/**
	 * @return Paragraph separator used in documentation.
	 */
	public static String getDocParagraphSeparator() {
		return documentationParagraphSeparator;
	}

	/**
	 * @return The warning to not use the method from outside.
	 */
	public static String getDoNotUseFromOutsideDocNote() {
		return doNotUseFromOutsideDocumentationNote;
	}

	/**
	 * @return Introduction to method list in class documentations.
	 */
	public static String getClassMethodOverviewIntroTemplate() {
		return classMethodOverviewIntroTemplate;
	}

	/**
	 * @param text A given text
	 * @return {@code text +} {@link #getDocParagraphSeparator()}
	 */
	public static String appendToDocumentationStart(String text) {
		return text + getDocParagraphSeparator();
	}

	/**
	 * @param methodNameToSummaryMap A map of (method name, short description)
	 *                               entries
	 * @return A single string containing the given method names and their summaries
	 *         in a formatted version.
	 */
	public static String serialiseSummaries(Map<String, String> methodNameToSummaryMap) {
		var sb = new StringBuilder();
		methodNameToSummaryMap
				.forEach((metName, summary) -> sb.append("<li><b>").append(metName).append("</b>: ").append(summary));
		return sb.toString();
	}
}
