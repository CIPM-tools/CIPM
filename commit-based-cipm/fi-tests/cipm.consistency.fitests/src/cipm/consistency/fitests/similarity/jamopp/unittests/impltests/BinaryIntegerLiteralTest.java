package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.math.BigInteger;
import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class BinaryIntegerLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<BigInteger> binaryValue1 = () -> BigInteger.ONE;
	private final Supplier<BigInteger> binaryValue2 = () -> BigInteger.TWO;

	@Test
	public void testBinaryValue() {
		this.testSimilarity(getAPI().newBinaryIntegerLiteral(binaryValue1.get()),
				getAPI().newBinaryIntegerLiteral(binaryValue2.get()),
				LiteralsPackage.Literals.BINARY_INTEGER_LITERAL__BINARY_VALUE);
	}

	@Test
	public void testBinaryValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newBinaryIntegerLiteral(binaryValue1.get()),
				LiteralsPackage.Literals.BINARY_INTEGER_LITERAL__BINARY_VALUE);
	}
}