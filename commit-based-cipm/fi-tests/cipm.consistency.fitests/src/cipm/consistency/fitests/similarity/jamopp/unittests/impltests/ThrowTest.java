package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ThrowTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> throwable1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> throwable2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testThrowable() {
		this.testSimilarity(getAPI().newThrow(throwable1.get()), getAPI().newThrow(throwable2.get()),
				StatementsPackage.Literals.THROW__THROWABLE);
	}

	@Test
	public void testThrowableNullCheck() {
		this.testSimilarityNullCheck(getAPI().newThrow(throwable1.get()), StatementsPackage.Literals.THROW__THROWABLE);
	}
}
