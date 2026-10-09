package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class HexLongLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Long> hexValue1 = () -> Long.valueOf(1);
	private final Supplier<Long> hexValue2 = () -> Long.valueOf(2);

	@Test
	public void testHexValue() {
		this.testSimilarity(getAPI().newHexLongLiteral(hexValue1.get()), getAPI().newHexLongLiteral(hexValue2.get()),
				LiteralsPackage.Literals.HEX_LONG_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newHexLongLiteral(hexValue1.get()),
				LiteralsPackage.Literals.HEX_LONG_LITERAL__HEX_VALUE);
	}
}