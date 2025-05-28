package cipm.consistency.vsum.test;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

public class TEAMMATESTagCITest extends TEAMMATESCITest {
	private String[] getListOfCommitsFromTags(File gitDir) throws Exception {
		class InternalCommit {
			private int time;
			private String id;
			
			InternalCommit(int time, String id) {
				this.time = time;
				this.id = id;
			}
		}
		
		var git = Git.open(gitDir);
		var commitList = new ArrayList<InternalCommit>();
		
		// Take all tags and convert them to a list containing their associated commit ids.
		var tagList = git.tagList().call();
		for (var tag : tagList) {
			// Get commit id of a tag.
			var commitId = tag.isPeeled() && tag.getPeeledObjectId() != null ? tag.getPeeledObjectId() : tag.getObjectId();
			// Extract commit time for later sorting.
			var loggedCommits = git.log().add(commitId).setMaxCount(1).call();
			for (var commit : loggedCommits) {
				commitList.add(new InternalCommit(commit.getCommitTime(), commitId.getName()));
			}
		}
		
		// Sort commits according to their commit time.
		commitList.sort((a, b) -> {
			return a.time - b.time;
		});
		
		// Extract the first commit as additional commit in the list.
		RevCommit lastCommit = null;
		for (var commit : git.log().call()) {
			lastCommit = commit;
		}
		commitList.add(0, new InternalCommit(lastCommit.getCommitTime(), lastCommit.getName()));
		
		git.getRepository().close();
		git.close();
		
		return commitList.parallelStream().map((c) -> c.id).toArray(i -> new String[i]);
	}
}
