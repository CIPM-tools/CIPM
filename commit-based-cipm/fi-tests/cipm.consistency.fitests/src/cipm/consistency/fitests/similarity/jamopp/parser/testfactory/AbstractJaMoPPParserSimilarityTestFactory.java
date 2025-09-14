package cipm.consistency.fitests.similarity.jamopp.parser.testfactory;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DynamicNode;

import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.IExpectedSimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.resultprovider.ResourceReferenceEqualitySimilarityResultProvider;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.GeneralTimeMeasurementTag;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurementKeyBuilder;
import cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement.ParserTestTimeMeasurer;

/**
 * An abstract class meant to be implemented by classes that encapsulate logic
 * about dynamic test generation.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractJaMoPPParserSimilarityTestFactory {
	/**
	 * Do not set it here (via {@code #getDefaultExpectedSimilarityResultProvider()}
	 * for instance), because the concrete implementor may need prior
	 * initialisation.
	 * 
	 * @see {@link #getExpectedSimilarityResultProvider()}
	 */
	private IExpectedSimilarityResultProvider resultProvider;

	/**
	 * Since there should always be a way to determine the expected similarity
	 * results, this method is implemented to provide a default way to do so. <br>
	 * <br>
	 * Defaults to {@link ResourceReferenceEqualitySimilarityResultProvider}. <br>
	 * <br>
	 * Can be overridden in concrete implementors to better align the return value
	 * with their intents.
	 * 
	 * @return The default object that provides expected similarity results to
	 *         dynamic tests.
	 */
	public IExpectedSimilarityResultProvider getDefaultExpectedSimilarityResultProvider() {
		return new ResourceReferenceEqualitySimilarityResultProvider();
	}

	/**
	 * @return The object that provides expected similarity results to dynamic
	 *         tests. If it is currently not set (i.e. it is null), sets it to
	 *         {@link #getDefaultExpectedSimilarityResultProvider()} and then
	 *         returns.
	 */
	public IExpectedSimilarityResultProvider getExpectedSimilarityResultProvider() {
		if (this.resultProvider == null)
			this.resultProvider = this.getDefaultExpectedSimilarityResultProvider();

		return this.resultProvider;
	}

	/**
	 * Changes how this instance provides expected similarity results to the dynamic
	 * tests it generates. <br>
	 * <br>
	 * If the given provider is null, sets the current provider to
	 * {@link #getDefaultExpectedSimilarityResultProvider()} instead, as there must
	 * always be a way to determine expected similarity results.
	 */
	public void setExpectedSimilarityResultProvider(IExpectedSimilarityResultProvider resultProvider) {
		this.resultProvider = resultProvider;

		if (this.resultProvider == null)
			this.resultProvider = this.getDefaultExpectedSimilarityResultProvider();
	}

	/**
	 * Defaults to the name of the concrete implementing type. Can be overridden in
	 * concrete implementors for a more accurate description.
	 * 
	 * @return A description for this test generation factory, which may be added to
	 *         test descriptions.
	 */
	public String getTestDescription() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Delegates computing the expected similarity result to the underlying
	 * provider.
	 * 
	 * @see {@link #getExpectedSimilarityResultProvider()}
	 */
	public boolean getExpectedSimilarityResultFor(Resource lhsModelResource, Path lhsModelSourceFileDirPath,
			Resource rhsModelResource, Path rhsModelSourceFileDirPath) {
		ParserTestTimeMeasurer.getInstance().startTimeMeasurement(
				this.getTimeMeasurementKeyBuilderFor(lhsModelResource, lhsModelSourceFileDirPath, rhsModelResource,
						rhsModelSourceFileDirPath).createKey(),
				GeneralTimeMeasurementTag.EXPECTED_SIMILARITY_RESULT_COMPUTATION);
		var result = this.getExpectedSimilarityResultProvider().getExpectedSimilarityResultFor(lhsModelResource,
				lhsModelSourceFileDirPath, rhsModelResource, rhsModelSourceFileDirPath);
		ParserTestTimeMeasurer.getInstance().stopTimeMeasurement();
		return result;
	}

	/**
	 * The returned builder contains the preliminary key, which can be further built
	 * upon. In the end, call
	 * {@link ParserTestTimeMeasurementKeyBuilder#createKey()} to get the final key.
	 * 
	 * @return A {@link ParserTestTimeMeasurementKeyBuilder} that contains all the
	 *         information regarding the time measurement, which is derivable from
	 *         the given parameters.
	 */
	protected ParserTestTimeMeasurementKeyBuilder getTimeMeasurementKeyBuilderFor(Resource lhsModelResource,
			Path lhsModelSourceFileDirPath, Resource rhsModelResource, Path rhsModelSourceFileDirPath) {
		var keyBuilder = new ParserTestTimeMeasurementKeyBuilder();

		keyBuilder.withTestFactoryClassName(this.getClass().getSimpleName());
		keyBuilder.withExpectedSimilarityResultProviderClassName(
				this.getExpectedSimilarityResultProvider().getClass().getSimpleName());

		if (lhsModelResource != null) {
			keyBuilder.withParsedLeftModelLocation(lhsModelResource.getURI().toString());
		}

		if (lhsModelSourceFileDirPath != null) {
			keyBuilder.withOriginalLeftModelLocation(lhsModelSourceFileDirPath.toString());
		}

		if (rhsModelResource != null) {
			keyBuilder.withParsedRightModelLocation(rhsModelResource.getURI().toString());
		}

		if (rhsModelSourceFileDirPath != null) {
			keyBuilder.withOriginalRightModelLocation(rhsModelSourceFileDirPath.toString());
		}

		return keyBuilder;
	}

	/**
	 * The provided model resources are not assumed to have a certain order here,
	 * because the concrete implementor could change what resources are checked for
	 * similarity in which order. Refer to the concrete implementations of
	 * {@link #getExpectedSimilarityResultProvider()} and
	 * {@link #getDefaultExpectedSimilarityResultProvider()} for more information.
	 * 
	 * @return Dynamic tests generated based on the concrete implementor.
	 */
	public abstract DynamicNode createTestsFor(Resource lhsModelResource, Path lhsModelSourceFileDirPath,
			Resource rhsModelResource, Path rhsModelSourceFileDirPath);
}
