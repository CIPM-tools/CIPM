package cipm.consistency.tools.evaluation.data;

/**
 * A data structure for execution times.
 * 
 * @author Martin Armbruster
 */
public class ExecutionTimeData {
    private long changePropagationTime = -1;
    private long instrumentationTime = -1;
    private long overallTime = -1;

    public long getChangePropagationTime() {
        return changePropagationTime;
    }

    public void setChangePropagationTime(long changePropagationTime) {
        this.changePropagationTime = changePropagationTime;
    }

    public long getInstrumentationTime() {
        return instrumentationTime;
    }

    public void setInstrumentationTime(long instrumentationTime) {
        this.instrumentationTime = instrumentationTime;
    }

    public long getOverallTime() {
        return overallTime;
    }

    public void setOverallTime(long overallTime) {
        this.overallTime = overallTime;
    }
}
