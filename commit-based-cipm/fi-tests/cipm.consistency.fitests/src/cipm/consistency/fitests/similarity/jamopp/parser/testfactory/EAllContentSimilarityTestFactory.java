package cipm.consistency.fitests.similarity.jamopp.parser.testfactory;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.FileContentSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.IExpectedSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurer;

/**
 * A test class factory, which generates dynamic tests that check the similarity
 * of all contents ({@code res.getAllContents()}) of the given Resources
 * {@code lhsModelResource, rhsModelResource} pairwise.
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
	protected void testSimilarityOfAllContents(Resource lhsModelResource, Resource rhsModelResource,
			Boolean expectedResult) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(lhsModelResource, null, rhsModelResource, null).createKey(),
				GeneralTimeMeasurementTag.TEST_OVERHEAD);
		var list1 = new ArrayList<EObject>();
		var list2 = new ArrayList<EObject>();

		lhsModelResource.getAllContents().forEachRemaining((o) -> list1.add(o));
		rhsModelResource.getAllContents().forEachRemaining((o) -> list2.add(o));

		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(lhsModelResource, null, rhsModelResource, null).createKey(),
				GeneralTimeMeasurementTag.SIMILARITY_CHECKING);
		Assertions.assertEquals(expectedResult, this.scc.areSimilar(list1, list2));
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
	}

	@Override
	public DynamicNode createTestsFor(Resource lhsModelResource, Path lhsModelSourceFileDirPath,
			Resource rhsModelResource, Path rhsModelSourceFileDirPath) {
		return DynamicTest.dynamicTest(String.format("%s vs %s", lhsModelSourceFileDirPath.getFileName(),
				rhsModelSourceFileDirPath.getFileName()), () -> {
					this.testSimilarityOfAllContents(lhsModelResource, rhsModelResource,
							this.getExpectedSimilarityResultFor(lhsModelResource, lhsModelSourceFileDirPath,
									rhsModelResource, rhsModelSourceFileDirPath));
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
