package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ReturnTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> returnValue1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> returnValue2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testReturnValue() {
		this.testSimilarity(getAPI().newReturn(returnValue1.get()), getAPI().newReturn(returnValue2.get()),
				StatementsPackage.Literals.RETURN__RETURN_VALUE);
	}

	@Test
	public void testReturnValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newReturn(returnValue1.get()),
				StatementsPackage.Literals.RETURN__RETURN_VALUE);
	}
}
