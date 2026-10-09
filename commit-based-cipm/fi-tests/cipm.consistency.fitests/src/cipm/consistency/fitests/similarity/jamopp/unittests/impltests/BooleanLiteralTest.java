package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class BooleanLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Boolean> value1 = () -> Boolean.TRUE;
	private final Supplier<Boolean> value2 = () -> Boolean.FALSE;

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newBooleanLiteral(value1.get()), getAPI().newBooleanLiteral(value2.get()),
				LiteralsPackage.Literals.BOOLEAN_LITERAL__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newBooleanLiteral(value1.get()),
				LiteralsPackage.Literals.BOOLEAN_LITERAL__VALUE);
	}
}