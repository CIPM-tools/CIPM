package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.Statement;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ConditionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Statement> elseStatement1 = () -> getAPI().createNewAssert();
	private final Supplier<Statement> elseStatement2 = () -> getAPI().createNewEmptyStatement();

	@Test
	public void testElseStatement() {
		this.testSimilarity(getAPI().newCondition().withElseStatement(elseStatement1.get()).createNow(),
				getAPI().newCondition().withElseStatement(elseStatement2.get()).createNow(),
				StatementsPackage.Literals.CONDITION__ELSE_STATEMENT);
	}

	@Test
	public void testElseStatementNullCheck() {
		this.testSimilarityNullCheck(getAPI().newCondition().withElseStatement(elseStatement1.get()).createNow(),
				StatementsPackage.Literals.CONDITION__ELSE_STATEMENT);
	}
}
