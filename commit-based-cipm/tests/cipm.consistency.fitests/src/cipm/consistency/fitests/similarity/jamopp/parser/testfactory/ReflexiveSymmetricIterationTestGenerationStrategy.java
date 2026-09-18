package cipm.consistency.fitests.similarity.jamopp.parser.testfactory;

import java.util.Iterator;

/**
 * An implementation of {@link IJaMoPPParserTestGenerationStrategy} that
 * provides an iterator, which denotes a linear and symmetric index sequence for
 * test resources. <br>
 * <br>
 * 
 * @see {@link #getTestResourceIterator(int)} for more information.
 * 
 * @author Alp Torac Genc
 */
public class ReflexiveSymmetricIterationTestGenerationStrategy implements IJaMoPPParserTestGenerationStrategy {
	private final static String description = "reflexive symmetric iteration strategy";

	/**
	 * @implSpec Returns an iterator that helps generate dynamic tests by using test
	 *           resources in an iterative, reflexive, symmetric way. The order
	 *           denoted by the returned iterator will be as follows, where
	 *           {@code (i, j)} stands for the indices of test resources that will
	 *           be used in the current test:
	 *           <ol>
	 *           <li>Start with {@code currentIdx = 0}: In-place (currentIdx,
	 *           currentIdx)
	 *           <li>while {@code currentIdx < testResourceCount} :
	 *           <ol>
	 *           <li>Forward (currentIdx, currentIdx + 1)
	 *           <li>Reverse (currentIdx + 1, currentIdx)
	 *           <li>In-place (currentIdx + 1, currentIdx + 1)
	 *           <li>{@code currentIdx++}
	 *           </ol>
	 *           </ol>
	 *           Example with {@code testResourceCount = 3}:
	 *           {@code (0,0); (0,1); (1,0); (1,1); (1,2); (2,1); (2,2)}
	 * @implNote Assuming both arrays' length is {@code N}, then the returned
	 *           iterator iterates {@code 1 + 3*(N - 1)} times. As such, a maximum
	 *           of {@code 1 + 3*(N - 1)} dynamic tests can be generated. In the
	 *           example, {@code N = 3} and hence 7 dynamic tests are to be created.
	 */
	@Override
	public Iterator<int[]> getTestResourceIterator(int testResourceCount) {
		return new Iterator<int[]>() {
			private int currentIdx = 0;
			/**
			 * Denotes the elements' order: <br>
			 * <br>
			 * <ul>
			 * <li>1: Forward iteration (currentIdx, currentIdx + 1)
			 * <li>0: In-place (currentIdx, currentIdx)
			 * <li>-1: Reverse iteration (currentIdx + 1, currentIdx)
			 * </ul>
			 */
			private byte status = 0;

			@Override
			public boolean hasNext() {
				return (status == 0 && currentIdx < testResourceCount) || (currentIdx < testResourceCount - 1);
			}

			@Override
			public int[] next() {
				int[] result = null;

				switch (status) {
				case 0:
					result = new int[] { currentIdx, currentIdx };
					status = 1;
					break;
				case 1:
					result = new int[] { currentIdx, currentIdx + 1 };
					status = -1;
					break;
				case -1:
					result = new int[] { currentIdx + 1, currentIdx };
					status = 0;
					++currentIdx;
					break;
				default:
					throw new IllegalStateException("Undefined state reached");
				}

				return result;
			}

		};
	}

	@Override
	public String getTestGenerationStrategyDescription() {
		return description;
	}
}
