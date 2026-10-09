package cipm.consistency.fitests.similarity.jamopp;

import java.io.File;

import cipm.consistency.fitests.similarity.ISimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.base.JavaSimilarityCheckerContainer;
import cipm.consistency.fitests.similarity.eobject.AbstractEObjectSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.params.JaMoPPSimilarityValues;
import cipm.consistency.fitests.similarity.params.InitialiserTestSettingsProvider;
import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.java.api.FluentJavaAPI;

/**
 * An abstract test class that extends {@link AbstractEObjectSimilarityTest}
 * with concrete method implementations for JaMoPP context, as well as static
 * methods that can be used in parameterised tests to generate initialiser
 * instances.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractJaMoPPSimilarityTest extends AbstractEObjectSimilarityTest {
	protected FluentJavaAPI getAPI() {
		return ApiFactory.eINSTANCE.createFluentJavaAPI();
	}

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

		if (instance.getSimilarityValues() == null) {
			instance.setSimilarityValues(new JaMoPPSimilarityValues());
		}

		return instance;
	}

	@Override
	public InitialiserTestSettingsProvider getInitialiserTestSettingsProvider() {
		return getClassesInitialiserTestSettingsProvider();
	}
}
