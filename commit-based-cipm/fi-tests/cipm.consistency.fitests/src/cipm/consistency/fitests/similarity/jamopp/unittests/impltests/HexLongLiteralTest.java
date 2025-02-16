package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.HexLongLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.HexLongLiteralInitialiser;

public class HexLongLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected HexLongLiteral initElement(long val) {
		var init = new HexLongLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setHexValue(lit, val));
		return lit;
	}

	@Test
	public void testHexValue() {
		this.testSimilarity(this.initElement(1), this.initElement(2),
				LiteralsPackage.Literals.HEX_LONG_LITERAL__HEX_VALUE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1), new HexLongLiteralInitialiser(), false,
				LiteralsPackage.Literals.HEX_LONG_LITERAL__HEX_VALUE);
	}
}