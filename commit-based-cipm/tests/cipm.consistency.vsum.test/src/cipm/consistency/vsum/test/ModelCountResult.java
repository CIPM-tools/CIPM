package cipm.consistency.vsum.test;

public class ModelCountResult {
    private int elements;
    private int containmentReferences;
    private int nonContainmentReferences;
    
    ModelCountResult(int elements, int containmentReferences, int nonContainmentReferences) {
        this.elements = elements;
        this.containmentReferences = containmentReferences;
        this.nonContainmentReferences = nonContainmentReferences;
    }

    public int getNumberOfElements() {
        return this.elements;
    }
    
    public int getNumberOfContainmentReferences() {
        return this.containmentReferences;
    }
    
    public int getNumberOfNonContainmentReferences() {
        return this.nonContainmentReferences;
    }
}
