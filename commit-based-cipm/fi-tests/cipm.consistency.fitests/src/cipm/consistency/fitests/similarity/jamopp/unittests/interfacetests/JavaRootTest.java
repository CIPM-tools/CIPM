package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.containers.ContainersPackage;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.containers.IJavaRootInitialiser;

public class JavaRootTest extends AbstractJaMoPPSimilarityTest {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IJavaRootInitialiser.class);
	}

	protected JavaRoot initElement(IJavaRootInitialiser init, Origin origin) {
		JavaRoot result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setOrigin(result, origin));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOrigin(IJavaRootInitialiser init, String displayName) {
		var objOne = this.initElement(init, Origin.BINDING);
		var objTwo = this.initElement(init, Origin.CLASS);

		this.testSimilarity(objOne, objTwo, ContainersPackage.Literals.JAVA_ROOT__ORIGIN);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOriginNullCheck(IJavaRootInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, Origin.BINDING), init, true,
				ContainersPackage.Literals.JAVA_ROOT__ORIGIN);
	}
}
