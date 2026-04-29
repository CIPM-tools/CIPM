package cipm.consistency.commitintegration.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.NullOutputStream;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

/**
 * Wraps and represents a Git repository.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class GitRepositoryWrapper {
    private Git git;
    private Repository repository;
    private RevCommit currentCheckoutCommit;

//    private String defaultBranch;
    private String sourceFileExt;
    private boolean detectRenames;
    private File repoDir;

    public GitRepositoryWrapper(String sourceFileExt, boolean detectRenames) {
        this.sourceFileExt = sourceFileExt;
        this.detectRenames = detectRenames;
    }

    public GitRepositoryWrapper withLocalDirectory(Path repoPath) throws IOException, NoHeadException, GitAPIException {
        repoDir = repoPath.toFile();
        git = Git.open(repoDir);
        repository = git.getRepository();
        return this;
    }

    public GitRepositoryWrapper withRemoteRepositoryCopy(Path targetPath, String uriToRemoteRepository)
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        repoDir = targetPath.toFile();
        git = Git.cloneRepository()
            .setURI(uriToRemoteRepository)
            .setDirectory(repoDir)
            .setCloneAllBranches(true)
            .call();
        repository = this.git.getRepository();
        return this;
    }

    public GitRepositoryWrapper withLocalSubmodule(Path parentRepoGitDir, String submoduleName)
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var parentGit = Git.open(parentRepoGitDir.toFile());
        repository = SubmoduleWalk.getSubmoduleRepository(parentGit.getRepository(), submoduleName);
        git = new Git(repository);
        return this;
    }

    public GitRepositoryWrapper initialize() throws NoHeadException, GitAPIException {
//        defaultBranch = repository.getBranch();
        git.log()
            .setMaxCount(1)
            .call()
            .forEach(c -> currentCheckoutCommit = c);
        return this;
    }

    /**
     * Closes the Git repository.
     */
    public void closeRepository() {
        if (git != null) {
            git.close();
        }
        if (repository != null) {
            repository.close();
        }
    }

    public boolean isInitialized() {
        return git != null;
    }

    /**
     * 
     * @return The path where this repository is checked out
     */
    public File getWorkTree() {
        if (repository != null) {
            return repository.getWorkTree();
        }
        return null;
    }

    public Repository getRepository() {
        return repository;
    }

    private void tryFetch() {
        try {
            git.fetch()
                .call();
        } catch (GitAPIException e) {
            // we ignore errors here
        }
    }

    /**
     * Performs the <code>git checkout</code> command.
     * 
     * @param id
     *            the commit id or branch to checkout.
     * 
     * @exception RefAlreadyExistsException
     *                thrown when trying to create a Ref with the same name as an existing one.
     * @exception RefNotFoundException
     *                thrown when a Ref cannot be resolved.
     * @exception InvalidRefNameException
     *                thrown when an invalid Ref name was encountered.
     * @exception CheckoutConflictException
     *                thrown when a command cannot succeed because of unresolved conflicts.
     * @exception GitAPIException
     *                if unable to compute a result.
     */
    public void checkout(String id) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException,
            CheckoutConflictException, GitAPIException {
        // always fetch before checkout
        tryFetch();

        git.checkout()
            .setName(id)
            .call();
        git.log()
            .setMaxCount(1)
            .call()
            .forEach(c -> currentCheckoutCommit = c);
    }

    /**
     * Returns the commit for a commit id.
     * 
     * @param commitId
     *            the commit id.
     * @return the commit.
     * @throws AmbiguousObjectException
     * @throws MissingObjectException
     * @throws IncorrectObjectTypeException
     * @throws RevisionSyntaxException
     * @throws GitAPIException
     *             if the commit id is invalid.
     * @throws IOException
     *             if the repository cannot be read.
     */
    public RevCommit getCommitForId(String commitId) throws RevisionSyntaxException, IOException {
        return repository.parseCommit(repository.resolve(commitId));
    }

    /**
     * Returns all commits in the Git repository.
     * 
     * @return all commits.
     */
    public List<RevCommit> getAllCommits() {
        List<RevCommit> listOfCommits = new ArrayList<>();
        try {
            git.log()
                .all()
                .call()
                .forEach(listOfCommits::add);
        } catch (GitAPIException | IOException e) {
        }
        Collections.reverse(listOfCommits);
        return listOfCommits;
    }

    /**
     * Returns all commits from a given branch.
     * 
     * @param branchName
     *            the given branch.
     * @return all commits from the given branch.
     */
    public List<RevCommit> getAllCommitsFromBranch(String branchName) {
        List<RevCommit> listOfCommits = new ArrayList<>();
        try {
            git.log()
                .add(repository.resolve(branchName))
                .call()
                .forEach(listOfCommits::add);
        } catch (RevisionSyntaxException | GitAPIException | IOException e) {
        }
        Collections.reverse(listOfCommits);
        return listOfCommits;
    }

    /**
     * Returns all commits between two particular commits.
     * 
     * @param startCommitHash
     *            start commit.
     * @param endCommitHash
     *            end commit.
     * @return commits between two particular commits.
     */
    public List<RevCommit> getAllCommitsBetweenTwoCommits(final String startCommitHash, final String endCommitHash) {
        if (endCommitHash == null) {
            return List.of();
        }
        List<RevCommit> listOfCommits = new ArrayList<>();
        try {
            ObjectId refTo = repository.resolve(endCommitHash);
            if (startCommitHash == null) {
                git.log()
                    .add(refTo)
                    .call()
                    .forEach(listOfCommits::add);
            } else {
                ObjectId refFrom = repository.resolve(startCommitHash);
                git.log()
                    .addRange(refFrom, refTo)
                    .call()
                    .forEach(listOfCommits::add);
            }
        } catch (IOException | GitAPIException e) {
        }
        Collections.reverse(listOfCommits);
        return listOfCommits;
    }

    /**
     * Computes all {@link DiffEntry} between <code>oldRevCommit</code> and
     * <code>newRevCommit</code> representing the changes between the two commits. For more
     * explanation of particular parts of the method
     * <a href="https://www.codeaffine.com/2016/06/16/jgit-diff/">https://www.codeaffine.com</a>
     * 
     * @param oldRevCommit
     *            start commit (usually an older commit).
     * @param newRevCommit
     *            end commit (usually a newer commit).
     * @return computed {@link List} with {@link DiffEntry}.
     * @throws IOException
     *             if an IO operation fails.
     * @throws IncorrectObjectTypeException
     *             if one of the given commits is invalid.
     */
    public List<DiffEntry> computeDiffsBetweenTwoCommits(String firstCommitId, String secondCommitId)
            throws RevisionSyntaxException, IOException, IncorrectObjectTypeException {
        RevCommit firstCommit = null;
        if (firstCommitId != null) {
            firstCommit = this.getCommitForId(firstCommitId);
        }
        var secondCommit = this.getCommitForId(secondCommitId);
        return computeDiffsBetweenTwoCommits(firstCommit, secondCommit);
    }

    private List<DiffEntry> computeDiffsBetweenTwoCommits(RevCommit oldRevCommit, RevCommit newRevCommit)
            throws IncorrectObjectTypeException, IOException {

        ObjectReader treeReader = repository.newObjectReader();

        AbstractTreeIterator oldParser;
        if (oldRevCommit != null) {
            ObjectId oldTreeId = oldRevCommit.getTree()
                .getId();
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            oldTreeParser.reset(treeReader, oldTreeId);
            oldParser = oldTreeParser;
        } else {
            oldParser = new EmptyTreeIterator();
        }

        ObjectId newTreeId = newRevCommit.getTree()
            .getId();
        CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
        newTreeParser.reset(treeReader, newTreeId);

        OutputStream outputStream = NullOutputStream.INSTANCE;
        DiffFormatter df = new DiffFormatter(outputStream) {
            @Override
            protected void writeAddedLine(RawText text, int line) {
                var cs = EvaluationDataContainer.get()
                    .getChangeStatistic();
                cs.setNumberAddedLines(cs.getNumberAddedLines() + 1);
            }

            @Override
            protected void writeRemovedLine(RawText text, int line) {
                var cs = EvaluationDataContainer.get()
                    .getChangeStatistic();
                cs.setNumberRemovedLines(cs.getNumberRemovedLines() + 1);
            }
        };
        df.setRepository(repository);

        // Set filter to detect only changes on Java files if necessary.
        if (sourceFileExt != null) {
            TreeFilter treeFilter = PathSuffixFilter.create("." + sourceFileExt);
            df.setPathFilter(treeFilter);
        }
        // Compute diffs between the commits.
        List<DiffEntry> diffs = df.scan(oldParser, newTreeParser);

        // Detect renames on changed files if necessary.
        if (detectRenames) {
            RenameDetector rd = new RenameDetector(repository);
            rd.addAll(diffs);
            diffs = rd.compute();
        }

        for (DiffEntry diff : diffs) {
            df.format(diff);
        }
        df.close();

        EvaluationDataContainer.get()
            .getChangeStatistic()
            .setNumberChangedJavaFiles(diffs.size());

        return diffs;
    }

    /**
     * Computes changes from the given {@link DiffEntry}. An {@link EditList} contain numbers of
     * lines which have to be added, removed, or replaced in the older file version in order to
     * obtain the same content as in the newer file version.
     * 
     * @param diff
     *            contains information about the changes on a file.
     * @return {@link EditList}
     * @throws MissingObjectException
     *             if the DiffEntry is missing.
     * @throws CorruptObjectException
     *             if the DiffEntry is invalid.
     * @throws IOException
     *             if an I/O exception occurs.
     */
    public EditList computeEditListFromDiffEntry(DiffEntry diff)
            throws CorruptObjectException, MissingObjectException, IOException {
        OutputStream outputStream = NullOutputStream.INSTANCE;
        DiffFormatter diffFormatter = new DiffFormatter(outputStream);
        diffFormatter.setRepository(repository);
        try {
            FileHeader fileHeader = diffFormatter.toFileHeader(diff);
            return fileHeader.toEditList();
        } finally {
            diffFormatter.close();
        }
    }

    /**
     * Returns the older version of file content in {@link String} format.
     * 
     * @param diff
     *            contains information about changes on a file.
     * @return older version of the file content.
     * @throws IOException
     *             if the diff cannot be read.
     * @throws MissingObjectException
     *             if the diff is missing.
     */
    public String getOldContentOfFileFromDiffEntry(DiffEntry diff) throws MissingObjectException, IOException {
        ObjectId oldObjectId = diff.getOldId()
            .toObjectId();
        return readObjectToString(oldObjectId);
    }

    /**
     * Returns the newer version of file content in {@link String} format.
     * 
     * @param diff
     *            contains information about changes on a file.
     * @return newer version of file content.
     * @throws IOException
     *             if the diff cannot be read.
     * @throws MissingObjectException
     *             if the diff is missing.
     */
    public String getNewContentOfFileFromDiffEntry(DiffEntry diff) throws MissingObjectException, IOException {
        ObjectId newObjectId = diff.getNewId()
            .toObjectId();
        return readObjectToString(newObjectId);
    }

    private String readObjectToString(ObjectId objId) throws MissingObjectException, IOException {
        ObjectLoader loader = repository.open(objId);
        return new String(loader.getBytes());
    }

    /**
     * Returns the older version of file content in an {@link OutputStream}.
     * 
     * @param diff
     *            contains information about changes on a file.
     * @return older version of file content.
     * @throws IOException
     *             if the diff cannot be read.
     * @throws MissingObjectException
     *             if the diff cannot be found.
     */
    public OutputStream getOldContentOfFileFromDiffEntryInOutputStream(DiffEntry diff)
            throws MissingObjectException, IOException {
        ObjectId oldObjectId = diff.getOldId()
            .toObjectId();
        return readObjectAsOutputStream(oldObjectId);
    }

    /**
     * Returns the newer version of file content in an {@link OutputStream}.
     * 
     * @param diff
     *            contains information about changes on a file.
     * @return older version of file content.
     * @throws IOException
     *             if the diff cannot be read.
     * @throws MissingObjectException
     *             if the diff cannot be found.
     */
    public OutputStream getNewContentOfFileFromDiffEntryInOutputStream(DiffEntry diff)
            throws MissingObjectException, IOException {
        ObjectId newObjectId = diff.getNewId()
            .toObjectId();
        return readObjectAsOutputStream(newObjectId);
    }

    private OutputStream readObjectAsOutputStream(ObjectId objId) throws MissingObjectException, IOException {
        OutputStream oldContent = new ByteArrayOutputStream();
        repository.open(objId)
            .copyTo(oldContent);
        return oldContent;
    }

    /**
     * Returns the {@link FileHeader} from the given <code>diff</code>.
     * 
     * @param diff
     *            the diff to obtain the {@link FileHeader} from.
     * @return the {@link FileHeader}.
     * @throws IOException
     *             if the diff cannot be read.
     */
    public FileHeader getFileHeaderFromDiffEntry(DiffEntry diff) throws IOException {
        OutputStream outputStream = NullOutputStream.INSTANCE;
        DiffFormatter diffFormatter = new DiffFormatter(outputStream);
        diffFormatter.setRepository(repository);

        try {
            return diffFormatter.toFileHeader(diff);
        } finally {
            diffFormatter.close();
        }
    }

    /**
     * Performs a <code>git fetch</code> in order to receive all new commits between the latest
     * local commit and the latest commit in the remote repository.
     * 
     * @return the list of all new commits.
     */
//    public List<RevCommit> fetchAndGetNewCommits() {
//        List<RevCommit> result = new ArrayList<>();
//        try {
//            git.fetch()
//                .call();
//            ObjectId curCommit = currentCheckoutCommit.getId();
//            ObjectId lastCommit = repository.resolve("origin/" + defaultBranch);
//            git.log()
//                .addRange(curCommit, lastCommit)
//                .call()
//                .forEach(result::add);
//        } catch (GitAPIException | IOException e) {
//        }
//        Collections.reverse(result);
//        return result;
//    }

    /**
     * Performs a complete cleaning of the git repository, i. e., all untracked and ignored files
     * are removed, and all changes are reset to the last commit.
     * 
     * @throws GitAPIException
     *             if a Git operation cannot be performed.
     * @throws IOException
     *             if an IO operation cannot be performed.
     */
    public void performCompleteClean() throws GitAPIException, IOException {
        git.reset()
            .setMode(ResetType.HARD)
            .call();

        if (repoDir == null) {
            return;
        }

        var files = this.repoDir.listFiles();
        if (files != null) {
            for (File innerFile : files) {
                if (innerFile.getName()
                    .equals(".git")) {
                    continue;
                }
                if (innerFile.isDirectory()) {
                    FileUtils.deleteDirectory(innerFile);
                } else if (innerFile.isFile()) {
                    innerFile.delete();
                }
            }
        }
    }

    public String getCurrentCommitHash() {
        return currentCheckoutCommit.getId()
            .getName();
    }
}
