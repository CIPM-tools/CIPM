package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A data structure for the evaluation of the update of the extended IM.
 * 
 * @author Martin Armbruster
 */
public class ImUpdateEvalData {
    private int numberIP;

    private int numberSIP;
    private int numberMatchedSIP;

    private int numberAIP;
    private int numberMatchedAIP;

    private int numberActiveAIP;
    private int numberMatchedActiveAIP;
    private int numberAddedActions;
    private int numberChangedActions;

    private double ratioActiveAIPs;

    private double ratioAipPerSip;

    private double proportionalOverheadReduction;

    private double fScoreServiceInstrumentationPoints;
    private double fScoreActionInstrumentationPoints;
    private double fScoreActiveActionInstrumentationPoints;

    private List<String> unmatchedSIPs = new ArrayList<>();
    private List<String> unmatchedAIPs = new ArrayList<>();
    private List<String> unmatchedActiveAIPs = new ArrayList<>();

    private List<String> unmatchedSEFFs = new ArrayList<>();
    private List<String> unmatchedActions = new ArrayList<>();
    private List<String> unmatchedChangedActions = new ArrayList<>();
    private List<String> unmatchedAddedActions = new ArrayList<>();

    // these are populated in the CPRs
    private List<String> createdActions = new ArrayList<>();
    private List<String> fusedActions = new ArrayList<>();
    private List<String> removedActions = new ArrayList<>();

    public void calculateDerivedValues() {
        numberAIP = numberIP - numberSIP;
        ratioActiveAIPs = (double) numberActiveAIP / numberAIP;
        ratioAipPerSip = (double) numberAIP / numberSIP;

        proportionalOverheadReduction = 1 - ratioActiveAIPs;

        fScoreServiceInstrumentationPoints = calcFScore(numberMatchedSIP, unmatchedSIPs.size(), unmatchedSEFFs.size());
        fScoreActionInstrumentationPoints = calcFScore(numberMatchedAIP, unmatchedAIPs.size(), unmatchedActions.size());

        fScoreActiveActionInstrumentationPoints = calcFScore(numberMatchedActiveAIP,
                unmatchedChangedActions.size() + unmatchedAddedActions.size(), unmatchedActiveAIPs.size());

        numberChangedActions = generateChangedActions().size();
    }
    
    public void cleanUp() {
        createdActions = null;
        fusedActions = null;
        removedActions = null;
    }

    private double calcFScore(int truePos, int falsePos, int falseNeg) {
        var fScore = ((double) 2 * truePos) / ((double) 2 * truePos + falsePos + falseNeg);
        if (Double.isNaN(fScore)) {
            fScore = -1;
        }
        return fScore;
    }

    public int getNumberMatchedAIP() {
        return numberMatchedAIP;
    }

    public void setNumberMatchedAIP(int numberMatchedIP) {
        this.numberMatchedAIP = numberMatchedIP;
    }

    public int getNumberMatchedSIP() {
        return numberMatchedSIP;
    }

    public void setNumberMatchedSIP(int numberMatchedSIP) {
        this.numberMatchedSIP = numberMatchedSIP;
    }

    public int getNumberIP() {
        return numberIP;
    }

    public void setNumberIP(int numberAllIP) {
        this.numberIP = numberAllIP;
    }

    public int getNumberSIP() {
        return numberSIP;
    }

    public void setNumberSIP(int numberSIP) {
        this.numberSIP = numberSIP;
    }

    public int getNumberAIP() {
        return numberAIP;
    }

    public void setNumberAIP(int numberAIP) {
        this.numberAIP = numberAIP;
    }

    public int getNumberActiveAIP() {
        return numberActiveAIP;
    }

    public void setNumberActiveAIP(int numberActivatedAIP) {
        this.numberActiveAIP = numberActivatedAIP;
    }

    public double getRatioActiveAIPs() {
        return ratioActiveAIPs;
    }

    public List<String> getUnmatchedSEFFs() {
        return unmatchedSEFFs;
    }

    public List<String> getUnmatchedActions() {
        return unmatchedActions;
    }

    public List<String> getUnmatchedSIPs() {
        return unmatchedSIPs;
    }

    public List<String> getUnmatchedAIPs() {
        return unmatchedAIPs;
    }

    public double getfScoreActionInstrumentation() {
        return fScoreActionInstrumentationPoints;
    }

    public double getfScoreServiceInstrumentation() {
        return fScoreServiceInstrumentationPoints;
    }

    public double getfScoreActiveActionInstrumentationPoints() {
        return fScoreActiveActionInstrumentationPoints;
    }

    public List<String> getUnmatchedActiveAIPs() {
        return unmatchedActiveAIPs;
    }

    public void setUnmatchedActiveAIPs(List<String> unmatchedActiveAIPs) {
        this.unmatchedActiveAIPs = unmatchedActiveAIPs;
    }

    public List<String> getUnmatchedChangedActions() {
        return unmatchedChangedActions;
    }

    public void setUnmatchedChangedActions(List<String> unmatchedChangedActions) {
        this.unmatchedChangedActions = unmatchedChangedActions;
    }

    public int getNumberMatchedActiveAIP() {
        return numberMatchedActiveAIP;
    }

    public void setNumberMatchedActiveAIP(int numberMatchedActiveAIP) {
        this.numberMatchedActiveAIP = numberMatchedActiveAIP;
    }

    public int getNumberChangedActions() {
        return numberChangedActions;
    }

    public void setNumberAddedActions(int numberChangedActions) {
        this.numberAddedActions = numberChangedActions;
    }

    public double getProportionalOverheadReduction() {
        return proportionalOverheadReduction;
    }

    public double getRatioAipPerSip() {
        return ratioAipPerSip;
    }

    public List<String> getCreatedActions() {
        return createdActions;
    }

    public List<String> getFusedActions() {
        return fusedActions;
    }

    public int getNumberAddedActions() {
        return numberAddedActions;
    }

    public List<String> generateChangedActions() {
        List<String> changedActions = new ArrayList<>();

        // added / changed action instrumentation activation
        for (var fused : fusedActions) {
            if (!createdActions.contains(fused) && !removedActions.contains(fused) && !changedActions.contains(fused)) {
                changedActions.add(fused);
            }
        }
        return changedActions;
    }

    public List<String> getUnmatchedAddedActions() {
        return unmatchedAddedActions;
    }

    public List<String> getRemovedActions() {
        return removedActions;
    }

    public void addActionRemoved(String id) {
        if (!removedActions.contains(id)) {
            removedActions.add(id);
        }
    }
}
