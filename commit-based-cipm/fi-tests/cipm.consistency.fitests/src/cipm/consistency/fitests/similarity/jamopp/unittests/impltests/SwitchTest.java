package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.SwitchCase;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class SwitchTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<SwitchCase> case1 = () -> getAPI().createNewNormalSwitchCase();
	private final Supplier<SwitchCase> case2 = () -> getAPI().createNewDefaultSwitchCase();

	private final Supplier<Expression> variable1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> variable2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testCase() {
		this.testSimilarity(getAPI().newSwitch().withAddedCases(case1.get()).createNow(),
				getAPI().newSwitch().withAddedCases(case2.get()).createNow(), StatementsPackage.Literals.SWITCH__CASES);
	}

	@Test
	public void testCaseSize() {
		this.testSimilarity(
				getAPI().newSwitch().withAddedCases(new SwitchCase[] { case1.get(), case2.get() }).createNow(),
				getAPI().newSwitch().withAddedCases(case1.get()).createNow(), StatementsPackage.Literals.SWITCH__CASES);
	}

	@Test
	public void testCaseNullCheck() {
		this.testSimilarityNullCheck(getAPI().newSwitch().withAddedCases(case1.get()).createNow(),
				StatementsPackage.Literals.SWITCH__CASES);
	}

	@Test
	public void testVariable() {
		this.testSimilarity(getAPI().newSwitch().withVariable(variable1.get()).createNow(),
				getAPI().newSwitch().withVariable(variable2.get()).createNow(),
				StatementsPackage.Literals.SWITCH__VARIABLE);
	}

	@Test
	public void testVariableNullCheck() {
		this.testSimilarityNullCheck(getAPI().newSwitch().withVariable(variable1.get()).createNow(),
				StatementsPackage.Literals.SWITCH__VARIABLE);
	}
}
