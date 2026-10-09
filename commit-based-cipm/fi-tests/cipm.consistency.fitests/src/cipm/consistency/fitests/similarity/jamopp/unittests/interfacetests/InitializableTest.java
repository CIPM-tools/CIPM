package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.instantiations.Initializable;
import org.emftext.language.java.instantiations.InstantiationsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class InitializableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> initialValue1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> initialValue2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Initializable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testInitialValue(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithFeat(InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE, initialValue1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithFeat(InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE, initialValue2.get())
						.createNow(),
				InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testInitialValueNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE, initialValue1.get())
				.createNow(), InstantiationsPackage.Literals.INITIALIZABLE__INITIAL_VALUE);
	}
}
