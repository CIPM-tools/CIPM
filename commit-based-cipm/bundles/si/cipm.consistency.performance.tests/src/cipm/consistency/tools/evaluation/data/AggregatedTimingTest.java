package cipm.consistency.tools.evaluation.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link AggregatedTiming}, the shared accumulator behind every fine-grained timing
 * measurement in this feature.
 */
class AggregatedTimingTest {

	@Test
	void newInstance_hasZeroState() {
		AggregatedTiming timing = new AggregatedTiming();

		assertEquals(0, timing.getCount());
		assertEquals(0, timing.getTotalTime());
		assertEquals(0, timing.getMaxTime());
	}

	@Test
	void singleRecord_updatesCountTotalAndMax() {
		AggregatedTiming timing = new AggregatedTiming();

		timing.record(100);

		assertEquals(1, timing.getCount());
		assertEquals(100, timing.getTotalTime());
		assertEquals(100, timing.getMaxTime());
	}

	@Test
	void multipleRecords_accumulateCountAndTotal() {
		AggregatedTiming timing = new AggregatedTiming();

		timing.record(100);
		timing.record(50);
		timing.record(30);

		assertEquals(3, timing.getCount());
		assertEquals(180, timing.getTotalTime());
	}

	@Test
	void recordingASmallerDurationAfterALargerOne_doesNotLowerMaxTime() {
		AggregatedTiming timing = new AggregatedTiming();

		timing.record(100);
		timing.record(10);

		assertEquals(100, timing.getMaxTime());
	}

	@Test
	void recordingALargerDurationAfterASmallerOne_raisesMaxTime() {
		AggregatedTiming timing = new AggregatedTiming();

		timing.record(10);
		timing.record(100);

		assertEquals(100, timing.getMaxTime());
	}
}
