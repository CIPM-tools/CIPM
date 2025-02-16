package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.BinaryIntegerLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.BinaryIntegerLiteralInitialiser;

public class BinaryIntegerLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected BinaryIntegerLiteral initElement(int val) {
		var init = new BinaryIntegerLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setBinaryValue(lit, val));
		return lit;
	}

	@Test
	public void testBinaryValue() {
		this.testSimilarity(this.initElement(1), this.initElement(2),
				LiteralsPackage.Literals.BINARY_INTEGER_LITERAL__BINARY_VALUE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testBinaryValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1), new BinaryIntegerLiteralInitialiser(), false,
				LiteralsPackage.Literals.BINARY_INTEGER_LITERAL__BINARY_VALUE);
	}
}