package cipm.consistency.initialisers.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.DummyModifiableObjInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.IDummyInitialiserWithoutMethods;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjFour;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjFourInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjOne;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjOneInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjThree;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjThreeInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjTwo;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.DummyObjTwoInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.IDummyObjFourInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.IDummyObjOneInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.IDummyObjThreeInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.flathierarchy.IDummyObjTwoInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyAlternateInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyNonTerminalObj;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyNonTerminalObjInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyTerminalObj;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyTerminalObjInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyTopLevelObj;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.DummyTopLevelObjInitialiser;
import cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy.IDummyAlternateInitialiser;

/**
 * Contains tests for (static/default) methods implemented in
 * {@link IInitialiser}
 * 
 * @author Alp Torac Genc
 */
public class IInitialiserTests {
	/**
	 * Ensures that
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * throws null pointer exceptions if the object to be modified is null.
	 */
	@Test
	public void test_doMultipleModifications_NullObject() {
		var init = new DummyModifiableObjInitialiser();
		Assertions.assertThrows(NullPointerException.class, () -> {
			init.addAttrs(null, new Object[] { 1 });
		});
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * throws exceptions, if null is passed as array of modification parameters.
	 * Also ensures that true is returned.
	 */
	@Test
	public void test_doMultipleModifications_NullModificationParams() {
		var init = new DummyModifiableObjInitialiser();
		Assertions.assertTrue(init.addAttrs(init.instantiate(), null));
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * throws exceptions, if array of modification parameters is empty. Also ensures
	 * that true is returned.
	 */
	@Test
	public void test_doMultipleModifications_EmptyModificationParams() {
		var init = new DummyModifiableObjInitialiser();
		Assertions.assertTrue(init.addAttrs(init.instantiate(), new Object[] {}));
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * works for 1 proper modification parameter (passed via an array of size 1).
	 */
	@Test
	public void test_doMultipleModifications_SingleModification_Success() {
		var init = new DummyModifiableObjInitialiser();
		Assertions.assertTrue(init.addAttrs(init.instantiate(), new Object[] { 1 }));
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * works for 1 inappropriate modification parameter (passed via an array of size
	 * 1).
	 */
	@Test
	public void test_doMultipleModifications_SingleModification_Failure() {
		var init = new DummyModifiableObjInitialiser();
		Assertions.assertFalse(init.addAttrs(init.instantiate(), new Object[] { null }));
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * works for its intended use case (i.e. summarising multiple modification
	 * method calls) with proper parameters.
	 */
	@Test
	public void test_doMultipleModifications_MultipleModifications_Success() {
		var init = new DummyModifiableObjInitialiser();
		Assertions.assertTrue(init.addAttrs(init.instantiate(), new Object[] { 1, 2, 3 }));
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * successfully performs modifications till it fails.
	 */
	@Test
	public void test_doMultipleModifications_MultipleModifications_LateFailure() {
		var init = new DummyModifiableObjInitialiser();
		var obj = init.instantiate();
		var attr1 = Integer.valueOf(1);
		var attr2 = Integer.valueOf(2);

		Assertions.assertFalse(init.addAttrs(obj, new Object[] { attr1, attr2, attr1 }));
		var attrs = obj.getAttrs();
		Assertions.assertTrue(attrs.contains(attr1));
		Assertions.assertTrue(attrs.contains(attr2));
		Assertions.assertEquals(2, attrs.size());
	}

	/**
	 * Check whether
	 * {@link IInitialiser#doMultipleModifications(Object, Object[], java.util.function.BiFunction)}
	 * fails early, if a modification fails, which is neither performed at the start
	 * nor at the end.
	 */
	@Test
	public void test_doMultipleModifications_MultipleModifications_FailEarly() {
		var init = new DummyModifiableObjInitialiser();
		var obj = init.instantiate();
		var attr1 = Integer.valueOf(1);
		var attr2 = Integer.valueOf(2);

		Assertions.assertFalse(init.addAttrs(obj, new Object[] { attr1, attr1, attr2 }));
		var attrs = obj.getAttrs();
		Assertions.assertTrue(attrs.contains(attr1));
		Assertions.assertFalse(attrs.contains(attr2));
		Assertions.assertEquals(1, attrs.size());
	}

	/**
	 * Checks whether {@link IInitialiser#declaresModificationMethods(Class)}
	 * circumvents null pointer exceptions.
	 */
	@Test
	public void test_DeclaresModificationMethods_NullClass() {
		Class<? extends IInitialiser> cls = null;
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(cls));
	}

	/**
	 * Checks whether {@link IInitialiser#declaresModificationMethods(IInitialiser)}
	 * circumvents null pointer exceptions.
	 */
	@Test
	public void test_DeclaresModificationMethods_NullInitialiser() {
		IInitialiser init = null;
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(init));
	}

	/**
	 * Check whether a modification method implemented only in the interface of an
	 * initialiser can be found.
	 */
	@Test
	public void test_DeclaresModificationMethods_InInterface() {
		var init = new DummyObjOneInitialiser();
		Assertions.assertFalse(init.declaresModificationMethods());
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(init));
		Assertions.assertTrue(IInitialiser.declaresModificationMethods(IDummyObjOneInitialiser.class));
	}

	/**
	 * Check whether a modification method implemented only in the concrete class of
	 * an initialiser can be found.
	 */
	@Test
	public void test_DeclaresModificationMethods_InClass() {
		var init = new DummyObjTwoInitialiser();
		Assertions.assertTrue(init.declaresModificationMethods());
		Assertions.assertTrue(IInitialiser.declaresModificationMethods(init));
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(IDummyObjTwoInitialiser.class));
	}

	/**
	 * Check whether initialiser types with neither direct nor overridden methods
	 * cause exceptions.
	 */
	@Test
	public void test_DeclaresModificationMethods_NoMethods() {
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(IDummyInitialiserWithoutMethods.class));
	}

	/**
	 * Check whether initialisers with only an overridden instantiate() method are
	 * detected as initialisers without modification methods.
	 */
	@Test
	public void test_DeclaresModificationMethods_NoAdditionalMethods() {
		var init = new DummyObjThreeInitialiser();
		Assertions.assertFalse(init.declaresModificationMethods());
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(init));
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(IDummyObjThreeInitialiser.class));
	}

	/**
	 * Check whether initialisers with no additional modification methods can be
	 * detected, even if they do have other methods.
	 */
	@Test
	public void test_DeclaresModificationMethods_NoModificationMethods() {
		var init = new DummyObjFourInitialiser();
		Assertions.assertFalse(init.declaresModificationMethods());
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(init));
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(IDummyObjFourInitialiser.class));
	}

	/**
	 * Check whether initialisers with no direct modification methods are detected,
	 * i.e. initialisers that do inherit modification methods yet do not implement
	 * any in their own body.
	 */
	@Test
	public void test_DeclaresModificationMethods_NoDirectModificationMethods() {
		var init = new DummyAlternateInitialiser();
		Assertions.assertFalse(init.declaresModificationMethods());
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(init));
		Assertions.assertFalse(IInitialiser.declaresModificationMethods(IDummyAlternateInitialiser.class));
	}

	/**
	 * Check whether initialiser types with neither direct nor overridden methods
	 * cause exceptions.
	 */
	@Test
	public void test_IsInitialiserFor_NoMethods() {
		Assertions.assertFalse(
				IInitialiser.isInitialiserFor(IDummyInitialiserWithoutMethods.class, DummyNonTerminalObj.class));
		Assertions.assertFalse(IInitialiser.isInitialiserFor(IDummyInitialiserWithoutMethods.class, null));
	}

	/**
	 * Check whether an initialiser, whose name does not contain the name of the
	 * object class it is supposed to instantiate, is still detected as its
	 * initialiser, if it has a proper instantiation method.
	 */
	@Test
	public void test_IsInitialiserFor_NameMismatch() {
		Assertions.assertTrue(new DummyAlternateInitialiser().isInitialiserFor(DummyNonTerminalObj.class));
		Assertions
				.assertTrue(IInitialiser.isInitialiserFor(IDummyAlternateInitialiser.class, DummyNonTerminalObj.class));
	}

	/**
	 * Checks whether {@link IInitialiser#isInitialiserFor(Class, Class)}
	 * circumvents null pointer exceptions, which could be caused by the first
	 * parameter being null.
	 */
	@Test
	public void test_IsInitialiserFor_NullInitialiserClass() {
		Class<? extends IInitialiser> initCls = null;
		Assertions.assertFalse(IInitialiser.isInitialiserFor(initCls, DummyNonTerminalObj.class));
	}

	/**
	 * Checks whether {@link IInitialiser#isInitialiserFor(IInitialiser, Class)}
	 * circumvents null pointer exceptions, which could be caused by the first
	 * parameter being null.
	 */
	@Test
	public void test_IsInitialiserFor_NullInitialiser() {
		IInitialiser init = null;
		Assertions.assertFalse(IInitialiser.isInitialiserFor(init, DummyNonTerminalObj.class));
	}

	/**
	 * Checks whether isInitialiserFor methods circumvent null pointer exceptions,
	 * which could be caused by the class object parameter being null.
	 */
	@Test
	public void test_IsInitialiserFor_NullClass() {
		Class<?> cls = null;

		Assertions.assertFalse(IInitialiser.isInitialiserFor(new DummyObjOneInitialiser(), cls));
		Assertions.assertFalse(IInitialiser.isInitialiserFor(DummyObjOneInitialiser.class, cls));
		Assertions.assertFalse(new DummyObjOneInitialiser().isInitialiserFor(cls));
	}

	/**
	 * Check whether initialisers that are meant to instantiate certain objects can
	 * be detected, where the said objects (and their initialisers) have no type
	 * hierarchy.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_IsInitialiserFor_NoTypeHierarchy() {
		var objClss = new Class[] { DummyObjOne.class, DummyObjTwo.class, DummyObjThree.class, DummyObjFour.class };
		var initInterfaceClss = new Class[] { IDummyObjOneInitialiser.class, IDummyObjTwoInitialiser.class,
				IDummyObjThreeInitialiser.class, IDummyObjFourInitialiser.class };
		var initClss = new Class[] { DummyObjOneInitialiser.class, DummyObjTwoInitialiser.class,
				DummyObjThreeInitialiser.class, DummyObjFourInitialiser.class };
		var inits = new IInitialiser[] { new DummyObjOneInitialiser(), new DummyObjTwoInitialiser(),
				new DummyObjThreeInitialiser(), new DummyObjFourInitialiser() };

		// Test over all the classes given above, in order to make sure that
		// other methods implemented in the classes/interfaces do not cause issues
		for (int i = 0; i < objClss.length; i++) {
			var cInitInterfaceCls = initInterfaceClss[i];
			var cInitClss = initClss[i];
			var cInit = inits[i];

			for (int j = 0; j < objClss.length; j++) {
				var cObjCls = objClss[j];

				Assertions.assertEquals(i == j, cInit.isInitialiserFor(cObjCls));
				Assertions.assertEquals(i == j, IInitialiser.isInitialiserFor(cInit, cObjCls));
				Assertions.assertEquals(i == j, IInitialiser.isInitialiserFor(cInitClss, cObjCls));
				Assertions.assertEquals(i == j, IInitialiser.isInitialiserFor(cInitInterfaceCls, cObjCls));
			}
		}
	}

	/**
	 * Check whether initialisers within a type hierarchy are correctly matched to
	 * the object class they are supposed to instantiate.
	 */
	@Test
	public void test_IsInitialiserFor_WithTypeHierarchy() {
		var tlInit = new DummyTopLevelObjInitialiser();
		var ntInit = new DummyNonTerminalObjInitialiser();
		var tInit = new DummyTerminalObjInitialiser();

		Assertions.assertFalse(tlInit.isInitialiserFor(DummyTerminalObj.class));
		Assertions.assertFalse(tlInit.isInitialiserFor(DummyNonTerminalObj.class));
		Assertions.assertTrue(tlInit.isInitialiserFor(DummyTopLevelObj.class));

		Assertions.assertFalse(ntInit.isInitialiserFor(DummyTerminalObj.class));
		Assertions.assertTrue(ntInit.isInitialiserFor(DummyNonTerminalObj.class));
		Assertions.assertFalse(ntInit.isInitialiserFor(DummyTopLevelObj.class));

		Assertions.assertTrue(tInit.isInitialiserFor(DummyTerminalObj.class));
		Assertions.assertFalse(tInit.isInitialiserFor(DummyNonTerminalObj.class));
		Assertions.assertFalse(tInit.isInitialiserFor(DummyTopLevelObj.class));

	}
}
