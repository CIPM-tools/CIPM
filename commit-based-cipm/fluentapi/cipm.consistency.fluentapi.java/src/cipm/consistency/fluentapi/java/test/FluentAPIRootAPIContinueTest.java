package cipm.consistency.fluentapi.java.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class for the following fluent api class methods: continue...(),
 * continueX(), continueMarked...(), continueMarkedX().
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIRootAPIContinueTest extends AbstractFluentAPITest {
	/**
	 * Checks whether the api.continueX() method works as intended.
	 */
	@Test
	public void testAPI_Continue_TopLevel() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var modInit = api.newModule();
		var pacInit = api.newPackage();

		var continuedModInit = api.continueX(org.emftext.language.java.containers.Module.class);
		Assertions.assertSame(modInit, continuedModInit);
		var continuedPacInit = api.continueX(org.emftext.language.java.containers.Package.class);
		Assertions.assertSame(pacInit, continuedPacInit);
	}

	/**
	 * Checks whether the api.continueX() method works as intended, when the
	 * construction of the same element is continued.
	 */
	@Test
	public void testAPI_Continue_SameElementInstance() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var modInit = api.newModule();
		var continuedModInit = api.continueModule();

		Assertions.assertSame(modInit, continuedModInit);
	}

	/**
	 * Checks whether the api.continueX() method works as intended, when the
	 * construction of a different element of the same type is continued.
	 */
	@Test
	public void testAPI_Continue_SameElementType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var modInit1 = api.newModule();
		var modInit2 = api.newModule();
		Assertions.assertNotSame(modInit1, modInit2);
		Assertions.assertSame(modInit2, api.continueModule());
	}

	/**
	 * Checks whether the api.continueX() method works as intended, when the
	 * construction of a different element is continued.
	 */
	@Test
	public void testAPI_Continue_DifferentElementType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var modInit = api.newModule();
		var pacInit = api.newPackage();

		Assertions.assertSame(modInit, api.continueModule());
		Assertions.assertSame(pacInit, api.continuePackage());
	}

	/**
	 * Checks whether api.continueX() returns null, if there is no XInitialisation
	 * instance to be found
	 */
	@Test
	public void testAPI_Continue_NoInitialisation() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		Assertions.assertNull(api.continueModule());
	}

	/**
	 * Checks whether the api.continueMarked...() method works as intended, when
	 * there is only one Initialisation instance.
	 */
	@Test
	public void testAPI_ContinueMarked_SingleInitialisation() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var modKey = new Object();

		var modInit = api.newModule().markCurrentElement(modKey);

		Assertions.assertSame(modInit, api.continueMarkedModule(modKey));
	}

	/**
	 * Checks whether the api.continueMarked...() method works as intended, when
	 * there are multiple Initialisation instances.
	 */
	@Test
	public void testAPI_ContinueMarked_MultipleInitialisation() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var keyOne = new Object();
		var keyTwo = new Object();

		var modInitOne = api.newModule().markCurrentElement(keyOne);
		var modInitTwo = api.newModule().markCurrentElement(keyTwo);

		Assertions.assertSame(modInitOne, api.continueMarkedModule(keyOne));
		Assertions.assertSame(modInitTwo, api.continueMarkedModule(keyTwo));
	}

	/**
	 * Checks whether the api.continueMarkedX() method works as intended.
	 */
	@Test
	public void testAPI_ContinueMarked_TopLevel() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var keyOne = new Object();
		var keyTwo = new Object();

		var modInit = api.newModule().markCurrentElement(keyOne);
		var pacInit = api.newPackage().markCurrentElement(keyTwo);

		Assertions.assertSame(modInit, api.continueMarkedX(keyOne));
		Assertions.assertSame(pacInit, api.continueMarkedX(keyTwo));
	}
}
