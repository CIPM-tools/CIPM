package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class DecimalDoubleLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Double> decimalValue1 = () -> Double.valueOf(1);
	private final Supplier<Double> decimalValue2 = () -> Double.valueOf(2);

	@Test
	public void testDecimalValue() {
		this.testSimilarity(getAPI().newDecimalDoubleLiteral(decimalValue1.get()),
				getAPI().newDecimalDoubleLiteral(decimalValue2.get()),
				LiteralsPackage.Literals.DECIMAL_DOUBLE_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newDecimalDoubleLiteral(decimalValue1.get()),
				LiteralsPackage.Literals.DECIMAL_DOUBLE_LITERAL__DECIMAL_VALUE);
	}
}