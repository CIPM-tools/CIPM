package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.Switch;
import org.emftext.language.java.statements.SwitchCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesSwitchCases;
import cipm.consistency.initialisers.jamopp.statements.SwitchInitialiser;

public class SwitchTest extends AbstractJaMoPPSimilarityTest implements UsesSwitchCases {
	protected Switch initElement(SwitchCase[] cases, Expression var) {
		var swInit = new SwitchInitialiser();
		var sw = swInit.instantiate();
		Assertions.assertTrue(swInit.addCases(sw, cases));
		Assertions.assertTrue(swInit.setVariable(sw, var));
		return sw;
	}

	@Test
	public void testCase() {
		var objOne = this.initElement(new SwitchCase[] { this.createEmptyNSC() }, null);
		var objTwo = this.initElement(new SwitchCase[] { this.createMinimalNSC() }, null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.SWITCH__CASES);
	}

	@Test
	public void testCaseSize() {
		var objOne = this.initElement(new SwitchCase[] { this.createEmptyNSC(), this.createMinimalNSC() }, null);
		var objTwo = this.initElement(new SwitchCase[] { this.createEmptyNSC() }, null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.SWITCH__CASES);
	}

	@Test
	public void testCaseNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new SwitchCase[] { this.createEmptyNSC() }, null),
				new SwitchInitialiser(), false, StatementsPackage.Literals.SWITCH__CASES);
	}

	@Test
	public void testVariable() {
		var objOne = this.initElement(null, this.createMinimalSR("str1"));
		var objTwo = this.initElement(null, this.createMinimalSR("str2"));

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.SWITCH__VARIABLE);
	}

	@Test
	public void testVariableNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createMinimalSR("str1")), new SwitchInitialiser(),
				false, StatementsPackage.Literals.SWITCH__VARIABLE);
	}
}
