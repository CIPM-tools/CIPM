package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class HexIntegerLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Integer> hexValue1 = () -> Integer.valueOf(1);
	private final Supplier<Integer> hexValue2 = () -> Integer.valueOf(2);

	@Test
	public void testHexValue() {
		this.testSimilarity(getAPI().newHexIntegerLiteral(hexValue1.get()),
				getAPI().newHexIntegerLiteral(hexValue2.get()),
				LiteralsPackage.Literals.HEX_INTEGER_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newHexIntegerLiteral(hexValue1.get()),
				LiteralsPackage.Literals.HEX_INTEGER_LITERAL__HEX_VALUE);
	}
}