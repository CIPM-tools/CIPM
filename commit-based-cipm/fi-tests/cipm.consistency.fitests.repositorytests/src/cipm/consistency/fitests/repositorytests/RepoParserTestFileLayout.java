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
	 * @see {@link #setRepoCloneRootDirName(String)}
	 */
	private String repoCloneRootDirName;

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
	 * Sets the name of the repository that will be locally cloned and used in tests
	 */
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	/**
	 * Sets the name of the root directory of local repository clones that will be
	 * used in tests
	 */
	public void setRepoCloneRootDirName(String repoCloneRootDirName) {
		this.repoCloneRootDirName = repoCloneRootDirName;
	}

	/**
	 * Sets the name of the folder, where cached expected similarity results should
	 * be saved. Cached expected similarity results may be saved in sub-directories.
	 */
	public void setExpectedSimilarityResultCacheDirName(String expectedSimilarityResultCacheDirName) {
		this.expectedSimilarityResultCacheDirName = expectedSimilarityResultCacheDirName;
	}

	/**
	 * Sets the name of the file (with extension), where cached expected similarity
	 * results should be saved.
	 */
	public void setExpectedSimilarityResultCacheFileName(String expectedSimilarityResultCacheFileName) {
		this.expectedSimilarityResultCacheFileName = expectedSimilarityResultCacheFileName;
	}

	/**
	 * @return The path, where cached expected similarity results should be saved
	 */
	public Path getExpectedSimilarityResultCachePath() {
		return this.getTestFilesSavePath().resolve(expectedSimilarityResultCacheDirName).resolve(this.repoName)
				.resolve(expectedSimilarityResultCacheFileName);
	}

	/**
	 * @return The URI, at which the parsed commit's model resource will point at.
	 */
	public URI getModelResourceSaveURIForCommit(String commitID) {
		return URI.createFileURI(this.getModelResourceSaveRootDirectory().toString()).appendSegment(this.repoName)
				.appendSegment(commitID).appendFileExtension(this.getModelResourceFileExtension());
	}

	/**
	 * @return The path, where the given commit should be cloned
	 */
	public Path getRepoClonePathForCommit(String commitID) {
		return this.getModelSourceParentRootDirPath().resolve(commitID);
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
	public Path getRepoCloneRootDirPath() {
		return this.getTestFilesSavePath().resolve(repoCloneRootDirName);
	}

	/**
	 * @implSpec Returns The path, at which the local repository clone of
	 *           {@link #setRepoName(String)} resides. Meant to be used for
	 *           accessing the local repository clone. Use
	 *           {@link #getRepoCloneRootDirPath()} while cloning instead, so that
	 *           the top-most folder of the repository is not duplicated.
	 */
	@Override
	public Path getModelSourceParentRootDirPath() {
		return this.getRepoCloneRootDirPath().resolve(this.repoName);
	}
}
