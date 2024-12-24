package cipm.consistency.vsum.test.evaluator.commitHistory;

public class ImHistoryEvaluation {
    private int numberAIP;
    private int numberActiveAIP;    

    private double instrumentationOverheadReduction;

    public void calculateDerivedValue() {
        instrumentationOverheadReduction = ((double) numberAIP - numberActiveAIP) / numberAIP;
        if (Double.isNaN(instrumentationOverheadReduction)) {
            instrumentationOverheadReduction = -1;
        }
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

    public void setNumberActiveAIP(int numberActiveAIP) {
        this.numberActiveAIP = numberActiveAIP;
    }
}