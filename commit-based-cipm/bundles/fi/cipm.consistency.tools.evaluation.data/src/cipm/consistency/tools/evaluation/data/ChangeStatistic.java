package cipm.consistency.tools.evaluation.data;

/**
 * A data structure for statistics about the propagated changes.
 * 
 * @author Martin Armbruster
 */
public class ChangeStatistic {
    private String oldCommit = "";
    private String newCommit = "";
    private int numberCommits = -1;
    private int numberChangedJavaFiles = -1;
    private int numberAddedLines = -1;
    private int numberRemovedLines = -1;
    private int numberVitruvChanges = -1;

    public String getOldCommit() {
        return oldCommit;
    }

    public void setOldCommit(String oldCommit) {
        this.oldCommit = oldCommit;
    }

    public String getNewCommit() {
        return newCommit;
    }

    public void setNewCommit(String newCommit) {
        this.newCommit = newCommit;
    }

    public int getNumberCommits() {
        return numberCommits;
    }

    public void setNumberCommits(int numberCommits) {
        this.numberCommits = numberCommits;
    }

    public int getNumberChangedJavaFiles() {
        return numberChangedJavaFiles;
    }

    public void setNumberChangedJavaFiles(int numberChangedJavaFiles) {
        this.numberChangedJavaFiles = numberChangedJavaFiles;
    }

    public int getNumberAddedLines() {
        return numberAddedLines;
    }

    public void setNumberAddedLines(int numberAddedLines) {
        this.numberAddedLines = numberAddedLines;
    }

    public int getNumberRemovedLines() {
        return numberRemovedLines;
    }

    public void setNumberRemovedLines(int numberRemovedLines) {
        this.numberRemovedLines = numberRemovedLines;
    }

    public int getNumberVitruvChanges() {
        return numberVitruvChanges;
    }

    public void setNumberVitruvChanges(int numberVitruvChanges) {
        this.numberVitruvChanges = numberVitruvChanges;
    }
}
