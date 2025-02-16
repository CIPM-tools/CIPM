package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.LambdaBody;
import org.emftext.language.java.expressions.LambdaExpression;
import org.emftext.language.java.expressions.LambdaParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesLambdaParameters;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.expressions.LambdaExpressionInitialiser;

public class LambdaExpressionTest extends AbstractJaMoPPSimilarityTest implements UsesStatements, UsesLambdaParameters {
	protected LambdaExpression initElement(LambdaBody body, LambdaParameters param) {
		var init = new LambdaExpressionInitialiser();
		LambdaExpression result = init.instantiate();
		Assertions.assertTrue(init.setBody(result, body));
		Assertions.assertTrue(init.setParameters(result, param));
		return result;
	}

	@Test
	public void testBody() {
		var objOne = this.initElement(this.createMinimalBlockWithNullReturn(), null);
		var objTwo = this.initElement(this.createMinimalBlockWithTrivialAssert(), null);

		this.testSimilarity(objOne, objTwo, ExpressionsPackage.Literals.LAMBDA_EXPRESSION__BODY);
	}

	@Test
	public void testBodyNullCheck() {
		this.testSimilarityNullCheck(this.initElement(this.createMinimalBlockWithNullReturn(), null),
				new LambdaExpressionInitialiser(), false, ExpressionsPackage.Literals.LAMBDA_EXPRESSION__BODY);
	}

	@Test
	public void testParameters() {
		var objOne = this.initElement(null, this.createMinimalETLP("p1", "c1"));
		var objTwo = this.initElement(null, this.createMinimalETLP("p2", "c2"));

		this.testSimilarity(objOne, objTwo, ExpressionsPackage.Literals.LAMBDA_EXPRESSION__PARAMETERS);
	}

	@Test
	public void testParametersNullCheck() {
		this.testSimilarityNullCheck(this.initElement(null, this.createMinimalETLP("p1", "c1")),
				new LambdaExpressionInitialiser(), false, ExpressionsPackage.Literals.LAMBDA_EXPRESSION__PARAMETERS);
	}
}
