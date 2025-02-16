package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.HexIntegerLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.HexIntegerLiteralInitialiser;

public class HexIntegerLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected HexIntegerLiteral initElement(int val) {
		var init = new HexIntegerLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setHexValue(lit, val));
		return lit;
	}

	@Test
	public void testHexValue() {
		this.testSimilarity(this.initElement(1), this.initElement(2),
				LiteralsPackage.Literals.HEX_INTEGER_LITERAL__HEX_VALUE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1), new HexIntegerLiteralInitialiser(), false,
				LiteralsPackage.Literals.HEX_INTEGER_LITERAL__HEX_VALUE);
	}
}