package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class OctalIntegerLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Integer> octalValue1 = () -> Integer.valueOf(1);
	private final Supplier<Integer> octalValue2 = () -> Integer.valueOf(2);

	@Test
	public void testOctalValue() {
		this.testSimilarity(getAPI().newOctalIntegerLiteral(octalValue1.get()),
				getAPI().newOctalIntegerLiteral(octalValue2.get()),
				LiteralsPackage.Literals.OCTAL_INTEGER_LITERAL__OCTAL_VALUE);
	}

	@Test
	public void testOctalValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newOctalIntegerLiteral(octalValue1.get()),
				LiteralsPackage.Literals.OCTAL_INTEGER_LITERAL__OCTAL_VALUE);
	}
}