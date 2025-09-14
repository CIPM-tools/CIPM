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
 * hierarchical similarity checking on the given Resources {@code res1, res2}
 * and returns the result in form of {@link Comparison} objects. <br>
 * <br>
 * The difference between this and {@link EAllContentSimilarityTestFactory} is
 * that the comparison here is much more detailed.
 * 
 * @author Alp Torac Genc
 */
public class ModelComparisonTestFactory extends AbstractJaMoPPParserSimilarityTestFactory {
	private static final String description = "Java model comparison on both sides";
	private boolean contentOrderMatters;

	public ModelComparisonTestFactory() {
		this(true);
	}

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
	 * @param res1 The new state
	 * @param res2 The old state
	 * @return Result of comparing {@code res2} to {@code res1}, i.e. what needs to
	 *         be done to {@code res2} to get to {@code res1}.
	 * 
	 * @see {@link #getSCC()}
	 */
	protected Comparison compareModels(Resource res1, Resource res2) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(res1, null, res2, null)
						.withModelComparisonClassName(JavaModelComparator.class.getSimpleName()).createKey(),
				GeneralTimeMeasurementTag.MODEL_RESOURCE_COMPARISON);
		// TODO Integrate "this.scc" into the model comparator
		var result = JavaModelComparator.compareJavaModels(res1, res2, null, null, null);
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
		return result;
	}

	/**
	 * Asserts that the result of similarity checking via model comparison results
	 * in differences or not (denoted by expectedResult). <br>
	 * <br>
	 * Compares res1 and res2, as well as res2 and res1; in order to ensure that the
	 * comparison is symmetric.
	 */
	protected void testSimilarityWithModelComparison(Resource res1, Resource res2, Boolean expectedResult) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(res1, null, res2, null).createKey(),
				GeneralTimeMeasurementTag.TEST_OVERHEAD);

		var cmp1To2 = this.compareModels(res1, res2);
		Assertions.assertEquals(expectedResult, cmp1To2.getDifferences().size() == 0);

		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();

		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(res2, null, res1, null).createKey(),
				GeneralTimeMeasurementTag.TEST_OVERHEAD);

		var cmp2To1 = this.compareModels(res2, res1);
		Assertions.assertEquals(expectedResult, cmp2To1.getDifferences().size() == 0);

		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
	}

	/**
	 * Checks if parsed {@link Resource} instances are detected as similar. Checks
	 * the similarity of res1 with res2.
	 */
	@Override
	public DynamicNode createTestsFor(Resource res1, Path path1, Resource res2, Path path2) {
		return DynamicTest.dynamicTest(String.format("%s vs %s", path1.getFileName(), path2.getFileName()), () -> {
			this.testSimilarityWithModelComparison(res1, res2,
					this.getExpectedSimilarityResultFor(res1, path1, res2, path2));
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