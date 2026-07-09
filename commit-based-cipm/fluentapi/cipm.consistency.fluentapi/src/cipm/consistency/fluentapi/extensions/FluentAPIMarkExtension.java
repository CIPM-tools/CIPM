package cipm.consistency.fluentapi.extensions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

/**
 * The extension class of fluent api that manages marks in form of (markKey,
 * markValue) pairs, where markKey is the object that was used to mark the model
 * element (markValue) via fluent api.
 * <p>
 * <p>
 * Currently, marks are stored in a Map, meaning that a markKey may only be used
 * to mark a single markValue. Using the same markKey to mark another model
 * element markValue2 will override (markKey, markValue) to (markKey,
 * markValue2). However, markValue can be marked with multiple markKeys.
 * <p>
 * <p>
 * Note: Changing any public member within this file (i.e. either this class or
 * its methods) requires adapting the generation of fluent api. This is due to
 * Java limitations, which do not allow dynamically adjusting static elements,
 * such as method or class names.
 * 
 * @author Alp Torac Genc
 * @see {@link FluentAPIWaitForMarkExtension}
 */
public class FluentAPIMarkExtension {
	/**
	 * The map that contains marks in form of (markKey, markVal) pairs.
	 */
	private static final Map<Object, EObject> markToObj = new LinkedHashMap<>();

	/**
	 * Adds the mark (markKey, markVal) to this class, overrides any existing mark
	 * of markKey.
	 * 
	 * @param markKey An object that is to be associated with markVal
	 * @param markVal A model element to be marked with markKey
	 */
	public static void mark(Object markKey, EObject markVal) {
		markToObj.put(markKey, markVal);
		elementMarked(markKey, markVal);
	}

	/**
	 * Removes the markKey from this class (if it exists), regardless of what
	 * markVal it was associated with.
	 * 
	 * @param markKey An object that is potentially associated with a model element
	 *                markVal
	 */
	public static EObject unmark(Object markKey) {
		return unmark(markKey, null);
	}

	/**
	 * Removes the mark (markKey, markVal) from this class, if it exists. Does
	 * nothing, if markKey is associated with another model element markVal2.
	 * 
	 * @param markKey An object that is potentially associated with the model
	 *                element markVal
	 * @param markVal A model element that is potentially marked with markKey
	 */
	public static EObject unmark(Object markKey, EObject markVal) {
		var toUnmark = markToObj.get(markKey);

		if (markVal == null || markVal == toUnmark) {
			return markToObj.remove(markKey);
		} else {
			return null;
		}
	}

	/**
	 * @param markKey An object that is potentially associated with a model element
	 *                markVal
	 * @return markVal (if it exists)
	 */
	public static EObject getMarked(Object markKey) {
		return getMarked(markKey, null);
	}

	/**
	 * @param markKey An object that is potentially associated with a model element
	 *                markVal
	 * @param cls     The class of markVal
	 * @return markVal, if it exists and its type is cls or cls is a super-type
	 */
	public static EObject getMarked(Object markKey, Class<?> cls) {
		var markVal = markToObj.get(markKey);
		if (cls != null && markVal != null && !(cls.isAssignableFrom(markVal.getClass()))) {
			return null;
		}
		return markVal;
	}

	/**
	 * @param markKey An object that is potentially associated with a model element
	 * @return Whether markKey is associated with a model element
	 */
	public static boolean hasMark(Object markKey) {
		return markToObj.containsKey(markKey);
	}

	/**
	 * @return All marks contained in this class (as an unmodifiable map instance),
	 *         i.e. (markKey, markVal) pairs.
	 */
	public static Map<Object, EObject> getAllMarks() {
		return Map.copyOf(markToObj);
	}

	/**
	 * Notifies {@link FluentAPIWaitForMarkExtension} to the mark (markKey, markVal)
	 * 
	 * @param markKey An object that is associated with the model element markVal
	 * @param markVal A model element that is marked with markKey
	 */
	private static void elementMarked(Object markKey, EObject markVal) {
		FluentAPIWaitForMarkExtension.elementMarked(markKey, markVal);
	}

	/**
	 * Removes all marks (markKey, markVal) from this class.
	 */
	public static void clearAllMarks() {
		markToObj.clear();
	}
}
