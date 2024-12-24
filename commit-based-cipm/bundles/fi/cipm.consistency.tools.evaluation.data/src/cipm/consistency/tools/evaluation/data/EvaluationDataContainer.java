package cipm.consistency.tools.evaluation.data;

/**
 * Container for the complete data.
 * 
 * @author Martin Armbruster
 */
public class EvaluationDataContainer {
    private static EvaluationDataContainer globalContainer;

    public static EvaluationDataContainer getGlobalContainer() {
        if (globalContainer == null) {
            globalContainer = new EvaluationDataContainer();
        }
        return globalContainer;
    }

    public static void setGlobalContainer(EvaluationDataContainer newContainer) {
        globalContainer = newContainer;
    }

    private long evaluationTime = System.currentTimeMillis();
	private int numberOfPropagation = 0;
    private ChangeStatistic changeStatistic = new ChangeStatistic();
    private JavaEvaluationData javaComparisonResult = new JavaEvaluationData();
	private RepositoryModelEvaluationData repoModelComparisonResult = new RepositoryModelEvaluationData();
    private IMEvaluationData imEvalResult = new IMEvaluationData();
    private InstrumentationEvaluationData instrumentationData = new InstrumentationEvaluationData();
	private InstrumentationEvaluationData instrumentationIndependentData = new InstrumentationEvaluationData();
    private ExecutionTimeData executionTimes = new ExecutionTimeData();

    public long getEvaluationTime() {
        return evaluationTime;
    }

	public int getNumberOfPropagation() {
		return numberOfPropagation;
	}

	public void setNumberOfPropagation(int num) {
		numberOfPropagation = num;
	}

    public ChangeStatistic getChangeStatistic() {
        return changeStatistic;
    }

    public JavaEvaluationData getJavaComparisonResult() {
        return javaComparisonResult;
    }

	public RepositoryModelEvaluationData getRepoComparisonResult() {
		return repoModelComparisonResult;
	}

    public IMEvaluationData getImEvalResult() {
        return imEvalResult;
    }

    public InstrumentationEvaluationData getInstrumentationData() {
        return instrumentationData;
    }

	public InstrumentationEvaluationData getInstrumentationIndependentData() {
		return instrumentationIndependentData;
	}

    public ExecutionTimeData getExecutionTimes() {
        return executionTimes;
    }
}
