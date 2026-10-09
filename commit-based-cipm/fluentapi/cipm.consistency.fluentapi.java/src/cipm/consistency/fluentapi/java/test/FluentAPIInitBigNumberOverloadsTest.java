package cipm.consistency.fluentapi.java.test;

import java.math.BigInteger;

import org.emftext.language.java.literals.DecimalIntegerLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class for checking whether fluent api model has overloading methods
 * for operations that consider {@link BigInteger} parameters and that they work
 * as intended.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIInitBigNumberOverloadsTest extends AbstractFluentAPITest {
	/**
	 * Ensures that overloading methods for BigInteger are generated
	 */
	@Test
	public void testInit_OverloadedBigIntegerMethodsTest_SingleValue() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		final var lit = new DecimalIntegerLiteral[3];

		BigInteger bigIntVal = BigInteger.valueOf(1);

		// Ensure that the original method for BigInteger is generated
		Assertions.assertDoesNotThrow(
				() -> lit[0] = api.newDecimalIntegerLiteral().withDecimalValue(bigIntVal).createNow());
		// Ensure that the original method for BigInteger works as intended
		Assertions.assertEquals(bigIntVal, lit[0].getDecimalValue());

		int intVal = bigIntVal.intValue();
		Assertions.assertEquals(1, intVal);
		// Ensure that the overloading method is generated (with int parameter)
		Assertions
				.assertDoesNotThrow(() -> lit[1] = api.newDecimalIntegerLiteral().withDecimalValue(intVal).createNow());
		// Ensure that the overloading method works as intended
		Assertions.assertEquals(intVal, lit[1].getDecimalValue().intValue());

		long longVal = bigIntVal.longValue();
		Assertions.assertEquals(1, longVal);
		// Ensure that the overloading method is generated (with long parameter)
		Assertions.assertDoesNotThrow(
				() -> lit[2] = api.newDecimalIntegerLiteral().withDecimalValue(longVal).createNow());
		// Ensure that the overloading method works as intended
		Assertions.assertEquals(longVal, lit[2].getDecimalValue().longValue());
	}

	/**
	 * Ensures that direct creation methods for types with only one modifiable
	 * features is possible via the generated API class
	 */
	@Test
	public void testInit_OverloadedBigIntegerMethodsTest_OnlyOneSingleValuedModifiableFeature() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		final var obj = new DecimalIntegerLiteral[2];

		int intVal = 1;
		// Ensure that the overloading method is generated (with int parameter)
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newDecimalIntegerLiteral(intVal));
		// Ensure that the overloading method works as intended
		Assertions.assertInstanceOf(DecimalIntegerLiteral.class, obj[0]);
		Assertions.assertEquals(intVal, obj[0].getDecimalValue().intValue());

		long longVal = 1;
		// Ensure that the overloading method is generated (with long parameter)
		Assertions.assertDoesNotThrow(() -> obj[1] = api.newDecimalIntegerLiteral(longVal));
		// Ensure that the overloading method works as intended
		Assertions.assertInstanceOf(DecimalIntegerLiteral.class, obj[1]);
		Assertions.assertEquals(longVal, obj[1].getDecimalValue().longValue());
	}
}
