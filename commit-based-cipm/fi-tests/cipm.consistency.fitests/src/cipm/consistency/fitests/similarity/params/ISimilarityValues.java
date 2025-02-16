package cipm.consistency.fitests.similarity.params;

/**
 * An interface for classes that contain expected similarity checking results
 * for (class object, attribute object) pairs. An attribute object denotes an
 * attribute within a class, rather than the value of an attribute. <br>
 * <br>
 * Variations of {@link #addSimilarityEntry(Class, Object, Boolean)} can be used
 * to group entries better, while adding them.
 * 
 * @see {@link #addSimilarityEntry(Class, Object, Boolean)} for more information
 *      on adding entries.
 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
 *      on expected similarity checking values.
 * 
 * @author Alp Torac Genc
 */
public interface ISimilarityValues {
	/**
	 * A variant of {@link #addSimilarityEntry(Class, Object, Boolean)}, where
	 * objCls is extracted from attr.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
	 *      on expected similarity checking result.
	 * 
	 * @see {@link #addSimilarityEntry(Class, Object, Boolean)}
	 */
	public void addSimilarityEntry(Object attr, Boolean expectedSimResult);

	/**
	 * Adds the (objCls, attr) pair, along with their expected similarity checking
	 * value to this instance.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
	 *      on expected similarity checking result and which entry is chosen, if
	 *      entries for super/sub-types of objCls exist.
	 * 
	 * @see {@link #addSimilarityEntry(Class[], Object, Boolean)}
	 * @see {@link #addSimilarityEntry(Class[], Object[], Boolean[])}
	 */
	public void addSimilarityEntry(Class<? extends Object> objCls, Object attr, Boolean expectedSimResult);

	/**
	 * A variant of {@link #addSimilarityEntry(Class, Object, Boolean)} for an array
	 * of class objects. <br>
	 * <br>
	 * Let
	 * {@code objClsArr = [cls1, cls2, ..., clsN], attr = a1, expectedSimResults = true}.
	 * Then this method is equivalent to:
	 * <ul>
	 * <li>addSimilarityEntry(cls1, a1, true)
	 * <li>addSimilarityEntry(cls2, a1, true)
	 * <li>...
	 * <li>addSimilarityEntry(clsN, a1, true)
	 * </ul>
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
	 *      on expected similarity checking result.
	 * 
	 * @see {@link #addSimilarityEntry(Class, Object, Boolean)}
	 * @see {@link #addSimilarityEntry(Class[], Object[], Boolean[])}
	 */
	public default void addSimilarityEntry(Class<?>[] objClsArr, Object attr, Boolean expectedSimResult) {
		if (objClsArr != null) {
			for (var cls : objClsArr) {
				this.addSimilarityEntry(cls, attr, expectedSimResult);
			}
		}
	}

	/**
	 * A variant of {@link #addSimilarityEntry(Class, Object, Boolean)}. <br>
	 * <br>
	 * Combines objClsArr with (attrArr[i], expectedSimResults[i]) for all i: <br>
	 * <br>
	 * Let
	 * {@code objClsArr = [cls1, cls2, cls3], attrArr = [a1, a2], expectedSimResults = [true, false]}.
	 * Then this method is equivalent to:
	 * <ul>
	 * <li>addSimilarityEntry(objClsArr, a1, true)
	 * <li>addSimilarityEntry(objClsArr, a2, false)
	 * </ul>
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
	 *      on expected similarity checking result.
	 * 
	 * @see {@link #addSimilarityEntry(Class[], Object, Boolean)}
	 * @see {@link #addSimilarityEntry(Class, Object, Boolean)}
	 */
	public default void addSimilarityEntry(Class<?>[] objClsArr, Object[] attrArr, Boolean[] expectedSimResults) {
		if (attrArr == null || expectedSimResults == null || objClsArr == null) {
			return;
		}

		if (attrArr.length != expectedSimResults.length) {
			return;
		}

		for (int i = 0; i < attrArr.length; i++) {
			var attr = attrArr[i];
			var simRes = expectedSimResults[i];
			this.addSimilarityEntry(objClsArr, attr, simRes);
		}
	}

	/**
	 * Removes the (objCls, attr) pair from this instance.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
	 *      on expected similarity checking result.
	 * 
	 * @see {@link #addSimilarityEntry(Class, Object, Boolean)}
	 */
	public void removeSimilarityEntry(Class<? extends Object> objCls, Object attr);

	/**
	 * Clears all entries stored in this instance. <br>
	 * <br>
	 * Resets this instance.
	 */
	public void clear();

	/**
	 * Returns the expected result of similarity checking 2 object instances of the
	 * class objCls, where both are the same, except for their attribute attr.
	 * 
	 * @return Let a and b be object instances of the same class, which are the
	 *         same, except for their attr attribute, then:
	 *         <ul>
	 *         <li>True: Similarity checking a and b yields true, despite them being
	 *         different regarding attr.
	 *         <li>False: Similarity checking a and b yields false, as they have
	 *         different attr.
	 *         <li>Null: The result of similarity checking a and b is not defined,
	 *         i.e. differences in attr play no decisive role.
	 *         </ul>
	 *         Note: If this instance does not have the entry for (objCls, attr), it
	 *         checks whether there is an entry for a parent class parObjCls (of
	 *         objCls) and attr (parObjCls, attr). If there is one such entry, the
	 *         expected result of similarity checking for (parObjCls, attr) is
	 *         returned. If there are still no matching entries,
	 *         {@link #getDefaultSimilarityResult()} is returned.
	 */
	public Boolean getExpectedSimilarityResult(Class<? extends Object> objCls, Object attr);

	/**
	 * A variant of {@link #getExpectedSimilarityResult(Class, Object)} that
	 * extracts objCls from attr.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)}
	 */
	public Boolean getExpectedSimilarityResult(Object attr);

	/**
	 * Sets the default return value for
	 * {@link #getExpectedSimilarityResult(Class, Object)}, in cases where no
	 * entries for a given (objCls, attr) pair is found.
	 * 
	 * @param defSimRes The default return value for
	 *                  {@link #getExpectedSimilarityResult(Class, Object)}
	 */
	public void setDefaultSimilarityResult(Boolean defSimRes);

	/**
	 * @return The default return value for
	 *         {@link #getExpectedSimilarityResult(Class, Object)}
	 */
	public Boolean getDefaultSimilarityResult();
}
