package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.BinaryLongLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.BinaryLongLiteralInitialiser;

public class BinaryLongLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected BinaryLongLiteral initElement(long val) {
		var init = new BinaryLongLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setBinaryValue(lit, val));
		return lit;
	}

	@Test
	public void testBinaryValue() {
		this.testSimilarity(this.initElement(1), this.initElement(2),
				LiteralsPackage.Literals.BINARY_LONG_LITERAL__BINARY_VALUE);
	}

	@Test
	public void testBinaryValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement(1), new BinaryLongLiteralInitialiser(), false,
				LiteralsPackage.Literals.BINARY_LONG_LITERAL__BINARY_VALUE);
	}
}