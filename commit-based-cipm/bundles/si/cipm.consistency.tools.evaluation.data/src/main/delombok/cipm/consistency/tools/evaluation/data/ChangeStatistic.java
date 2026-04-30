package cipm.consistency.tools.evaluation.data;

import java.util.HashMap;
import java.util.Map;

/**
 * A data structure for statistics about the propagated changes.
 * 
 * @author Martin Armbruster
 */
public class ChangeStatistic {
    private String oldCommit;
    private String newCommit;
    
    private int numberClocFiles;
    private int numberClocLinesBlanks;
    private int numberClocLinesComments;
    private int numberClocLinesCode;

    private int numberCommits;
    private int numberChangedJavaFiles;
    private int numberAddedLines;
    private int numberRemovedLines;
    private int numberVitruvChanges;

    private Map<String, Integer> numberVitruvChangesPerModel = new HashMap<>();

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

    public void setNumberVitruvChangesPerModel(String uri, int changeCount) {
        this.numberVitruvChangesPerModel.put(uri, changeCount);
    }

    public int getNumberClocFiles() {
        return numberClocFiles;
    }

    public void setNumberClocFiles(int numberClocFiles) {
        this.numberClocFiles = numberClocFiles;
    }

    public int getNumberClocLinesBlanks() {
        return numberClocLinesBlanks;
    }

    public void setNumberClocLinesBlanks(int numberClocLinesBlanks) {
        this.numberClocLinesBlanks = numberClocLinesBlanks;
    }

    public int getNumberClocLinesComments() {
        return numberClocLinesComments;
    }

    public void setNumberClocLinesComments(int numberClocLinesComments) {
        this.numberClocLinesComments = numberClocLinesComments;
    }

    public int getNumberClocLinesCode() {
        return numberClocLinesCode;
    }

    public void setNumberClocLinesCode(int numberClocLinesCode) {
        this.numberClocLinesCode = numberClocLinesCode;
    }

    public Map<String, Integer> getNumberVitruvChangesPerModel() {
        return numberVitruvChangesPerModel;
    }

    public void setNumberVitruvChangesPerModel(Map<String, Integer> numberVitruvChangesPerModel) {
        this.numberVitruvChangesPerModel = numberVitruvChangesPerModel;
    }
}
