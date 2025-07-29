package cipm.consistency.fitests.repositorytests.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * A class that stores expected similarity checking results of the commits with
 * the given IDs.
 * 
 * Assumptions on similarity checking:
 * <ul>
 * <li>Reflexivity: Same commits are similar with respect to similarity checking
 * <li>Symmetry: Similarity is symmetric (i.e. swapping commitID1 and commitID2
 * does not change the similarity result).
 * <ul>
 * <li>The expected similarity results stored here are also symmetric, i.e. the
 * result for (commitID1, commitID2) is always the same as the result for
 * (commitID2, commitID1). Therefore, there is only one result stored for
 * (commitID1, commitID2) and (commitID2, commitID1).
 * <li>This also implies that it does not matter on what side the 2 commits are.
 * </ul>
 * <li>Transitivity: Assuming C1, C2 and C3 are different commits; if C1 and C2
 * are similar, C2 and C3 are similar; then C1 and C3 should also be similar.
 * <ul>
 * <li>Assuming C1, C2, ..., CN is commit chain; where for each sequential
 * commit pair {@code e_i = (C_i, C_i+1) = (C_i+1, C_i)} there are entries in
 * this instance; if any entry {@code e_i} has the expected result false (i.e.
 * non-similarity), the result computed via transitivity is considered invalid,
 * as there is no easy way to determine whether non-similar commits were
 * reverted at some point, so that similarity between C1 and CN is re-achieved.
 * <li><b>It is further assumed that if an entry chain between 2 commits
 * consists only of entries indicating similarity (i.e. expectedResult = true),
 * all such entry chains will. <i>If this is not fulfilled, expected result
 * computation via transitivity may produce different results and not work as
 * intended. It is the caller's responsibility to ensure this.</i></b>
 * </ul>
 * </ul>
 * 
 * It is possible to determine the expected similarity result for commits C_X
 * and C_Y by using the assumptions above, without explicitly added expected
 * similarity results, by utilising transitivity for instance. In such cases,
 * there may not be an explicitly added expected similarity result for C_X and
 * C_Y in this cache.
 * 
 * @author Alp Torac Genc
 */
public class RepoTestResultCache {
	private final Collection<SimilarityResultEntry> similarityResults = new ArrayList<SimilarityResultEntry>();

	/**
	 * Constructs an instance with no similarity results.
	 */
	public RepoTestResultCache() {
	}

	/**
	 * Constructs an instance and copies the contents of the given cache into this
	 * cache.
	 */
	public RepoTestResultCache(RepoTestResultCache cache) {
		this.copyResultsOf(cache, true);
	}

	/**
	 * Copies all expected similarity results into this cache.
	 * 
	 * @param overrideResultIfPresent Whether the copied expected similarity results
	 *                                should override any potentially existing ones
	 */
	public void copyResultsOf(RepoTestResultCache cache, boolean overrideResultIfPresent) {
		this.similarityResults.addAll(cache.similarityResults);
	}

	/**
	 * Adds the expected similarity result denoted by the parameters:
	 * {@code isSimilar(commitID1, commitID2) = expectedResult}
	 * 
	 * @param overrideResultIfPresent Whether the potentially existing result should
	 *                                be overridden. Note that the existing result
	 *                                could be stored in an entry, where the given
	 *                                commit IDs are swapped (due to symmetry
	 *                                assumption).
	 */
	public void addResult(String commitID1, String commitID2, boolean expectedResult, boolean overrideResultIfPresent) {
		if (commitID1.equals(commitID2))
			return;

		// Check of there is a duplicated entry, override its content if desired
		var duplEntry = this.getEntryFor(commitID1, commitID2);
		if (overrideResultIfPresent && duplEntry != null) {
			if (duplEntry.expectedResultEquals(expectedResult)) {
				return;
			}
			this.similarityResults.remove(duplEntry);
		}

		if (overrideResultIfPresent || duplEntry == null) {
			this.similarityResults.add(new SimilarityResultEntry(commitID1, commitID2, expectedResult));
		}
	}

	/**
	 * Adds the expected similarity result denoted by the parameters, overrides any
	 * potentially existing result.
	 * 
	 * @see #addResult(String, String, boolean, boolean)
	 */
	public void addResult(String commitID1, String commitID2, boolean expectedResult) {
		this.addResult(commitID1, commitID2, expectedResult, true);
	}

	/**
	 * Uses the reflexivity, symmetry and transitivity assumptions in
	 * {@link RepoTestResultCache}. Transitivity property will be utilised last,
	 * i.e. entry chains will only be considered, if there are no direct entries in
	 * this instance or the given commit IDs are equal. <br>
	 * <br>
	 * This method can also be used to check, whether the results stored in this
	 * instance can be used to determine the expected similarity of the given
	 * commits.
	 * 
	 * @return The expected similarity result of the commits with the given IDs
	 *         (Boolean.TRUE or Boolean.FALSE). Returns null, if neither there is a
	 *         result for the given commit IDs nor is it computable via
	 *         transitivity.
	 */
	public Boolean getResult(String commitID1, String commitID2) {
		// Reflexivity and symmetry
		var result = this.getDirectResult(commitID1, commitID2);
		if (result != null) {
			return result;
		}

		// Transitivity
		if (this.getTransitiveResult(commitID1, commitID2) == Boolean.TRUE) {
			return Boolean.TRUE;
		}

		return null;
	}

	/**
	 * Accounts for reflexivity and symmetry properties, does not account for
	 * transitivity. <br>
	 * <br>
	 * This method can also be used to check, whether the results directly stored in
	 * this instance can be used to determine the expected similarity of the given
	 * commits.
	 * 
	 * @return The expected similarity result of the given commits (Boolean.TRUE or
	 *         Boolean.FALSE), if it is directly present in this instance, i.e. if a
	 *         result for (commitID1, commitID2) or (commitID2, commitID1) has been
	 *         added. Returns null, if there is no added result for the given commit
	 *         IDs.
	 */
	public Boolean getDirectResult(String commitID1, String commitID2) {
		// Reflexivity
		if (commitID1.equals(commitID2))
			return true;

		// Symmetry
		var entry = this.getEntryFor(commitID1, commitID2);
		if (entry != null) {
			return entry.getExpectedResult();
		}

		return null;
	}

	/**
	 * Attempts to compute the expected similarity result using the transitivity
	 * property. Accounts for there being a direct entry between the given commits.
	 * Account for reflexivity property. <br>
	 * <br>
	 * In particular, looks for an entry chain starting with commitID1 and ending
	 * with commitID2, such that each entry indicates similarity. <b><i>Returns true
	 * upon finding any such entry chain, even if there are further chains, which
	 * include entries indicating non-similarity.</i></b> <br>
	 * <br>
	 * Returns Boolean instead of boolean, because it is only possible to determine
	 * similarity by using transitivity. In case of non-similarity, this method
	 * returns NULL instead of FALSE, to signal that transitivity yields no accurate
	 * result.
	 * 
	 * @return TRUE, if there exists a chain of entries between the given commits,
	 *         such that all entries indicate similarity, or the given commits are
	 *         equal. Said entry chain may also only consist of a single entry for
	 *         the given commits. NULL, if there were no entry chains from commitID1
	 *         to commitID2, where all entries indicate similarity.
	 */
	public Boolean getTransitiveResult(String commitID1, String commitID2) {
		if (commitID1.equals(commitID2)) {
			return Boolean.TRUE;
		}

		var entryChain = new Stack<SimilarityResultEntry>();
		var visitedElements = new HashSet<SimilarityResultEntry>();
		this.findTransitiveEntryChain(commitID1, commitID2, entryChain, visitedElements);

		if (entryChain.isEmpty() || !entryChain.peek().hasCommitID(commitID2)) {
			// No entry chain found => Result cannot be determined via transitivity
			return null;
		} else {
			return Boolean.TRUE;
		}
	}

	/**
	 * Attempts find an entry chain between commitID1 and commitID2, in order to
	 * make use of the transitivity property. Uses depth-first search starting from
	 * commitID1 to do so. Only includes entries to the chain, which indicate
	 * similarity.
	 * 
	 * @param entryChain      The stack, which will contain the entry chain, if
	 *                        present. THIS ATTRIBUTE WILL BE MODIFIED
	 * @param visitedElements The set, which will contain all visited entries, while
	 *                        computing entryChain. THIS ATTRIBUTE WILL BE MODIFIED
	 */
	private void findTransitiveEntryChain(String commitID1, String commitID2,
			Stack<SimilarityResultEntry> currentEntryChain, Set<SimilarityResultEntry> visitedElements) {

		// Check if there are any direct entries for (ID1, ID2) or (ID2, ID1)
		var entry = this.getEntryFor(commitID1, commitID2);
		if (entry != null && !visitedElements.contains(entry)) {
			if (entry.getExpectedResult()) {
				currentEntryChain.add(entry);
				visitedElements.add(entry);
			}
			// There is one such entry, which indicates non-similarity
			// Transitivity cannot be used
			return;
		}
		// There are no direct entries, try to find an entry chain

		for (var e : this.similarityResults) {

			// If e does not indicate similarity, it cannot be on the entry chain
			if (!e.getExpectedResult()) {
				continue;
			}

			/*
			 * Since this resembles depth-first search, the resulting chain will always be
			 * cycle-free and lead from commitID1 to commitID2, if there is a transitive
			 * entry chain between them. Therefore, cycles indicate that e is the wrong
			 * entry in the chain.
			 */
			if (visitedElements.contains(e)) {
				continue;
			}

			// Check whether e and the top-most entry in currentEntryChain can be linked
			// in order to continue building the entry chain. If currentEntryChain is empty,
			// the entry chain has been reset, check if e contains commitID1 and
			// re-try to reach commitID2 by trying to build another entry chain.

			var nextCommitID = !currentEntryChain.isEmpty() ? currentEntryChain.peek().getLinkingCommit(e) : null;
			if (nextCommitID == null && e.hasCommitID(commitID1)) {
				nextCommitID = commitID1;
			}

			if (nextCommitID != null) {
				currentEntryChain.add(e);
				visitedElements.add(e);
				this.findTransitiveEntryChain(e.getOtherCommitID(nextCommitID), commitID2, currentEntryChain,
						visitedElements);
			}
		}

		// The end of the current recursion is reached, check if commitID2 has been
		// reached. If not, pop currentEntryChain and look for another path to
		// commitID2.
		if (!currentEntryChain.isEmpty()) {
			if (currentEntryChain.peek().hasCommitID(commitID2)) {
				// An entry chain between commitID1 and commitID2 has been found
				return;
			} else {
				/*
				 * The last entry does not lead to commitID2, so it cannot be a part of the
				 * transitive entry chain, per the definition of depth-first search.
				 * 
				 * Remove it the last entry and try anew.
				 */
				currentEntryChain.pop();
			}
		}
	}

	/**
	 * Removes the expected similarity result for the given commit IDs.
	 */
	public void removeResult(String commitID1, String commitID2) {
		var entry = this.getEntryFor(commitID1, commitID2);
		if (entry != null) {
			this.similarityResults.remove(entry);
		}
	}

	/**
	 * Replaces the expected similarity result for the given commit IDs with the
	 * given expectedResult.
	 */
	public void replaceResult(String commitID1, String commitID2, boolean expectedResult) {
		this.addResult(commitID1, commitID2, expectedResult);
	}

	/**
	 * See {@link RepoTestResultCache} for assumptions on expected similarity
	 * results.
	 * 
	 * @return Whether the expected similarity result for the given commits can be
	 *         determined by the direct contents of this cache. Therefore, this
	 *         method will return false, even if the desired result can be computed
	 *         using the transitivity or reflexivity assumptions.
	 */
	public boolean isInCache(String commitID1, String commitID2) {
		return this.getEntryFor(commitID1, commitID2) != null;
	}

	/**
	 * Removes all expected similarity results saved in this instance.
	 */
	public void clear() {
		this.similarityResults.clear();
	}

	/**
	 * @return The entry for the commits with the given commit IDs. Accounts for
	 *         symmetry property.
	 */
	protected SimilarityResultEntry getEntryFor(String commitID1, String commitID2) {
		var entryOpt = this.similarityResults.stream().filter((e) -> e.isEntryFor(commitID1, commitID2)).findFirst();
		if (entryOpt.isPresent()) {
			return entryOpt.get();
		}
		entryOpt = this.similarityResults.stream().filter((e) -> e.isEntryFor(commitID2, commitID1)).findFirst();
		if (entryOpt.isPresent()) {
			return entryOpt.get();
		}
		return null;
	}

	/**
	 * A class that encapsulates an expected similarity result added to the cache.
	 * Commits are not distinguished based on which side they are
	 * {@code isSimilar(commit1, commit2) = isSimilar(commit2, commit1)}.
	 * 
	 * @author Alp Torac Genc
	 */
	private class SimilarityResultEntry {
		private final String commitID1;
		private final String commitID2;
		private final boolean expectedResult;

		private SimilarityResultEntry(String commitID1, String commitID2, boolean expectedResult) {
			this.commitID1 = commitID1;
			this.commitID2 = commitID2;
			this.expectedResult = expectedResult;
		}

		public String getCommitID1() {
			return commitID1;
		}

		public String getCommitID2() {
			return commitID2;
		}

		public boolean getExpectedResult() {
			return expectedResult;
		}

		/**
		 * @return Whether this instance has the given commit
		 */
		public boolean hasCommitID(String commitID) {
			return this.getCommitID1().equals(commitID) || this.getCommitID2().equals(commitID);
		}

		/**
		 * @return The other commit ID inside this entry, i.e. the commit ID that is not
		 *         equal to {@code commitID}. Returns null, if {@code commitID} is not
		 *         in this entry.
		 */
		public String getOtherCommitID(String commitID) {
			if (!this.hasCommitID(commitID))
				return null;

			return this.getCommitID1().equals(commitID) ? this.getCommitID2() : this.getCommitID1();
		}

		/**
		 * @return The commit, which potentially links this entry with the given one.
		 *         One such commit is mutual in both entries. If there is no such
		 *         commit, returns null.
		 */
		public String getLinkingCommit(SimilarityResultEntry entry) {
			if (this.hasCommitID(entry.getCommitID1())) {
				return entry.getCommitID1();
			} else if (this.hasCommitID(entry.getCommitID2())) {
				return entry.getCommitID2();
			} else {
				return null;
			}
		}

		/**
		 * @return Whether this entry contains both given commits, and therefore
		 *         encapsulates an expected similarity result for them.
		 */
		public boolean isEntryFor(String commitID1, String commitID2) {
			return this.getCommitID1().equals(commitID1) && this.getCommitID2().equals(commitID2);
		}

		/**
		 * @return Whether the expected similarity result stored in this entry is the
		 *         same as the given one.
		 */
		public boolean expectedResultEquals(boolean expectedResult) {
			return expectedResult == this.expectedResult;
		}
	}
}
