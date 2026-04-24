package cipm.consistency.tools.evaluation.data;

import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;

/**
 * A data structure for the evaluation of the update of the Java models.
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public final class PcmUpdateEvalData {

    public enum PcmEvalType {
        ComparisonWithAutomatic, ComparisonWithManuallyCreated;
    }

    private PcmEvalType evalType;

    public enum ComparisonType {
        LukasHierarchical, PcmDiffUtil;
    }

    private ComparisonType comparisonType;

    private int numberUnmatchedOldElements;
    private int numberUnmatchedNewElements;
    private int intersectionCardinality;
    private int unionCardinality;
    private double jaccardCoefficient;

    public void setValuesUsingJaccardCoefficientResult(JaccardCoefficientResult comparison) {
        this.jaccardCoefficient = comparison.getJC();
        this.numberUnmatchedNewElements = comparison.getNewUnmatched()
            .size();
        this.numberUnmatchedOldElements = comparison.getOldUnmatched()
            .size();
        this.intersectionCardinality = comparison.getIntersectionCardinality();
        this.unionCardinality = comparison.getUnionCardinality();
    }

    public int getOldElementsCount() {
        return numberUnmatchedOldElements;
    }

    public void setOldElementsCount(int oldElementsCount) {
        this.numberUnmatchedOldElements = oldElementsCount;
    }

    public int getNewElementsCount() {
        return numberUnmatchedNewElements;
    }

    public void setNewElementsCount(int newElementsCount) {
        this.numberUnmatchedNewElements = newElementsCount;
    }

    public int getIntersectionCardinality() {
        return intersectionCardinality;
    }

    public void setIntersectionCardinality(int intersectionCardinality) {
        this.intersectionCardinality = intersectionCardinality;
    }

    public int getUnionCardinality() {
        return unionCardinality;
    }

    public void setUnionCardinality(int unionCardinality) {
        this.unionCardinality = unionCardinality;
    }

    public double getJc() {
        return jaccardCoefficient;
    }

    public void setJc(double jc) {
        this.jaccardCoefficient = jc;
    }

    public PcmEvalType getEvalType() {
        return evalType;
    }

    public void setEvalType(PcmEvalType evalType) {
        this.evalType = evalType;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }

}
