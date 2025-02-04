package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * An interface that contains default methods for tests, which dynamically set
 * attributes/features of {@link EObject} instances. This also includes some
 * utility methods revolving around {@link EList}.
 * 
 * @author Alp Torac Genc
 */
public interface IStructuralFeatureTest {
	/**
	 * Sets the value of the given feature (feat) of the given object (obj) to the
	 * given value (val). <br>
	 * <br>
	 * If feat can contain multiple values ({@code feat.isMany()}), i.e. feat is
	 * many-valued, and val does not implement {@link EList} or is not null; uses
	 * {@link #createEListWith(Object, boolean)} on val first and then sets the
	 * result as the value of feat in obj. If val does implement EList, directly
	 * sets val as the value of feat in obj. <br>
	 * <br>
	 * Note on ({@code feat.isUnsettable()}): There are features that allow null
	 * (called unsettable feature), therefore val can be null, in order to set the
	 * value of feat to null. If feat is no such feature, the method will return and
	 * a log message will be generated. <br>
	 * <br>
	 * Note on ({@code feat.isChangeable()}): Not all features can be changed
	 * (called changeable feature). If feat cannot be changed, the method will
	 * return and a log message will be generated.
	 * 
	 * @param obj              A given {@link EObject} instance, must be non-null.
	 * @param feat             An attribute/feature that obj supports, must be
	 *                         non-null.
	 * @param val              The new value of the feature in obj. If feat supports
	 *                         multiple values and val neither implements
	 *                         {@link EList} nor {@code val == null},
	 *                         {@link #createEListWith(Object, boolean)} will be
	 *                         used on it. Refer to its documentation for allowed
	 *                         values of val.
	 * @param filterNullValues Whether null values should be filtered away, if val
	 *                         is an array or iterable. Does not filter
	 *                         {@code val = null} in any way, since it is allowed.
	 * 
	 * @return Whether setting feat feature of obj was successful and its value is
	 *         val. Note that if val was an iterable data structure, such as
	 *         iterable or collection, and this returns true; this <b>does not</b>
	 *         mean {@code obj.eGet(feat) == val} in general.
	 */
	public default boolean setValueOf(EObject obj, EStructuralFeature feat, Object val, boolean filterNullValues) {
		if (!feat.isChangeable()) {
			// Only changeable features can be changed/set
			return false;
		}

		// Handle cases where val is null, so that null checking later can be spared
		if (!feat.isUnsettable() && val == null) {
			// Only unsettable features can be set to null
			return false;
		} else if (feat.isUnsettable() && val == null) {
			obj.eSet(feat, val);
			return obj.eGet(feat) == val;
		}
		// val != null from now on

		var valCls = val.getClass();
		var featValType = feat.getEType().getInstanceClass();

		if (!valCls.isArray() && !featValType.isAssignableFrom(valCls)) {
			// Non-array type mismatch, feature cannot be set
			return false;
		}

		var valToUse = val;
		if (feat.isMany() && !(val instanceof EList)) {
			// feat is a multi-valued feature, i.e. it requires a list of value(s)
			// Make sure to allow using valToUse = val, if val implements EList
			valToUse = this.createEListWith(val, filterNullValues);
		}

		// Make sure that all elements' types conform feat's needs
		if (valToUse instanceof EList) {
			var castedVal = (EList<?>) valToUse;
			for (var cVal : castedVal) {
				if (!featValType.isAssignableFrom(cVal.getClass())) {
					return false;
				}
			}
			obj.eSet(feat, castedVal);
			var afterVals = this.createEListWith(obj.eGet(feat), filterNullValues);
			return this.eListContentsEqual(afterVals, castedVal);
		} else {
			obj.eSet(feat, valToUse);
			return obj.eGet(feat) == valToUse;
		}
	}

	/**
	 * Uses {@code l.containsAll(...)} for both lists when determining content
	 * equality.
	 * 
	 * @return Whether l1 and l2 contain the same elements. Accounts for l1 and l2
	 *         possibly being null and returns true, if both of them are null. If
	 *         only one of them is null, returns false, regardless of the other
	 *         one's contents.
	 */
	public default boolean eListContentsEqual(EList<?> l1, EList<?> l2) {
		if (l1 == null && l2 == null) {
			return true;
		} else if (l1 == null ^ l2 == null) {
			return false;
		} else {
			return l1.containsAll(l2) && l2.containsAll(l1);
		}
	}

	/**
	 * A variant of
	 * {@link #setValueOf(EObject, EStructuralFeature, Object, boolean)}, which does
	 * not attempt to filter away null elements from val, if it is an array or
	 * iterable. <br>
	 * <br>
	 * This may be desirable sometimes, as it can serve as an assertion that none of
	 * the values stored in val are null.
	 */
	public default boolean setValueOf(EObject obj, EStructuralFeature feat, Object val) {
		return this.setValueOf(obj, feat, val, false);
	}

	/**
	 * Can be used for constructing more advanced values to be used in
	 * {@link #setValueOf(EObject, EStructuralFeature, Object)}. <br>
	 * <br>
	 * Can be overridden to change the default list construction inside the method
	 * from above.
	 * 
	 * @return An empty EList instance that can be filled and used for value
	 *         constructions.
	 */
	public default <T> EList<T> createDefaultEList() {
		return new BasicEList<T>();
	}

	/**
	 * If val is a data structure, contains null elements and filterNullElements is
	 * false; it may throw exceptions depending on whether
	 * {@link #createDefaultEList()} allows null elements. Throws no exceptions for
	 * {@code val == null}.
	 * 
	 * @param val                See below
	 * @param filterNullElements Whether null elements within val should be filtered
	 *                           away. Does not filter away {@code val == null}.
	 * 
	 * @return An EList instance (type determined by {@link #createDefaultEList()})
	 *         based on val and the following order:
	 *         <ol>
	 *         <li>val == null: returns null
	 *         <li>val implements EList: returns val itself
	 *         <li>val is an array: returns an EList instance with elements from val
	 *         <li>val implements Iterable: returns an EList instance with elements
	 *         from val. Note that EList case takes precedence, in order to allow
	 *         returning val itself.
	 *         <li>Otherwise: returns an EList instance with only val
	 *         </ol>
	 */
	public default EList<?> createEListWith(Object val, boolean filterNullElements) {
		if (val == null) {
			return null;
		} else if (val instanceof EList) {
			return (EList<?>) val;
		}
		var list = this.createDefaultEList();
		if (val.getClass().isArray()) {
			for (var v : (Object[]) val) {
				if (!filterNullElements || v != null) {
					list.add(v);
				}
			}
		} else if (val instanceof Iterable) {
			for (var v : (Iterable<?>) val) {
				if (!filterNullElements || v != null) {
					list.add(v);
				}
			}
		} else {
			if (!filterNullElements || val != null) {
				list.add(val);
			}
		}
		return list;
	}

	/**
	 * A variant of {@link #createEListWith(Object, boolean)}, which does not
	 * attempt to filter away null elements from val, if it is an array or iterable.
	 * <br>
	 * <br>
	 * This may be desirable sometimes, as it can serve as an assertion that none of
	 * the values stored in val are null.
	 */
	public default EList<?> createEListWith(Object val) {
		return this.createEListWith(val, false);
	}
}
