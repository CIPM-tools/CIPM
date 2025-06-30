package cipm.consistency.commitintegration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.VsumFacade;
import cipm.consistency.vsum.VsumFacadeImpl;

/**
 * Encapsulates all state of an ongoing integration
 * 
 * @param <CM>
 *            The code model class that is used for the integration
 * 
 * @author Lukas Burgey
 *
 */
public class CommitIntegrationState<CM extends CodeModelFacade> {
    private static final Logger LOGGER = Logger.getLogger(CommitIntegrationState.class.getName());

    private CommitIntegration<CM> commitIntegration;

    private CommitIntegrationDirLayout dirLayout;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private VsumFacade vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private CM codeModelFacade;

    private int snapshotCount = 0;
    private int parsedCodeModelSnapshotCount = 0;
    private int pcmSnapshotCount = 0;
    private Path currentParsedModelPath = null;
    
    private Propagation lastSuccessfulPropagation = null;

    // was this state previously used to propagate something?
    private boolean isFresh = false;

    public CommitIntegrationState() {
        dirLayout = new CommitIntegrationDirLayout();
        vsumFacade = new VsumFacadeImpl();
        pcmFacade = new PcmFacade();
        imFacade = new ImFacade();

        // the codeModel is initialized in initialize()
    }

    public void initialize(CommitIntegration<CM> commitIntegration)
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        initialize(commitIntegration, commitIntegration.getRootPath(), false);
    }

    public void initialize(CommitIntegration<CM> commitIntegration, Path rootPath, boolean overwrite)
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        initialize(commitIntegration, rootPath, overwrite, true);
    }

    public void initialize(CommitIntegration<CM> commitIntegration, Path rootPath, boolean overwrite, boolean loadVsum)
            throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        LOGGER.debug("Initializing the CommitIntegrationState");
        this.commitIntegration = commitIntegration;
        dirLayout.initialize(rootPath);

        // delete the directory layout if we are overwriting
        if (overwrite) {
            LOGGER.info(String.format("Deleting CommitIntegrationState at %s", commitIntegration.getRootPath()));
            dirLayout.delete();
            dirLayout.initialize(commitIntegration.getRootPath());
            isFresh = true;
        }

        // the settings container needs to be initialized before everything else
        // CommitIntegrationSettingsContainer.initialize(dirLayout.getSettingsFilePath());
        gitRepositoryWrapper = commitIntegration.getGitRepositoryWrapper();

        // initialize models
        pcmFacade.initialize(dirLayout.getPcmDirPath());
        imFacade.initialize(dirLayout.getImDirPath());

        // initialize the code model
        codeModelFacade = commitIntegration.getCodeModelFacadeSupplier()
            .get();
        codeModelFacade.initialize(dirLayout.getCodeDirPath());

        // load evaluation data from disk into the current singleton
        loadEvaluationData();

        // initialize the vsum
        if (loadVsum) {
            vsumFacade.initialize(dirLayout.getVsumDirPath(), List.of(pcmFacade, imFacade),
                    commitIntegration.getChangeSpecs(), commitIntegration.getStateBasedChangeResolutionStrategy());
        }
    }

    @SuppressWarnings("restriction")
    public void dispose() {
        LOGGER.debug("Disposing of the CommitIntegrationState");
        vsumFacade.getVsum()
            .dispose();
        gitRepositoryWrapper.closeRepository();
    }

    public Path createParsedCodeModelSnapshot() {
        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
        var name = String.valueOf(++parsedCodeModelSnapshotCount) + "-" + currentCommitHash;
        try {
            return getCodeModelFacade().createNamedCopyOfParsedModel(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path createRepositorySnapshot() {
        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
        var name = String.valueOf(++pcmSnapshotCount) + "-" + currentCommitHash;
        try {
            return pcmFacade.createNamedCopyOfRepositoryModel(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public Path createVsumCodeModelSnapshot() {
//        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
//        vsumCodeModelCount += 1;
//        var name = "vsum-" + String.valueOf(vsumCodeModelCount) + "-" + currentCommitHash + ".code.xmi";
//        var path = dirLayout.getVsumCodeModelPath();
//        var targetPath = path.resolveSibling(name);
//        try {
//            FileUtils.copyFile(path.toFile(), targetPath.toFile());
//            return targetPath;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public Path createInstrumentationModelSnapshot() {
//        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
//        immCount += 1;
//        var name = "imm-" + String.valueOf(vsumCodeModelCount) + "-" + currentCommitHash + ".imm";
//        var path = getImFacade().getDirLayout()
//            .getImFilePath();
//        var targetPath = path.resolveSibling(name);
//        try {
//            FileUtils.copyFile(path.toFile(), targetPath.toFile());
//            return targetPath;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public Path createRepositoryModelSnapshot() {
//        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
//        repositoryModelCount += 1;
//        var name = "Repository-" + String.valueOf(vsumCodeModelCount) + "-" + currentCommitHash + ".repository";
//        var path = pcmFacade.getDirLayout()
//            .getPcmRepositoryPath();
//        var targetPath = path.resolveSibling(name);
//        try {
//            FileUtils.copyFile(path.toFile(), targetPath.toFile());
//            return targetPath;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private void loadEvaluationData() {
        var loadedContainer = EvaluationDataContainerReaderWriter.read(dirLayout.getEvaluationDataFilePath());
        if (loadedContainer != null) {
            EvaluationDataContainer.set(loadedContainer);
        }
    }

    public void persistEvaluationData() {
        var data = EvaluationDataContainer.get();
        EvaluationDataContainerReaderWriter.write(data, dirLayout.getEvaluationDataFilePath());
    }

    public Path createSnapshot() {
        // save all the evaluation data that may have been produced
        persistEvaluationData();

        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash().substring(0, 7);
        var name = getDirLayout().getRootDirPath()
            .getFileName() + "-" + String.valueOf(++snapshotCount) + "-" + currentCommitHash;
        try {
            return createCopy(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a copy of this commit integration state in the same directory as the original with
     * the given fileName
     * 
     * @param fileName
     *            the
     * @throws IOException
     */
    private Path createCopy(String fileName) throws IOException {
        LOGGER.debug("Creating copy of CommitIntegrationState: " + fileName);

        Path copyPath = commitIntegration.getRootPath()
            .resolveSibling(fileName);

        FileUtils.copyDirectory(commitIntegration.getRootPath()
            .toFile(), copyPath.toFile());
        return copyPath;
    }

    public GitRepositoryWrapper getGitRepositoryWrapper() {
        return gitRepositoryWrapper;
    }

    public VsumFacade getVsumFacade() {
        return vsumFacade;
    }

    public PcmFacade getPcmFacade() {
        return pcmFacade;
    }

    public ImFacade getImFacade() {
        return imFacade;
    }

    public CM getCodeModelFacade() {
        return codeModelFacade;
    }

    public CommitIntegrationDirLayout getDirLayout() {
        return dirLayout;
    }

    public CommitIntegration<CM> getCommitIntegration() {
        return commitIntegration;
    }

    public boolean isFresh() {
        return isFresh;
    }

    public void setNotFresh() {
        isFresh = false;
    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }

    public Path getCurrentParsedModelPath() {
        return currentParsedModelPath;
    }

    public void setCurrentParsedModelPath(Path currentParsedModelPath) {
        this.currentParsedModelPath = currentParsedModelPath;
    }

    public int getSnapshotCount() {
        return snapshotCount;
    }

    public Propagation getLastSuccessfulPropagation() {
        return lastSuccessfulPropagation;
    }

    public void setLastSuccessfulPropagation(Propagation lastSuccessfulPropagation) {
        this.lastSuccessfulPropagation = lastSuccessfulPropagation;
    }
}
