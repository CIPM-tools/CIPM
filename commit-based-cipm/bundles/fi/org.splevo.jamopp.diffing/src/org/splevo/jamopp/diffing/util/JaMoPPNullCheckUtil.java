package org.splevo.jamopp.diffing.util;

/**
 * A utility class for null checking.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPNullCheckUtil {
	/**
	 * Method to check if only one of the provided elements is null.
	 *
	 * @param element1 The first element.
	 * @param element2 The second element.
	 * @return True if only one element is null and the other is not.
	 */
	public static boolean onlyOneIsNull(Object element1, Object element2) {
		return element1 == null ^ element2 == null;
	}

	/**
	 * @return Whether none of the objs are null.
	 */
	public static boolean allNonNull(Object... objs) {
		for (var obj : objs)
			if (obj == null)
				return false;

		return true;
	}

	/**
	 * @return Whether all objs are null.
	 */
	public static boolean allNull(Object... objs) {
		for (var obj : objs)
			if (obj != null)
				return false;

		return true;
	}

	/**
	 * @return Whether both obj1 and obj2 are null / non-null and equal (as in
	 *         {@code .equals(...)}). Make sure to check equality in both ways to
	 *         ensure that this method is symmetric for its parameters.
	 */
	public static boolean bothNullOrEqual(Object obj1, Object obj2) {
		return (obj1 == null && obj2 == null)
				|| ((obj1 != null && obj2 != null) && obj1.equals(obj2) && obj2.equals(obj1));
	}
}