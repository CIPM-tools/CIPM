package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class NumberLiteralNaNTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Double> nanDoubleValue = () -> Double.NaN;
	private final Supplier<Double> otherDoubleValue = () -> Double.valueOf(1);

	private final Supplier<Float> nanFloatValue = () -> Float.NaN;
	private final Supplier<Float> otherFloatValue = () -> Float.valueOf(1);

	@Test
	public void testDecimalDoubleLiteral_OneValueNaN() {
		this.testSimilarity(getAPI().newDecimalDoubleLiteral(nanDoubleValue.get()),
				getAPI().newDecimalDoubleLiteral(otherDoubleValue.get()),
				LiteralsPackage.Literals.DECIMAL_DOUBLE_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalDoubleLiteral_BothValuesNaN() {
		this.testSimilarity(getAPI().newDecimalDoubleLiteral(nanDoubleValue.get()),
				getAPI().newDecimalDoubleLiteral(nanDoubleValue.get()), Boolean.TRUE);
	}

	@Test
	public void testHexDoubleLiteral_OneValueNaN() {
		this.testSimilarity(getAPI().newHexDoubleLiteral(nanDoubleValue.get()),
				getAPI().newHexDoubleLiteral(otherDoubleValue.get()),
				LiteralsPackage.Literals.HEX_DOUBLE_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexDoubleLiteral_BothValuesNaN() {
		this.testSimilarity(getAPI().newHexDoubleLiteral(nanDoubleValue.get()),
				getAPI().newHexDoubleLiteral(nanDoubleValue.get()), Boolean.TRUE);
	}

	@Test
	public void testDecimalFloatLiteral_OneValueNaN() {
		this.testSimilarity(getAPI().newDecimalFloatLiteral(nanFloatValue.get()),
				getAPI().newDecimalFloatLiteral(otherFloatValue.get()),
				LiteralsPackage.Literals.DECIMAL_FLOAT_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalFloatLiteral_BothValuesNaN() {
		this.testSimilarity(getAPI().newDecimalFloatLiteral(nanFloatValue.get()),
				getAPI().newDecimalFloatLiteral(nanFloatValue.get()), Boolean.TRUE);
	}

	@Test
	public void testHexFloatLiteral_OneValueNaN() {
		this.testSimilarity(getAPI().newHexFloatLiteral(nanFloatValue.get()),
				getAPI().newHexFloatLiteral(otherFloatValue.get()),
				LiteralsPackage.Literals.HEX_FLOAT_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexFloatLiteral_BothValuesNaN() {
		this.testSimilarity(getAPI().newHexFloatLiteral(nanFloatValue.get()),
				getAPI().newHexFloatLiteral(nanFloatValue.get()), Boolean.TRUE);
	}
}
