package cipm.consistency.fitests.similarity.base.dummy.tests;

import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolbox;
import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolboxBuilder;
import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolboxFactory;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequestHandler;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequestHandler;

/**
 * An abstract class that contains utility methods for tests that use dummy
 * implementations of similarity checking elements.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractDummySimilarityCheckingTest {
	/**
	 * Constructs a {@link DummySimilarityToolbox} instance using a
	 * {@link DummySimilarityToolboxBuilder}. Adds request-handler pairs to that
	 * instance and returns it.
	 */
	protected DummySimilarityToolbox buildFullToolbox() {
		var tbBuilder = new DummySimilarityToolboxBuilder();
		tbBuilder.setSimilarityToolboxFactory(new DummySimilarityToolboxFactory());

		return (DummySimilarityToolbox) tbBuilder.instantiate().buildEqualityCheckingHandlers().build();
	}

	/**
	 * Constructs a {@link DummySimilarityToolbox} instance without using any
	 * builders. Adds request-handler pairs to that instance and returns it.
	 */
	protected DummySimilarityToolbox createFullToolbox() {
		var tb = new DummySimilarityToolbox();

		tb.addRequestHandlerPair(EqualsCheckRequest.class, new EqualsCheckRequestHandler());
		tb.addRequestHandlerPair(ReferenceCheckRequest.class, new ReferenceCheckRequestHandler());

		return tb;
	}
}
