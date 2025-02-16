package org.splevo.jamopp.diffing.util;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.commons.NamespaceAwareElement;

/**
 * A utility class for checking namespaces of {@link NamespaceAwareElement}
 * instances. <br>
 * <br>
 * The methods of this class can handle null parameters without throwing
 * NullPointerExceptions.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPNamespaceUtil {
	/**
	 * Compares the namespaces of the given {@link NamespaceAwareElement}s part by
	 * part.
	 * 
	 * @return False if namespaces have parts different parts, true if not. If only
	 *         one parameter is null, returns false. If both parameters are null,
	 *         returns true.
	 */
	public static boolean compareNamespacesByPart(NamespaceAwareElement nae1, NamespaceAwareElement nae2) {
		if (JaMoPPNullCheckUtil.allNull(nae1, nae2)) {
			return true;
		} else if (JaMoPPNullCheckUtil.onlyOneIsNull(nae1, nae2)) {
			return false;
		}

		var nss1 = nae1.getNamespaces();
		var nss2 = nae2.getNamespaces();

		if (JaMoPPNullCheckUtil.allNull(nss1, nss2)) {
			return true;
		} else if (JaMoPPNullCheckUtil.onlyOneIsNull(nss1, nss2)) {
			return false;
		}

		if (nss1.size() != nss2.size()) {
			return false;
		}
		for (int idx = 0; idx < nss1.size(); idx++) {
			if (!JaMoPPStringUtil.stringsEqual(nss1.get(idx), nss2.get(idx))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Build the package path for a given element. Either the element itself is
	 * aware of it's name space or the closest aware container is used.
	 *
	 * @param element The element to get the package for.
	 * @return The identified name space or null if none could be found. Returns
	 *         null, if the parameter is null.
	 */
	public static String buildNamespacePath(EObject element) {

		while (element != null) {
			if (element instanceof NamespaceAwareElement) {

				String namespace = ((NamespaceAwareElement) element).getNamespacesAsString();

				// Null check to avoid NullPointerExceptions
				if (namespace == null) {
					return null;
				}
				if (namespace.lastIndexOf('$') != -1) {
					namespace = namespace.substring(0, namespace.lastIndexOf('$'));
				}
				if (namespace.length() > 0 && namespace.charAt(namespace.length() - 1) == '.') {
					namespace = namespace.substring(0, namespace.length() - 1);
				}
				return namespace;
			}

			element = element.eContainer();
		}

		return null;
	}

	/**
	 * Compares the namespaces of the given {@link NamespaceAwareElement}s as a
	 * whole (i.e. all namespace parts concatenated together).
	 * 
	 * @return False if namespaces are different, true if not. If only one parameter
	 *         is null, returns false. If both parameters are null, returns true.
	 */
	public static boolean compareNamespacesAsString(NamespaceAwareElement nae1, NamespaceAwareElement nae2) {
		if (JaMoPPNullCheckUtil.allNull(nae1, nae2)) {
			return true;
		} else if (JaMoPPNullCheckUtil.onlyOneIsNull(nae1, nae2)) {
			return false;
		}

		return JaMoPPStringUtil.stringsEqual(nae1.getNamespacesAsString(), nae2.getNamespacesAsString());
	}
}
