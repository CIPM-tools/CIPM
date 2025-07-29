package cipm.consistency.fitests.repositorytests;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

import cipm.consistency.fitests.similarity.jamopp.parser.ParserTestFileLayout;

/**
 * Extension of {@link ParserTestFileLayout} with GIT-Repository-related
 * options.
 * 
 * @author Alp Torac Genc
 */
public class RepoParserTestFileLayout extends ParserTestFileLayout {
	/**
	 * @see {@link #setRepoModelImplDirName(String)}
	 */
	private String repoModelImplDirName;

	/**
	 * @see {@link #setExpectedSimilarityResultCacheDirName(String)}
	 */
	private String expectedSimilarityResultCacheDirName;

	/**
	 * @see {@link #setExpectedSimilarityResultCacheFileName(String)}
	 */
	private String expectedSimilarityResultCacheFileName;

	/**
	 * @see {@link #setRepoName(String)}
	 */
	private String repoName;

	public RepoParserTestFileLayout() {
		super();
	}

	public RepoParserTestFileLayout(ParserTestFileLayout layout) {
		super(layout);
	}

	/**
	 * Sets the name of the repository
	 */
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	/**
	 * Sets the name of the root directory of the models
	 */
	public void setRepoModelImplDirName(String repoModelImplDirName) {
		this.repoModelImplDirName = repoModelImplDirName;
	}

	/**
	 * Sets the name of the folder, where contents of {@link #resultCache} should be
	 * saved. Note: This folder does not have to directly contain the contents of
	 * {@link RepoTestResultCache}. They may be saved in sub-directories as well.
	 */
	public void setExpectedSimilarityResultCacheDirName(String expectedSimilarityResultCacheDirName) {
		this.expectedSimilarityResultCacheDirName = expectedSimilarityResultCacheDirName;
	}

	/**
	 * Sets the name of the file (with extension), where contents of
	 * {@link RepoTestResultCache} should be saved.
	 */
	public void setExpectedSimilarityResultCacheFileName(String expectedSimilarityResultCacheFileName) {
		this.expectedSimilarityResultCacheFileName = expectedSimilarityResultCacheFileName;
	}

	/**
	 * @return The path to the saved contents of {@link RepoTestResultCache}
	 */
	public Path getExpectedSimilarityResultCachePath() {
		return this.getTestFilesSavePath().resolve(expectedSimilarityResultCacheDirName).resolve(this.repoName)
				.resolve(expectedSimilarityResultCacheFileName);
	}

	/**
	 * @return The URI, at which the parsed commit's resource will point at.
	 */
	public URI getModelResourceSaveURIForCommit(String commitID) {
		return URI.createFileURI(this.getModelResourceSaveRootDirectory().toString()).appendSegment(this.repoName)
				.appendSegment(commitID).appendFileExtension(this.getModelResourceFileExtension());
	}

	/**
	 * @return The path, where the given commit should be cloned
	 */
	public Path getRepoClonePathForCommit(String commitID) {
		return this.getModelSourceFileRootDirPath().resolve(commitID);
	}

	/**
	 * @return The URI to the folder, where the given commit should be cloned
	 */
	public URI getRepoCloneURIForCommit(String commitID) {
		return URI.createFileURI(this.getRepoClonePathForCommit(commitID).toString());
	}

	/**
	 * Use this method for root directory, so that the top-most folder of the
	 * repository is not duplicated.
	 * 
	 * @return The top-most directory, where the repositories will be cloned to
	 */
	public Path getRepoClonesDirPath() {
		return this.getTestFilesSavePath().resolve(repoModelImplDirName);
	}

	/**
	 * @implSpec Returns The path, at which the repository clone resides. Meant to
	 *           be used for accessing the local repository clone. Use
	 *           {@link #getRepoClonesDirPath()} while cloning instead, so that the
	 *           top-most folder of the repository is not duplicated.
	 */
	@Override
	public Path getModelSourceFileRootDirPath() {
		return this.getRepoClonesDirPath().resolve(this.repoName);
	}
}
