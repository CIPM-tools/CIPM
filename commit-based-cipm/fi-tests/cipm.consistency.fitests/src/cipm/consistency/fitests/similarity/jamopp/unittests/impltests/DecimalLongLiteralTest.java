package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.DecimalLongLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.DecimalLongLiteralInitialiser;

public class DecimalLongLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected DecimalLongLiteral initElement(long val) {
		var init = new DecimalLongLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setDecimalValue(lit, val));
		return lit;
	}

	@Test
	public void testDecimalValue() {
		this.testSimilarity(this.initElement(1), this.initElement(2),
				LiteralsPackage.Literals.DECIMAL_LONG_LITERAL__DECIMAL_VALUE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testDecimalValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1), new DecimalLongLiteralInitialiser(), false,
				LiteralsPackage.Literals.DECIMAL_LONG_LITERAL__DECIMAL_VALUE);
	}
}