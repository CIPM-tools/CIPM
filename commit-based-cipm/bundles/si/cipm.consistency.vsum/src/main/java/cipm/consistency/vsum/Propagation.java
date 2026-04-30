package cipm.consistency.vsum;

import java.nio.file.Path;
import java.util.List;

import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * This class bundles the results of a propagation.
 * 
 * This includes the changes which were propagated, an the path to the previous, target and actual models of the propagation.
 * 
 * If the propagation and change resolution work correctly the files at the targetVersionParsedModel and propagationResultModel
 * should be (apart from ids) identical.
 * 
 * The original changes are the changes between the models at previousVersionParsedModel and targetVersionParsedModel.
 * 
 * @author Lukas Burgey
 */
public class Propagation {

    private Path previousParsedCodeModelPath;
    private Path previousPcmRepositoryPath;

    // target version parsed model and propagation result should be identical if the change
    // resolution and propagation worked correctly
    private Path parsedCodeModelPath;
    private Path vsumCodeModelPath;
    
    // the commit that was propagated with this propagation;
    private String commitId;

    // the path to the snapshot of the commit integration state that was taken
    // after the propagation was complete
    private Path commitIntegrationStateCopyPath;
    
    // the path to the commit integration state where the propagation was originally made
    private Path commitIntegrationStateOriginalPath;

    private int originalChangeCount = 0;
    private int consequentialChangeCount = 0;

    private List<PropagatedChange> changes = null;
    
    private IllegalStateException exception;


    public Propagation(List<PropagatedChange> changes) {
        if (changes == null) {
            return;
        }

        this.changes = changes;
        for (var change : changes) {
            consequentialChangeCount += change.getConsequentialChanges()
                .getEChanges()
                .size();
            originalChangeCount += change.getOriginalChange()
                .getEChanges()
                .size();
        }
        originalChangeCount -= consequentialChangeCount;
    }

    public List<PropagatedChange> getChanges() {
        return changes;
    }

    public int getOriginalChangeCount() {
        return originalChangeCount;
    }

    public int getConsequentialChangeCount() {
        return consequentialChangeCount;
    }

    public boolean isEmpty() {
        return changes != null && changes.isEmpty();
    }

    public Path getParsedCodeModelPreviousVersionPath() {
        return previousParsedCodeModelPath;
    }

    public void setPreviousParsedCodeModelPath(Path parsedCodeModelPreviousVersionPath) {
        this.previousParsedCodeModelPath = parsedCodeModelPreviousVersionPath;
    }

    public Path getParsedCodeModelTargetVersionPath() {
        return parsedCodeModelPath;
    }

    public void setParsedCodeModelPath(Path parsedCodeModelTargetVersionPath) {
        this.parsedCodeModelPath = parsedCodeModelTargetVersionPath;
    }

    public Path getPropagationResultCodeModelPath() {
        return vsumCodeModelPath;
    }

    public void setPropagationResultCodeModelPath(Path propagationResultCodeModelPath) {
        this.vsumCodeModelPath = propagationResultCodeModelPath;
    }


    public Path getCommitIntegrationStateCopyPath() {
        return commitIntegrationStateCopyPath;
    }

    public void setCommitIntegrationStateSnapshotPath(Path commitIntegrationStateCopyPath) {
        this.commitIntegrationStateCopyPath = commitIntegrationStateCopyPath;
    }

    public Path getPreviousPcmRepositoryPath() {
        return previousPcmRepositoryPath;
    }

    public void setPreviousPcmRepositoryPath(Path previousPcmRepositoryPath) {
        this.previousPcmRepositoryPath = previousPcmRepositoryPath;
    }

    public Path getCommitIntegrationStateOriginalPath() {
        return commitIntegrationStateOriginalPath;
    }

    public void setCommitIntegrationStateOriginalPath(Path commitIntegrationStateOriginalPath) {
        this.commitIntegrationStateOriginalPath = commitIntegrationStateOriginalPath;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public IllegalStateException getException() {
        return exception;
    }

    public void setException(IllegalStateException exception) {
        this.exception = exception;
    }

}
