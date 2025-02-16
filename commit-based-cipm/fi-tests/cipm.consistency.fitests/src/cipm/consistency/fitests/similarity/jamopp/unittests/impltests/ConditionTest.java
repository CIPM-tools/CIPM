package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.statements.ConditionInitialiser;

public class ConditionTest extends AbstractJaMoPPSimilarityTest implements UsesStatements {
	protected Condition initElement(Statement elseSt) {
		var conInit = new ConditionInitialiser();
		var con = conInit.instantiate();
		Assertions.assertTrue(conInit.setElseStatement(con, elseSt));
		return con;
	}

	@Test
	public void testElseStatement() {
		var objOne = this.initElement(this.createMinimalTrivialAssert());
		var objTwo = this.initElement(this.createMinimalNullReturn());

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.CONDITION__ELSE_STATEMENT);
	}

	@Test
	public void testElseStatementNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalTrivialAssert()), new ConditionInitialiser(),
				false, StatementsPackage.Literals.CONDITION__ELSE_STATEMENT);
	}
}
