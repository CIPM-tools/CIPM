package cipm.consistency.fluentapi.java.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * Tests the construction of models, where model elements of the same type are
 * nested within one another. Also tests construction of model elements with
 * multiple EReferences using values of the same type.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIContainmentTest extends AbstractFluentAPITest {
	/**
	 * Tests whether model elements of the same type can be successfully nested in
	 * one another.
	 * <p>
	 * <p>
	 * Ensures that the following construction is possible and works as intended:
	 * 
	 * class outer { class inner {} }
	 */
	@Test
	public void testNesting() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var innerClsName = "inner";
		var outerClsName = "outer";

		var innerCls = api.newClass().withName(innerClsName).createNow();
		var outerCls = api.newClass().withName(outerClsName).withAddedMembers(innerCls).createNow();

		Assertions.assertEquals(1, outerCls.getMembers().size());
		Assertions.assertEquals(innerCls, outerCls.getMembers().get(0));
		Assertions.assertEquals(0, outerCls.getDefaultMembers().size());
		Assertions.assertNull(outerCls.eContainer());

		Assertions.assertEquals(0, innerCls.getMembers().size());
		Assertions.assertEquals(0, innerCls.getDefaultMembers().size());
		Assertions.assertEquals(outerCls, innerCls.eContainer());
	}

	/**
	 * Tests whether it is possible to set the features of model elements to other
	 * (nested) model elements of the same type.
	 * <p>
	 * <p>
	 * Ensures that the following construction is possible and works as intended:
	 * 
	 * class cls1 { class cls2 extends cls1 {} }
	 */
	@Test
	public void testNestingAndReferencing() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var outerClsName = "outer";
		var innerClsName = "inner";

		var outerCls = api.newClass().withName(outerClsName).createNow();
		var innerCls = api.newClass().withName(innerClsName)
				.withExtends(api.newClassifierReference().withTarget(outerCls).createNow()).createNow();
		api.modifyClass(outerCls).withAddedMembers(innerCls).dropInitialisation();

		Assertions.assertEquals(1, outerCls.getMembers().size());
		Assertions.assertEquals(innerCls, outerCls.getMembers().get(0));
		Assertions.assertEquals(0, outerCls.getDefaultMembers().size());
		Assertions.assertNull(outerCls.eContainer());

		Assertions.assertEquals(0, innerCls.getMembers().size());
		Assertions.assertEquals(0, innerCls.getDefaultMembers().size());
		Assertions.assertEquals(outerCls, innerCls.getExtends().getPureClassifierReference().getTarget());
		Assertions.assertEquals(outerCls, innerCls.eContainer());
	}

	/**
	 * Tests whether fluent api sets the value of the correct feature of the model
	 * element.
	 * <p>
	 * <p>
	 * Ensures setting values EReferences using the same value type works as
	 * intended, for instance:
	 * 
	 * <p>
	 * PrimitiveTypeReference has the EReferences ArrayDimensionsBefore (ADB) and
	 * ArrayDimensionsAfter (ADA), which consider ArrayDimension instances AD1 and
	 * AD2. Assuming AD1 and AD2 are separate ArrayDimension instances, setting ADB
	 * = AD1 and ADA = AD2 via Fluent API should not mix up ADB and ADA.
	 */
	@Test
	public void testTwoContainmentFeaturesWithSameType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var arrDimBefore = api.newArrayDimension().createNow();
		var arrDimAfter = api.newArrayDimension().createNow();
		var ptr = api.newPrimitiveTypeReference().withAddedArrayDimensionsBefore(arrDimBefore)
				.withAddedArrayDimensionsAfter(arrDimAfter).createNow();

		Assertions.assertEquals(1, ptr.getArrayDimensionsBefore().size());
		Assertions.assertEquals(arrDimBefore, ptr.getArrayDimensionsBefore().get(0));
		Assertions.assertEquals(ptr, arrDimBefore.eContainer());

		Assertions.assertEquals(1, ptr.getArrayDimensionsAfter().size());
		Assertions.assertEquals(arrDimAfter, ptr.getArrayDimensionsAfter().get(0));
		Assertions.assertEquals(ptr, arrDimAfter.eContainer());
	}
}
