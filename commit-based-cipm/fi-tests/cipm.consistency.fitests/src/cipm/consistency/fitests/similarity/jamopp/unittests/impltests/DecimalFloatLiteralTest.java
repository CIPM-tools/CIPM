package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class DecimalFloatLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Float> decimalValue1 = () -> Float.valueOf(1);
	private final Supplier<Float> decimalValue2 = () -> Float.valueOf(2);

	@Test
	public void testDecimalValue() {
		this.testSimilarity(getAPI().newDecimalFloatLiteral(decimalValue1.get()),
				getAPI().newDecimalFloatLiteral(decimalValue2.get()),
				LiteralsPackage.Literals.DECIMAL_FLOAT_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newDecimalFloatLiteral(decimalValue1.get()),
				LiteralsPackage.Literals.DECIMAL_FLOAT_LITERAL__DECIMAL_VALUE);
	}
}