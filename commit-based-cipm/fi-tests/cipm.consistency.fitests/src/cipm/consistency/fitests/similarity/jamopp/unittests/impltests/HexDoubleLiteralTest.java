package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.HexDoubleLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.HexDoubleLiteralInitialiser;

public class HexDoubleLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected HexDoubleLiteral initElement(double val) {
		var init = new HexDoubleLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setHexValue(lit, val));
		return lit;
	}

	@Test
	public void testHexValue() {
		this.testSimilarity(this.initElement(1.5d), this.initElement(2.5d),
				LiteralsPackage.Literals.HEX_DOUBLE_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1.5d), new HexDoubleLiteralInitialiser(), false,
				LiteralsPackage.Literals.HEX_DOUBLE_LITERAL__HEX_VALUE);
	}

	@Test
	public void testHexValueNaN() {
		this.testSimilarity(this.initElement(Double.NaN), this.initElement(1.5d),
				LiteralsPackage.Literals.HEX_DOUBLE_LITERAL__HEX_VALUE);
	}
}