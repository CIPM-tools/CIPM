package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class TextBlockReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<String> value1 = () -> "a";
	private final Supplier<String> value2 = () -> "b";

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newTextBlockReference().withValue(value1.get()).createNow(),
				getAPI().newTextBlockReference().withValue(value2.get()).createNow(),
				ReferencesPackage.Literals.TEXT_BLOCK_REFERENCE__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newTextBlockReference().withValue(value1.get()).createNow(),
				ReferencesPackage.Literals.TEXT_BLOCK_REFERENCE__VALUE);
	}
}
