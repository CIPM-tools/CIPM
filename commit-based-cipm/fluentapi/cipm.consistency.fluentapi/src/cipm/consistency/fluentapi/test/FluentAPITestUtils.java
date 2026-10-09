package cipm.consistency.fluentapi.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;

/**
 * A utility class for the fluent api tests.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPITestUtils {
	/**
	 * Asserts that the contents of the given array and the list are pairwise equal
	 * (in the sense of {@code Assertions.assertEquals(...)}.
	 * 
	 * @param arr  A given array
	 * @param list A given list
	 */
	public static void assertPairwiseEqual(Object[] arr, List<?> list) {
		Assertions.assertEquals(arr.length, list.size());
		for (int i = 0; i < list.size(); i++) {
			Assertions.assertEquals(arr[i], list.get(i));
		}
	}

	/**
	 * Asserts that the contents of the given lists are pairwise equal (in the sense
	 * of {@code Assertions.assertEquals(...)}.
	 * 
	 * @param list1 A given list
	 * @param list2 Another given list
	 */
	public static void assertPairwiseEqual(List<?> list1, List<?> list2) {
		Assertions.assertEquals(list1.size(), list2.size());
		for (int i = 0; i < list1.size(); i++) {
			Assertions.assertEquals(list1.get(i), list2.get(i));
		}
	}
}
