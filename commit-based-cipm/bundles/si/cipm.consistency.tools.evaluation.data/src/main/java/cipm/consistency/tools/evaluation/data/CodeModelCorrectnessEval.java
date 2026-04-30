package cipm.consistency.tools.evaluation.data;

public class CodeModelCorrectnessEval {
    
    private int identicalFiles;
    private int similarFiles;
    private int dissimilarFiles;

    public int getIdenticalFiles() {
        return identicalFiles;
    }

    public void setIdenticalFiles(int identicalFiles) {
        this.identicalFiles = identicalFiles;
    }

    public int getSimilarFiles() {
        return similarFiles;
    }

    public void setSimilarFiles(int similarFiles) {
        this.similarFiles = similarFiles;
    }

    public int getDissimilarFiles() {
        return dissimilarFiles;
    }

    public void setDissimilarFiles(int dissimilarFiles) {
        this.dissimilarFiles = dissimilarFiles;
    }

}
