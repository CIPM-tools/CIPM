package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class HexFloatLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Float> hexValue1 = () -> Float.valueOf(1);
	private final Supplier<Float> hexValue2 = () -> Float.valueOf(2);

	@Test
	public void testHexValue() {
		this.testSimilarity(getAPI().newHexFloatLiteral(hexValue1.get()), getAPI().newHexFloatLiteral(hexValue2.get()),
				LiteralsPackage.Literals.HEX_FLOAT_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newHexFloatLiteral(hexValue1.get()),
				LiteralsPackage.Literals.HEX_FLOAT_LITERAL__HEX_VALUE);
	}
}