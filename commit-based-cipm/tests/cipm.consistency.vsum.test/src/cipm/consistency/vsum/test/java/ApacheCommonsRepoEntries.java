package cipm.consistency.vsum.test.java;

import java.util.HashMap;
import java.util.Map;

public class ApacheCommonsRepoEntries {
	public static class RepoEntry {
		public final String repoId;
		public final String remoteRepoURI;
		public final String commitId;

		private RepoEntry(String repoId, String remoteRepoURI, String commitId) {
			this.repoId = repoId;
			this.remoteRepoURI = remoteRepoURI;
			this.commitId = commitId;
		}
	}

	private static final String APACHE_URL_PREFIX = "https://github.com/apache/";

	private static final String COMMONS_CSV_ID = "commons-csv";
	private static final String COMMONS_CSV_URL = APACHE_URL_PREFIX + COMMONS_CSV_ID;

	private static final String COMMONS_EXEC_ID = "commons-exec";
	private static final String COMMONS_EXEC_URL = APACHE_URL_PREFIX + COMMONS_EXEC_ID;

	private static final String COMMONS_CLI_ID = "commons-cli";
	private static final String COMMONS_CLI_URL = APACHE_URL_PREFIX + COMMONS_CLI_ID;

	private static final String COMMONS_STATISTICS_ID = "commons-statistics";
	private static final String COMMONS_STATISTICS_URL = APACHE_URL_PREFIX + COMMONS_STATISTICS_ID;

	private static final String COMMONS_CODEC_ID = "commons-codec";
	private static final String COMMONS_CODEC_URL = APACHE_URL_PREFIX + COMMONS_CODEC_ID;

	private static Map<String, RepoEntry> getTestCaseMap(String csvCommit, String execCommit, String cliCommit,
			String statisticsCommit, String codecCommit) {
		var map = new HashMap<String, RepoEntry>();

		if (csvCommit != null)
			map.put(COMMONS_CSV_ID, new RepoEntry(COMMONS_CSV_ID, COMMONS_CSV_URL, csvCommit));

		if (execCommit != null)
			map.put(COMMONS_EXEC_ID, new RepoEntry(COMMONS_EXEC_ID, COMMONS_EXEC_URL, execCommit));

		if (cliCommit != null)
			map.put(COMMONS_CLI_ID, new RepoEntry(COMMONS_CLI_ID, COMMONS_CLI_URL, cliCommit));

		if (statisticsCommit != null)
			map.put(COMMONS_STATISTICS_ID,
					new RepoEntry(COMMONS_STATISTICS_ID, COMMONS_STATISTICS_URL, statisticsCommit));

		if (codecCommit != null)
			map.put(COMMONS_CODEC_ID, new RepoEntry(COMMONS_CODEC_ID, COMMONS_CODEC_URL, codecCommit));
		return map;
	}

	/**
	 * Apache test case from the "case-vitruv" branch.
	 * <p>
	 * Results in about 285 source files and 0 classpath entries parsed
	 */
	public static Map<String, RepoEntry> getCaseVitruvTestCase() {
		// "rel/commons-csv-1.14.1"
		// "rel/commons-exec-1.6.0"
		// "commons-cli-1.11.0-RC1 rel/commons-cli-1.11.0"
		// "commons-statistics-1.3-RC1 rel/commons-statistics-1.3"
		return getTestCaseMap("e14ef8", "3ee697", "d74613", "2937eb", null);
	}

	public static Map<String, RepoEntry> getMinimalPropagationTestCase() {
		// "rel/commons-codec-1.19.0"
		return getTestCaseMap("e14ef8", "3ee697", "d74613", "2937eb", "351cb22");
	}
}