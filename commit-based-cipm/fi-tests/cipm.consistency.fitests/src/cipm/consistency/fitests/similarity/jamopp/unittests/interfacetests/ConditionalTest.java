package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Conditional;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.statements.IConditionalInitialiser;

public class ConditionalTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IConditionalInitialiser.class);
	}

	protected Conditional initElement(IConditionalInitialiser init, Expression cond) {
		Conditional result = init.instantiate();
		Assertions.assertTrue(init.setCondition(result, cond));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testCondition(IConditionalInitialiser init) {
		var objOne = this.initElement(init, this.createMinimalTrueEE());
		var objTwo = this.initElement(init, this.createMinimalTrueNEE());

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.CONDITIONAL__CONDITION);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testConditionNullCheck(IConditionalInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalTrueEE()), init, false,
				StatementsPackage.Literals.CONDITIONAL__CONDITION);
	}
}
