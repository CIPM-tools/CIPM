package org.splevo.jamopp.diffing.util;

/**
 * A utility class for comparing String instances. <br>
 * <br>
 * Contains methods that can work with null Strings without throwing
 * NullPointerExceptions.
 * 
 * @author Alp Torac Genc
 *
 */
public class JaMoPPStringUtil {
	/**
	 * @return Whether the given String instances are equal. Accounts for null
	 *         parameters.
	 */
	public static boolean stringsEqual(String s1, String s2) {
		if (JaMoPPNullCheckUtil.allNull(s1, s2)) {
			return true;
		} else if (JaMoPPNullCheckUtil.allNonNull(s1, s2)) {
			return s1.equals(s2);
		} else {
			return false;
		}
	}
}