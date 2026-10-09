package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.UnaryModificationExpression;
import org.emftext.language.java.expressions.UnaryModificationExpressionChild;
import org.emftext.language.java.operators.UnaryModificationOperator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class UnaryModificationExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<UnaryModificationExpressionChild> child1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<UnaryModificationExpressionChild> child2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<UnaryModificationOperator> operator1 = () -> getAPI().newPlusPlus();
	private final Supplier<UnaryModificationOperator> operator2 = () -> getAPI().newMinusMinus();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(UnaryModificationExpression.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testChild(Class<?> cls, String displayName) {
		this.testSimilarity(getAPI().newX(cls)
				.xWithFeat(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD, child1.get()).createNow(),
				getAPI().newX(cls)
						.xWithFeat(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD, child2.get())
						.createNow(),
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testChildNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD, child1.get()).createNow(),
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__CHILD);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOperator(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithFeat(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR, operator1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithFeat(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR, operator2.get())
						.createNow(),
				ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testOperatorNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR, operator1.get())
				.createNow(), ExpressionsPackage.Literals.UNARY_MODIFICATION_EXPRESSION__OPERATOR);
	}
}
