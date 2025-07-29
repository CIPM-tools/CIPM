package cipm.consistency.fitests.similarity.jamopp.parser;

import java.util.Iterator;

/**
 * An interface meant for classes that encapsulate the logic on how to iterate
 * through the given Resource array, while generating dynamic tests. <br>
 * <br>
 * Does not directly generate dynamic tests, because the order of elements may
 * matter for other concerns as well. Therefore, implementors of this interface
 * are meant to return an order via customised {@link Iterator}s, and not the
 * desired dynamic tests.
 * 
 * @author Alp Torac Genc
 */
public interface IJaMoPPParserTestGenerationStrategy {
	/**
	 * Returns an iterator that denotes the order of dynamic test generation the
	 * concrete implementor uses. The returned iterator {@code it} will return an
	 * int array of size 2 upon {@code idxs = it.next()}, until all intended dynamic
	 * tests are generated. Ints stored in {@code idxs} stand for the indices of
	 * elements in resArr and pathArr, which are to be used in the current dynamic
	 * test. In other words, the current dynamic test will use the
	 * {@code idxs[0]}-th elements for the left hand side (lhs) and
	 * {@code idxs[1]}-th elements for the right hand side in the current dynamic
	 * test.
	 * 
	 * @param testResourceCount The amount of test resources that are present. Used
	 *                          to mark when the returned iterator finishes,
	 *                          depending on its implementation.
	 * @return An iterator that denotes which test resources will be used in which
	 *         order.
	 */
	public Iterator<int[]> getTestResourceIterator(int testResourceCount);

	/**
	 * Defaults to the name of the concrete implementing type. Can be overridden in
	 * concrete implementors for a more accurate description.
	 * 
	 * @return A description for this test generation strategy, which may be added
	 *         to test descriptions.
	 */
	public default String getTestGenerationStrategyDescription() {
		return this.getClass().getSimpleName();
	}
}
