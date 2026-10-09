package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class DecimalLongLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Long> decimalValue1 = () -> Long.valueOf(1);
	private final Supplier<Long> decimalValue2 = () -> Long.valueOf(2);

	@Test
	public void testDecimalValue() {
		this.testSimilarity(getAPI().newDecimalLongLiteral(decimalValue1.get()),
				getAPI().newDecimalLongLiteral(decimalValue2.get()),
				LiteralsPackage.Literals.DECIMAL_LONG_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newDecimalLongLiteral(decimalValue1.get()),
				LiteralsPackage.Literals.DECIMAL_LONG_LITERAL__DECIMAL_VALUE);
	}
}