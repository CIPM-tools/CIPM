package cipm.consistency.fitests.similarity.base.dummy.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityComparer;
import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolbox;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequestHandler;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequestHandler;

/**
 * Contains structural and setup related tests for
 * {@link DummySimilarityComparer}.
 * 
 * @author Alp Torac Genc
 */
public class SimilarityComparerTest extends AbstractDummySimilarityCheckingTest {
	/**
	 * Tests whether certain {@link ISimilarityRequest} classes can be handled after
	 * adding their corresponding request-handler pair to the underlying toolbox.
	 */
	@Test
	public void testCanHandleSimilarityRequest() {
		var simComparer = new DummySimilarityComparer(this.buildFullToolbox());

		Assertions.assertTrue(simComparer.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertTrue(simComparer.canHandleSimilarityRequest(ReferenceCheckRequest.class));
		// Make sure that requests without a corresponding handler are not handled
		Assertions.assertFalse(simComparer.canHandleSimilarityRequest(ISimilarityRequest.class));
	}

	/**
	 * Tests whether certain {@link ISimilarityRequest} instances are handled
	 * accordingly after proper setup.
	 */
	@Test
	public void testHandleSimilarityRequest() {
		var tb = new DummySimilarityToolbox();
		var ecrh = new EqualsCheckRequestHandler();
		var rcrh = new ReferenceCheckRequestHandler();

		tb.addRequestHandlerPair(EqualsCheckRequest.class, ecrh);
		tb.addRequestHandlerPair(ReferenceCheckRequest.class, rcrh);

		var simComparer = new DummySimilarityComparer(tb);

		var req = new EqualsCheckRequest(1, 2);
		var result = simComparer.handleSimilarityRequest(req);

		var history = tb.getHandlingHistory();
		Assertions.assertEquals(1, history.size());
		var he = history.get(0);

		Assertions.assertEquals(req, he.getRequest());
		Assertions.assertEquals(ecrh, he.getHandler());
		Assertions.assertEquals(result, he.getOutput());
		Assertions.assertEquals(ecrh.handleSimilarityRequest(req), he.getOutput());
	}
}
