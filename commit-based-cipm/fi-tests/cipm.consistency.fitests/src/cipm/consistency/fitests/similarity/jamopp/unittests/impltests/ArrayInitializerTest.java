package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.arrays.ArrayInitializationValue;
import org.emftext.language.java.arrays.ArraysPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ArrayInitializerTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ArrayInitializationValue> initialValue1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<ArrayInitializationValue> initialValue2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testInitialValues() {
		this.testSimilarity(getAPI().newArrayInitializer(initialValue1.get()), getAPI().newArrayInitializer(initialValue2.get()),
				ArraysPackage.Literals.ARRAY_INITIALIZER__INITIAL_VALUES);
	}

	@Test
	public void testInitialValuesSize() {
		this.testSimilarity(getAPI().newArrayInitializer(new ArrayInitializationValue[] { initialValue1.get(), initialValue2.get() }),
				getAPI().newArrayInitializer(new ArrayInitializationValue[] { initialValue1.get() }),
				ArraysPackage.Literals.ARRAY_INITIALIZER__INITIAL_VALUES);
	}

	@Test
	public void testInitialValuesNullCheck() {
		this.testSimilarityNullCheck(getAPI().newArrayInitializer(initialValue1.get()),
				ArraysPackage.Literals.ARRAY_INITIALIZER__INITIAL_VALUES);
	}
}
