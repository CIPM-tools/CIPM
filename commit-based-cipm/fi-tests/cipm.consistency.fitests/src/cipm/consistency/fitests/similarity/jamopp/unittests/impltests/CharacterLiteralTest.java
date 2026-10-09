package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class CharacterLiteralTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<String> value1 = () -> "a";
	private final Supplier<String> value2 = () -> "b";

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newCharacterLiteral(value1.get()), getAPI().newCharacterLiteral(value2.get()),
				LiteralsPackage.Literals.CHARACTER_LITERAL__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newCharacterLiteral(value1.get()),
				LiteralsPackage.Literals.CHARACTER_LITERAL__VALUE);
	}
}