package cipm.consistency.cpr.pcmjava;

public class PCMSettings {
	private static final String namespaceSeparator = ".";
	private static final String namespaceSeparatorRegex = "\\.";

	private PCMSettings() {

	}

	public static String getNamespaceSeparator() {
		return namespaceSeparator;
	}
	
	public static String getNamespaceSeparatorAsRegex() {
		return namespaceSeparatorRegex;
	}
}
