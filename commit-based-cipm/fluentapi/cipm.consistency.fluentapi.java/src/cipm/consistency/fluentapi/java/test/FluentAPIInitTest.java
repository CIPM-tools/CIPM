package cipm.consistency.fluentapi.java.test;

import java.util.List;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;
import cipm.consistency.fluentapi.extensions.FluentAPIMarkExtension;
import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class containing tests for initialisation classes within the fluent
 * api model.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIInitTest extends AbstractFluentAPITest {
	/**
	 * Checks whether init.getInitialisedEClass() works as intended.
	 */
	@Test
	public void testInit_getInitialisedEClass() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var eCls = ClassifiersPackage.Literals.CLASS;
		Assertions.assertEquals(eCls, api.newX(eCls).getInitialisedEClass());
	}

	/**
	 * Checks whether init.toAPI() works as intended.
	 */
	@Test
	public void testInit_ToAPI() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		Assertions.assertSame(api, api.newAdditionalField().toAPI());
	}

	/**
	 * Checks whether init.reset() works as intended.
	 */
	@Test
	public void testInit_Reset() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsInit = api.newClass();
		Assertions.assertNotNull(clsInit.getCurrentElement());

		clsInit.reset();
		Assertions.assertNull(clsInit.getCurrentElement());

		// Ensure that reset() does not remove the Initialisation instance from API
		Assertions.assertNotNull(api.continueClass());
	}

	/**
	 * Checks whether init.createNow() works as intended.
	 */
	@Test
	public void testInit_CreateNow() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var mod = api.newModule().createNow();
		Assertions.assertInstanceOf(org.emftext.language.java.containers.Module.class, mod);

		// Ensure that createNow() removes the Initialisation instance from api
		Assertions.assertNull(api.continueModule());
	}

	/**
	 * Checks whether init.createNow(class) works as intended, when class is the
	 * exact type of the created object.
	 */
	@Test
	public void testInit_CreateNow_WithTypeCast_ExactType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var mod = api.newModule().createNow(org.emftext.language.java.containers.Module.class);
		Assertions.assertInstanceOf(org.emftext.language.java.containers.Module.class, mod);
	}

	/**
	 * Checks whether init.createNow(class) works as intended, where class is a
	 * super-type of the created object.
	 */
	@Test
	public void testInit_CreateNow_WithTypeCast_Upcasting() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsToCastTo = org.emftext.language.java.commons.NamedElement.class;
		var mod = api.newModule().createNow(clsToCastTo);
		Assertions.assertInstanceOf(org.emftext.language.java.containers.Module.class, mod);
		Assertions.assertInstanceOf(clsToCastTo, mod);
	}

	/**
	 * Checks whether init.createNow(class) works as intended, where class is a
	 * sub-type of the created object.
	 */
	@Test
	public void testInit_CreateNow_WithTypeCast_Downcasting() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsToCastTo = org.emftext.language.java.containers.impl.ModuleImpl.class;
		var mod = api.newModule().createNow(clsToCastTo);
		Assertions.assertInstanceOf(org.emftext.language.java.containers.Module.class, mod);
		Assertions.assertInstanceOf(clsToCastTo, mod);

		// Make sure that the method basicGetOpen() in ModuleImpl is actually found
		// during compilation (not present in
		// org.emftext.language.java.containers.Module, which is the usual return type
		// of api.newModule().createNow())
		Assertions.assertDoesNotThrow(() -> mod.basicGetOpen());
	}

	/**
	 * Checks whether init.dropInitialisation() works as intended.
	 */
	@Test
	public void testInit_DropInitialisation() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsInit = api.newClass();

		// Ensure that clsInit is added to FluentAPIInitialisationStorage
		var inits = FluentAPIInitialisationStorage.getOngoingInitialisations();
		Assertions.assertEquals(1, inits.size());
		Assertions.assertSame(clsInit, inits.get(0));

		clsInit.dropInitialisation();
		// Re-retrieve ongoing initialisations
		inits = FluentAPIInitialisationStorage.getOngoingInitialisations();
		// Ensure that clsInit is removed from FluentAPIInitialisationStorage
		Assertions.assertEquals(0, inits.size());
	}

	/**
	 * Checks whether init.markCurrentElement(key) works as intended.
	 */
	@Test
	public void testInit_MarkCurrentElement() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var key = new Object();

		var clsInit = api.newClass();
		var cls = clsInit.markCurrentElement(key).getCurrentElement();

		Assertions.assertTrue(FluentAPIMarkExtension.hasMark(key));
		Assertions.assertSame(cls, FluentAPIMarkExtension.getMarked(key));
	}

	/**
	 * Checks whether init.unmarkCurrentElement(key) works as intended.
	 */
	@Test
	public void testInit_UnmarkCurrentElement() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var key = new Object();

		var clsInit = api.newClass();
		var cls = clsInit.markCurrentElement(key).getCurrentElement();

		Assertions.assertTrue(FluentAPIMarkExtension.hasMark(key));
		Assertions.assertSame(cls, FluentAPIMarkExtension.getMarked(key));

		clsInit.unmarkCurrentElement(key);

		Assertions.assertFalse(FluentAPIMarkExtension.hasMark(key));
		Assertions.assertNull(FluentAPIMarkExtension.getMarked(key));
	}

	/**
	 * Checks whether init.waitForMark(singleKey, task) works as intended.
	 */
	@Test
	public void testInit_WaitForMark_SingleKey() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsOneName = "clsOne";
		var clsTwoName = "clsTwo";
		var clsOneKey = new Object();
		var clsTwoKey = new Object();

		// Goal:
		// class clsOne extends clsTwo {}
		// class clsTwo {}

		var clsOne =
				// Create clsOne (in isolation)
				api.newClass().withName(clsOneName).markCurrentElement(clsOneKey)
						// Have clsOne extend clsTwo, once clsTwo exists and is marked with clsTwoKey
						.waitForMark(clsTwoKey,
								() -> api.continueMarkedClass(clsOneKey).withExtends(api.getMarkedClass(clsTwoKey)))
						// Create clsTwo and mark it
						.toAPI().newClass().withName(clsTwoName).markCurrentElement(clsTwoKey)
						// Swap to clsOne and return it
						.toAPI().continueMarkedClass(clsOneKey).createNow();

		Assertions.assertEquals(clsOneName, clsOne.getName());
		Assertions.assertEquals(clsTwoName, clsOne.getExtends().getPureClassifierReference().getTarget().getName());
	}

	/**
	 * Checks whether init.waitForMark(keyArray, task) works as intended.
	 */
	@Test
	public void testInit_WaitForMark_KeyArray() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsOneName = "clsOne";
		var clsTwoName = "clsTwo";
		var clsThreeName = "clsThree";
		var clsOneKey = new Object();
		var clsTwoKey = new Object();
		var clsThreeKey = new Object();
		var clsMemberKeys = new Object[] { clsTwoKey, clsThreeKey };

		// Goal:
		// class clsOne {class clsTwo {} class clsThree {}}

		var clsOne =
				// Create clsOne (in isolation)
				api.newClass().withName(clsOneName).markCurrentElement(clsOneKey)
						// Add clsTwo and clsThree to clsOne once both of them are present and marked
						.waitForMark(clsMemberKeys,
								() -> api.continueMarkedClass(clsOneKey).withAddedMembers(
										List.of(api.getMarkedClass(clsTwoKey), api.getMarkedClass(clsThreeKey))))
						// Create clsTwo and mark it
						.toAPI().newClass().withName(clsTwoName).markCurrentElement(clsTwoKey)
						// Create clsThree and mark it
						.toAPI().newClass().withName(clsThreeName).markCurrentElement(clsThreeKey)
						// Swap to clsOne and return it
						.toAPI().continueMarkedClass(clsOneKey).createNow();

		Assertions.assertEquals(clsOneName, clsOne.getName());
		Assertions.assertEquals(2, clsOne.getMembers().size());
		Assertions.assertEquals(clsTwoName, clsOne.getMembers().get(0).getName());
		Assertions.assertEquals(clsThreeName, clsOne.getMembers().get(1).getName());
	}

	/**
	 * Checks whether init.waitForMark(keyCollection, task) works as intended.
	 */
	@Test
	public void testInit_WaitForMark_KeyCollection() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsOneName = "clsOne";
		var clsTwoName = "clsTwo";
		var clsThreeName = "clsThree";
		var clsOneKey = new Object();
		var clsTwoKey = new Object();
		var clsThreeKey = new Object();
		var clsMemberKeys = List.of(clsTwoKey, clsThreeKey);

		// Goal:
		// class clsOne {class clsTwo {} class clsThree {}}

		var clsOne =
				// Create clsOne (in isolation)
				api.newClass().withName(clsOneName).markCurrentElement(clsOneKey)
						// Add clsTwo and clsThree to clsOne once both of them are present and marked
						.waitForMark(clsMemberKeys,
								() -> api.continueMarkedClass(clsOneKey).withAddedMembers(
										List.of(api.getMarkedClass(clsTwoKey), api.getMarkedClass(clsThreeKey))))
						// Create clsTwo and mark it
						.toAPI().newClass().withName(clsTwoName).markCurrentElement(clsTwoKey)
						// Create clsThree and mark it
						.toAPI().newClass().withName(clsThreeName).markCurrentElement(clsThreeKey)
						// Swap to clsOne and return it
						.toAPI().continueMarkedClass(clsOneKey).createNow();

		Assertions.assertEquals(clsOneName, clsOne.getName());
		Assertions.assertEquals(2, clsOne.getMembers().size());
		Assertions.assertEquals(clsTwoName, clsOne.getMembers().get(0).getName());
		Assertions.assertEquals(clsThreeName, clsOne.getMembers().get(1).getName());
	}

}
