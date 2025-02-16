package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.variables.LocalVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLocalVariables;
import cipm.consistency.initialisers.jamopp.statements.LocalVariableStatementInitialiser;

public class LocalVariableStatementTest extends AbstractJaMoPPSimilarityTest implements UsesLocalVariables {
	protected LocalVariableStatement initElement(LocalVariable var) {
		var lvsInit = new LocalVariableStatementInitialiser();
		var lvs = lvsInit.instantiate();
		Assertions.assertTrue(lvsInit.setVariable(lvs, var));
		return lvs;
	}

	@Test
	public void testVariable() {
		var objOne = this.initElement(this.createMinimalLV("lv1"));
		var objTwo = this.initElement(this.createMinimalLV("lv2"));

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.LOCAL_VARIABLE_STATEMENT__VARIABLE);
	}

	@Test
	public void testVariableNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalLV("lv1")),
				new LocalVariableStatementInitialiser(), false,
				StatementsPackage.Literals.LOCAL_VARIABLE_STATEMENT__VARIABLE);
	}
}
