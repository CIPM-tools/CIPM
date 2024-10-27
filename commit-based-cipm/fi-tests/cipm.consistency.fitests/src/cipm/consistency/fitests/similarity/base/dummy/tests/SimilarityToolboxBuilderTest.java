package cipm.consistency.fitests.similarity.base.dummy.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolboxBuilder;
import cipm.consistency.fitests.similarity.base.dummy.DummySimilarityToolboxFactory;
import cipm.consistency.fitests.similarity.base.dummy.EqualsCheckRequest;
import cipm.consistency.fitests.similarity.base.dummy.ReferenceCheckRequest;

/**
 * Contains tests regarding the construction of {@link DummySimilarityToolbox}
 * using {@link DummySimilarityToolboxBuilder}. Methods inherited by dummy
 * classes from their abstract classes are tested along the way.
 * 
 * @author Alp Torac Genc
 */
public class SimilarityToolboxBuilderTest extends AbstractDummySimilarityCheckingTest {
	/**
	 * Test whether the {@link DummySimilarityToolboxBuilder#build()} method is
	 * working as intended.
	 */
	@Test
	public void testBuild() {
		var tbBuilder = new DummySimilarityToolboxBuilder();
		tbBuilder.setSimilarityToolboxFactory(new DummySimilarityToolboxFactory());

		// No toolbox under construction present within builder
		Assertions.assertNull(tbBuilder.build());

		tbBuilder.instantiate();
		// Builder now has a toolbox under construction
		Assertions.assertNotNull(tbBuilder.build());

		// Builder already returned the toolbox under construction
		Assertions.assertNull(tbBuilder.build());
	}

	/**
	 * Test whether adding request-handler pairs step-wise works as intended.
	 */
	@Test
	public void testRequestHandlerPairs() {
		var tbBuilder = new DummySimilarityToolboxBuilder();
		tbBuilder.setSimilarityToolboxFactory(new DummySimilarityToolboxFactory());

		var tbNoPairs = tbBuilder.instantiate().build();

		Assertions.assertFalse(tbNoPairs.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertFalse(tbNoPairs.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		var tbOnePair = tbBuilder.instantiate().buildEqualsCheckHandler().build();

		Assertions.assertTrue(tbOnePair.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertFalse(tbOnePair.canHandleSimilarityRequest(ReferenceCheckRequest.class));

		var tbBothPairs = tbBuilder.instantiate().buildEqualityCheckingHandlers().build();

		Assertions.assertTrue(tbBothPairs.canHandleSimilarityRequest(EqualsCheckRequest.class));
		Assertions.assertTrue(tbBothPairs.canHandleSimilarityRequest(ReferenceCheckRequest.class));
	}
}
