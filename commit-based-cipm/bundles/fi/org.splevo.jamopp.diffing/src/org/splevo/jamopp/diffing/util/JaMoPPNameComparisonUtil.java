package org.splevo.jamopp.diffing.util;

import org.emftext.language.java.commons.NamedElement;

/**
 * A utility class for comparing names of {@link NamedElement} instances.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPNameComparisonUtil {
	/**
	 * Compares the names of the given parameters. Can be used with null parameters.
	 * If both parameters are null, returns true. If only one parameter is null,
	 * returns false.
	 * 
	 * @return Whether the names of the given parameters are equal. If only one of
	 *         the names is null, returns false. If both names are null, returns
	 *         true.
	 */
	public static boolean namesEqual(NamedElement ne1, NamedElement ne2) {
		if (JaMoPPNullCheckUtil.allNull(ne1, ne2)) {
			return true;
		} else if (JaMoPPNullCheckUtil.onlyOneIsNull(ne1, ne2)) {
			return false;
		}

		return JaMoPPStringUtil.stringsEqual(ne1.getName(), ne2.getName());
	}
}
