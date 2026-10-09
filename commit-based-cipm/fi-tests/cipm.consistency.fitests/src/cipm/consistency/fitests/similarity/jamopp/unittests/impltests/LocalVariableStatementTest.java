package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.variables.LocalVariable;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class LocalVariableStatementTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<LocalVariable> variable1 = () -> getAPI().newLocalVariable().withName("lv1").createNow();
	private final Supplier<LocalVariable> variable2 = () -> getAPI().newLocalVariable().withName("lv2").createNow();

	@Test
	public void testVariable() {
		this.testSimilarity(getAPI().newLocalVariableStatement(variable1.get()),
				getAPI().newLocalVariableStatement(variable2.get()),
				StatementsPackage.Literals.LOCAL_VARIABLE_STATEMENT__VARIABLE);
	}

	@Test
	public void testVariableNullCheck() {
		this.testSimilarityNullCheck(getAPI().newLocalVariableStatement(variable1.get()),
				StatementsPackage.Literals.LOCAL_VARIABLE_STATEMENT__VARIABLE);
	}
}
