package cipm.consistency.fitests.similarity.base.dummy.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityChecker;
import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolbox;
import cipm.consistency.fitests.similarity.base.dummy.DummySingleSimilarityRequest;
import cipm.consistency.fitests.similarity.base.dummy.DummySingleSimilarityRequestHandler;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequestHandler;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequestHandler;
import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolbox.HandleHistoryEntry;

/**
 * Contains tests for similarity checking methods from
 * {@link DummySimilarityChecker}.
 * 
 * @author Alp Torac Genc
 */
public class SimilarityCheckerTest extends AbstractDummySimilarityCheckingTest {
	/**
	 * Contains exemplary setup of the {@link DummySimilarityChecker} using the
	 * other dummy elements. This is followed by a similarity checking attempt for 2
	 * singular elements and the relevant assertions.
	 */
	@Test
	public void testIsSimilar() {
		var tb = new DummySimilarityToolbox();
		var dssh = new DummySingleSimilarityRequestHandler(tb);
		var ecrh = new EqualsCheckRequestHandler();
		var rcrh = new ReferenceCheckRequestHandler();

		tb.addRequestHandlerPair(DummySingleSimilarityRequest.class, dssh);
		tb.addRequestHandlerPair(EqualsCheckRequest.class, ecrh);
		tb.addRequestHandlerPair(ReferenceCheckRequest.class, rcrh);

		var sc = new DummySimilarityChecker(tb);

		// Check if primitive integers 1 and 2 are similar
		Assertions.assertFalse(sc.isSimilar(1, 2));

		var history = tb.getHandlingHistory();

		/*
		 * 3 history entries are expected, since DummySingleSimilarityRequest results in
		 * a EqualsCheckRequest and a ReferenceCheckRequest. All in all, one history
		 * entry from each handler above is expected.
		 */
		Assertions.assertEquals(3, history.size());

		var dsHistoryArr = history.stream().filter((e) -> e.getHandler().equals(dssh))
				.toArray(HandleHistoryEntry[]::new);
		Assertions.assertEquals(1, dsHistoryArr.length);
		var dsHistory = dsHistoryArr[0];

		var rcHistoryArr = history.stream().filter((e) -> e.getHandler().equals(rcrh))
				.toArray(HandleHistoryEntry[]::new);
		Assertions.assertEquals(1, rcHistoryArr.length);
		var rcHistory = rcHistoryArr[0];

		var ecHistoryArr = history.stream().filter((e) -> e.getHandler().equals(ecrh))
				.toArray(HandleHistoryEntry[]::new);
		Assertions.assertEquals(1, ecHistoryArr.length);
		var ecHistory = ecHistoryArr[0];

		Assertions.assertEquals(Boolean.FALSE, dsHistory.getOutput());
		Assertions.assertEquals(Boolean.FALSE, rcHistory.getOutput());
		Assertions.assertEquals(Boolean.FALSE, ecHistory.getOutput());
	}

	/**
	 * Contains exemplary setup of the {@link DummySimilarityChecker} using the
	 * other dummy elements. This is followed by a similarity checking attempt for 2
	 * lists of elements and the relevant assertions.
	 */
	@Test
	public void testAreSimilar() {
		var tb = new DummySimilarityToolbox();
		var dssh = new DummySingleSimilarityRequestHandler(tb);
		var ecrh = new EqualsCheckRequestHandler();
		var rcrh = new ReferenceCheckRequestHandler();

		tb.addRequestHandlerPair(DummySingleSimilarityRequest.class, dssh);
		tb.addRequestHandlerPair(EqualsCheckRequest.class, ecrh);
		tb.addRequestHandlerPair(ReferenceCheckRequest.class, rcrh);

		var sc = new DummySimilarityChecker(tb);
		var elem11 = "ab";
		var elem12 = "cd";
		/*
		 * Fancier version of elem21 = "ab" (same value as elem11), in order to make
		 * sure that compiler does not optimise and internally set elem11 = elem21,
		 * which would make their references equal
		 */
		var elem21 = String.copyValueOf(elem11.toCharArray());
		var elem22 = "ef";

		// Make sure that the first elements (elem11, elem21) are equal in value but not
		// in reference
		Assertions.assertTrue(elem11 != elem21);
		Assertions.assertTrue(elem11.equals(elem21));

		// Ensure that the second elements (elem12, elem22) are not equal in any way
		Assertions.assertTrue(!elem12.equals(elem22));

		// Check if lists of elements are pair-wise equal
		Assertions.assertFalse(sc.areSimilar(List.of(elem11, elem12), List.of(elem21, elem22)));

		var history = tb.getHandlingHistory();

		/*
		 * 6 history entries are expected, since areSimilar generates 2
		 * DummySingleSimilarityRequest instances, each of which in return generate 1
		 * EqualsCheckRequest and 1 ReferenceCheckRequest.
		 */
		Assertions.assertEquals(6, history.size());

		var dsHistoryArr = history.stream().filter((e) -> e.getHandler().equals(dssh))
				.toArray(HandleHistoryEntry[]::new);
		Assertions.assertEquals(2, dsHistoryArr.length);
		var dsHistory1 = dsHistoryArr[0];
		var dsHistory2 = dsHistoryArr[1];

		var rcHistoryArr = history.stream().filter((e) -> e.getHandler().equals(rcrh))
				.toArray(HandleHistoryEntry[]::new);
		Assertions.assertEquals(2, rcHistoryArr.length);
		var rcHistory1 = rcHistoryArr[0];
		var rcHistory2 = rcHistoryArr[1];

		var ecHistoryArr = history.stream().filter((e) -> e.getHandler().equals(ecrh))
				.toArray(HandleHistoryEntry[]::new);
		Assertions.assertEquals(2, ecHistoryArr.length);
		var ecHistory1 = ecHistoryArr[0];
		var ecHistory2 = ecHistoryArr[1];

		// Similarity checking of first elements should succeed because of
		// elem11.equals(elem21)
		Assertions.assertEquals(Boolean.TRUE, dsHistory1.getOutput());
		// elem11 != elem21
		Assertions.assertEquals(Boolean.FALSE, rcHistory1.getOutput());
		// elem11.equals(elem21)
		Assertions.assertEquals(Boolean.TRUE, ecHistory1.getOutput());

		// Similarity checking of second elements should fail as expected
		Assertions.assertEquals(Boolean.FALSE, dsHistory2.getOutput());
		Assertions.assertEquals(Boolean.FALSE, rcHistory2.getOutput());
		Assertions.assertEquals(Boolean.FALSE, ecHistory2.getOutput());
	}
}
