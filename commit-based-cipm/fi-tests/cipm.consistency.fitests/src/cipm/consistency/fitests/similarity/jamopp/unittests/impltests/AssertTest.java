package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class AssertTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> errorMessage1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> errorMessage2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testErrorMessage() {
		this.testSimilarity(getAPI().newAssert().withErrorMessage(errorMessage1.get()).createNow(),
				getAPI().newAssert().withErrorMessage(errorMessage2.get()).createNow(),
				StatementsPackage.Literals.ASSERT__ERROR_MESSAGE);
	}

	@Test
	public void testErrorMessageNullCheck() {
		this.testSimilarityNullCheck(getAPI().newAssert().withErrorMessage(errorMessage1.get()).createNow(),
				StatementsPackage.Literals.ASSERT__ERROR_MESSAGE);
	}
}
