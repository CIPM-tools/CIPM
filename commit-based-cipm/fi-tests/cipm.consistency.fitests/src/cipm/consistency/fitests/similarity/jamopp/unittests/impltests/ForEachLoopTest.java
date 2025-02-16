package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.ForEachLoop;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesExpressions;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesParameters;
import cipm.consistency.initialisers.jamopp.statements.ForEachLoopInitialiser;

public class ForEachLoopTest extends AbstractJaMoPPSimilarityTest implements UsesExpressions, UsesParameters {
	protected ForEachLoop initElement(Expression col, OrdinaryParameter next) {
		var felInit = new ForEachLoopInitialiser();
		var fel = felInit.instantiate();
		Assertions.assertTrue(felInit.setCollection(fel, col));
		Assertions.assertTrue(felInit.setNext(fel, next));
		return fel;
	}

	@Test
	public void testCollection() {
		var objOne = this.initElement(this.createMinimalFalseEE(), null);
		var objTwo = this.initElement(this.createMinimalTrueNEE(), null);

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.FOR_EACH_LOOP__COLLECTION);
	}

	@Test
	public void testCollectionNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalFalseEE(), null), new ForEachLoopInitialiser(),
				false, StatementsPackage.Literals.FOR_EACH_LOOP__COLLECTION);
	}

	@Test
	public void testNext() {
		var objOne = this.initElement(null, this.createMinimalOrdParamWithClsTarget("param1", "cls1"));
		var objTwo = this.initElement(null, this.createMinimalOrdParamWithClsTarget("param2", "cls2"));

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.FOR_EACH_LOOP__NEXT);
	}

	@Test
	public void testNextNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createMinimalOrdParamWithClsTarget("param1", "cls1")),
				new ForEachLoopInitialiser(), false, StatementsPackage.Literals.FOR_EACH_LOOP__NEXT);
	}
}
