package cipm.consistency.tools.evaluation.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link EvaluationDataContainer}, the process-global singleton holding all evaluation
 * data, with a focus on {@link EvaluationDataContainer#resetExecutionTimes()} - the fix for a
 * real cross-commit accumulation bug found while verifying this feature end-to-end.
 */
class EvaluationDataContainerTest {

	@BeforeEach
	@AfterEach
	void resetGlobalState() {
		// EvaluationDataContainer.get() is process-global static state; every test must leave
		// it clean so earlier/later tests in the same JVM don't see each other's data.
		EvaluationDataContainer.set(new EvaluationDataContainer());
	}

	@Test
	void resetExecutionTimes_returnsAFreshInstance_discardingPriorData() {
		EvaluationDataContainer container = EvaluationDataContainer.get();
		container.getExecutionTimes().recordFineGrainedTiming("javaModelComparison", 100);

		ExecutionTimeData freshTimes = container.resetExecutionTimes();

		assertTrue(freshTimes.getFineGrainedTimings().isEmpty());
		assertTrue(container.getExecutionTimes().getFineGrainedTimings().isEmpty());
	}

	@Test
	void resetExecutionTimes_aPreviouslyHeldReference_stillPointsAtTheStaleData() {
		EvaluationDataContainer container = EvaluationDataContainer.get();
		ExecutionTimeData staleTimes = container.getExecutionTimes();
		staleTimes.recordFineGrainedTiming("javaModelComparison", 100);

		container.resetExecutionTimes();

		// The reset replaces the container's own reference - it does not clear the old object,
		// so a reference taken before the reset is unaffected (and therefore stale).
		assertEquals(1, staleTimes.getFineGrainedTimings().get("javaModelComparison").getCount());
		assertNotSame(staleTimes, container.getExecutionTimes());
	}

	@Test
	void getAfterSet_returnsTheExactInstanceThatWasSet() {
		EvaluationDataContainer newContainer = new EvaluationDataContainer();

		EvaluationDataContainer.set(newContainer);

		assertSame(newContainer, EvaluationDataContainer.get());
	}
}
