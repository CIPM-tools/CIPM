package cipm.consistency.fitests.similarity.jamopp.unittests.mocktests;

import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.BinaryIntegerLiteralInitialiser;

/**
 * Contains tests using mock objects, which check the robustness of similarity
 * checking for asymmetric {@code .equals(...)} implementations. <br>
 * <br>
 * Not parameterised due to a limitation of Mockito, which does not allow
 * {@code .equals(...)} to be mocked or altered via Mockito directly.
 * 
 * @author Alp Torac Genc
 */
public class AsymmetricEqualsMockTest extends AbstractJaMoPPSimilarityTest implements IMockTest {
	/**
	 * Ensures the robustness of similarity checking in the face of asymmetric
	 * {@code .equals(...)} overrides. <br>
	 * <br>
	 * Currently {@link BinaryIntegerLiteral} instances are used to test this, since
	 * the said method is called while comparing them. Another reason for this test
	 * being separate is that mocking {@code .equals(...)} is not allowed.
	 * Therefore, testing this case requires a more advanced setup. <br>
	 * <br>
	 * Even though this case is unrealistic and it showing up indicates a violation
	 * of the said method, it is used within similarity checking and is likely to be
	 * used in the future.
	 */
	@SuppressWarnings({ "unlikely-arg-type", "serial" })
	@Test
	public void testAsymmetricEqualsMethod() {
		var init = new BinaryIntegerLiteralInitialiser();
		var eqFalseWrapee = init.instantiate();
		var eqTrueWrapee = init.instantiate();

		/*
		 * Construct anonymous BigInteger extensions and override the equals method, in
		 * order to bypass the limitations of mocking.
		 */

		var eqFalseInt = new BigInteger(0, new byte[] { 0 }) {
			@Override
			public boolean equals(Object x) {
				return false;
			}
		};
		var eqTrueInt = new BigInteger(1, new byte[] { 1 }) {
			@Override
			public boolean equals(Object x) {
				return true;
			}
		};

		/*
		 * The equals method is set to return false/true no matter what is passed to it.
		 * Ensure that this is indeed the case.
		 */

		Assertions.assertFalse(eqFalseInt.equals(null));
		Assertions.assertFalse(eqFalseInt.equals(eqFalseInt));
		Assertions.assertFalse(eqFalseInt.equals(eqTrueInt));
		Assertions.assertTrue(eqTrueInt.equals(null));
		Assertions.assertTrue(eqTrueInt.equals(eqFalseInt));
		Assertions.assertTrue(eqTrueInt.equals(eqTrueInt));

		/*
		 * Change the return value of .getBinaryValue(), in order to reach the
		 * unrealistic case of .equals() being asymmetric.
		 */

		var eqFalse = this.spyEObject(eqFalseWrapee);
		when(eqFalse.getBinaryValue()).thenReturn(eqFalseInt);
		var eqTrue = this.spyEObject(eqTrueWrapee);
		when(eqTrue.getBinaryValue()).thenReturn(eqTrueInt);

		/*
		 * In similarity checking, both A.equals(B) and B.equals(A) are checked.
		 * Therefore, the presence of eqFalse will change the end result to false, no
		 * matter what.
		 */

		Assertions.assertFalse(this.isSimilar(eqFalse, eqFalse));
		Assertions.assertFalse(this.isSimilar(eqFalse, eqTrue));
		Assertions.assertFalse(this.isSimilar(eqTrue, eqFalse));
		Assertions.assertTrue(this.isSimilar(eqTrue, eqTrue));
	}
}
