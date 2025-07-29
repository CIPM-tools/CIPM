package cipm.consistency.fitests.repositorytests;

import cipm.consistency.fitests.similarity.jamopp.parser.ITimeMeasurementTag;

/**
 * An enum containing various GIT-Repository-related tags that can be used while
 * taking time measurements.
 * 
 * @author Alp Torac Genc
 */
public enum RepoTimeMeasurementTag implements ITimeMeasurementTag {
	/**
	 * A tag meant for time measurements from cloning repositories
	 */
	CLONE_REPOSITORY,
	/**
	 * A tag meant for time measurements from performing checkout operations on
	 * commits
	 */
	CHECKOUT_TO_COMMIT,
	/**
	 * A tag meant for time measurements from releasing (ex: via close() and similar
	 * methods) resources associated with in-memory representation of repositories
	 */
	CLOSE_REPOSITORY,
	/**
	 * A tag meant for time measurements from deleting local repository clones
	 */
	DELETE_LOCAL_REPO_CLONE,

	/**
	 * A tag meant for time measurements from loading expected similarity results
	 * for similarity checking commits
	 */
	LOAD_EXPECTED_SIMILARITY_RESULTS,
	/**
	 * A tag meant for time measurements from saving expected similarity results for
	 * similarity checking commits
	 */
	SAVE_EXPECTED_SIMILARITY_RESULTS,

	;
}
