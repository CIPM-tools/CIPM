package cipm.consistency.fluentapi.test.metamodel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInfo;

/**
 * An interface meant for fluent API related tests, which contain mutation
 * tests. Mutation tests ensure that the other test cases within the implementor
 * fail, given the preceding mutations.
 * <p>
 * <p>
 * Note: The methods in this interface NEITHER perform modifications (i.e.
 * mutations) to existing EMF elements NOR do they revert the performed
 * mutations. Those should be done within the concrete implementor of this
 * interface.
 * 
 * @author Alp Torac Genc
 */
public interface IFluentAPIMutationTest {
	/**
	 * Ensures that all non-disabled tests other than the currently running test
	 * case fail, given the preceding mutations. Note: This method itself NEITHER
	 * performs modifications (i.e. mutations) to existing EMF elements NOR does it
	 * remove the performed mutations.
	 * <p>
	 * <p>
	 * Instead of throwing an assertion error after running the test cases on the
	 * mutated EMF model, returns test results so that the mutations can be
	 * reverted, before any further test case is run. Otherwise, the mutations may
	 * not always be reverted and normal test runs might fail.
	 * 
	 * @param info An object that yields information on the currently running test
	 *             method and various other aspects during testing
	 * @return A map containing (test case, passed) pairs from test cases, which
	 *         were run within this mutation test. True means that the test case
	 *         passed, false means that the test case failed in the face of
	 *         mutations.
	 */
	public default Map<Method, Boolean> performMutationTesting(TestInfo info) {
		var testMethodsToRun = List.of(this.getClass().getMethods()).stream()
				// Filters out the mutation test case itself, which should be the currently
				// running test case
				.filter((tm) -> !tm.getName().equals(info.getDisplayName()))
				// Consider only test methods (annotated with @Test), which are not disabled
				// (annotated with @Disabled)
				.filter((tm) -> tm.isAnnotationPresent(org.junit.jupiter.api.Test.class)
						&& !tm.isAnnotationPresent(org.junit.jupiter.api.Disabled.class))
				.collect(Collectors.toList());
		// Ensure that either trivial mutation testing is allowed or that there is at
		// least one test case, which should fail for the previously performed mutations
		Assertions.assertTrue(allowTrivialMutationTest() || testMethodsToRun.size() > 0,
				"No test methods detected, even though trivial mutation test is not allowed");

		var tmRuns = new HashMap<Method, Boolean>();
		for (var tm : testMethodsToRun) {
			try {
				tm.invoke(this);
				tmRuns.put(tm, Boolean.TRUE);
			} catch (Exception e) {
				tmRuns.put(tm, Boolean.FALSE);
			}
		}
		return tmRuns;
	}

	/**
	 * Can be overridden to stop the mutation test from failing due to not finding
	 * any non-mutation tests.
	 * 
	 * @return Whether there should exist at least one test case, which should fail
	 *         after mutation is performed.
	 */
	public default boolean allowTrivialMutationTest() {
		return false;
	}

	/**
	 * Asserts that all test cases failed in the face of the preceding mutations to
	 * the EMF structure. This method is split from
	 * {@link #performMutationTesting(TestInfo)}, in order to allow reverting the
	 * mutations.
	 * <p>
	 * <p>
	 * This method should be called after the preceding mutations have been
	 * reverted, in order to allow following test cases to run without mutations.
	 * 
	 * @param mutTestRes The return value of
	 *                   {@link #performMutationTesting(TestInfo)}
	 */
	public default void assertTestsFailed(Map<Method, Boolean> mutTestRes) {
		for (var e : mutTestRes.entrySet()) {
			if (e.getValue()) {
				Assertions.fail("The test case " + e.getKey().getName() + " was expected to fail, but it passed");
			}
		}
	}
}
