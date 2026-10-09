package cipm.consistency.fluentapi.test;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;

/**
 * A test case for testing {@link FluentAPIInitialisationStorage}.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIInitialisationStorageTest {
	/**
	 * Resets {@link FluentAPIInitialisationStorage}
	 */
	@BeforeEach
	public void setUp() {
		FluentAPIInitialisationStorage.clearAllOngoingInitialisations();
	}

	/**
	 * Ensures that adding a single (supposed) initialisation instance works as
	 * intended.
	 */
	@Test
	public void testAddOngoingInitialisations_SingleInitialisation() {
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());

		var supposedInit = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit);

		Assertions.assertEquals(1, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertTrue(FluentAPIInitialisationStorage.getOngoingInitialisations().contains(supposedInit));
	}

	/**
	 * Ensures that adding duplicated initialisation instances is handled
	 * accordingly.
	 */
	@Test
	public void testAddOngoingInitialisations_NoDuplicatedInitialisation() {
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());

		var supposedInit = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit);
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit);

		Assertions.assertEquals(1, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertTrue(FluentAPIInitialisationStorage.getOngoingInitialisations().contains(supposedInit));
	}

	/**
	 * Ensures that adding multiple (supposed) initialisation instances works as
	 * intended.
	 */
	@Test
	public void testAddOngoingInitialisations_MultipleInitialisations() {
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());

		var supposedInit1 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit1);
		var supposedInit2 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit2);

		Assertions.assertEquals(2, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertTrue(FluentAPIInitialisationStorage.getOngoingInitialisations().contains(supposedInit1));
		Assertions.assertTrue(FluentAPIInitialisationStorage.getOngoingInitialisations().contains(supposedInit2));
	}

	/**
	 * Ensures that the (supposed) initialisation instances are retrieved in the
	 * order they were added.
	 */
	@Test
	public void testAddOngoingInitialisations_Order() {
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());

		var supposedInit1 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit1);
		var supposedInit2 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit2);

		Assertions.assertEquals(2, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertSame(supposedInit1, FluentAPIInitialisationStorage.getOngoingInitialisations().get(0));
		Assertions.assertSame(supposedInit2, FluentAPIInitialisationStorage.getOngoingInitialisations().get(1));
	}

	/**
	 * Ensures that removing a single (supposed) initialisation instance works as
	 * intended.
	 */
	@Test
	public void testDropInitialisation_SingleInitialisation() {
		var supposedInit = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit);
		FluentAPIInitialisationStorage.dropOngoingInitialisation(supposedInit);
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
	}

	/**
	 * Ensures that removing multiple (supposed) initialisation instances works as
	 * intended.
	 */
	@Test
	public void testDropInitialisation_MultipleInitialisations() {
		var supposedInit1 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit1);
		var supposedInit2 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit2);

		FluentAPIInitialisationStorage.dropOngoingInitialisation(supposedInit1);
		Assertions.assertEquals(1, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertFalse(FluentAPIInitialisationStorage.getOngoingInitialisations().contains(supposedInit1));
		Assertions.assertTrue(FluentAPIInitialisationStorage.getOngoingInitialisations().contains(supposedInit2));

		FluentAPIInitialisationStorage.dropOngoingInitialisation(supposedInit2);
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
	}

	/**
	 * Ensures that the order of (supposed) initialisation instances are retained
	 * when they are removed (up to the one initialisation instance that was
	 * removed).
	 */
	@Test
	public void testDropInitialisation_Order() {
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());

		var supposedInit1 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit1);
		var supposedInit2 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit2);
		var supposedInit3 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit3);

		Assertions.assertArrayEquals(new EObject[] { supposedInit1, supposedInit2, supposedInit3 },
				FluentAPIInitialisationStorage.getOngoingInitialisations().toArray());

		FluentAPIInitialisationStorage.dropOngoingInitialisation(supposedInit2);

		Assertions.assertEquals(2, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertSame(supposedInit1, FluentAPIInitialisationStorage.getOngoingInitialisations().get(0));
		Assertions.assertSame(supposedInit3, FluentAPIInitialisationStorage.getOngoingInitialisations().get(1));
	}

	/**
	 * Ensures that removing all (supposed) initialisation instances works as
	 * intended.
	 */
	@Test
	public void testClear() {
		var supposedInit1 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit1);
		var supposedInit2 = EcoreFactory.eINSTANCE.createEObject();
		FluentAPIInitialisationStorage.addOngoingInitialisation(supposedInit2);

		FluentAPIInitialisationStorage.clearAllOngoingInitialisations();
		Assertions.assertEquals(0, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
	}
}
