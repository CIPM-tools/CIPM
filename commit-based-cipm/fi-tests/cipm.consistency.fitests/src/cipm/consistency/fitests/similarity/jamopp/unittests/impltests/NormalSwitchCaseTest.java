package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class NormalSwitchCaseTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> additionalCondition1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> additionalCondition2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testAdditionalCondition() {
		this.testSimilarity(
				getAPI().newNormalSwitchCase().withAddedAdditionalConditions(additionalCondition1.get()).createNow(),
				getAPI().newNormalSwitchCase().withAddedAdditionalConditions(additionalCondition2.get()).createNow(),
				StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS);
	}

	@Test
	public void testAdditionalConditionSize() {
		this.testSimilarity(
				getAPI().newNormalSwitchCase().withAddedAdditionalConditions(
						new Expression[] { additionalCondition1.get(), additionalCondition2.get() }).createNow(),
				getAPI().newNormalSwitchCase().withAddedAdditionalConditions(additionalCondition1.get()).createNow(),
				StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS);
	}

	@Test
	public void testAdditionalConditionNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newNormalSwitchCase().withAddedAdditionalConditions(additionalCondition1.get()).createNow(),
				StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS);
	}
}
