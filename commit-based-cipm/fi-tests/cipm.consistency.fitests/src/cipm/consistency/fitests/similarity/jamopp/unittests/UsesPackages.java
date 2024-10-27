package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.containers.Package;

import cipm.consistency.initialisers.jamopp.containers.PackageInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link Package} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Package} instances.
 */
public interface UsesPackages {
	/**
	 * @param pacNss The namespaces of the instance to be constructed
	 * @return A {@link Package} instance with the given parameters
	 */
	public default Package createMinimalPackage(String[] pacNss) {
		var pacInit = new PackageInitialiser();

		Package result = pacInit.instantiate();
		pacInit.addNamespaces(result, pacNss);

		return result;
	}

	/**
	 * A variant of {@link #createMinimalPackage(String[])}, where namespaces are
	 * generated using the given parameters. <br>
	 * <br>
	 * The generated namespaces will each consist of the given prefix and a suffix.
	 * As suffix, the namespaces will have a number between 0 (including) and the
	 * given count (excluding). <br>
	 * <br>
	 * Example: {@code nsPrefix = "ns", nsCount = 3} constructs a {@link Package}
	 * instance with namespaces "ns0", "ns1", "ns2".
	 * 
	 * @param pacNsPrefix The prefix of the namespaces to be generated
	 * @param pacNsCount  The count of the namespaces to be generated
	 */
	public default Package createMinimalPackage(String pacNsPrefix, int pacNsCount) {
		var nss = new String[pacNsCount];

		for (int i = 0; i < pacNsCount; i++)
			nss[i] = pacNsPrefix + i;

		return this.createMinimalPackage(nss);
	}
}
