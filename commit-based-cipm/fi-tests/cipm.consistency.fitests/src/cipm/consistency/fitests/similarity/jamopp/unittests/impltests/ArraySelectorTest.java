package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.arrays.ArraysPackage;
import org.emftext.language.java.expressions.Expression;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class ArraySelectorTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Expression> position1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<Expression> position2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testPosition() {
		this.testSimilarity(getAPI().newArraySelector().withPosition(position1.get()).createNow(),
				getAPI().newArraySelector().withPosition(position2.get()).createNow(),
				ArraysPackage.Literals.ARRAY_SELECTOR__POSITION);
	}

	@Test
	public void testPositionNullCheck() {
		this.testSimilarityNullCheck(getAPI().newArraySelector().withPosition(position1.get()).createNow(),
				ArraysPackage.Literals.ARRAY_SELECTOR__POSITION);
	}
}
