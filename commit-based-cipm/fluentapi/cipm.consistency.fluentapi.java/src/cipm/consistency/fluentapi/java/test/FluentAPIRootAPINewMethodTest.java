package cipm.consistency.fluentapi.java.test;

import java.math.BigInteger;
import java.util.List;

import org.emftext.language.java.containers.ContainersPackage;
import org.emftext.language.java.expressions.AndExpression;
import org.emftext.language.java.expressions.AndExpressionChild;
import org.emftext.language.java.literals.DecimalIntegerLiteral;
import org.emftext.language.java.modifiers.Abstract;
import org.emftext.language.java.statements.Break;
import org.emftext.language.java.variables.AdditionalLocalVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.java.api.FluentAPISuperInitialisation;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class for the following methods of fluent api class: new...(), newX(),
 * createNew...(), createNewX().
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIRootAPINewMethodTest extends AbstractFluentAPITest {
	/**
	 * Ensures that direct creation methods for types with no modifiable features is
	 * possible via the generated API
	 */
	@Test
	public void testAPI_New_NoModifiableFeatures() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		final var obj = new Abstract[1];
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newAbstract());
		Assertions.assertInstanceOf(Abstract.class, obj[0]);
	}

	/**
	 * Ensures that direct creation methods for types with only one modifiable
	 * features is possible via the generated API class
	 */
	@Test
	public void testAPI_New_OnlyOneSingleValuedModifiableFeature_EAttribute() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var val = BigInteger.valueOf(1);
		final var obj = new DecimalIntegerLiteral[1];
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newDecimalIntegerLiteral(val));
		Assertions.assertInstanceOf(DecimalIntegerLiteral.class, obj[0]);
		Assertions.assertEquals(val, obj[0].getDecimalValue());
	}

	/**
	 * Ensures that direct creation methods for types with only one modifiable
	 * features is possible via the generated API class
	 */
	@Test
	public void testAPI_New_OnlyOneSingleValuedModifiableFeature_EReference() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var val = api.createNewJumpLabel();
		final var obj = new Break[1];
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newBreak(val));
		Assertions.assertInstanceOf(Break.class, obj[0]);
		Assertions.assertSame(val, obj[0].getTarget());
	}

	/**
	 * Ensures that direct creation methods for types with only one modifiable
	 * features is possible via the generated API class
	 */
	@Test
	public void testAPI_New_OnlyOneManyValuedModifiableFeature_EReference_SingleValue() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var val = api.createNewEqualityExpression();
		final var obj = new AndExpression[1];
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newAndExpression(val));
		Assertions.assertInstanceOf(AndExpression.class, obj[0]);
		Assertions.assertSame(val, obj[0].getChildren().get(0));
	}

	/**
	 * Ensures that direct creation methods for types with only one modifiable
	 * features is possible via the generated API class
	 */
	@Test
	public void testAPI_New_OnlyOneManyValuedModifiableFeature_EReference_Array() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var val = new AndExpressionChild[] { api.createNewEqualityExpression(), api.createNewEqualityExpression() };
		final var obj = new AndExpression[1];
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newAndExpression(val));
		Assertions.assertInstanceOf(AndExpression.class, obj[0]);
		Assertions.assertSame(val[0], obj[0].getChildren().get(0));
		Assertions.assertSame(val[1], obj[0].getChildren().get(1));
	}

	/**
	 * Ensures that direct creation methods for types with only one modifiable
	 * features is possible via the generated API class
	 */
	@Test
	public void testAPI_New_OnlyOneManyValuedModifiableFeature_EReference_Collection() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var val = List.of(api.createNewEqualityExpression(), api.createNewEqualityExpression());
		final var obj = new AndExpression[1];
		Assertions.assertDoesNotThrow(() -> obj[0] = api.newAndExpression(val));
		Assertions.assertInstanceOf(AndExpression.class, obj[0]);
		Assertions.assertSame(val.get(0), obj[0].getChildren().get(0));
		Assertions.assertSame(val.get(1), obj[0].getChildren().get(1));
	}

	/**
	 * Ensures that instantiation of types with multiple modifiable features is
	 * possible via Initialisation classes.
	 */
	@Test
	public void testAPI_New_MultipleModifiableFeatures() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var init = api.newAdditionalLocalVariable();
		Assertions.assertInstanceOf(FluentAPISuperInitialisation.class, init);
		Assertions.assertInstanceOf(AdditionalLocalVariable.class, init.createNow());
	}

	/**
	 * Ensures that api.newX(EClass) works as intended.
	 */
	@Test
	public void testAPI_NewX_WithEClass() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ContainersPackage.Literals.MODULE.getInstanceClass();
		var mod = api.newX(cls).createNow();
		Assertions.assertInstanceOf(cls, mod);
	}

	/**
	 * Ensures that api.newX(class) works as intended.
	 */
	@Test
	public void testAPI_NewX_WithClass() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ContainersPackage.Literals.MODULE.getInstanceClass();
		var mod = api.newX(cls).createNow();
		Assertions.assertInstanceOf(cls, mod);
	}

	/**
	 * Ensures that api.createNewX(class) works as intended.
	 */
	@Test
	public void testAPI_CreateNewX() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ContainersPackage.Literals.MODULE.getInstanceClass();
		var mod = api.createNewX(cls);
		Assertions.assertInstanceOf(cls, mod);
	}

	/**
	 * Ensures that api.createNewX() works as intended, where X is the type of the
	 * model element to create.
	 */
	@Test
	public void testAPI_CreateNew() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var mod = api.createNewModule();
		Assertions.assertInstanceOf(org.emftext.language.java.containers.Module.class, mod);
	}
}
