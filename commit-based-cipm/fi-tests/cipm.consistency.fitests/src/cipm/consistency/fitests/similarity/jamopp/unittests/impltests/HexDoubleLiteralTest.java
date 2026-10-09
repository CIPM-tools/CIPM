package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class HexDoubleLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Double> hexValue1 = () -> Double.valueOf(1);
	private final Supplier<Double> hexValue2 = () -> Double.valueOf(2);

	@Test
	public void testHexValue() {
		this.testSimilarity(getAPI().newHexDoubleLiteral(hexValue1.get()),
				getAPI().newHexDoubleLiteral(hexValue2.get()), LiteralsPackage.Literals.HEX_DOUBLE_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newHexDoubleLiteral(hexValue1.get()),
				LiteralsPackage.Literals.HEX_DOUBLE_LITERAL__HEX_VALUE);
	}
}