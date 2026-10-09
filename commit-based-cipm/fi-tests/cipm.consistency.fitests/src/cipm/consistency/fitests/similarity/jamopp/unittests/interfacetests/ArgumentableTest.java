package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.references.Argumentable;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ArgumentableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> arguments1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> arguments2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Argumentable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArguments(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithAddedFeat(ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS, arguments1.get())
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS, arguments2.get())
						.createNow(),
				ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArgumentsSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS,
								new Expression[] { arguments1.get(), arguments2.get() })
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS, arguments1.get())
						.createNow(),
				ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArgumentsNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS, arguments1.get()).createNow(),
				ReferencesPackage.Literals.ARGUMENTABLE__ARGUMENTS);
	}
}
