package cipm.consistency.vsum.test.evaluator.commitHistory;

public class EvaluationSummary {
    
    private int total = 0;
    private int failures = 0;
    private int invalids = 0;

    private double worstCodeModelUpdateEvalJaccardCoefficient = 1;
    private double worstPcmUpdateEvalJaccardCoefficient = 1;
    private double worstImUpdateEvalFScoreAIP = 1;
    private double worstImUpdateEvalFScoreActiveAIP = 1;

    public double getWorstCodeUpdateEvalJaccardCoefficient() {
        return worstCodeModelUpdateEvalJaccardCoefficient;
    }

    public void addCodeModelUpdateEvalJaccardCoefficient(double worstCodeUpdateEvalJaccardCoefficient) {
        if (worstCodeUpdateEvalJaccardCoefficient < this.worstCodeModelUpdateEvalJaccardCoefficient) {
            this.worstCodeModelUpdateEvalJaccardCoefficient = worstCodeUpdateEvalJaccardCoefficient;
        }
    }

    public double getWorstPcmUpdateEvalJaccardCoefficient() {
        return worstPcmUpdateEvalJaccardCoefficient;
    }

    public void addWorstPcmUpdateEvalJaccardCoefficient(double worstPcmUpdateEvalJaccardCoefficient) {
        if (worstPcmUpdateEvalJaccardCoefficient < this.worstPcmUpdateEvalJaccardCoefficient) {
            this.worstPcmUpdateEvalJaccardCoefficient = worstPcmUpdateEvalJaccardCoefficient;
        }
    }

    public double getWorstImUpdateEvalFScore() {
        return worstImUpdateEvalFScoreAIP;
    }

    public void setWorstImUpdateEvalFScoreAIP(double worstImUpdateEvalFScore) {
        if (worstImUpdateEvalFScore < this.worstImUpdateEvalFScoreAIP) {
            this.worstImUpdateEvalFScoreAIP = worstImUpdateEvalFScore;
        }
    }

    public double getWorstImUpdateEvalFScoreActiveAIP() {
        return worstImUpdateEvalFScoreActiveAIP;
    }

    public void setWorstImUpdateEvalFScoreActiveAIP(double worstImUpdateEvalFScoreActiveAIP) {
        if (worstImUpdateEvalFScoreActiveAIP < this.worstImUpdateEvalFScoreActiveAIP
                && worstImUpdateEvalFScoreActiveAIP >= 0) {
            this.worstImUpdateEvalFScoreActiveAIP = worstImUpdateEvalFScoreActiveAIP;
        }
    }

    public int getInvalids() {
        return invalids;
    }

    public void setInvalids(int invalids) {
        this.invalids = invalids;
    }

    public int getFailures() {
        return failures;
    }

    public void incrementFailures() {
        this.failures++;
    }

    public void incrementTotal() {
        this.setTotal(this.getTotal() + 1);
    }

    public void incrementInvalids() {
        this.invalids++;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
