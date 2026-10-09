package cipm.consistency.fluentapi.gen;

/**
 * A utility class for generating method bodies for EOperations.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIMethodsUtil {
	private static final String semicolon = ";";

	private static final String newLine = System.lineSeparator();
	private static final String endLine = semicolon + newLine;

	/**
	 * Constructs the method body consisting of the given lines.
	 * 
	 * @param loc Lines of code (without semicolon and new line characters), where
	 *            each line is a string
	 * @return The method body as a single string instance.
	 */
	public static String joinLOC(String... loc) {
		var result = "";
		for (int i = 0; i < loc.length - 1; i++) {
			result += loc[i] + endLine;
		}
		result += loc[loc.length - 1] + semicolon;
		return result;
	}
}
