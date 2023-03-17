package cipm.consistency.commitintegration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;

import cipm.consistency.commitintegration.lang.lua.runtimedata.ChangedResources;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.vsum.Propagation;
import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * This class is responsible for controlling the complete change propagation and adaptive
 * instrumentation.
 * 
 * @param <CM>
 *            The code model class that is used for the integration
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public abstract class CommitIntegrationController<CM extends CodeModelFacade> {
    private static final Logger LOGGER = Logger.getLogger(CommitIntegrationController.class.getName());
    protected CommitIntegrationState<CM> state;

    public void initialize(CommitIntegration<CM> commitIntegration)
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        state = new CommitIntegrationState<CM>();
        state.initialize(commitIntegration);
    }

    /**
     * Disposes the integration state if it is not fresh
     * 
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws IOException
     * @throws GitAPIException
     */
    protected void reset() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        if (!state.isFresh()) {
            LOGGER.info("Resetting commitintegration");
            var ci = state.getCommitIntegration();
            state.getDirLayout()
                .delete();
            state.dispose();
            state.initialize(ci, ci.getRootPath(), true);
        }
    }

    /**
     * Reload the current integration state from disk
     * 
     * @throws GitAPIException
     * @throws IOException
     * @throws TransportException
     * @throws InvalidRemoteException
     */
    protected void reload() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        var ci = state.getCommitIntegration();
        state.dispose();
        state.initialize(ci);
    }

    /**
     * Propagate the work tree that is currently checked out by the git repo wrapper.
     * 
     * @return The Propagation instance including the used model paths
     */
    private Optional<Propagation> propagateCurrentCheckout() {
        // run possible hooks
        prePropagationHook();

        LOGGER.info(String.format("\n\tPropagating commit #%d: %s", state.getSnapshotCount() + 1,
                state.getGitRepositoryWrapper()
                    .getCurrentCommitHash()));

        var previousParsedModelPath = state.getCurrentParsedModelPath();

        var workTree = state.getGitRepositoryWrapper()
            .getWorkTree()
            .toPath();
        var resource = state.getCodeModelFacade()
            .parseSourceCodeDir(workTree);
        if (resource == null) {
            LOGGER.error("Error parsing code model, not running propagation");
            return Optional.empty();
        }

        // this informs the ComponentSetInfoRegistry singleton that we changed resources which it
        // had
        // mapped infos for
        ChangedResources.setResourcesWereChanged();

        var previousRepositoryPath = state.createRepositorySnapshot();
        var parsedModelPath = state.createParsedCodeModelSnapshot();
        state.setCurrentParsedModelPath(parsedModelPath);

        long propagationTime = System.currentTimeMillis();

        // the actual propagation is done here
        var propagation = state.getVsumFacade()
            .propagateResource(resource, state.getDirLayout()
                .getVsumCodeModelURI());

        propagationTime = System.currentTimeMillis() - propagationTime;
        EvaluationDataContainer.get()
            .getExecutionTimes()
            .setChangePropagationTime(propagationTime);

        addChangeNumbersToEvaluationData(propagation.getChanges());

        var snapshotPath = state.createSnapshot();

        // add some information needed for the evaluation to the propagation object
        propagation.setCommitIntegrationStateSnapshotPath(snapshotPath);
        propagation.setCommitIntegrationStateOriginalPath(state.getDirLayout()
            .getRootDirPath());
        propagation.setPreviousParsedCodeModelPath(previousParsedModelPath);
        propagation.setParsedCodeModelPath(parsedModelPath);
        propagation.setPreviousPcmRepositoryPath(previousRepositoryPath);

        // trigger some post propagation hooks
        postPropagationHook();

        return Optional.of(propagation);
    }

    protected Optional<Propagation> propagateChanges(String firstCommitId, String secondCommitId)
            throws IncorrectObjectTypeException, IOException {
        if (!prePropagationChecks(firstCommitId, secondCommitId)) {
            LOGGER.info("Prechecks indicate no propagation is needed.");
            return Optional.empty();
        }

        var cs = EvaluationDataContainer.get()
            .resetChangeStatistic();
        cs.setOldCommit(firstCommitId);
        cs.setNewCommit(secondCommitId);
        cs.setNumberCommits(state.getGitRepositoryWrapper()
            .getAllCommitsBetweenTwoCommits(firstCommitId, secondCommitId)
            .size());

        // this computes diff data and puts it into the evaluation data
        state.getGitRepositoryWrapper()
            .computeDiffsBetweenTwoCommits(firstCommitId, secondCommitId);

        if (checkout(secondCommitId)) {
            var propagation = propagateCurrentCheckout();
            if (propagation.isPresent()) {
                propagation.get()
                    .setCommitId(secondCommitId);
            }
            return propagation;
        }

        return Optional.empty();
    }

    /**
     * Propagates changes for a given list of commitsIds. If no commitIds are given, the current
     * checkout of the git repo will be propagated. If there is one commitIds are given, it is
     * checked out and propagated to the state. If the first commitId is null, a fresh commit
     * integration state will be used for the commit integration. If the first commitId is not null,
     * it is expected that this commitId was the last propagated commitId of the commit integration
     * state
     * 
     * @param commitIds
     *            ids of the commits.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             if the repository cannot be read.
     */
    public List<Optional<Propagation>> propagateChanges(String... commitIds) throws GitAPIException, IOException {
        if (commitIds.length == 0) {
            return List.of();
        } else if (commitIds.length == 1 && commitIds[0] != null) {
            return List.of(propagateChanges(null, commitIds[0]));
        }

        // make sure the state is clean if the first id is null
        if (commitIds[0] == null) {
            reset();
        }

        var numberOfPropagations = commitIds.length - 1;
        List<Optional<Propagation>> allPropagations = new ArrayList<>(numberOfPropagations);

        for (var i = 0; i < numberOfPropagations; i++) {
            var propagation = propagateChanges(commitIds[i], commitIds[i + 1]);
            allPropagations.add(propagation);
        }
        return allPropagations;
    }

    protected boolean prePropagationChecks(String firstCommitId, String secondCommitId) {
        if (firstCommitId != null) {
            return true;
        }
        LOGGER.debug("Obtaining all differences.");
        List<DiffEntry> diffs;
        try {
            diffs = state.getGitRepositoryWrapper()
                .computeDiffsBetweenTwoCommits(firstCommitId, secondCommitId);
        } catch (RevisionSyntaxException | IOException e) {
            e.printStackTrace();
            return false;
        }
        if (diffs.isEmpty()) {
            LOGGER.info("No source files changed between " + firstCommitId + " and " + secondCommitId + ".");
            return false;
        }
        return true;
    }

    private void addChangeNumbersToEvaluationData(List<PropagatedChange> changes) {
        var cs = EvaluationDataContainer.get()
            .getChangeStatistic();
        var totalChanges = 0;

        for (var change : changes) {
            var changeCount = change.getOriginalChange()
                .getEChanges()
                .size();
            totalChanges += changeCount;
            for (var modelDescriptor : change.getOriginalChange()
                .getAffectedEObjectsMetamodelDescriptors()) {
                for (var uri : modelDescriptor.getNsUris()) {
                    cs.setNumberVitruvChangesPerModel(uri, changeCount);
                }
            }
        }
        cs.setNumberVitruvChanges(totalChanges);
    }

    /**
     * Can be overwritten to do processing after every checkout
     * 
     * @return
     */
    protected boolean preprocessCheckout() {
        return true;
    }

    protected void prePropagationHook() {
        LOGGER.info("Running Pre Propagation Hook");

    }

    protected void postPropagationHook() {
        LOGGER.info("Running Post Propagation Hook");
        // reload models which may have changed
//        state.getPcmFacade().reload();
//        state.getImFacade().reload();
//        state.getVsumFacade().forceReload();
    }

    protected boolean checkout(String commitId) {
        LOGGER.debug("Checkout of " + commitId);
        try {
            state.getGitRepositoryWrapper()
                .checkout(commitId);
            if (!preprocessCheckout()) {
                LOGGER.debug("The preprocessing failed. Aborting.");
                return false;
            }
            return true;
        } catch (GitAPIException e) {
            LOGGER.error("Unable to checkout", e);
        }
        return false;
    }

}
