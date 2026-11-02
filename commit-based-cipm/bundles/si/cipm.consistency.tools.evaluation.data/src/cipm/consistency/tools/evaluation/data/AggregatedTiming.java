package cipm.consistency.tools.evaluation.data;

/**
 * Accumulates timing measurements for repeated calls of the same kind (e.g. all calls
 * propagating the same concrete EChange subtype), without storing one entry per call.
 * Holds a running count and total time, plus the single longest call seen, so an average
 * can be derived without a slow individual call being hidden inside it.
 *
 * @author Khalid Ukasha
 */
public class AggregatedTiming {
    private long count;
    private long totalTime;
    private long maxTime;

    /**
     * Folds one more measurement into this aggregate.
     *
     * @param duration the duration of the call just measured, in the same unit for every
     *            call folded into this instance.
     */
    public void record(long duration) {
        count++;
        totalTime += duration;
        if (duration > maxTime) {
            maxTime = duration;
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }
}
