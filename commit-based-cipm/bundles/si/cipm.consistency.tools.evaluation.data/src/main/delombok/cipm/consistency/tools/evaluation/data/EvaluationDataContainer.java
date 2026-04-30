package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for the complete data.
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class EvaluationDataContainer {
    private static EvaluationDataContainer globalContainer;

    public static EvaluationDataContainer get() {
        if (globalContainer == null) {
            globalContainer = new EvaluationDataContainer();
        }
        return globalContainer;
    }

    public static void set(EvaluationDataContainer newContainer) {
        globalContainer = newContainer;
    }

    /**
     * the result of the binary evaluations
     */
    private boolean validated = false;
    private boolean evaluationRan = false;
    private String errorMessage = null;

//    private long evaluationTime = System.currentTimeMillis();
    private ChangeStatistic changeStatistic = new ChangeStatistic();
    private CodeModelCorrectnessEval codeModelCorrectness = new CodeModelCorrectnessEval();
    private CodeModelUpdateEvalData codeModelUpdateEval = null;
    private List<PcmUpdateEvalData> pcmUpdateEvals = new ArrayList<>();
    private ImUpdateEvalData imUpdateEval = null;
//    private InstrumentationEvaluationData instrumentationData = new InstrumentationEvaluationData();
    private InstrumentationEvaluationData instrumentationData = null;
    private ExecutionTimeData executionTimes = new ExecutionTimeData();

//    public long getEvaluationTime() {
//        return evaluationTime;
//    }
    
    public ChangeStatistic resetChangeStatistic() {
        changeStatistic = new ChangeStatistic();
        return changeStatistic;
    }    
    
    public ImUpdateEvalData resetImUpdateEval() {
        imUpdateEval = new ImUpdateEvalData();
        return imUpdateEval;
    }

    public ChangeStatistic getChangeStatistic() {
        return changeStatistic;
    }

    public CodeModelUpdateEvalData getCodeModelUpdateEvalData() {
        if (codeModelUpdateEval == null) {
            codeModelUpdateEval = new CodeModelUpdateEvalData();
        }
        return codeModelUpdateEval;
    }

    public ImUpdateEvalData getImUpdateEvalData() {
        if (imUpdateEval == null) {
            imUpdateEval = new ImUpdateEvalData();
        }
        return imUpdateEval;
    }

    public InstrumentationEvaluationData getInstrumentationData() {
        return instrumentationData;
    }

    public ExecutionTimeData getExecutionTimes() {
        return executionTimes;
    }

    public boolean valid() {
        return validated;
    }

    public void setSuccessful(boolean success) {
        this.validated = success;
    }

    public List<PcmUpdateEvalData> getPcmUpdateEvals() {
        return pcmUpdateEvals;
    }

    public CodeModelCorrectnessEval getCodeModelCorrectness() {
        return codeModelCorrectness;
    }

    public void setCodeModelCorrectness(CodeModelCorrectnessEval codeModelCorrectness) {
        this.codeModelCorrectness = codeModelCorrectness;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isEvaluationRan() {
        return evaluationRan;
    }

    public void setEvaluationRan(boolean evaluationRan) {
        this.evaluationRan = evaluationRan;
    }
}
