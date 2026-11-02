package cipm.consistency.tools.evaluation.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ExecutionTimeData}'s recording methods, the thin wrapper around
 * {@link AggregatedTiming} that every instrumented production call site actually invokes.
 */
class ExecutionTimeDataTest {

	private ExecutionTimeData executionTimes;

	@BeforeEach
	void setUp() {
		executionTimes = new ExecutionTimeData();
	}

	@Test
	void recordFineGrainedTiming_firstCallForALabel_createsANewAggregate() {
		executionTimes.recordFineGrainedTiming("javaModelComparison", 100);

		AggregatedTiming timing = executionTimes.getFineGrainedTimings().get("javaModelComparison");
		assertEquals(1, timing.getCount());
		assertEquals(100, timing.getTotalTime());
	}

	@Test
	void recordFineGrainedTiming_repeatCallsForTheSameLabel_accumulateIntoTheSameAggregate() {
		executionTimes.recordFineGrainedTiming("javaModelComparison", 100);
		executionTimes.recordFineGrainedTiming("javaModelComparison", 50);

		AggregatedTiming timing = executionTimes.getFineGrainedTimings().get("javaModelComparison");
		assertEquals(2, timing.getCount());
		assertEquals(150, timing.getTotalTime());
	}

	@Test
	void recordFineGrainedTiming_differentLabels_stayIndependent() {
		executionTimes.recordFineGrainedTiming("javaModelComparison", 100);
		executionTimes.recordFineGrainedTiming("changeReplay", 20);

		assertEquals(1, executionTimes.getFineGrainedTimings().get("javaModelComparison").getCount());
		assertEquals(1, executionTimes.getFineGrainedTimings().get("changeReplay").getCount());
	}

	@Test
	void recordChangePropagationTimeForChangeType_accumulatesPerChangeType() {
		executionTimes.recordChangePropagationTimeForChangeType("AddEObject", 10);
		executionTimes.recordChangePropagationTimeForChangeType("AddEObject", 20);
		executionTimes.recordChangePropagationTimeForChangeType("RemoveEObject", 5);

		assertEquals(2, executionTimes.getChangePropagationTimePerChangeType().get("AddEObject").getCount());
		assertEquals(30, executionTimes.getChangePropagationTimePerChangeType().get("AddEObject").getTotalTime());
		assertEquals(1, executionTimes.getChangePropagationTimePerChangeType().get("RemoveEObject").getCount());
	}

	@Test
	void recordBodyTransformationTimeForChangeType_accumulatesPerChangeType() {
		executionTimes.recordBodyTransformationTimeForChangeType("AddEObject", 10);
		executionTimes.recordBodyTransformationTimeForChangeType("AddEObject", 40);

		assertEquals(2, executionTimes.getBodyTransformationTimePerChangeType().get("AddEObject").getCount());
		assertEquals(50, executionTimes.getBodyTransformationTimePerChangeType().get("AddEObject").getTotalTime());
	}

	@Test
	void theThreeNewMapsAndTheThreeOriginalScalarFields_areIndependentOfEachOther() {
		executionTimes.setChangePropagationTime(111);
		executionTimes.setInstrumentationTime(222);
		executionTimes.setOverallTime(333);

		executionTimes.recordFineGrainedTiming("javaModelComparison", 10);
		executionTimes.recordChangePropagationTimeForChangeType("AddEObject", 20);
		executionTimes.recordBodyTransformationTimeForChangeType("AddEObject", 30);

		assertEquals(111, executionTimes.getChangePropagationTime());
		assertEquals(222, executionTimes.getInstrumentationTime());
		assertEquals(333, executionTimes.getOverallTime());
		assertTrue(executionTimes.getFineGrainedTimings().containsKey("javaModelComparison"));
		assertTrue(executionTimes.getChangePropagationTimePerChangeType().containsKey("AddEObject"));
		assertTrue(executionTimes.getBodyTransformationTimePerChangeType().containsKey("AddEObject"));
	}
}
