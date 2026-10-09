package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Conditional;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ConditionalTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> condition1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> condition2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Conditional.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testCondition(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.CONDITIONAL__CONDITION, condition1.get())
						.createNow(),
				getAPI().newX(cls).xWithFeat(StatementsPackage.Literals.CONDITIONAL__CONDITION, condition2.get())
						.createNow(),
				StatementsPackage.Literals.CONDITIONAL__CONDITION);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testConditionNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(StatementsPackage.Literals.CONDITIONAL__CONDITION, condition1.get()).createNow(),
				StatementsPackage.Literals.CONDITIONAL__CONDITION);
	}
}
