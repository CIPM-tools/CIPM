package cipm.consistency.fitests.similarity.jamopp.parser;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;

/**
 * A test class factory, which generates dynamic tests that check the similarity
 * of all contents ({@code res.getAllContents()}) of the given Resources
 * {@code res1, res2} pairwise.
 * 
 * @author Alp Torac Genc
 */
public class EAllContentSimilarityTestFactory extends AbstractJaMoPPParserSimilarityTestFactory {
	private static final String description = "areSimilar on eAllContents";

	private ISimilarityCheckerContainer scc;

	public EAllContentSimilarityTestFactory(ISimilarityCheckerContainer scc) {
		this.scc = scc;
	}

	/**
	 * Checks whether the given {@link Resource} instances are similar, based on
	 * {@code res_i.getAllContents()}. The order of the direct contents is also
	 * considered and will impact the result.
	 */
	protected void testSimilarityOfAllContents(Resource res1, Resource res2, Boolean expectedResult) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(this.getClass().getSimpleName(),
				GeneralTimeMeasurementTag.TEST_OVERHEAD);
		var list1 = new ArrayList<EObject>();
		var list2 = new ArrayList<EObject>();

		res1.getAllContents().forEachRemaining((o) -> list1.add(o));
		res2.getAllContents().forEachRemaining((o) -> list2.add(o));

		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(this.getClass().getSimpleName(),
				GeneralTimeMeasurementTag.SIMILARITY_CHECKING);
		Assertions.assertEquals(expectedResult, this.scc.areSimilar(list1, list2));
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
	}

	/**
	 * Ensures that all contents of the parsed models are only then similar
	 * (accounting for their order too), if the content of their source files are
	 * equal (in terms of code, not whitespace nor comments etc.).
	 */
	@Override
	public DynamicNode createTestsFor(Resource res1, Path path1, Resource res2, Path path2) {
		return DynamicTest.dynamicTest(String.format("%s vs %s", path1.getFileName(), path2.getFileName()), () -> {
			this.testSimilarityOfAllContents(res1, res2, this.getExpectedSimilarityResultFor(res1, path1, res2, path2));
		});
	}

	@Override
	public String getTestDescription() {
		return description;
	}

	@Override
	public IExpectedSimilarityResultProvider getDefaultExpectedSimilarityResultProvider() {
		return new FileContentSimilarityResultProvider();
	}
}
