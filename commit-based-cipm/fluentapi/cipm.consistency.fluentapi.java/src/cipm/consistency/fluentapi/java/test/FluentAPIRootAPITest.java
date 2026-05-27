package cipm.consistency.fluentapi.java.test;

import java.util.List;
import java.util.stream.Collectors;

import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;
import cipm.consistency.fluentapi.extensions.FluentAPIMarkExtension;
import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.java.metamodel.FluentAPIJavaMetamodelFilter;
import cipm.consistency.fluentapi.java.metamodel.FluentAPIJavaMetamodelPackageProvider;
import cipm.consistency.fluentapi.test.AbstractFluentAPITest;

/**
 * A test class for fluent api class.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIRootAPITest extends AbstractFluentAPITest {
	/**
	 * Checks whether api.mark(key, obj) works as intended
	 */
	@Test
	public void testAPI_Mark() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var clsKey = new Object();

		api.mark(clsKey, cls);
		Assertions.assertSame(cls, FluentAPIMarkExtension.getMarked(clsKey));
	}

	/**
	 * Ensures that different fluent api instances share the same marks.
	 */
	@Test
	public void testAPI_Mark_DifferentAPIs() {
		var keyOne = new Object();
		var apiOne = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsOne = ClassifiersFactory.eINSTANCE.createClass();

		apiOne.mark(keyOne, clsOne);

		var keyTwo = new Object();
		var apiTwo = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var clsTwo = ClassifiersFactory.eINSTANCE.createClass();

		apiTwo.mark(keyTwo, clsTwo);

		Assertions.assertSame(clsOne, apiOne.getMarkedX(keyOne));
		Assertions.assertSame(clsTwo, apiOne.getMarkedX(keyTwo));
		Assertions.assertSame(clsOne, apiTwo.getMarkedX(keyOne));
		Assertions.assertSame(clsTwo, apiTwo.getMarkedX(keyTwo));
	}

	/**
	 * Checks that api.unmark(key) works as intended.
	 */
	@Test
	public void testAPI_Unmark_WithKey() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var clsKey = new Object();

		api.mark(clsKey, cls);
		Assertions.assertSame(cls, FluentAPIMarkExtension.getMarked(clsKey));
		api.unmark(clsKey);
		Assertions.assertNull(FluentAPIMarkExtension.getMarked(clsKey));
	}

	/**
	 * Checks that api.unmark(key, val) works as intended.
	 */
	@Test
	public void testAPI_Unmark_WithKeyAndValue() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var clsKey = new Object();

		api.mark(clsKey, cls);
		Assertions.assertSame(cls, FluentAPIMarkExtension.getMarked(clsKey));
		api.unmark(clsKey, cls);
		Assertions.assertNull(FluentAPIMarkExtension.getMarked(clsKey));
	}

	/**
	 * Checks whether api.getMarkedX(key) works as intended
	 */
	@Test
	public void testAPI_GetMarkedX() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var clsKey = new Object();

		api.mark(clsKey, cls);
		// Retrieve regardless of type
		Assertions.assertSame(cls, api.getMarkedX(clsKey));
	}

	/**
	 * Checks whether api.getMarkedX(key) works as intended, where X is the exact
	 * type of the model element.
	 */
	@Test
	public void testAPI_GetMarkedType_MatchingType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var clsKey = new Object();

		api.mark(clsKey, cls);
		Assertions.assertSame(cls, api.getMarkedClass(clsKey));
	}

	/**
	 * Checks whether api.getMarkedX(key) works as intended, where X is a super-type
	 * of the model element
	 */
	@Test
	public void testAPI_GetMarkedType_AbstractSuperType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		var clsKey = new Object();

		api.mark(clsKey, cls);
		Assertions.assertSame(cls, api.getMarkedNamedElement(clsKey));
	}

	/**
	 * Checks whether api.modifyX(obj) works as intended.
	 */
	@Test
	public void testAPI_ModifyX() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var cls = api.newClass().createNow();

		var init = api.modifyX(cls);
		// Ensure that modifyX() does not remove the Initialisation instance
		Assertions.assertEquals(1, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertSame(init, FluentAPIInitialisationStorage.getOngoingInitialisations().get(0));

		var cls2 = init.createNow();
		Assertions.assertSame(cls, cls2);
	}

	/**
	 * Checks whether api.modifyX(obj) works as intended, where X is the type of
	 * obj.
	 */
	@Test
	public void testAPI_ModifyType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var cls = api.newClass().createNow();

		var init = api.modifyClass(cls);
		// Ensure that modifyX() does not remove the Initialisation instance
		Assertions.assertEquals(1, FluentAPIInitialisationStorage.getOngoingInitialisations().size());
		Assertions.assertSame(init, FluentAPIInitialisationStorage.getOngoingInitialisations().get(0));

		var cls2 = init.createNow();
		Assertions.assertSame(cls, cls2);
	}

	/**
	 * Checks whether api.modifyMarkedX(obj) works as intended.
	 */
	@Test
	public void testAPI_ModifyMarkedX() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsKey = new Object();
		var cls = api.newClass().markCurrentElement(clsKey).createNow();

		Assertions.assertSame(cls, api.getMarkedClass(clsKey));

		var cls2 = api.modifyMarkedX(clsKey).createNow();
		Assertions.assertSame(cls, cls2);

	}

	/**
	 * Checks whether api.modifyMarkedX(obj) works as intended, where X is the type
	 * of obj.
	 */
	@Test
	public void testAPI_ModifyMarkedType() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var clsKey = new Object();
		var cls = api.newClass().markCurrentElement(clsKey).createNow();

		Assertions.assertSame(cls, api.getMarkedClass(clsKey));

		var cls2 = api.modifyMarkedClass(clsKey).createNow();
		Assertions.assertSame(cls, cls2);

	}

	/**
	 * Checks whether api.getAllSupportedClasses() works as intended.
	 */
	@Test
	public void testAPI_GetAllSupportedClasses() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();

		var supportedClasses = api.getAllSupportedClasses();
		var provider = new FluentAPIJavaMetamodelPackageProvider();
		var filter = new FluentAPIJavaMetamodelFilter();
		var expectedSupportedEClasses = provider.getAllTargetMetamodelConcreteEClasses().stream()
				.filter((eCls) -> filter.isEClassEligible(eCls)).collect(Collectors.toList());
		var expectedSupportedClasses = expectedSupportedEClasses.stream().map((eCls) -> eCls.getInstanceClass())
				.collect(Collectors.toList());
		Assertions.assertEquals(expectedSupportedClasses.size(), supportedClasses.size());
		Assertions.assertTrue(supportedClasses.containsAll(expectedSupportedClasses));
		var originalEClasses = provider.getAllConcreteEClassesInOriginalMetamodel().stream()
				.filter((eCls) -> filter.isEClassEligible(eCls)).collect(Collectors.toList());
		Assertions.assertEquals(originalEClasses.size(), supportedClasses.size());
		Assertions.assertTrue(originalEClasses.stream().allMatch(
				(orECls) -> supportedClasses.stream().anyMatch((suCls) -> orECls.getInstanceClass().equals(suCls))));
	}

	/**
	 * Checks whether api.waitForMark(key, task) works as intended
	 */
	@Test
	public void testAPI_WaitForMark_SingleKey() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var key = new Object();
		final var waitForMarkRan = new boolean[] { false };

		api.waitForMark(key, () -> waitForMarkRan[0] = true);
		Assertions.assertFalse(waitForMarkRan[0]);
		api.newModule().markCurrentElement(key);
		Assertions.assertTrue(waitForMarkRan[0]);
	}

	/**
	 * Checks whether api.waitForMark(keyArray, task) works as intended
	 */
	@Test
	public void testAPI_WaitForMark_KeyArray() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var key1 = new Object();
		var key2 = new Object();
		final var waitForMarkRan = new boolean[] { false };

		api.waitForMark(new Object[] { key1, key2 }, () -> waitForMarkRan[0] = true);
		Assertions.assertFalse(waitForMarkRan[0]);
		api.newModule().markCurrentElement(key1);
		Assertions.assertFalse(waitForMarkRan[0]);
		api.newModule().markCurrentElement(key2);
		Assertions.assertTrue(waitForMarkRan[0]);
	}

	/**
	 * Checks whether api.waitForMark(keyCol, task) works as intended.
	 */
	@Test
	public void testAPI_WaitForMark_KeyCollection() {
		var api = ApiFactory.eINSTANCE.createFluentJavaAPI();
		var key1 = new Object();
		var key2 = new Object();
		final var waitForMarkRan = new boolean[] { false };

		api.waitForMark(List.of(key1, key2), () -> waitForMarkRan[0] = true);
		Assertions.assertFalse(waitForMarkRan[0]);
		api.newModule().markCurrentElement(key1);
		Assertions.assertFalse(waitForMarkRan[0]);
		api.newModule().markCurrentElement(key2);
		Assertions.assertTrue(waitForMarkRan[0]);
	}
}
