package cipm.consistency.fluentapi.java.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class for the following fluent api class methods: getOngoingInits(),
 * clearAllOngoingInits().
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIRootAPIGetOngoingInitTest extends AbstractFluentAPITest {
	/**
	 * Checks whether api.getOngoingInitialisations() returns an empty list, if
	 * there are no initialisations.
	 */
	@Test
	public void testAPI_GetOngoingInitialisations_NoInitialisations() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		Assertions.assertEquals(0, api.getOngoingInitialisations().size());
	}

	/**
	 * Checks whether api.getOngoingInitialisations() works as intended.
	 */
	@Test
	public void testAPI_GetOngoingInitialisations() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsInit = api.newClass();

		Assertions.assertEquals(1, api.getOngoingInitialisations().size());
		Assertions.assertSame(clsInit, api.getOngoingInitialisations().get(0));
	}

	/**
	 * Ensures that different fluent api instances share the same initialisation
	 * instances.
	 */
	@Test
	public void testAPI_GetOngoingInitialisations_DifferentAPIInstances() {
		var apiOne = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var apiTwo = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var init1 = apiOne.newClass();
		var init2 = apiTwo.newClass();
		var init3 = apiOne.newClass();

		for (var api : List.of(apiOne, apiTwo)) {
			Assertions.assertEquals(3, api.getOngoingInitialisations().size());
			Assertions.assertSame(init1, api.getOngoingInitialisations().get(0));
			Assertions.assertSame(init2, api.getOngoingInitialisations().get(1));
			Assertions.assertSame(init3, api.getOngoingInitialisations().get(2));
		}
	}

	/**
	 * Ensures that api.clearAllOngoingInitialisations() works as intended.
	 */
	@Test
	public void testAPI_ClearAllOngoingInitialisations() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		api.newClass();
		api.newInterface();
		Assertions.assertEquals(2, api.getOngoingInitialisations().size());
		api.clearAllOngoingInitialisations();
		Assertions.assertEquals(0, api.getOngoingInitialisations().size());
	}
}
