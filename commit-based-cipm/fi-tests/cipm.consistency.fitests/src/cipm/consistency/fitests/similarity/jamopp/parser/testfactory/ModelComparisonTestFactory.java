package cipm.consistency.fitests.similarity.jamopp.parser.testfactory;

import java.nio.file.Path;

import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPSimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.IExpectedSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.ResourceContentSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurer;

/**
 * A test class factory, which generates dynamic tests that performs
 * hierarchical similarity checking on the given Resources
 * {@code lhsModelResource, rhsModelResource} and returns the result in form of
 * {@link Comparison} objects. <br>
 * <br>
 * The difference between this and {@link EAllContentSimilarityTestFactory} is
 * that the comparison here is much more detailed. <br>
 * <br>
 * Currently, {@link JavaModelComparator} is used for model comparison.
 * 
 * @author Alp Torac Genc
 */
public class ModelComparisonTestFactory extends AbstractJaMoPPParserSimilarityTestFactory {
	private static final String description = "Java model comparison on both sides";
	/**
	 * Whether the order of model resource contents should matter in model
	 * comparison
	 */
	private boolean contentOrderMatters;

	/**
	 * Constructs an instance, for which {@code contentOrderMatters == true}
	 */
	public ModelComparisonTestFactory() {
		this(true);
	}

	/**
	 * @param contentOrderMatters Whether the order of model resource contents
	 *                            should matter in model comparison
	 */
	public ModelComparisonTestFactory(boolean contentOrderMatters) {
		this.contentOrderMatters = contentOrderMatters;
	}

	/**
	 * Compares the given {@link Resource} instances representing Java models. Uses
	 * the underlying similarity checking mechanisms for identifying changes. <br>
	 * <br>
	 * Note that the order of the given parameters matters and will influence the
	 * result, since reaching from one side to the other will require "opposite"
	 * operations.
	 * 
	 * @param lhsModelResource The new state
	 * @param rhsModelResource The old state
	 * @return Result of comparing {@code rhsModelResource} to
	 *         {@code lhsModelResource}, i.e. what needs to be done to
	 *         {@code rhsModelResource} to get to {@code lhsModelResource}.
	 * 
	 * @see {@link #getSCC()}
	 */
	protected Comparison compareModels(Resource lhsModelResource, Resource rhsModelResource) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(lhsModelResource, null, rhsModelResource, null)
						.withModelComparisonClassName(JavaModelComparator.class.getSimpleName()).createKey(),
				GeneralTimeMeasurementTag.MODEL_RESOURCE_COMPARISON);
		// TODO Integrate "this.scc" into the model comparator
		var result = JavaModelComparator.compareJavaModels(lhsModelResource, rhsModelResource, null, null, null);
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
		return result;
	}

	/**
	 * Asserts that the result of similarity checking via model comparison results
	 * in differences or not (denoted by expectedResult). <br>
	 * <br>
	 * Compares lhsModelResource to rhsModelResource, as well as rhsModelResource to
	 * lhsModelResource; in order to ensure that the comparison is symmetric.
	 */
	protected void testSimilarityWithModelComparison(Resource lhsModelResource, Resource rhsModelResource,
			Boolean expectedResult) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(lhsModelResource, null, rhsModelResource, null).createKey(),
				GeneralTimeMeasurementTag.TEST_OVERHEAD);

		var cmp1To2 = this.compareModels(lhsModelResource, rhsModelResource);
		Assertions.assertEquals(expectedResult, cmp1To2.getDifferences().size() == 0);

		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();

		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(rhsModelResource, null, lhsModelResource, null).createKey(),
				GeneralTimeMeasurementTag.TEST_OVERHEAD);

		var cmp2To1 = this.compareModels(rhsModelResource, lhsModelResource);
		Assertions.assertEquals(expectedResult, cmp2To1.getDifferences().size() == 0);

		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
	}

	@Override
	public DynamicNode createTestsFor(Resource lhsModelResource, Path lhsModelSourceFileDirPath,
			Resource rhsModelResource, Path rhsModelSourceFileDirPath) {
		return DynamicTest.dynamicTest(String.format("%s vs %s", lhsModelSourceFileDirPath.getFileName(),
				rhsModelSourceFileDirPath.getFileName()), () -> {
					this.testSimilarityWithModelComparison(lhsModelResource, rhsModelResource,
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
		// TODO Extract the ISimilarityCheckerContainer as a private variable, which
		// should be acquired through the constructor. Since currently
		// JavaModelComparator is used, using any other ISimilarityCheckerContainer
		// could change test results. Hence it is currently not configurable
		return new ResourceContentSimilarityResultProvider(new JaMoPPSimilarityCheckerContainer(),
				this.contentOrderMatters);
	}
}