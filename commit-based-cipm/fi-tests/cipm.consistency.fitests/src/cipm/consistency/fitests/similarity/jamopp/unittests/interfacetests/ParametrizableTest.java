package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.ParametersPackage;
import org.emftext.language.java.parameters.Parametrizable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ParametrizableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Parameter> parameters1 = () -> getAPI().newOrdinaryParameter().withName("param1")
			.withTypeReference(getAPI().newClassifierReference()
					.withTarget(getAPI().newClass().withName("cls1").createNow()).createNow())
			.createNow();
	private final Supplier<Parameter> parameters2 = () -> getAPI().newOrdinaryParameter().withName("param2")
			.withTypeReference(getAPI().newClassifierReference()
					.withTarget(getAPI().newClass().withName("cls2").createNow()).createNow())
			.createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Parametrizable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testParameters(Class<?> cls, String displayName) {
		this.testSimilarity(getAPI().newX(cls)
				.xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, parameters1.get()).createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, parameters2.get())
						.createNow(),
				ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testParametersSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS,
								new Parameter[] { parameters1.get(), parameters2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, parameters1.get())
						.createNow(),
				ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testParametersNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS, parameters1.get()).createNow(),
				ParametersPackage.Literals.PARAMETRIZABLE__PARAMETERS);
	}
}
