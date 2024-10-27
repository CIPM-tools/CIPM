package cipm.consistency.fitests.similarity.base.dummy.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolbox;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequestHandler;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequestHandler;

/**
 * Contains tests for adding/removing/clearing request-handler pairs in
 * {@link DummySimilarityToolbox}. Methods inherited from abstract classes are
 * tested along the way.
 * 
 * @author Alp Torac Genc
 */
public class SimilarityToolboxTest extends AbstractDummySimilarityCheckingTest {
	/**
	 * Ensure that step-wise request-handler pair adding via
	 * {@link DummySimilarityToolbox#addRequestHandlerPair(Class, org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler)}
	 * works as intended.
	 */
	@Test
	public void testAddRequestHandlerPair() {
		var tb = new DummySimilarityToolbox();
		Assertions.assertFalse(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertFalse(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		tb.addRequestHandlerPair(EqualsCheckRequest.class, new EqualsCheckRequestHandler());
		Assertions.assertTrue(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertFalse(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		tb.addRequestHandlerPair(ReferenceCheckRequest.class, new ReferenceCheckRequestHandler());
		Assertions.assertTrue(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertTrue(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));
	}

	/**
	 * Ensure that step-wise request-handler pair removing via
	 * {@link DummySimilarityToolbox#removeRequestHandlerPair(Class)} works as
	 * intended.
	 */
	@Test
	public void testRemoveRequestHandlerPairs() {
		var tb = this.createFullToolbox();

		Assertions.assertTrue(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertTrue(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		tb.removeRequestHandlerPair(EqualsCheckRequest.class);
		Assertions.assertFalse(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertTrue(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		tb.removeRequestHandlerPair(ReferenceCheckRequest.class);
		Assertions.assertFalse(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertFalse(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));
	}

	/**
	 * Ensure that removing all request-handler pairs via the
	 * {@link DummySimilarityToolbox#clearRequestHandlerPairs()} method works as
	 * intended.
	 */
	@Test
	public void testClearRequestHandlerPairs() {
		var tb = this.createFullToolbox();

		Assertions.assertTrue(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertTrue(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		tb.clearRequestHandlerPairs();

		Assertions.assertFalse(tb.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertFalse(tb.canHandleSimilarityRequest(ReferenceCheckRequest.class));
	}

	/**
	 * Ensure that certain request classes are handled accordingly, once their
	 * corresponding request-handler pairs are added.
	 */
	@Test
	public void testHandleSimilarityRequest() {
		var tb = new DummySimilarityToolbox();
		var ecrh = new EqualsCheckRequestHandler();
		var rcrh = new ReferenceCheckRequestHandler();

		tb.addRequestHandlerPair(EqualsCheckRequest.class, ecrh);
		tb.addRequestHandlerPair(ReferenceCheckRequest.class, rcrh);

		var req = new EqualsCheckRequest(1, 2);
		var result = tb.handleSimilarityRequest(req);

		var history = tb.getHandlingHistory();
		Assertions.assertEquals(1, history.size());
		var he = history.get(0);

		Assertions.assertEquals(req, he.getRequest());
		Assertions.assertEquals(ecrh, he.getHandler());
		Assertions.assertEquals(result, he.getOutput());
		Assertions.assertEquals(ecrh.handleSimilarityRequest(req), he.getOutput());
	}
}
