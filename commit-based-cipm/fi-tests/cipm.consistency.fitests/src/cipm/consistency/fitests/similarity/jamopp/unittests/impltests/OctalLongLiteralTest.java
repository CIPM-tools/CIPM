package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class OctalLongLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Long> octalValue1 = () -> Long.valueOf(1);
	private final Supplier<Long> octalValue2 = () -> Long.valueOf(2);

	@Test
	public void testOctalValue() {
		this.testSimilarity(getAPI().newOctalLongLiteral(octalValue1.get()),
				getAPI().newOctalLongLiteral(octalValue2.get()),
				LiteralsPackage.Literals.OCTAL_LONG_LITERAL__OCTAL_VALUE);
	}

	@Test
	public void testOctalValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newOctalLongLiteral(octalValue1.get()),
				LiteralsPackage.Literals.OCTAL_LONG_LITERAL__OCTAL_VALUE);
	}
}