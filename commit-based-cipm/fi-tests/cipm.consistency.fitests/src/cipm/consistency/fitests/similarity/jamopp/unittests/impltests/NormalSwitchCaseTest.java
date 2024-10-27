package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.NormalSwitchCase;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.statements.NormalSwitchCaseInitialiser;

public class NormalSwitchCaseTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected NormalSwitchCase initElement(Expression[] additionalConds) {
		var nscInit = new NormalSwitchCaseInitialiser();
		var nsc = nscInit.instantiate();
		Assertions.assertTrue(nscInit.addAdditionalConditions(nsc, additionalConds));
		return nsc;
	}

	@Test
	public void testAdditionalCondition() {
		var objOne = this.initElement(new Expression[] { this.createMinimalFalseEE() });
		var objTwo = this.initElement(new Expression[] { this.createMinimalTrueNEE() });

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS);
	}

	@Test
	public void testAdditionalConditionSize() {
		var objOne = this.initElement(new Expression[] { this.createMinimalFalseEE(), this.createMinimalTrueNEE() });
		var objTwo = this.initElement(new Expression[] { this.createMinimalFalseEE() });

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS);
	}

	@Test
	public void testAdditionalConditionNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new Expression[] { this.createMinimalFalseEE() }),
				new NormalSwitchCaseInitialiser(), false,
				StatementsPackage.Literals.NORMAL_SWITCH_CASE__ADDITIONAL_CONDITIONS);
	}
}
