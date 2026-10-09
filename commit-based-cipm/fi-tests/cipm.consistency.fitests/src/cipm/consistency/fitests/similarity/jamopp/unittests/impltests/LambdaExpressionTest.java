package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.ExpressionsPackage;
import org.emftext.language.java.expressions.LambdaBody;
import org.emftext.language.java.expressions.LambdaParameters;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class LambdaExpressionTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<LambdaBody> body1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<LambdaBody> body2 = () -> getAPI().newDecimalIntegerLiteral(2);

	private final Supplier<LambdaParameters> parameters1 = () -> getAPI()
			.newExplicitlyTypedLambdaParameters(getAPI().newOrdinaryParameter().withName("param1").createNow());
	private final Supplier<LambdaParameters> parameters2 = () -> getAPI()
			.newExplicitlyTypedLambdaParameters(getAPI().newOrdinaryParameter().withName("param2").createNow());

	@Test
	public void testBody() {
		this.testSimilarity(getAPI().newLambdaExpression().withBody(body1.get()).createNow(),
				getAPI().newLambdaExpression().withBody(body2.get()).createNow(),
				ExpressionsPackage.Literals.LAMBDA_EXPRESSION__BODY);
	}

	@Test
	public void testBodyNullCheck() {
		this.testSimilarityNullCheck(getAPI().newLambdaExpression().withBody(body1.get()).createNow(),
				ExpressionsPackage.Literals.LAMBDA_EXPRESSION__BODY);
	}

	@Test
	public void testParameters() {
		this.testSimilarity(getAPI().newLambdaExpression().withParameters(parameters1.get()).createNow(),
				getAPI().newLambdaExpression().withParameters(parameters2.get()).createNow(),
				ExpressionsPackage.Literals.LAMBDA_EXPRESSION__PARAMETERS);
	}

	@Test
	public void testParametersNullCheck() {
		this.testSimilarityNullCheck(getAPI().newLambdaExpression().withParameters(parameters1.get()).createNow(),
				ExpressionsPackage.Literals.LAMBDA_EXPRESSION__PARAMETERS);
	}
}
