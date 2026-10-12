package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

public class LegacyEvaluationDataContainer {
	
	private boolean validated = false;
    private boolean evaluationRan = false;
    private String errorMessage = null;

    private ChangeStatistic changeStatistic = new ChangeStatistic();
    private CodeModelCorrectnessEval codeModelCorrectness = new CodeModelCorrectnessEval();
    private CodeModelUpdateEvalData codeModelUpdateEval = null;
    private List<PcmUpdateEvalData> pcmUpdateEvals = new ArrayList<>();
    private ImUpdateEvalData imUpdateEval = null;
    private InstrumentationEvaluationData instrumentationData = null;
    private ExecutionTimeData executionTimes = new ExecutionTimeData();
    
    public boolean isValidated() {
        return validated;
    }

    public boolean isEvaluationRan() {
        return evaluationRan;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ChangeStatistic getChangeStatistic() {
        return changeStatistic;
    }

    public CodeModelCorrectnessEval getCodeModelCorrectness() {
        return codeModelCorrectness;
    }

    public CodeModelUpdateEvalData getCodeModelUpdateEval() {
        return codeModelUpdateEval;
    }

    public List<PcmUpdateEvalData> getPcmUpdateEvals() {
        return pcmUpdateEvals;
    }

    public ImUpdateEvalData getImUpdateEval() {
        return imUpdateEval;
    }

    public InstrumentationEvaluationData getInstrumentationData() {
        return instrumentationData;
    }

    public ExecutionTimeData getExecutionTimes() {
        return executionTimes;
    }
    
    
}
