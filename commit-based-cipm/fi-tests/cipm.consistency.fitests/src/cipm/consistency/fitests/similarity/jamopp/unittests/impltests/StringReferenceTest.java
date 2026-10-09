package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class StringReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<String> value1 = () -> "a";
	private final Supplier<String> value2 = () -> "b";

	@Test
	public void testValue() {
		this.testSimilarity(getAPI().newStringReference().withValue(value1.get()).createNow(),
				getAPI().newStringReference().withValue(value2.get()).createNow(),
				ReferencesPackage.Literals.STRING_REFERENCE__VALUE);
	}

	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newStringReference().withValue(value1.get()).createNow(),
				ReferencesPackage.Literals.STRING_REFERENCE__VALUE);
	}
}
