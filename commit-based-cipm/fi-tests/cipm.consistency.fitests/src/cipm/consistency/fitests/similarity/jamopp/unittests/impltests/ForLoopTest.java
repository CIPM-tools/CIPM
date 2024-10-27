package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.ForLoop;
import org.emftext.language.java.statements.StatementsPackage;
import org.emftext.language.java.statements.ForLoopInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.initialisers.jamopp.statements.ForLoopInitialiser;

public class ForLoopTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions {
	protected ForLoop initElement(ForLoopInitializer flInit, Expression[] updateExprs) {
		var init = new ForLoopInitialiser();
		var fl = init.instantiate();
		Assertions.assertTrue(init.setInit(fl, flInit));
		Assertions.assertTrue(init.addUpdates(fl, updateExprs));
		return fl;
	}

	@Test
	public void testInit() {
		var objOne = this.initElement(this.createExprList(), null);
		var objTwo = this.initElement(this.createExprList(this.createMinimalFalseEE()), null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.FOR_LOOP__INIT);
	}

	@Test
	public void testInitNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createExprList(), null), new ForLoopInitialiser(), false,
				StatementsPackage.Literals.FOR_LOOP__INIT);
	}

	@Test
	public void testUpdate() {
		var objOne = this.initElement(null, new Expression[] { this.createMinimalFalseEE() });
		var objTwo = this.initElement(null, new Expression[] { this.createMinimalTrueNEE() });

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.FOR_LOOP__UPDATES);
	}

	@Test
	public void testUpdateSize() {
		var objOne = this.initElement(null,
				new Expression[] { this.createMinimalFalseEE(), this.createMinimalTrueNEE() });
		var objTwo = this.initElement(null, new Expression[] { this.createMinimalFalseEE() });

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.FOR_LOOP__UPDATES);
	}

	@Test
	public void testUpdateNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, new Expression[] { this.createMinimalFalseEE() }),
				new ForLoopInitialiser(), false, StatementsPackage.Literals.FOR_LOOP__UPDATES);
	}
}
