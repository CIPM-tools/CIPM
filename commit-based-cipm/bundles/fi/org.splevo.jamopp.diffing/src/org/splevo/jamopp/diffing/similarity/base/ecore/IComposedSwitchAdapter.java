package org.splevo.jamopp.diffing.similarity.base.ecore;

import org.eclipse.emf.ecore.EObject;

/**
 * An interface that is meant to be implemented by classes, which extend
 * {@link org.eclipse.emf.ecore.util.ComposedSwitch}. <br>
 * <br>
 * Adapts its implementors to comply with the ComposedSwitch, which requires an
 * {@link EObject} instance that remains constant throughout the entire
 * similarity checking process. Unlike ComposedSwitch, the implementors will
 * provide the {@link #compare(EObject, EObject)} method, which takes two
 * {@link EObject} instances and compares them. Due to ComposedSwitch taking
 * only one {@link EObject} instance, the second {@link EObject} instance is
 * stored in an attribute inside the implementors and is retrieved with
 * {@link #getCompareElement()} when needed.
 * 
 * @see {@link IComposedSimilaritySwitch}
 * @author atora
 */
public interface IComposedSwitchAdapter {
	/**
	 * Returns the current compare element, which is the second parameter in
	 * {@link #compare(EObject, EObject)}.
	 */
	public EObject getCompareElement();

	/**
	 * Compares the given {@link EObject} instances and returns the result.
	 * 
	 * @param eo1 An {@link EObject} instance, which will be compared with eo2.
	 * @param eo2 The {@link EObject} instance, which will be the compare element.
	 * @return
	 *         <ul>
	 *         <li>True, if given {@link EObject} instances are similar
	 *         <li>False, if they are not similar
	 *         <li>Null, if their similarity cannot be decided
	 *         </ul>
	 */
	public Boolean compare(EObject eo1, EObject eo2);
}
