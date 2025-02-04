package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.DecimalDoubleLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.DecimalDoubleLiteralInitialiser;

public class DecimalDoubleLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected DecimalDoubleLiteral initElement(double val) {
		var init = new DecimalDoubleLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setDecimalValue(lit, val));
		return lit;
	}

	@Test
	public void testDecimalValue() {
		this.testSimilarity(this.initElement(1.5d), this.initElement(2.5d),
				LiteralsPackage.Literals.DECIMAL_DOUBLE_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1.5d), new DecimalDoubleLiteralInitialiser(), false,
				LiteralsPackage.Literals.DECIMAL_DOUBLE_LITERAL__DECIMAL_VALUE);
	}

	@Test
	public void testDecimalValueNaN() {
		this.testSimilarity(this.initElement(Double.NaN), this.initElement(1.5d),
				LiteralsPackage.Literals.DECIMAL_DOUBLE_LITERAL__DECIMAL_VALUE);
	}
}