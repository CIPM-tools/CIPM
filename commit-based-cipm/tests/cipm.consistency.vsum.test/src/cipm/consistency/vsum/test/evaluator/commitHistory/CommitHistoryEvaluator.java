package cipm.consistency.vsum.test.evaluator.commitHistory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

public class CommitHistoryEvaluator {

    private List<EvaluationDataContainer> commitPropagationEvals = new ArrayList<>();
    private CommitHistoryEvaluationData historyEvalData = new CommitHistoryEvaluationData();

    public void addEvaluationDataContainer(EvaluationDataContainer commitPropagationEval) {
        commitPropagationEvals.add(commitPropagationEval);
    }

    public void evaluate() {
        // iterate all the propagations and do accounting for each
        for (var eval : commitPropagationEvals) {
            perPropagationAccounting(eval);
        }
        
        calculate();

        // also copy the per commit evals into the class
        historyEvalData.setCommitPropagationEvals(commitPropagationEvals);
    }

    private void perPropagationAccounting(EvaluationDataContainer eval) {
        historyEvalData.summary.incrementTotal();
        if (!eval.valid()) {
            historyEvalData.summary.incrementInvalids();
        }
        if (eval.getErrorMessage() != null) {
            historyEvalData.summary.incrementFailures();
        }

        historyEvalData.summary.addCodeModelUpdateEvalJaccardCoefficient(eval.getCodeModelUpdateEvalData()
            .getJc());
        for (var pcmUpdateEval : eval.getPcmUpdateEvals()) {
            historyEvalData.summary.addWorstPcmUpdateEvalJaccardCoefficient(pcmUpdateEval.getJc());
        }

        historyEvalData.summary.setWorstImUpdateEvalFScoreActiveAIP(eval.getImUpdateEvalData()
            .getfScoreActiveActionInstrumentationPoints());
        historyEvalData.summary.setWorstImUpdateEvalFScoreAIP(eval.getImUpdateEvalData()
            .getfScoreActionInstrumentation());

        var imHistoryEval = historyEvalData.imUpdateHistoryEval;
        historyEvalData.imUpdateHistoryEval.setNumberAIP(imHistoryEval.getNumberAIP() + eval.getImUpdateEvalData()
            .getNumberAIP());
        historyEvalData.imUpdateHistoryEval
            .setNumberActiveAIP(imHistoryEval.getNumberActiveAIP() + eval.getImUpdateEvalData()
                .getNumberActiveAIP());
    }
    
    private void calculate() {
        historyEvalData.imUpdateHistoryEval.calculateDerivedValue();
    }

    public void write(Path evalFileDir) {
        Gson gson = new Gson();
        try (BufferedWriter writer = Files.newBufferedWriter(evalFileDir.resolve("commitHistoryEvaluationData.json"))) {
            gson.toJson(historyEvalData, CommitHistoryEvaluationData.class, gson.newJsonWriter(writer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class CommitHistoryEvaluationData {
        private List<EvaluationDataContainer> commitPropagationEvals = new ArrayList<>();
        private ImHistoryEvaluation imUpdateHistoryEval = new ImHistoryEvaluation();
        private EvaluationSummary summary = new EvaluationSummary();

        public ImHistoryEvaluation getImUpdateHistoryEval() {
            return imUpdateHistoryEval;
        }

        public List<EvaluationDataContainer> getCommitPropagationEvals() {
            return commitPropagationEvals;
        }

        public void setCommitPropagationEvals(List<EvaluationDataContainer> commitPropagationEvals) {
            this.commitPropagationEvals = commitPropagationEvals;
        }
    }
}
