package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class DecimalIntegerLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Integer> decimalValue1 = () -> Integer.valueOf(1);
	private final Supplier<Integer> decimalValue2 = () -> Integer.valueOf(2);

	@Test
	public void testDecimalValue() {
		this.testSimilarity(getAPI().newDecimalIntegerLiteral(decimalValue1.get()),
				getAPI().newDecimalIntegerLiteral(decimalValue2.get()),
				LiteralsPackage.Literals.DECIMAL_INTEGER_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newDecimalIntegerLiteral(decimalValue1.get()),
				LiteralsPackage.Literals.DECIMAL_INTEGER_LITERAL__DECIMAL_VALUE);
	}
}