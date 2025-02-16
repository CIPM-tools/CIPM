package cipm.consistency.fitests.similarity.jamopp;

import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.base.JavaSimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.eobject.AbstractEObjectSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.params.JaMoPPInitialiserParameters;
import cipm.consistency.fitests.similarity.jamopp.params.JaMoPPSimilarityValues;
import cipm.consistency.fitests.similarity.params.IInitialiserParameters;
import cipm.consistency.fitests.similarity.params.InitialiserTestSettingsProvider;
import cipm.consistency.initialisers.jamopp.IJaMoPPEObjectInitialiser;

/**
 * An abstract test class that extends {@link AbstractEObjectSimilarityTest}
 * with concrete method implementations for JaMoPP context, as well as static
 * methods that can be used in parameterised tests to generate initialiser
 * instances.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractJaMoPPSimilarityTest extends AbstractEObjectSimilarityTest {
	@Override
	protected ISimilarityCheckerContainer initSCC() {
		return new JavaSimilarityCheckerContainer();
	}

	@Override
	public String getAbsoluteResourceRootPath() {
		return new File("").getAbsoluteFile().getAbsolutePath() + File.separator + "testModels";
	}

	@Override
	public String getResourceFileExtension() {
		return "javaxmi";
	}

	/**
	 * @return The {@link InitialiserTestSettingsProvider} that will be used in
	 *         tests. Initialises the instance to be returned, if not properly
	 *         initialised.
	 */
	public static InitialiserTestSettingsProvider getClassesInitialiserTestSettingsProvider() {
		var instance = InitialiserTestSettingsProvider.getInstance();

		if (instance == null) {
			InitialiserTestSettingsProvider.initialise();
			instance = InitialiserTestSettingsProvider.getInstance();
		}

		if (instance.getParameters() == null) {
			instance.setParameters(new JaMoPPInitialiserParameters());
		}

		if (instance.getSimilarityValues() == null) {
			instance.setSimilarityValues(new JaMoPPSimilarityValues());
		}

		return instance;
	}

	@Override
	public InitialiserTestSettingsProvider getInitialiserTestSettingsProvider() {
		return getClassesInitialiserTestSettingsProvider();
	}

	/**
	 * @see {@link IInitialiserParameters#getEachInitialiserOnceBySuper(Class)}
	 */
	public static Stream<Arguments> getEachInitialiserArgumentsOnceFor(
			Class<? extends IJaMoPPEObjectInitialiser> superType) {
		return getClassesInitialiserTestSettingsProvider().getParameters().getEachInitialiserOnceBySuper(superType)
				.stream().map((i) -> Arguments.of(i));
	}

	/**
	 * @see {@link IInitialiserParameters#getAllInitialisersBySuper(Class)}
	 */
	public static Stream<Arguments> getAllInitialiserArgumentsFor(
			Class<? extends IJaMoPPEObjectInitialiser> superType) {
		return getClassesInitialiserTestSettingsProvider().getParameters().getAllInitialisersBySuper(superType).stream()
				.map((i) -> Arguments.of(i));
	}

	/**
	 * @see {@link IInitialiserParameters#getAdaptedInitialisersBySuper(Class)}
	 */
	public static <T extends IJaMoPPEObjectInitialiser> Stream<Arguments> getAdaptedInitialiserArgumentsFor(
			Class<T> superType) {
		return getClassesInitialiserTestSettingsProvider().getParameters().getAdaptedInitialisersBySuper(superType)
				.stream().map((i) -> Arguments.of(i));
	}

	/**
	 * @see {@link IInitialiserParameters#getNonAdaptedInitialisersBySuper(Class)}
	 */
	public static Stream<Arguments> getNonAdaptedInitialiserArgumentsFor(
			Class<? extends IJaMoPPEObjectInitialiser> superType) {
		return getClassesInitialiserTestSettingsProvider().getParameters().getNonAdaptedInitialisersBySuper(superType)
				.stream().map((i) -> Arguments.of(i));
	}
}
