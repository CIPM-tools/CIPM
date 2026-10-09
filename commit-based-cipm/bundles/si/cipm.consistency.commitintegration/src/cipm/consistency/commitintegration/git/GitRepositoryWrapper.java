package cipm.consistency.commitintegration.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleStatus;
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

	// TODO: Keep track of submodule Git and Repository instances too and close them
	// as well

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
		git = Git.cloneRepository().setURI(uriToRemoteRepository).setDirectory(repoDir).setCloneAllBranches(true)
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

	private void readInitialState() throws NoHeadException, GitAPIException {
		git.log().setMaxCount(1).call().forEach(c -> currentCheckoutCommit = c);
	}

	public GitRepositoryWrapper initialize() throws NoHeadException, GitAPIException {
//        defaultBranch = repository.getBranch();
		readInitialState();
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
			git.fetch().call();
		} catch (GitAPIException e) {
			// we ignore errors here
		}
	}

	/**
	 * Performs the <code>git checkout</code> command.
	 * 
	 * @param id the commit id or branch to checkout.
	 * 
	 * @exception RefAlreadyExistsException thrown when trying to create a Ref with
	 *                                      the same name as an existing one.
	 * @exception RefNotFoundException      thrown when a Ref cannot be resolved.
	 * @exception InvalidRefNameException   thrown when an invalid Ref name was
	 *                                      encountered.
	 * @exception CheckoutConflictException thrown when a command cannot succeed
	 *                                      because of unresolved conflicts.
	 * @exception GitAPIException           if unable to compute a result.
	 */
	public void checkout(String id) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException,
			CheckoutConflictException, GitAPIException {
		// always fetch before checkout
		tryFetch();

		git.checkout().setName(id).call();
		git.log().setMaxCount(1).call().forEach(c -> currentCheckoutCommit = c);
	}

	/**
	 * Returns the commit for a commit id.
	 * 
	 * @param commitId the commit id.
	 * @return the commit.
	 * @throws AmbiguousObjectException
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws RevisionSyntaxException
	 * @throws GitAPIException              if the commit id is invalid.
	 * @throws IOException                  if the repository cannot be read.
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
			git.log().all().call().forEach(listOfCommits::add);
		} catch (GitAPIException | IOException e) {
		}
		Collections.reverse(listOfCommits);
		return listOfCommits;
	}

	/**
	 * Returns all commits from a given branch.
	 * 
	 * @param branchName the given branch.
	 * @return all commits from the given branch.
	 */
	public List<RevCommit> getAllCommitsFromBranch(String branchName) {
		List<RevCommit> listOfCommits = new ArrayList<>();
		try {
			git.log().add(repository.resolve(branchName)).call().forEach(listOfCommits::add);
		} catch (RevisionSyntaxException | GitAPIException | IOException e) {
		}
		Collections.reverse(listOfCommits);
		return listOfCommits;
	}

	/**
	 * Returns all commits between two particular commits.
	 * 
	 * @param startCommitHash start commit.
	 * @param endCommitHash   end commit.
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
				git.log().add(refTo).call().forEach(listOfCommits::add);
			} else {
				ObjectId refFrom = repository.resolve(startCommitHash);
				git.log().addRange(refFrom, refTo).call().forEach(listOfCommits::add);
			}
		} catch (IOException | GitAPIException e) {
		}
		Collections.reverse(listOfCommits);
		return listOfCommits;
	}

	/**
	 * Computes all {@link DiffEntry} between <code>oldRevCommit</code> and
	 * <code>newRevCommit</code> representing the changes between the two commits.
	 * For more explanation of particular parts of the method <a href=
	 * "https://www.codeaffine.com/2016/06/16/jgit-diff/">https://www.codeaffine.com</a>
	 * 
	 * @param oldRevCommit start commit (usually an older commit).
	 * @param newRevCommit end commit (usually a newer commit).
	 * @return computed {@link List} with {@link DiffEntry}.
	 * @throws IOException                  if an IO operation fails.
	 * @throws IncorrectObjectTypeException if one of the given commits is invalid.
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
			ObjectId oldTreeId = oldRevCommit.getTree().getId();
			CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
			oldTreeParser.reset(treeReader, oldTreeId);
			oldParser = oldTreeParser;
		} else {
			oldParser = new EmptyTreeIterator();
		}

		ObjectId newTreeId = newRevCommit.getTree().getId();
		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		newTreeParser.reset(treeReader, newTreeId);

		OutputStream outputStream = NullOutputStream.INSTANCE;
		DiffFormatter df = new DiffFormatter(outputStream) {
			@Override
			protected void writeAddedLine(RawText text, int line) {
				var cs = EvaluationDataContainer.get().getChangeStatistic();
				cs.setNumberAddedLines(cs.getNumberAddedLines() + 1);
			}

			@Override
			protected void writeRemovedLine(RawText text, int line) {
				var cs = EvaluationDataContainer.get().getChangeStatistic();
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

		EvaluationDataContainer.get().getChangeStatistic().setNumberChangedJavaFiles(diffs.size());

		return diffs;
	}

	/**
	 * Computes changes from the given {@link DiffEntry}. An {@link EditList}
	 * contain numbers of lines which have to be added, removed, or replaced in the
	 * older file version in order to obtain the same content as in the newer file
	 * version.
	 * 
	 * @param diff contains information about the changes on a file.
	 * @return {@link EditList}
	 * @throws MissingObjectException if the DiffEntry is missing.
	 * @throws CorruptObjectException if the DiffEntry is invalid.
	 * @throws IOException            if an I/O exception occurs.
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
	 * @param diff contains information about changes on a file.
	 * @return older version of the file content.
	 * @throws IOException            if the diff cannot be read.
	 * @throws MissingObjectException if the diff is missing.
	 */
	public String getOldContentOfFileFromDiffEntry(DiffEntry diff) throws MissingObjectException, IOException {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
		return readObjectToString(oldObjectId);
	}

	/**
	 * Returns the newer version of file content in {@link String} format.
	 * 
	 * @param diff contains information about changes on a file.
	 * @return newer version of file content.
	 * @throws IOException            if the diff cannot be read.
	 * @throws MissingObjectException if the diff is missing.
	 */
	public String getNewContentOfFileFromDiffEntry(DiffEntry diff) throws MissingObjectException, IOException {
		ObjectId newObjectId = diff.getNewId().toObjectId();
		return readObjectToString(newObjectId);
	}

	private String readObjectToString(ObjectId objId) throws MissingObjectException, IOException {
		ObjectLoader loader = repository.open(objId);
		return new String(loader.getBytes());
	}

	/**
	 * Returns the older version of file content in an {@link OutputStream}.
	 * 
	 * @param diff contains information about changes on a file.
	 * @return older version of file content.
	 * @throws IOException            if the diff cannot be read.
	 * @throws MissingObjectException if the diff cannot be found.
	 */
	public OutputStream getOldContentOfFileFromDiffEntryInOutputStream(DiffEntry diff)
			throws MissingObjectException, IOException {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
		return readObjectAsOutputStream(oldObjectId);
	}

	/**
	 * Returns the newer version of file content in an {@link OutputStream}.
	 * 
	 * @param diff contains information about changes on a file.
	 * @return older version of file content.
	 * @throws IOException            if the diff cannot be read.
	 * @throws MissingObjectException if the diff cannot be found.
	 */
	public OutputStream getNewContentOfFileFromDiffEntryInOutputStream(DiffEntry diff)
			throws MissingObjectException, IOException {
		ObjectId newObjectId = diff.getNewId().toObjectId();
		return readObjectAsOutputStream(newObjectId);
	}

	private OutputStream readObjectAsOutputStream(ObjectId objId) throws MissingObjectException, IOException {
		OutputStream oldContent = new ByteArrayOutputStream();
		repository.open(objId).copyTo(oldContent);
		return oldContent;
	}

	/**
	 * Returns the {@link FileHeader} from the given <code>diff</code>.
	 * 
	 * @param diff the diff to obtain the {@link FileHeader} from.
	 * @return the {@link FileHeader}.
	 * @throws IOException if the diff cannot be read.
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
	 * Performs a <code>git fetch</code> in order to receive all new commits between
	 * the latest local commit and the latest commit in the remote repository.
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
	 * Performs a complete cleaning of the git repository, i. e., all untracked and
	 * ignored files are removed, and all changes are reset to the last commit.
	 * 
	 * @throws GitAPIException if a Git operation cannot be performed.
	 * @throws IOException     if an IO operation cannot be performed.
	 */
	public void performCompleteClean() throws GitAPIException, IOException {
		git.reset().setMode(ResetType.HARD).call();

		if (repoDir == null) {
			return;
		}

		var files = this.repoDir.listFiles();
		if (files != null) {
			for (File innerFile : files) {
				if (innerFile.getName().equals(".git")) {
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
		return currentCheckoutCommit.getId().getName();
	}

	/**
	 * Physically removes a submodule from the file system, i.e. deletes its working
	 * tree directory in the root directory and its repository metadata under
	 * {@code .git/modules/<submodulePath>}. The submodule's registration
	 * ({@code .gitmodules} entry, repository config, and index gitlink) is not
	 * touched; use {@link #deregisterSubmodule(String)} for that.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @exception IllegalStateException if the wrapper is not initialized.
	 * @exception IOException           if a directory cannot be deleted.
	 */
	public void removeSubmodulePhysically(String submodulePath) throws IOException {
		if (!isInitialized()) {
			throw new IllegalStateException("The repository wrapper is not initialized.");
		}
		// Delete the submodule's working tree.
		File submoduleDirectory = new File(this.repoDir, submodulePath);
		if (submoduleDirectory.isDirectory()) {
			FileUtils.deleteDirectory(submoduleDirectory);
		}
		// Delete the submodule's repository metadata (cloned object database and refs).
		File moduleRepositoryDirectory = new File(git.getRepository().getDirectory(),
				Paths.get(Constants.MODULES, submodulePath).toString());
		if (moduleRepositoryDirectory.isDirectory()) {
			FileUtils.deleteDirectory(moduleRepositoryDirectory);
		}
	}

	/**
	 * De-registers a submodule, i.e. removes its entry from the {@code .gitmodules}
	 * file, its config section from the repository config, and its gitlink from the
	 * index of the parent repository. The submodule's working tree and stored
	 * repository are not touched; use {@link #removeSubmodulePhysically(String)}
	 * for that.
	 * 
	 * The changes are staged but not committed; call
	 * {@link #commitAllSubmoduleChanges(String)} afterwards to record the
	 * de-registration in a commit.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @exception IOException if a file or the index cannot be read or written.
	 */
	public void deregisterSubmodule(String submodulePath) throws IOException {
		if (!isInitialized()) {
			throw new IllegalStateException("The repository wrapper is not initialized.");
		}
		// 1. Determine the submodule's name from its path entry in .gitmodules.
		String submodulePathToUse = submodulePath;
		File modulesFile = new File(this.repoDir, Constants.DOT_GIT_MODULES);
		Config modulesConfig = new Config();
		if (modulesFile.isFile()) {
			try {
				modulesConfig.fromText(Files.readString(modulesFile.toPath(), StandardCharsets.UTF_8));
			} catch (ConfigInvalidException e) {
				throw new IOException("Invalid .gitmodules file in " + this.repoDir, e);
			}
		}
		String submoduleUrl = null;
		for (String name : modulesConfig.getSubsections(ConfigConstants.CONFIG_SUBMODULE_SECTION)) {
			String entryPath = modulesConfig.getString(ConfigConstants.CONFIG_SUBMODULE_SECTION, name,
					ConfigConstants.CONFIG_KEY_PATH);
			if (submodulePathToUse.equals(entryPath)) {
				submoduleUrl = modulesConfig.getString(ConfigConstants.CONFIG_SUBMODULE_SECTION, name,
						ConfigConstants.CONFIG_KEY_URL);
				modulesConfig.unsetSection(ConfigConstants.CONFIG_SUBMODULE_SECTION, name);
			}
		}
		// 2. Rewrite or remove the .gitmodules file.
		if (modulesConfig.getSubsections(ConfigConstants.CONFIG_SUBMODULE_SECTION).isEmpty()) {
			Files.deleteIfExists(modulesFile.toPath());
		} else if (modulesFile.isFile()) {
			Files.writeString(modulesFile.toPath(), modulesConfig.toText(), StandardCharsets.UTF_8);
		}
		// 3. Remove the config section initialized by submodule init/add
		// (submodule.<name>.url, .fetch, .branch), keyed by the URL in .gitmodules.
		if (submoduleUrl != null) {
			StoredConfig repositoryConfig = git.getRepository().getConfig();
			for (String name : repositoryConfig.getSubsections(ConfigConstants.CONFIG_SUBMODULE_SECTION)) {
				if (submoduleUrl.equals(repositoryConfig.getString(ConfigConstants.CONFIG_SUBMODULE_SECTION, name,
						ConfigConstants.CONFIG_KEY_URL))) {
					repositoryConfig.unsetSection(ConfigConstants.CONFIG_SUBMODULE_SECTION, name);
				}
			}
			repositoryConfig.save();
		}
		// 4. Remove the gitlink entry from the index (staged change).
		Repository repository = git.getRepository();
		DirCache dirCache = repository.lockDirCache();
		boolean committed = false;
		try {
			DirCacheBuilder builder = dirCache.builder();
			for (int i = 0; i < dirCache.getEntryCount(); i++) {
				DirCacheEntry entry = dirCache.getEntry(i);
				if (!submodulePathToUse.equals(entry.getPathString())) {
					builder.add(entry);
				}
			}
			builder.commit();
			committed = true;
		} finally {
			if (!committed) {
				dirCache.unlock();
			}
		}
	}

	/**
	 * Checks whether a submodule is registered in the {@code .gitmodules} file,
	 * i.e. whether a {@code git submodule add} recorded a submodule entry whose
	 * path matches the given path. This is independent of the index and of the
	 * submodule's initialization state.
	 * 
	 * Assumes trivial submodule paths, i.e. single-segment path names such as
	 * {@code mySubmodule}, exactly as they were given to
	 * {@link #addSubmodule(String, String)}.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @return true if the {@code .gitmodules} file contains a submodule entry for
	 *         the given path.
	 * @exception IOException if the {@code .gitmodules} file cannot be read or is
	 *                        invalid.
	 */
	public boolean isSubmoduleRegistered(String submodulePath) throws IOException {
		File modulesFile = new File(this.repoDir, Constants.DOT_GIT_MODULES);
		if (!modulesFile.isFile()) {
			return false;
		}
		Config modulesConfig = new Config();
		try {
			modulesConfig.fromText(Files.readString(modulesFile.toPath(), StandardCharsets.UTF_8));
		} catch (ConfigInvalidException e) {
			throw new IOException("Invalid .gitmodules file in " + this.repoDir, e);
		}

		for (String name : modulesConfig.getSubsections(ConfigConstants.CONFIG_SUBMODULE_SECTION)) {
			if (submodulePath.equals(modulesConfig.getString(ConfigConstants.CONFIG_SUBMODULE_SECTION, name,
					ConfigConstants.CONFIG_KEY_PATH))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a submodule is physically present, i.e. whether its working
	 * tree directory exists in the repository's root directory and contains
	 * checked-out content. A submodule that is registered but not yet initialized
	 * or cloned is not physically present.
	 * 
	 * <p>
	 * Assumes trivial submodule paths, i.e. single-segment path names such as
	 * {@code mySubmodule}.
	 * 
	 * <p>
	 * Note that this method returns a best effort result: It only checks whether
	 * the submodule directory has any non-GIT-metadata files or directories.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @return true if the submodule directory exists and contains non-GIT-metadata
	 *         content.
	 */
	public boolean isSubmodulePhysicallyPresent(String submodulePath) {
		File submoduleDirectory = new File(this.repoDir, submodulePath);
		if (!submoduleDirectory.isDirectory()) {
			return false;
		}

		// Best effort result, looks for a file / folder with a name that does not start
		// with ".". Since most GIT-related files' names start with a dot ".", this
		// should help decide whether there are non-GIT metadata files present. In that
		// case, assume that the submodule is physically present.
		return submoduleDirectory.listFiles((f) -> !f.getName().startsWith(".")).length > 0;
	}

	/**
	 * Checks whether the metadata associated with the given submodule physically
	 * exists.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @return true if the module directory of the submodule exists (
	 *         {@code mainRepo/.git/modules/submodulePath} )
	 */
	public boolean isSubmoduleMetadataPhysicallyPresent(String submodulePath) {
		var submoduleModuleFolder = this.repoDir.toPath().resolve(Constants.DOT_GIT).resolve(Constants.MODULES)
				.resolve(submodulePath).toFile();
		return submoduleModuleFolder.exists();
	}

	/**
	 * Checks whether there are any physical submodule content, which could cause
	 * issues while initialising and cloning the submodule.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @return true if the given submodule can be initialised and cloned
	 */
	public boolean canInitAndCloneSubmodule(String submodulePath) {
		return !isSubmodulePhysicallyPresent(submodulePath) && !isSubmoduleMetadataPhysicallyPresent(submodulePath);
	}

	/**
	 * Initializes a single existing submodule and clones its content, i.e. performs
	 * the {@code git submodule init <path>} and {@code git submodule update <path>}
	 * commands for the given submodule.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @exception GitAPIException if unable to compute a result.
	 * @exception IOException     if the submodule repository cannot be initialized.
	 */
	public void initAndCloneSubmodule(String submodulePath) throws GitAPIException, IOException {
		git.submoduleInit().addPath(submodulePath).call();
		git.submoduleUpdate().addPath(submodulePath).call();
		ensureSubmoduleGitFiles();
	}

	/**
	 * Adds a new submodule to the repository, i.e. performs the
	 * {@code git submodule add} command. The submodule is registered in the index
	 * and {@code .gitmodules}, and its content is cloned into the given path inside
	 * the repository.
	 * 
	 * @param uriToSubmoduleRepository URI to the remote repository of the
	 *                                 submodule.
	 * @param path                     path relative to the root directory at which
	 *                                 the submodule is stored.
	 * @return the {@link Repository} of the newly added submodule.
	 * @exception InvalidRemoteException thrown when the remote repository is
	 *                                   invalid.
	 * @exception TransportException     thrown when the transport operation failed.
	 * @exception GitAPIException        if unable to compute a result.
	 * @exception IOException            if the submodule repository cannot be read
	 *                                   or registered.
	 */
	public Repository addSubmodule(String uriToSubmoduleRepository, String path)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		Repository submoduleRepository = git.submoduleAdd().setURI(uriToSubmoduleRepository).setPath(path).call();
		ensureSubmoduleGitFiles();
		return submoduleRepository;
	}

	/**
	 * Ensures that the {@code .git} gitfiles of all submodules exist, i.e. one-line
	 * files containing {@code gitdir: <path to the repository>} inside each
	 * submodule directory. JGit does not always create these files when cloning
	 * submodules, which makes the submodule working trees unopenable as
	 * repositories (in contrast to the git CLI).
	 * 
	 * @exception IOException if a gitfile cannot be read or written.
	 */
	private void ensureSubmoduleGitFiles() throws IOException, GitAPIException {
		Map<String, SubmoduleStatus> statuses = git.submoduleStatus().call();
		for (String submodulePath : statuses.keySet()) {
			File submoduleDirectory = new File(this.repoDir, submodulePath);
			File gitFile = new File(submoduleDirectory, Constants.DOT_GIT);
			if (gitFile.exists()) {
				continue;
			}
			// JGit stores the cloned submodule repository under
			// <parent>/.git/modules/<submodulePath>.
			File moduleRepositoryDirectory = new File(git.getRepository().getDirectory(),
					Paths.get(Constants.MODULES, submodulePath).toString());
			if (new File(moduleRepositoryDirectory, Constants.HEAD).exists()) {
				Path gitDirPath = Paths.get(submoduleDirectory.getAbsolutePath())
						.relativize(Paths.get(moduleRepositoryDirectory.getAbsolutePath()));
				FileUtils.writeStringToFile(gitFile, Constants.GITDIR + gitDirPath.toString() + System.lineSeparator(),
						StandardCharsets.UTF_8);
			}
		}
	}

	/**
	 * Returns the paths of all submodules registered in the repository.
	 * 
	 * @return the list of all submodule paths relative to the root directory.
	 * @exception GitAPIException if unable to compute a result.
	 * @exception IOException     if the repository cannot be read.
	 */
	public List<String> getSubmodulePaths() throws GitAPIException, IOException {
		List<String> submodulePaths = new ArrayList<>();
		Map<String, SubmoduleStatus> statuses = git.submoduleStatus().call();
		statuses.forEach((path, status) -> submodulePaths.add(path));
		return submodulePaths;
	}

	/**
	 * Performs the git checkout command inside a single submodule, i.e. checks out
	 * a commit id or branch in the submodule repository.
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @param id            the commit id or branch to checkout in the submodule.
	 * @exception RefAlreadyExistsException thrown when trying to create a Ref with
	 *                                      the same name as an existing one.
	 * @exception RefNotFoundException      thrown when a Ref cannot be resolved.
	 * @exception InvalidRefNameException   thrown when an invalid Ref name was
	 *                                      encountered.
	 * @exception CheckoutConflictException thrown when a command cannot succeed
	 *                                      because of unresolved conflicts.
	 * @exception GitAPIException           if unable to compute a result.
	 * @exception IOException               if the submodule repository cannot be
	 *                                      read.
	 */
	public void checkoutInSubmodule(String submodulePath, String id) throws RefAlreadyExistsException,
			RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException, IOException {
		File submoduleDirectory = new File(this.repoDir, submodulePath);
		Git submodule = openSubmodule(submodulePath, submoduleDirectory);
		try {
			ensureBornHead(submodule.getRepository(), id);
			submodule.checkout().setName(id).call();
		} finally {
			submodule.close();
		}
	}

	/**
	 * Ensures that the HEAD of the given repository can be resolved to a commit. If
	 * HEAD is unborn (a symbolic reference to a non-existing branch), HEAD is
	 * detached to the commit the given id resolves to. Without this, JGit's
	 * checkout refuses with {@code Cannot check out from unborn branch}, whereas
	 * the git CLI would perform the checkout.
	 * 
	 * @param repository the repository whose HEAD is checked and repaired.
	 * @param id         the commit id or branch the subsequent checkout targets.
	 * @exception RefNotFoundException if the id cannot be resolved in the
	 *                                 repository.
	 * @exception GitAPIException      if a Git operation cannot be performed.
	 * @exception IOException          if the repository cannot be read or written.
	 */
	private void ensureBornHead(Repository repository, String id)
			throws RefNotFoundException, GitAPIException, IOException {
		var head = repository.exactRef(Constants.HEAD);
		if (head != null && head.getObjectId() != null) {
			return;
		}
		ObjectId target = repository.resolve(id);
		if (target == null) {
			throw new RefNotFoundException(id);
		}
		// Update the HEAD file itself (deref=false), turning the unborn symref
		// into a detached HEAD at the target commit.
		var headUpdate = repository.updateRef(Constants.HEAD, false);
		headUpdate.disableRefLog();
		headUpdate.setNewObjectId(target);
		headUpdate.update();
	}

	/**
	 * Opens the repository of a submodule. If the submodule directory cannot be
	 * opened directly (e.g. because JGit did not create its {@code .git} gitfile),
	 * the repository is opened from JGit's module storage under
	 * {@code <parent>/.git/modules/<submodulePath>} with the submodule directory as
	 * work tree.
	 * 
	 * @param submodulePath      path of the submodule relative to the root
	 *                           directory.
	 * @param submoduleDirectory directory of the submodule in the file system.
	 * @return the {@link Git} instance of the submodule repository.
	 * @exception IOException if the submodule repository cannot be opened.
	 */
	private Git openSubmodule(String submodulePath, File submoduleDirectory) throws IOException {
		try {
			return Git.open(submoduleDirectory);
		} catch (RepositoryNotFoundException e) {
			File moduleRepositoryDirectory = new File(git.getRepository().getDirectory(),
					Paths.get(Constants.MODULES, submodulePath).toString());
			Repository submoduleRepository = new RepositoryBuilder().setGitDir(moduleRepositoryDirectory)
					.setWorkTree(submoduleDirectory).build();
			return Git.wrap(submoduleRepository);
		}
	}

	/**
	 * Initializes a brand new empty Git repository in the root directory, i.e.
	 * performs the {@code git init} command. If the root directory does not exist
	 * yet, it is created. An existing Git repository in the root directory is left
	 * untouched (i.e. re-initializing is a no-op, just as for the git CLI command).
	 * 
	 * @exception GitAPIException if the repository cannot be created.
	 * @exception IOException     if the repository cannot be read after its
	 *                            creation.
	 */
	public void initNewRepository(File repoDir) throws GitAPIException, IOException {
		FileUtils.forceMkdir(repoDir);
		this.git = Git.init().setDirectory(repoDir).call();
		this.repository = this.git.getRepository();
		this.repoDir = repoDir;
	}

	/**
	 * Stages the gitlinks of all submodules whose current state differs from the
	 * state recorded in the index and commits the changes in the parent repository.
	 * This finalizes both adding submodules and checkout operations on submodules.
	 * If no submodule state has changed, no commit is created and the current
	 * latest commit is returned.
	 * 
	 * @param commitMessage message for the commit in the parent repository.
	 * @return the created commit, or the current latest commit if there was nothing
	 *         to commit.
	 * @exception GitAPIException if the staging or the commit fails, or the
	 *                            repository has no head yet.
	 * @exception IOException     if the repository cannot be read.
	 */
	public RevCommit commitAllSubmoduleChanges(String commitMessage) throws GitAPIException, IOException {
		Map<String, SubmoduleStatus> statuses = git.submoduleStatus().call();
		var addCommand = git.add();

		// Assume submodulePaths are equal to the name of the submodule repository
		statuses.keySet().forEach(addCommand::addFilepattern);

		// Stage .gitmodules as well, in case a submodule was added since the last
		// commit.
		addCommand.addFilepattern(Constants.DOT_GIT_MODULES).call();
		RevCommit commit = git.commit().setMessage(commitMessage).call();
		currentCheckoutCommit = commit;
		return commit;
	}

	/**
	 * Checks whether a submodule is currently checked out at the given commit, i.e.
	 * whether its HEAD resolves to that commit.
	 * 
	 * The id may be a full SHA, an abbreviated (short) SHA, a branch name, or a ref
	 * expression. If a branch name is given, the check compares against the commit
	 * the branch currently points to (not the branch name itself).
	 * 
	 * @param submodulePath path of the submodule relative to the root directory.
	 * @param id            the commit id or branch to check against.
	 * @return true if the submodule's HEAD resolves to the given commit.
	 * @exception RefNotFoundException if the given id cannot be resolved to any
	 *                                 commit.
	 * @exception IOException          if a submodule repository cannot be opened or
	 *                                 read.
	 */
	public boolean isSubmoduleCheckedOutAt(String submodulePath, String id) throws RefNotFoundException, IOException {
		try (Git submodule = openSubmodule(submodulePath, new File(this.repoDir, submodulePath))) {
			ObjectId expected = submodule.getRepository().resolve(id);
			if (expected == null) {
				throw new RefNotFoundException(id);
			}
			ObjectId headId = submodule.getRepository().resolve(Constants.HEAD);
			return expected.equals(headId);
		}
	}
}
