package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.arrays.ArraysPackage;
import org.emftext.language.java.expressions.Expression;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ArrayInstantiationBySizeTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> size1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> size2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testSize() {
		this.testSimilarity(getAPI().newArrayInstantiationBySize().withAddedSizes(size1.get()).createNow(),
				getAPI().newArrayInstantiationBySize().withAddedSizes(size2.get()).createNow(),
				ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_SIZE__SIZES);
	}

	@Test
	public void testSizeSize() {
		this.testSimilarity(
				getAPI().newArrayInstantiationBySize().withAddedSizes(new Expression[] { size1.get(), size2.get() }).createNow(),
				getAPI().newArrayInstantiationBySize().withAddedSizes(size1.get()).createNow(),
				ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_SIZE__SIZES);
	}

	@Test
	public void testSizeNullCheck() {
		this.testSimilarityNullCheck(getAPI().newArrayInstantiationBySize().withAddedSizes(size1.get()).createNow(),
				ArraysPackage.Literals.ARRAY_INSTANTIATION_BY_SIZE__SIZES);
	}
}
